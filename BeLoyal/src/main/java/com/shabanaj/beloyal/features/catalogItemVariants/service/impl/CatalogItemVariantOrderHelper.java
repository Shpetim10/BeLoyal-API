package com.shabanaj.beloyal.features.catalogItemVariants.service.impl;

import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Shared utility that re-assigns consecutive 0-based order indices to all
 * non-deleted variants of a catalog item.
 *
 * <p><b>Constraint-safe strategy:</b> The unique index on
 * {@code (catalog_item_id, order_index)} is enforced row-level by the DB
 * engine, so a naive in-place renumber can violate the constraint mid-update.
 * We avoid this with a two-phase approach:
 * <ol>
 *   <li>Set every variant's {@code order_index} to a large unique negative
 *       sentinel value and flush.</li>
 *   <li>Assign the final 0-based sequential indices and flush again.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class CatalogItemVariantOrderHelper {

    private final CatalogItemVariantRepository catalogItemVariantRepository;

    /**
     * Recompact all active variants for the given catalog item by their current
     * stored order. Delegates to {@link #recompact(List)} after loading.
     */
    public void recompact(Long catalogItemId) {
        List<CatalogItemVariant> variants =
                catalogItemVariantRepository.findByCatalogItemIdAndIsDeletedFalseOrderByOrderIndexAsc(catalogItemId);
        recompact(variants);
    }

    /**
     * Assign consecutive 0-based indices to the supplied list in the order the
     * list is given. The caller is responsible for pre-sorting the list to the
     * desired final order before calling this method.
     *
     * @param variants pre-sorted list of variants to be recompacted (must not be null)
     */
    public void recompact(List<CatalogItemVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            return;
        }

        // Fast-path: already perfectly compacted — nothing to write.
        boolean alreadyCompact = true;
        for (int i = 0; i < variants.size(); i++) {
            Integer idx = variants.get(i).getOrderIndex();
            if (idx == null || idx != i) {
                alreadyCompact = false;
                break;
            }
        }
        if (alreadyCompact) {
            return;
        }

        // Phase 1: unique negative sentinels to clear all conflicts.
        for (int i = 0; i < variants.size(); i++) {
            variants.get(i).setOrderIndex(-(1_000_000 + i));
        }
        catalogItemVariantRepository.saveAllAndFlush(variants);

        // Phase 2: final 0-based sequential indices.
        for (int i = 0; i < variants.size(); i++) {
            variants.get(i).setOrderIndex(i);
        }
        catalogItemVariantRepository.saveAllAndFlush(variants);
    }
}
