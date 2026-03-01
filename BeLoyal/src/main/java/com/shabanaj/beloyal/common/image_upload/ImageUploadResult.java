package com.shabanaj.beloyal.common.image_upload;

public record ImageUploadResult(
        String url,
        String key,
        String contentType,
        long sizeBytes
) {}