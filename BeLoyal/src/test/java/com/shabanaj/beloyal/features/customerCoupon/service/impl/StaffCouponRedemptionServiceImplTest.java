package com.shabanaj.beloyal.features.customerCoupon.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.features.customerCoupon.dto.StaffCouponScanRequest;
import com.shabanaj.beloyal.features.customerCoupon.dto.StaffCouponScanResponse;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffCouponRedemptionServiceImplTest {

    @Mock CustomerCouponRepository customerCouponRepository;
    @Mock BusinessMemberService businessMemberService;
    @Mock UserService userService;

    @InjectMocks
    StaffCouponRedemptionServiceImpl service;

    private static final Long BUSINESS_ID = 1L;
    private static final Long STAFF_USER_ID = 20L;
    private static final String VALID_QR = "test-qr-code-uuid";

    private User staffUser;
    private BusinessMember staffMember;
    private Business business;
    private LoyaltyCoupon coupon;
    private CustomerCoupon customerCoupon;

    @BeforeEach
    void setUp() {
        staffUser = new User();

        business = new Business();
        setField(business, "id", BUSINESS_ID);

        staffMember = new BusinessMember();
        setField(staffMember, "id", 50L);

        coupon = LoyaltyCoupon.builder()
                .type(CouponType.FREE_PRODUCT)
                .title("Free Pastry")
                .pointsCost(50)
                .currency(CurrencyCode.EUR)
                .status(CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .totalRedemptions(0)
                .build();
        setField(coupon, "id", 5L);
        setField(coupon, "business", business);

        customerCoupon = new CustomerCoupon();
        setField(customerCoupon, "id", 99L);
        setField(customerCoupon, "coupon", coupon);
        setField(customerCoupon, "business", business);
        setField(customerCoupon, "status", CustomerCouponStatus.REDEEMED);
        setField(customerCoupon, "qrCode", VALID_QR);
        setField(customerCoupon, "snapshotTitle", "Free Pastry");
    }

    @Test
    void scanAndRedeem_successfulFreeProductRedemption() {
        when(userService.getUserOrThrow(STAFF_USER_ID)).thenReturn(staffUser);
        when(businessMemberService.getBusinessMemberByUserIdAndBusinessId(STAFF_USER_ID, BUSINESS_ID)).thenReturn(staffMember);
        when(customerCouponRepository.findWithLockByQrCode(VALID_QR)).thenReturn(Optional.of(customerCoupon));
        when(customerCouponRepository.save(any())).thenReturn(customerCoupon);

        StaffCouponScanRequest request = new StaffCouponScanRequest();
        setField(request, "qrCode", VALID_QR);
        setField(request, "redemptionLocation", "Counter A");

        StaffCouponScanResponse response = service.scanAndRedeem(BUSINESS_ID, STAFF_USER_ID, request);

        assertThat(response.getStatus()).isEqualTo(CustomerCouponStatus.USED);
        assertThat(response.getCouponType()).isEqualTo(CouponType.FREE_PRODUCT);
        assertThat(customerCoupon.getRedemptionChannel()).isEqualTo(RedemptionChannel.STAFF_SCAN);
        assertThat(customerCoupon.getRedeemedByStaff()).isEqualTo(staffMember);
    }

    @Test
    void scanAndRedeem_throwsInvalidQrCode_whenQrNotFound() {
        when(userService.getUserOrThrow(STAFF_USER_ID)).thenReturn(staffUser);
        when(businessMemberService.getBusinessMemberByUserIdAndBusinessId(STAFF_USER_ID, BUSINESS_ID)).thenReturn(staffMember);
        when(customerCouponRepository.findWithLockByQrCode("bad-qr")).thenReturn(Optional.empty());

        StaffCouponScanRequest request = new StaffCouponScanRequest();
        setField(request, "qrCode", "bad-qr");

        assertThatThrownBy(() -> service.scanAndRedeem(BUSINESS_ID, STAFF_USER_ID, request))
                .isInstanceOf(InvalidQrCodeException.class);
    }

    @Test
    void scanAndRedeem_throwsInvalidQrCode_whenQrBelongsToDifferentBusiness() {
        Business otherBusiness = new Business();
        setField(otherBusiness, "id", 999L);
        setField(customerCoupon, "business", otherBusiness);

        when(userService.getUserOrThrow(STAFF_USER_ID)).thenReturn(staffUser);
        when(businessMemberService.getBusinessMemberByUserIdAndBusinessId(STAFF_USER_ID, BUSINESS_ID)).thenReturn(staffMember);
        when(customerCouponRepository.findWithLockByQrCode(VALID_QR)).thenReturn(Optional.of(customerCoupon));

        StaffCouponScanRequest request = new StaffCouponScanRequest();
        setField(request, "qrCode", VALID_QR);

        assertThatThrownBy(() -> service.scanAndRedeem(BUSINESS_ID, STAFF_USER_ID, request))
                .isInstanceOf(InvalidQrCodeException.class);
    }

    @Test
    void scanAndRedeem_throwsCouponAlreadyUsed_whenStatusIsUsed() {
        setField(customerCoupon, "status", CustomerCouponStatus.USED);

        when(userService.getUserOrThrow(STAFF_USER_ID)).thenReturn(staffUser);
        when(businessMemberService.getBusinessMemberByUserIdAndBusinessId(STAFF_USER_ID, BUSINESS_ID)).thenReturn(staffMember);
        when(customerCouponRepository.findWithLockByQrCode(VALID_QR)).thenReturn(Optional.of(customerCoupon));

        StaffCouponScanRequest request = new StaffCouponScanRequest();
        setField(request, "qrCode", VALID_QR);

        assertThatThrownBy(() -> service.scanAndRedeem(BUSINESS_ID, STAFF_USER_ID, request))
                .isInstanceOf(CouponAlreadyUsedException.class);
    }

    @Test
    void scanAndRedeem_throwsCouponNotActive_whenCouponPaused() {
        coupon.setStatus(CouponStatus.PAUSED);

        when(userService.getUserOrThrow(STAFF_USER_ID)).thenReturn(staffUser);
        when(businessMemberService.getBusinessMemberByUserIdAndBusinessId(STAFF_USER_ID, BUSINESS_ID)).thenReturn(staffMember);
        when(customerCouponRepository.findWithLockByQrCode(VALID_QR)).thenReturn(Optional.of(customerCoupon));

        StaffCouponScanRequest request = new StaffCouponScanRequest();
        setField(request, "qrCode", VALID_QR);

        assertThatThrownBy(() -> service.scanAndRedeem(BUSINESS_ID, STAFF_USER_ID, request))
                .isInstanceOf(CouponNotActiveException.class);
    }

    @Test
    void scanAndRedeem_throwsCouponExpired_whenPastEndDate() {
        coupon.setEndDate(LocalDateTime.now().minusHours(1));

        when(userService.getUserOrThrow(STAFF_USER_ID)).thenReturn(staffUser);
        when(businessMemberService.getBusinessMemberByUserIdAndBusinessId(STAFF_USER_ID, BUSINESS_ID)).thenReturn(staffMember);
        when(customerCouponRepository.findWithLockByQrCode(VALID_QR)).thenReturn(Optional.of(customerCoupon));

        StaffCouponScanRequest request = new StaffCouponScanRequest();
        setField(request, "qrCode", VALID_QR);

        assertThatThrownBy(() -> service.scanAndRedeem(BUSINESS_ID, STAFF_USER_ID, request))
                .isInstanceOf(CouponExpiredException.class);
    }

    @Test
    void scanAndRedeem_throwsCouponNotYetValid_whenBeforeStartDate() {
        coupon.setStartDate(LocalDateTime.now().plusDays(1));

        when(userService.getUserOrThrow(STAFF_USER_ID)).thenReturn(staffUser);
        when(businessMemberService.getBusinessMemberByUserIdAndBusinessId(STAFF_USER_ID, BUSINESS_ID)).thenReturn(staffMember);
        when(customerCouponRepository.findWithLockByQrCode(VALID_QR)).thenReturn(Optional.of(customerCoupon));

        StaffCouponScanRequest request = new StaffCouponScanRequest();
        setField(request, "qrCode", VALID_QR);

        assertThatThrownBy(() -> service.scanAndRedeem(BUSINESS_ID, STAFF_USER_ID, request))
                .isInstanceOf(CouponNotYetValidException.class);
    }

    @Test
    void scanAndRedeem_throwsCouponTypeMismatch_forDiscountCoupon() {
        coupon.setType(CouponType.PERCENTAGE_DISCOUNT);

        when(userService.getUserOrThrow(STAFF_USER_ID)).thenReturn(staffUser);
        when(businessMemberService.getBusinessMemberByUserIdAndBusinessId(STAFF_USER_ID, BUSINESS_ID)).thenReturn(staffMember);
        when(customerCouponRepository.findWithLockByQrCode(VALID_QR)).thenReturn(Optional.of(customerCoupon));

        StaffCouponScanRequest request = new StaffCouponScanRequest();
        setField(request, "qrCode", VALID_QR);

        assertThatThrownBy(() -> service.scanAndRedeem(BUSINESS_ID, STAFF_USER_ID, request))
                .isInstanceOf(CouponTypeMismatchException.class);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }
}
