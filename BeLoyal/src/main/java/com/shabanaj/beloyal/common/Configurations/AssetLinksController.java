package com.shabanaj.beloyal.common.Configurations;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
public class AssetLinksController {

    @GetMapping(value = "/.well-known/assetlinks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAssetLinks() throws IOException {
        Resource resource = new ClassPathResource("static/.well-known/assetlinks.json");
        String content = new String(Files.readAllBytes(resource.getFile().toPath()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(content);
    }
}