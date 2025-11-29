package com.shabanaj.beloyal.Controller;

import com.shabanaj.beloyal.Service.CustomerProfileService;
import com.shabanaj.beloyal.Service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beloyal/customers/me")
public class CustomerProfileController {
    private final CustomerProfileService customerProfileService;
    private final UserService userService;

    public CustomerProfileController(CustomerProfileService customerProfileService, UserService userService) {
        this.customerProfileService = customerProfileService;
        this.userService = userService;
    }


}
