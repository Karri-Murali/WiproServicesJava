package com.wipro.person;

import java.util.regex.Pattern;

public class EmailAddress extends Address {
    private final String email;

    public EmailAddress(String email) {
        super("", "", "", "", "");
        if (isValidEmail(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
    }

    @Override
    public String getAddressType() {
        return "Email";
    }

    @Override
    public String getAddressDetails() {
        return email;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
