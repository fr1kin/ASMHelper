package com.fr1kin.asmhelper.test;

/**
 * Created on 1/6/2017 by fr1kin
 */

public class TestMain {
    // TODO: actual unit tests

    public enum TestEnum {
        VAR1,
        VAR2,
        VAR3
    }

    public static void main(String[] args) {
        TestEnum testEnum = TestEnum.VAR3;

        method(TestEnum.VAR1);
        method(TestEnum.VAR2);
        method(testEnum);

        testEnum = returnEnum();

        method(testEnum);
    }

    public static void method(TestEnum e) {
        e.name();
    }

    public static TestEnum returnEnum() {
        return TestEnum.VAR1;
    }
}
