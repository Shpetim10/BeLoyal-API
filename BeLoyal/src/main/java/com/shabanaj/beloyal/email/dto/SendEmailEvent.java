package com.shabanaj.beloyal.email.dto;

public record SendEmailEvent(String to, String subject, String bodyHtml) {}