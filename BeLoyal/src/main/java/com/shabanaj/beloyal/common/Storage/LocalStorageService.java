package com.shabanaj.beloyal.common.Storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootDir;
    private final String publicBaseUrl;

    public LocalStorageService(
            @Value("${app.storage.local.root:uploads}") String root,
            @Value("${app.storage.public-base-url:http://localhost:8080}") String publicBaseUrl) {
        this.rootDir = Path.of(root).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    public StoredObject put(String key, String contentType, byte[] bytes) {
        try {
            // key example: "users/4/profile/uuid.jpg"
            // Ensure no leading slashes to prevent root-escape attempts
            String safeKey = key.startsWith("/") ? key.substring(1) : key;
            Path target = rootDir.resolve(safeKey).normalize();

            if (target.getParent() != null) {
                Files.createDirectories(target.getParent());
            }

            Files.write(target, bytes);
            return new StoredObject(key, publicUrl(key));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file to local storage", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Path target = rootDir.resolve(key).normalize();
            Files.deleteIfExists(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from local storage", e);
        }
    }

    @Override
    public String publicUrl(String key) {
        return publicBaseUrl + "/uploads/" + key.replace("\\", "/");
    }
}
