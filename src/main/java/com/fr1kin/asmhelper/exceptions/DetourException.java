package com.fr1kin.asmhelper.exceptions;

/**
 * Created on 1/6/2017 by fr1kin
 */
public class DetourException extends RuntimeException {
    public DetourException(Class<?> o, String msg, Object... fmt) {
        super(String.format("[%s] " + msg, new Object[]{o.getClass().getSimpleName(), fmt}));
    }
}
