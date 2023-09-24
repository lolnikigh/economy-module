package com.honepix.zarena.module.economy;

import com.honepix.lib.util.Declension;

public class CoinDeclension implements Declension {

    @Override
    public String accusative() {
        return "монета";
    }

    @Override
    public String genitive() {
        return "монеты";
    }

    @Override
    public String pluralGenitive() {
        return "монет";
    }
}
