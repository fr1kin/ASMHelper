package com.fr1kin.asmhelper.exceptions;

/**
 * Created on 1/6/2017 by fr1kin
 */
public class DetourException extends RuntimeException {
    public DetourException(String msg, Object... fmt) {
        super(String.format(msg, fmt));
    }
}
