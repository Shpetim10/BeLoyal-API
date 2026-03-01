package com.shabanaj.beloyal.features.userProfiles.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class BusinessProfileUpdateDto {

    // cannot be cleared
    private JsonNullable<String> businessName = JsonNullable.undefined();
    private JsonNullable<String> businessType = JsonNullable.undefined();
    private JsonNullable<String> city = JsonNullable.undefined();
    private JsonNullable<String> vatId = JsonNullable.undefined();
    @Size(max = 1000)
    private JsonNullable<String> businessDescription = JsonNullable.undefined();

    private JsonNullable<String> logoPath = JsonNullable.undefined();
    private JsonNullable<String> logoKey = JsonNullable.undefined();

    private JsonNullable<String> address = JsonNullable.undefined();
    private JsonNullable<String> country = JsonNullable.undefined();
    private JsonNullable<String> websiteUrl = JsonNullable.undefined();

    private JsonNullable<String> businessPhoneNumber = JsonNullable.undefined();

    @Email
    private JsonNullable<String> businessEmail = JsonNullable.undefined();
}
