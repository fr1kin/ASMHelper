package com.fr1kin.asmhelper.exceptions;

/**
 * Created on 1/8/2017 by fr1kin
 */
public class NullNodeException extends DetourException {
    public NullNodeException(String msg, Object... fmt) {
        super(msg, fmt);
    }
}
