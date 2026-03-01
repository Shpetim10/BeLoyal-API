package com.shabanaj.beloyal.common.image_upload;

import com.shabanaj.beloyal.model.Enums.ImageCategory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/besahub/media")
public class MediaController {

    private final ImageUploadService imageUploadService;

    public MediaController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    // TODO: ADD SECURITY
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResult> uploadImage(
            @RequestParam ImageCategory category,
            @RequestParam Long ownerId,
            @RequestPart("file") MultipartFile file) {
        // You MUST secure this. Example policies:
        // - If category == USER_PROFILE, ownerId must equal authenticated user id.
        // - If category == BUSINESS_LOGO or MENU_ITEM, check business role access.

        ImageUploadResult result = imageUploadService.upload(category, ownerId, file);
        return ResponseEntity.ok(result);
    }
}