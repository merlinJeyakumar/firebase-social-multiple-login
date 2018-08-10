package com.androidbash.androidbashfirebaseupdated.Utility;

import android.util.Patterns;

import com.androidbash.androidbashfirebaseupdated.Constant.Constant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Merlin on 5/30/2018.
 */

public class Validation {
    public static String VALIDATION_ERROR_MSG;

    public boolean isContainsInvalid(String inputString) {
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.toCharArray()[i] > Constant.VALID_CHARACTER[0] && inputString.toCharArray()[i] < Constant.VALID_CHARACTER[1]) {
                if (inputString.length() - 1 == i) {
                    return false;
                }
            } else {
                VALIDATION_ERROR_MSG = "Invalid Character:" + i;
                return true;
            }
        }
        return false;
    }

    public boolean isValidName(String inputString) {
        if (inputString == null) {
            VALIDATION_ERROR_MSG = "invalid Name";
            return false;
        }
        if (inputString.length() < Constant.MIN_NAME_LEN) {
            VALIDATION_ERROR_MSG = "Name Length more than " + Constant.MIN_NAME_LEN;
            return false;
        }
        if (inputString.length() > Constant.MAX_NAME_LEN) {
            VALIDATION_ERROR_MSG = "Name Length less than " + Constant.MAX_NAME_LEN;
            return false;
        }
        if ((inputString.split("%20").length > Constant.MAX_SPACE_NAME)) {
            VALIDATION_ERROR_MSG = "Name Contains " + Constant.MAX_SPACE_NAME + " Spaces";
            return false;
        }
        if (isContainsInvalid(inputString)) {
            VALIDATION_ERROR_MSG = "Name contains invalid characters";
            return false;
        }
        return true;
    }

    public boolean isAlphanumeric(String inputString) {
        if (inputString != null && (inputString.matches("^.*(?=.{8,16})(?=.*\\d)(?=.*[a-zA-Z]).*$"))) {
            return true;
        } else {
            VALIDATION_ERROR_MSG = "Password must Alphanumeric";
            return false;
        }
    }

    public boolean isValidURL(String Url) {
        if (Url == null || !Patterns.WEB_URL.matcher(Url).matches()) {
            VALIDATION_ERROR_MSG = "Invalid URL!";
            return false;
        }
        return true;
    }

    public boolean isValidAddress(String inputString) {
        if (inputString == null) {
            VALIDATION_ERROR_MSG = "Invalid Address!";
            return false;
        }
        if (inputString.length() > Constant.MIN_ADDRESS_LEN && inputString.length() <= Constant.MAX_ADDRESS_LEN) {
            VALIDATION_ERROR_MSG = "Invalid Address length! " + Constant.MIN_ADDRESS_LEN + " - " + Constant.MAX_ADDRESS_LEN;
            return false;
        }
        return true;
    }

    public boolean isValidPassword(String inputString, int passwordStrength) {
        /*0.NormalLength
        1.AlphaNumeric
        2.AlphaNumbericSpecialChar*/

        if (inputString == null || (inputString.length() < Constant.MIN_PASSWORD_LEN || inputString.length() > Constant.MAX_PASSWORD_LEN)) {
            VALIDATION_ERROR_MSG = "Invalid password length!";
            return false;
        }
        if (passwordStrength == 1) {
            if (!isAlphanumeric(inputString)) {
                VALIDATION_ERROR_MSG = "Alphanumeric password required!";
                return false;
            }
        } else if (passwordStrength == 2) {
            if (!isAlphanumeric(inputString)) {
                VALIDATION_ERROR_MSG = "Alphanumeric password required!";
                return false;
            }
            if (isContainsInvalid(inputString)) {
                VALIDATION_ERROR_MSG = "password contains invalid characters!";
            }
        }
        return true;
    }

    public boolean isValidMobileNum(String inputString) {
        if (inputString == null || inputString.length() == 0) {
            VALIDATION_ERROR_MSG = "Invalid mobile number!";
            return false;
        }
        if (inputString.length() < Constant.MIN_MOBILE_LEN || inputString.length() > Constant.MAX_MOBILE_LEN) {
            VALIDATION_ERROR_MSG = "Invalid mobile number length! " + Constant.MIN_MOBILE_LEN + " - " + Constant.MAX_MOBILE_LEN;
            return false;
        }
        return true;
    }

    public boolean isValidEmail(String inputString) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        if (inputString == null) {
            VALIDATION_ERROR_MSG = "Invalid Email!";
            return false;
        }
        Matcher matcher = pattern.matcher(inputString);
        if (!matcher.matches()) {
            VALIDATION_ERROR_MSG = "Invalid Email!";
            return false;
        }
        return true;
    }

    public boolean isValidInput(String inputString, String whenError) {
        if (inputString == null || inputString.length() == 0) {
            VALIDATION_ERROR_MSG = whenError;
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidCountryCode(String[] mCountryCodeList, String inputString) {
        VALIDATION_ERROR_MSG = "invalid Country Code!";
        if (inputString == null || inputString.length() == 0) {
            return false;
        } else {
            for (String mCountryCode : mCountryCodeList) {
                if (mCountryCode.equals(inputString)) {
                    return true;
                }
            }
        }
        return false;
    }
}