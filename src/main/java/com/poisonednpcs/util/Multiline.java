package com.poisonednpcs.util;

public class Multiline {
    private final StringBuilder builder;

    public Multiline() {
        this.builder = new StringBuilder();
    }

    public Multiline append(String s) {
        builder.append(s).append("</br>");
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
