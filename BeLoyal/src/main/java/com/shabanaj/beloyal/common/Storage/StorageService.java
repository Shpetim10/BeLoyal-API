package com.shabanaj.beloyal.common.Storage;

public interface StorageService {
    StoredObject put(String key, String contentType, byte[] bytes);
    void delete(String key);
    String publicUrl(String key);
}
