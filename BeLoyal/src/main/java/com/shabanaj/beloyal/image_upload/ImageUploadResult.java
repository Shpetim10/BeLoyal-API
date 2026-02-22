package com.shabanaj.beloyal.image_upload;

public record ImageUploadResult(
        String url,
        String key,
        String contentType,
        long sizeBytes
) {}