package com.shabanaj.beloyal.features.catalogCategories.controller;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-category")
@RequiredArgsConstructor
public class ViewCatalogCategoryController {
    private final CatalogCategoryViewService  catalogCategoryViewService;

    @GetMapping("/{id}")
    public ResponseEntity<CatalogCategoryViewDto> viewCatalogCategory(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id){
        return ResponseEntity.ok(catalogCategoryViewService.viewCatalogCategory(businessId, id));
    }

    @GetMapping
    public ResponseEntity<List<CatalogCategoryViewDto>> viewCatalogCategories(@PathVariable("businessId") Long businessId){
        return ResponseEntity.ok(catalogCategoryViewService.viewCatalogCategories(businessId));
    }
}
