package com.edwin.trial_bank_app.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates phone numbers using Google's libphonenumber, tailored to Nigeria (NG).

 * Accepts two input shapes:
 *   - International: +2348012345678
 *   - Local:         08012345678

 * Both are parsed against the NG region so a local-format number is correctly
 * interpreted as a Nigerian number even without a country code. This replaces
 * a plain "is it 11 digits" regex, which accepted strings like "00000000000"
 * that are 11 digits but not a real, dialable number.
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String DEFAULT_REGION = "NG";

    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private boolean optional;

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return optional;
        }

        try {
            PhoneNumber number = phoneNumberUtil.parse(value, DEFAULT_REGION);

            if (!phoneNumberUtil.isValidNumber(number)) {
                return false;
            }

            // isValidNumberForRegion is stricter than isValidNumber: it confirms
            // the number actually belongs to NG specifically, not just that it's
            // a structurally valid number for *some* country libphonenumber
            // happened to match it against.
            return phoneNumberUtil.isValidNumberForRegion(number, DEFAULT_REGION);

        } catch (NumberParseException e) {
            return false;
        }
    }
}