package com.shabanaj.beloyal.features.userProfiles.user.dto;

import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDetailsDto {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phoneNumber;
    private String imagePath;
    private String profileImageUrl;
    private boolean acceptedTerms;
    private Set<Role> roles;

    public UserDetailsDto(User user){
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.imagePath = user.getProfileImage();
        this.profileImageUrl = user.getProfileImage();
        this.acceptedTerms = user.getAcceptedTcVersion() != null;
        this.roles = user.getRoles();
    }
}
