package br.iesb.imarket.validators;

import java.util.regex.Pattern;

public abstract class Validate {
    protected static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    protected abstract void validate(String parameters);
}
