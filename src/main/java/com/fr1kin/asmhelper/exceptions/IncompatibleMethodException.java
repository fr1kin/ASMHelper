package com.fr1kin.asmhelper.exceptions;

/**
 * Created on 1/7/2017 by fr1kin
 */
public class IncompatibleMethodException extends DetourException {
    public IncompatibleMethodException(Class<?> o, String msg, Object... fmt) {
        super(o, msg, fmt);
    }
}
