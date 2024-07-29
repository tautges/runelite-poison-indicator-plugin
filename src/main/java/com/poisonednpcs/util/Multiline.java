package com.poisonednpcs.util;

import java.util.function.Supplier;

public class Multiline {
    private final StringBuilder builder;

    public Multiline() {
        this.builder = new StringBuilder();
    }

    public Multiline append(String s) {
        builder.append(s).append("</br>");
        return this;
    }

    public Multiline appendIf(boolean condition, Supplier<String> s) {
        if (condition) {
            append(s.get());
        }
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
