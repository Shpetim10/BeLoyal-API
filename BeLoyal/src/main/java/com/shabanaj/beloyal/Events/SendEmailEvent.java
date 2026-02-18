package com.shabanaj.beloyal.Events;

public record SendEmailEvent(String to, String subject, String bodyHtml) {}