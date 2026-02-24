package com.shabanaj.beloyal.userProfiles.user.dto;

import com.shabanaj.beloyal.common.Validation.Annotation.UniqueUsernameOnCreate;
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
    @UniqueUsernameOnCreate
    private JsonNullable<String> username;
    private JsonNullable<String> phoneNumber;
    private JsonNullable<String> imagePath;
    private JsonNullable<String> imageKey;
}
