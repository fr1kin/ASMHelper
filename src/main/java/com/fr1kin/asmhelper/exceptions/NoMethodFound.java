package com.fr1kin.asmhelper.exceptions;

/**
 * Created on 1/5/2017 by fr1kin
 */
public class NoMethodFound extends DetourException {
    public NoMethodFound(String msg, Object... fmt) {
        super(msg, fmt);
    }
}
