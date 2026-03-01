package com.shabanaj.beloyal.features.userProfiles.customer.dto;

import com.shabanaj.beloyal.model.Enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerProfileUpdateDto {
    @Past
    private JsonNullable<LocalDate> birthDate;

    @Enumerated(EnumType.STRING)
    private JsonNullable<Gender> gender;

    @Size(max = 100)
    private JsonNullable<String> city;

    @Size(max = 100)
    private JsonNullable<String> country;

    private JsonNullable<Boolean> notificationEnabled;
}
