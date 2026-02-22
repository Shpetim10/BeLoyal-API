package com.shabanaj.beloyal.image_upload;

import com.shabanaj.beloyal.common.Storage.StorageService;
import com.shabanaj.beloyal.model.Enums.ImageCategory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageUploadService {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );

    private final StorageService storageService;

    public ImageUploadService(StorageService storageService) {
        this.storageService = storageService;
    }

    public ImageUploadResult upload(ImageCategory category, Long ownerId, MultipartFile file) {
        validate(file, category);

        byte[] bytes = toBytes(file);

        // Decode to ensure it's a real image (don’t trust Content-Type alone)
        ensureDecodableImage(bytes);

        String ext = extensionFor(file.getContentType()); // normalize naming
        String key = buildKey(category, ownerId, ext);

        var stored = storageService.put(key, file.getContentType(), bytes);

        return new ImageUploadResult(
                stored.url(),
                stored.key(),
                file.getContentType(),
                bytes.length
        );
    }

    public void deleteByKey(String key) {
        storageService.delete(key);
    }

    private void validate(MultipartFile file, ImageCategory category) {
        if (file == null || file.isEmpty()) throw new RuntimeException("File is required");

        long maxBytes = switch (category) {
            case USER_PROFILE -> 3 * 1024 * 1024;  // 3MB
            case BUSINESS_LOGO -> 3 * 1024 * 1024; // 3MB
            case MENU_ITEM -> 5 * 1024 * 1024;     // 5MB
        };

        if (file.getSize() > maxBytes) {
            throw new RuntimeException("File too large. Max is " + (maxBytes / (1024 * 1024)) + "MB");
        }

        String ct = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        if (!ALLOWED_TYPES.contains(ct)) {
            throw new RuntimeException("Only JPG, PNG, WEBP allowed");
        }
    }

    private byte[] toBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }
    }

    private void ensureDecodableImage(byte[] bytes) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
            if (img == null) throw new RuntimeException("Invalid image content");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Invalid image content", e);
        }
    }

    private String buildKey(ImageCategory category, Long ownerId, String ext) {
        String uuid = UUID.randomUUID().toString();
        // Example paths:
        // users/12/profile/<uuid>.jpg
        // businesses/9/logo/<uuid>.png
        // businesses/9/menu/<uuid>.jpg
        return switch (category) {
            case USER_PROFILE -> "users/%d/profile/%s.%s".formatted(ownerId, uuid, ext);
            case BUSINESS_LOGO -> "businesses/%d/logo/%s.%s".formatted(ownerId, uuid, ext);
            case MENU_ITEM -> "businesses/%d/menu/%s.%s".formatted(ownerId, uuid, ext);
        };
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg"; // treat jpeg as jpg
        };
    }
}
