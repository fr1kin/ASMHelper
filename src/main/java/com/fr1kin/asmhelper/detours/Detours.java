package com.fr1kin.asmhelper.detours;

import com.fr1kin.asmhelper.utils.locator.Locators;

/**
 * Created on 1/8/2017 by fr1kin
 */
public class Detours {
    public static SimpleDetour createTopDetour() {
        return null;
    }

    static {
        Detour detour = new SimpleDetour(null, null).setLocator(Locators::firstNode);
    }
}
