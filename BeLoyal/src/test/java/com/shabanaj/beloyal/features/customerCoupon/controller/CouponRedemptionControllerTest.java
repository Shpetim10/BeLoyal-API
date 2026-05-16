package com.shabanaj.beloyal.features.customerCoupon.controller;

import com.shabanaj.beloyal.common.Exception.InvalidCouponOperationException;
import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerCoupon.service.CouponRedemptionService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class CouponRedemptionControllerTest {

    @Test
    void useEndpointRejectsCustomerSelfUse() {
        CouponRedemptionController controller = new CouponRedemptionController(mock(CouponRedemptionService.class));
        UserPrincipal principal = new UserPrincipal(
                1L,
                "customer@example.com",
                "password",
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        assertThatThrownBy(() -> controller.use(99L, principal))
                .isInstanceOf(InvalidCouponOperationException.class)
                .hasMessageContaining("cannot self-use");
    }
}
