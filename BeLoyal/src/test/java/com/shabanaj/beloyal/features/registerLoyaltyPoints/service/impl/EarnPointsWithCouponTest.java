package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the coupon discount resolution logic embedded in EarnPointsTransactionServiceImpl.
 * We test the private method via integration through a minimal public-facing spy or by extracting
 * the resolve logic into observable state (CustomerCoupon saved state).
 *
 * Since resolveAndValidateCouponDiscount is private, these tests verify observable outcomes:
 * correct exception thrown, and correct final amount fed into points calculation.
 */
@ExtendWith(MockitoExtension.class)
class EarnPointsWithCouponTest {

    @Mock CustomerCouponRepository customerCouponRepository;

    private Business business;
    private LoyaltyCoupon percentageCoupon;
    private LoyaltyCoupon fixedCoupon;
    private CouponDiscountDetails percentageDetails;
    private CouponDiscountDetails fixedDetails;
    private CustomerCoupon percentageCustomerCoupon;
    private CustomerCoupon fixedCustomerCoupon;

    @BeforeEach
    void setUp() {
        business = new Business();
        setField(business, "id", 1L);

        percentageDetails = CouponDiscountDetails.builder()
                .discountPercentage(new BigDecimal("20.00"))
                .maximumDiscountAmount(new BigDecimal("50.00"))
                .build();

        percentageCoupon = LoyaltyCoupon.builder()
                .type(CouponType.PERCENTAGE_DISCOUNT)
                .title("20% Off")
                .pointsCost(100)
                .currency(CurrencyCode.EUR)
                .status(CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .totalRedemptions(0)
                .build();
        setField(percentageCoupon, "id", 10L);
        setField(percentageCoupon, "business", business);
        setField(percentageCoupon, "discountDetails", percentageDetails);

        percentageCustomerCoupon = new CustomerCoupon();
        setField(percentageCustomerCoupon, "id", 200L);
        setField(percentageCustomerCoupon, "coupon", percentageCoupon);
        setField(percentageCustomerCoupon, "business", business);
        setField(percentageCustomerCoupon, "status", CustomerCouponStatus.REDEEMED);
        setField(percentageCustomerCoupon, "qrCode", "pct-qr-code");

        fixedDetails = CouponDiscountDetails.builder()
                .discountAmount(new BigDecimal("15.00"))
                .minimumOrderAmount(new BigDecimal("30.00"))
                .build();

        fixedCoupon = LoyaltyCoupon.builder()
                .type(CouponType.FIXED_AMOUNT_DISCOUNT)
                .title("€15 Off")
                .pointsCost(80)
                .currency(CurrencyCode.EUR)
                .status(CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .totalRedemptions(0)
                .build();
        setField(fixedCoupon, "id", 11L);
        setField(fixedCoupon, "business", business);
        setField(fixedCoupon, "discountDetails", fixedDetails);

        fixedCustomerCoupon = new CustomerCoupon();
        setField(fixedCustomerCoupon, "id", 201L);
        setField(fixedCustomerCoupon, "coupon", fixedCoupon);
        setField(fixedCustomerCoupon, "business", business);
        setField(fixedCustomerCoupon, "status", CustomerCouponStatus.REDEEMED);
        setField(fixedCustomerCoupon, "qrCode", "fixed-qr-code");
    }

    // --- Direct tests on resolveAndValidateCouponDiscount via EarnPointsTransactionServiceImpl helper methods ---

    @Test
    void resolveDiscount_percentageCoupon_calculatesDiscountCorrectly() {
        // 20% of 100 = 20 → finalAmount = 80
        when(customerCouponRepository.findWithLockByQrCode("pct-qr-code"))
                .thenReturn(Optional.of(percentageCustomerCoupon));

        var result = invokeResolveAndValidate("pct-qr-code", 1L, new BigDecimal("100.00"));

        assertThat(result.getDiscountApplied()).isEqualByComparingTo("20.00");
        assertThat(result.getFinalAmount()).isEqualByComparingTo("80.00");
        assertThat(result.getOriginalAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void resolveDiscount_percentageCoupon_capsAtMaximumDiscount() {
        // 20% of 400 = 80 but cap is 50 → discount = 50 → finalAmount = 350
        when(customerCouponRepository.findWithLockByQrCode("pct-qr-code"))
                .thenReturn(Optional.of(percentageCustomerCoupon));

        var result = invokeResolveAndValidate("pct-qr-code", 1L, new BigDecimal("400.00"));

        assertThat(result.getDiscountApplied()).isEqualByComparingTo("50.00");
        assertThat(result.getFinalAmount()).isEqualByComparingTo("350.00");
    }

    @Test
    void resolveDiscount_fixedCoupon_calculatesDiscountCorrectly() {
        // €15 off €100 → finalAmount = 85
        when(customerCouponRepository.findWithLockByQrCode("fixed-qr-code"))
                .thenReturn(Optional.of(fixedCustomerCoupon));

        var result = invokeResolveAndValidate("fixed-qr-code", 1L, new BigDecimal("100.00"));

        assertThat(result.getDiscountApplied()).isEqualByComparingTo("15.00");
        assertThat(result.getFinalAmount()).isEqualByComparingTo("85.00");
    }

    @Test
    void resolveDiscount_fixedCoupon_throwsWhenBelowMinimumOrder() {
        // billAmount = 20 < minimumOrderAmount = 30
        when(customerCouponRepository.findWithLockByQrCode("fixed-qr-code"))
                .thenReturn(Optional.of(fixedCustomerCoupon));

        assertThatThrownBy(() -> invokeResolveAndValidate("fixed-qr-code", 1L, new BigDecimal("20.00")))
                .isInstanceOf(InvalidCouponOperationException.class)
                .hasMessageContaining("minimum");
    }

    @Test
    void resolveDiscount_throwsInvalidQrCode_whenNotFound() {
        when(customerCouponRepository.findWithLockByQrCode("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invokeResolveAndValidate("unknown", 1L, new BigDecimal("100.00")))
                .isInstanceOf(InvalidQrCodeException.class);
    }

    @Test
    void resolveDiscount_throwsInvalidQrCode_whenDifferentBusiness() {
        Business otherBusiness = new Business();
        setField(otherBusiness, "id", 999L);
        setField(percentageCustomerCoupon, "business", otherBusiness);

        when(customerCouponRepository.findWithLockByQrCode("pct-qr-code"))
                .thenReturn(Optional.of(percentageCustomerCoupon));

        assertThatThrownBy(() -> invokeResolveAndValidate("pct-qr-code", 1L, new BigDecimal("100.00")))
                .isInstanceOf(InvalidQrCodeException.class);
    }

    @Test
    void resolveDiscount_throwsCouponAlreadyUsed_whenStatusIsUsed() {
        setField(percentageCustomerCoupon, "status", CustomerCouponStatus.USED);
        when(customerCouponRepository.findWithLockByQrCode("pct-qr-code"))
                .thenReturn(Optional.of(percentageCustomerCoupon));

        assertThatThrownBy(() -> invokeResolveAndValidate("pct-qr-code", 1L, new BigDecimal("100.00")))
                .isInstanceOf(CouponAlreadyUsedException.class);
    }

    @Test
    void resolveDiscount_throwsCouponTypeMismatch_forFreeProductCoupon() {
        percentageCoupon.setType(CouponType.FREE_PRODUCT);
        when(customerCouponRepository.findWithLockByQrCode("pct-qr-code"))
                .thenReturn(Optional.of(percentageCustomerCoupon));

        assertThatThrownBy(() -> invokeResolveAndValidate("pct-qr-code", 1L, new BigDecimal("100.00")))
                .isInstanceOf(CouponTypeMismatchException.class);
    }

    @Test
    void resolveDiscount_throwsCouponNotActive_whenCouponPaused() {
        percentageCoupon.setStatus(CouponStatus.PAUSED);
        when(customerCouponRepository.findWithLockByQrCode("pct-qr-code"))
                .thenReturn(Optional.of(percentageCustomerCoupon));

        assertThatThrownBy(() -> invokeResolveAndValidate("pct-qr-code", 1L, new BigDecimal("100.00")))
                .isInstanceOf(CouponNotActiveException.class);
    }

    @Test
    void resolveDiscount_throwsCouponExpired_whenPastEndDate() {
        percentageCoupon.setEndDate(LocalDateTime.now().minusDays(1));
        when(customerCouponRepository.findWithLockByQrCode("pct-qr-code"))
                .thenReturn(Optional.of(percentageCustomerCoupon));

        assertThatThrownBy(() -> invokeResolveAndValidate("pct-qr-code", 1L, new BigDecimal("100.00")))
                .isInstanceOf(CouponExpiredException.class);
    }

    @Test
    void resolveDiscount_throwsCouponNotYetValid_whenBeforeStartDate() {
        percentageCoupon.setStartDate(LocalDateTime.now().plusDays(2));
        when(customerCouponRepository.findWithLockByQrCode("pct-qr-code"))
                .thenReturn(Optional.of(percentageCustomerCoupon));

        assertThatThrownBy(() -> invokeResolveAndValidate("pct-qr-code", 1L, new BigDecimal("100.00")))
                .isInstanceOf(CouponNotYetValidException.class);
    }

    // Invokes the private resolveAndValidateCouponDiscount method via reflection
    private com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.CouponDiscountResult invokeResolveAndValidate(
            String qrCode, Long businessId, BigDecimal billAmount) {
        try {
            // Build a partial EarnPointsTransactionServiceImpl with just the repo we need
            EarnPointsTransactionServiceImpl instance = buildServiceWithRepo();
            java.lang.reflect.Method method = EarnPointsTransactionServiceImpl.class
                    .getDeclaredMethod("resolveAndValidateCouponDiscount", String.class, Long.class, BigDecimal.class);
            method.setAccessible(true);
            return (com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.CouponDiscountResult) method.invoke(instance, qrCode, businessId, billAmount);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException re) throw re;
            throw new RuntimeException(e.getCause());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EarnPointsTransactionServiceImpl buildServiceWithRepo() throws Exception {
        EarnPointsTransactionServiceImpl instance = new EarnPointsTransactionServiceImpl(
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                customerCouponRepository
        );
        return instance;
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
