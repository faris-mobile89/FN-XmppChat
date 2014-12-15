package com.fn.reunion.app.model;

public enum TypingStateType {
    // Enumerated Types
    ACTIVE(""), TYPING("typing..."), PAUSED("has entered text"), GONE("has left chat"), INACTIVE("is inactive");

    // Date members
    private String name;

    /**
     * Default constructor. Assigns a String name to each type for output on the
     * GUI.
     *
     * @param name
     */

    private TypingStateType(String name) {
        this.name = name;
    }

    /**
     * Converts the enumerated type to a String.
     *
     * @return The name of the enumerated type.
     */

    public String toString() {
        return this.name;

    }
}