package com.whitbread.premierinn.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.domain.common.DomainValidator;

import java.util.regex.Pattern;

public final class Validator {

    public static final int MAX_FIRST_NAME_LENGTH = 20;
    public static final int MAX_LAST_NAME_LENGTH = 30;
    public static final int COMPANY_NAME_MIN_LENGTH = 1;
    public static final int COMPANY_NAME_MAX_LENGTH = 40;

    private static final String NAME_CHARACTERS = "A-Z \\[\\]\\.,'\"\\/#\\\\!\\$@€£%\\?<>\\|\\+\\^&\\*\\{}=\\-_`\\(\\)";
    private static final int ISSUE_NUMBER_LENGTH = 2;

    private static final Pattern VALID_EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9.!#$%&'*+-/=?^_`{|}~]+@[A-Z0-9.-]+\\.[A-Z]{2,64}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern PHONE_NUMBER_REGEX = Pattern.compile("^\\+?[0-9]{6,14}$");

    private static final Pattern CARD_DATE_REGEX = Pattern.compile("[0-1][0-9]/[0-9]{2}");

    //RULE: Minimum eight characters, at least one uppercase letter, one lowercase letter and one number
    private static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

    private static final Pattern VALID_PASSWORD_EIGHT_CHAR_REGEX = Pattern.compile("^.{8,}$");

    public static final Pattern SPECIAL_CHARACTERS_REGEX = Pattern.compile("^.*[^A-Za-z0-9 ].*$");

    public static final Pattern NAME_REGEX = Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$");

    private static final Pattern FIRST_NAME_REGEX
            = Pattern.compile("^[" + NAME_CHARACTERS + "]{1," + MAX_FIRST_NAME_LENGTH + "}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern LAST_NAME_REGEX
            = Pattern.compile("^[" + NAME_CHARACTERS + "]{1," + MAX_LAST_NAME_LENGTH + "}$", Pattern.CASE_INSENSITIVE);

    private static final String COMPANY_NAME_REGEX = "^[a-zA-ZÀÁÂÃÄÅĀĂĄẠẮÆǼÇĆĈĊČĎĐÈÉÊËĒĔĖĘĚĜĞĠĢĤĦÌÍÎÏĨĪǏĮİĴĶĹĻĽĿŁÑŃŅŇȠ"
            + "ÒÓÔÕÖØŌǑŐǾŒṘŖŘŚŜṢŠŢŤŦÙÚÛÜŨŪǓŮŰŲŴẂẀẄỲÝŸŶŹŻŽ"
            + "àáâãäåāăąạắæǽçćĉċčďđèéêëēĕėęěĝğġģĥħìíîïĩīǐįıĵķĺļľŀłñńņňȵ"
            + "òóôõöøōǒőǿœṙŗřśŝṣšţťŧùúûüũūǔůűųŵẃẁẅỳýÿŷźżž"
            + "\\[\\]\\\\/«».,:;_!?\"*%=+£$€¥&@#()\\-'\\d ]+$";

    private static String passwordNew;

    private static final Pattern CUSTOMER_AND_PO_REFERENCE_PATTERN = Pattern.compile("^([a-zA-Z0-9\\s.-]){0,24}$");

    private static final Pattern USER_DEFINED_PATTERN = Pattern.compile("^([A-Za-zÀ-ÖØ-öø-ÿ0-9.,;:&()_?!\\-\\s\"'~#*]){0,50}$");

    private Validator() {

    }

    public static boolean isOptionalEmailValid(@Nullable String email) {
        if (email == null || email.isEmpty()) {
            return true;
        } else {
            return isEmailValid(email);
        }
    }

    public static boolean isEmailValid(@NonNull String email) {
        return VALID_EMAIL_REGEX.matcher(email).find();
    }

    public static boolean isCardDate(@NonNull String date) {
        return CARD_DATE_REGEX.matcher(date).find();
    }

    public static boolean isNotEmpty(@NonNull String name) {
        return !name.trim().isEmpty();
    }

    public static boolean isPhoneNumberValid(@Nullable String phoneNumber) {
        return phoneNumber != null && PHONE_NUMBER_REGEX.matcher(phoneNumber).find();
    }

    public static boolean isPasswordValid(String password) {
        passwordNew = password;
        return VALID_PASSWORD_REGEX.matcher(password).find();
    }

    public static boolean isPasswordEightChardValid(String password) {
        return VALID_PASSWORD_EIGHT_CHAR_REGEX.matcher(password).find();
    }

    public static boolean doPasswordsMatch(String passwordConfirm) {
        return passwordConfirm.equals(passwordNew);
    }

    public static boolean verifyPassword(String password, String regex) {
        return Pattern.compile(regex).matcher(password).find();
    }

    public static boolean hasSpecialCharacters(String password) {
        return SPECIAL_CHARACTERS_REGEX.matcher(password).find();
    }

    public static boolean isIssueNumberValid(@NonNull String issueNumber) {
        return issueNumber.length() == ISSUE_NUMBER_LENGTH;
    }

    public static boolean isChecked(boolean value) {
        return value;
    }

    public static boolean isFirstNameValid(String firstName) {
        return firstName != null && FIRST_NAME_REGEX.matcher(firstName.trim()).find();
    }

    public static boolean isLastNameValid(String lastName) {
        return lastName != null && LAST_NAME_REGEX.matcher(lastName.trim()).find();
    }

    public static boolean isGermanPostcodeValid(String postcode) {
        return DomainValidator.Companion.isGermanPostcodeValid(postcode.trim());
    }

    public static boolean isUkPostcodeValid(String postcode) {
        return DomainValidator.Companion.isUkPostcodeValid(postcode.trim());
    }

    public static boolean isCompanyNameLengthValid(String name) {
        return !StringUtils.isBlank(name) && name.length() <= COMPANY_NAME_MAX_LENGTH;
    }

    public static boolean isCompanyNameValid(String name) {
        return Pattern.compile(COMPANY_NAME_REGEX).matcher(name.trim()).matches();
    }

    public static boolean isBusinessAccountQuestionValid(String text) {
        return CUSTOMER_AND_PO_REFERENCE_PATTERN.matcher(text.trim()).find();
    }

    public static boolean isCustomQuestionValid(String text) {
        return USER_DEFINED_PATTERN.matcher(text.trim()).find();
    }
}
