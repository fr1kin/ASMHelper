package com.fr1kin.asmhelper.exceptions;

/**
 * Created on 1/4/2017 by fr1kin
 */
public class FailedToMatchPatternException extends DetourException {
    public FailedToMatchPatternException(String msg, Object... fmt) {
        super(String.format(msg, fmt));
    }
}
