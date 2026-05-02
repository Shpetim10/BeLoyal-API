package com.shabanaj.beloyal.features.redirect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
public class DeepLinkRedirectController {

    @GetMapping(value = "/activate", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> activate(@RequestParam String token) {
        String deepLink = "besahub:///api/besahub/auth/activate?token=" + token;
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Opening BeLoyal...</title>
                  <meta http-equiv="refresh" content="0; url=%s">
                </head>
                <body>
                  <script>
                    window.location.replace('%s');
                  </script>
                  <p>Opening the BeLoyal app…
                    <a href="%s">Tap here if it doesn't open</a>
                  </p>
                </body>
                </html>
                """.formatted(deepLink, deepLink, deepLink);
        return ResponseEntity.ok(html);
    }

    @GetMapping(value = "/accept-invitation", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> acceptInvitation(
            @RequestParam String token,
            @RequestParam(required = false, defaultValue = "false") String existing,
            @RequestParam(required = false, defaultValue = "") String email) {
        String deepLink = "besahub:///api/besahub/auth/accept-invitation?token=" + token
                + "&existing=" + existing
                + "&email=" + email;
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Opening BeLoyal...</title>
                  <meta http-equiv="refresh" content="0; url=%s">
                </head>
                <body>
                  <script>
                    window.location.replace('%s');
                  </script>
                  <p>Opening the BeLoyal app…
                    <a href="%s">Tap here if it doesn't open</a>
                  </p>
                </body>
                </html>
                """.formatted(deepLink, deepLink, deepLink);
        return ResponseEntity.ok(html);
    }

    @GetMapping(value = "/reset-password", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> resetPassword(@RequestParam String token) {
        String deepLink = "besahub:///forget-password?token=" + token;
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Opening BeLoyal...</title>
                  <meta http-equiv="refresh" content="0; url=%s">
                </head>
                <body>
                  <script>
                    window.location.replace('%s');
                  </script>
                  <p>Opening the BeLoyal app…
                    <a href="%s">Tap here if it doesn't open</a>
                  </p>
                </body>
                </html>
                """.formatted(deepLink, deepLink, deepLink);
        return ResponseEntity.ok(html);
    }
}
