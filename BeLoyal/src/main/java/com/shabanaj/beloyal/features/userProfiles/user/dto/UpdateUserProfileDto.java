package com.shabanaj.beloyal.features.userProfiles.user.dto;

import com.shabanaj.beloyal.common.Validation.Annotation.UniqueUsernameOnUpdate;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UpdateUserProfileDto {
    @Size(max = 100)
    private JsonNullable<String> firstName;
    @Size(max = 100)
    private JsonNullable<String> lastName;
    @UniqueUsernameOnUpdate
    private JsonNullable<String> username;
    private JsonNullable<String> phoneNumber;
    private JsonNullable<String> imagePath;
    private JsonNullable<String> imageKey;
}
