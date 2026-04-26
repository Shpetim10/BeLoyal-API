package com.shabanaj.beloyal.features.catalogItems.service.impl;

import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Shared utility that re-assigns consecutive 0-based order-indices to all
 * non-deleted items in a category.
 *
 * <p>Called after create, delete, restore, move, and explicit reorder operations
 * to keep indices contiguous and gap-free.
 *
 * <p><b>Constraint-safe strategy:</b> MySQL enforces the unique index at the
 * row level on each UPDATE, so we cannot simply renumber rows in place when
 * two items need to swap indices. Instead we use a two-phase approach:
 * <ol>
 *   <li>Set every item's {@code order_index} to a large negative value that is
 *       guaranteed to be unique and will never collide with a real index.</li>
 *   <li>Flush, then assign the final 0-based sequential indices.</li>
 * </ol>
 *
 * The large negative offset {@code -(Integer.MAX_VALUE / 2 + id)} is chosen
 * so that even if an entity has a very large ID the result stays well within
 * {@code int} range without overflow.
 */
@Component
@RequiredArgsConstructor
public class CatalogItemOrderHelper {

    private final CatalogItemRepository catalogItemRepository;

    /**
     * Recompact all non-deleted items for the given category by their current
     * stored order.  Delegates to {@link #recompact(List)} after loading.
     */
    public void recompact(CatalogCategory category) {
        List<CatalogItem> items =
                catalogItemRepository.findAllByCategoryAndIsDeletedFalseOrderByOrderIndexAsc(category);
        recompact(items);
    }

    /**
     * Assign consecutive 0-based indices to the supplied list in the order
     * the list is given.  The caller is responsible for pre-sorting the list
     * to the desired final order before calling this method.
     *
     * <p>Uses a two-phase DB write to avoid unique-constraint violations:
     * <ol>
     *   <li>Phase 1 – shift all indices far into negative space so no two rows
     *       share the same value, then flush to the DB.</li>
     *   <li>Phase 2 – assign the final 0..n-1 indices, then flush again.</li>
     * </ol>
     *
     * @param items pre-sorted list of items to be recompacted (must not be null)
     */
    public void recompact(List<CatalogItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        // Fast-path: if the list is already perfectly compacted, do nothing.
        boolean alreadyCompact = true;
        for (int i = 0; i < items.size(); i++) {
            Integer idx = items.get(i).getOrderIndex();
            if (idx == null || idx != i) {
                alreadyCompact = false;
                break;
            }
        }
        if (alreadyCompact) {
            return;
        }

        // Phase 1: set unique negative sentinel values to clear all conflicts.
        // We use -(1_000_000 + position) — well within int range regardless of ID size.
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setOrderIndex(-(1_000_000 + i));
        }
        catalogItemRepository.saveAllAndFlush(items);

        // Phase 2: assign final 0-based indices.
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setOrderIndex(i);
        }
        catalogItemRepository.saveAllAndFlush(items);
    }
}
