package com.shabanaj.beloyal.features.coupon.controller;

import com.shabanaj.beloyal.common.image_upload.ImageUploadResult;
import com.shabanaj.beloyal.common.image_upload.ImageUploadService;
import com.shabanaj.beloyal.model.Enums.ImageCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/media")
@RequiredArgsConstructor
public class CouponImageUploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/coupon-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<ImageUploadResult> uploadCouponImage(
            @PathVariable Long businessId,
            @RequestPart("file") MultipartFile file) {
        ImageUploadResult result = imageUploadService.upload(ImageCategory.COUPON_IMAGE, businessId, file);
        return ResponseEntity.ok(result);
    }
}
