package com.example.whatwillyoube.whatwillyoube_backend.dto;

public class HollandDto {
    private final String value;
    private final String displayName;
    private final String description;

    public HollandDto(String value, String displayName, String description) {
        this.value = value;
        this.displayName = displayName;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
