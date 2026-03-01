package com.shabanaj.beloyal.common.email.dto;

public record SendEmailEvent(String to, String subject, String bodyHtml) {}