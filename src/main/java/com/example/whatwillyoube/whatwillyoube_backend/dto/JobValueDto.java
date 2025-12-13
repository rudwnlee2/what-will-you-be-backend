package com.example.whatwillyoube.whatwillyoube_backend.dto;

public class JobValueDto {
    private final String value;
    private final String displayName;
    private final String description;

    public JobValueDto(String value, String displayName, String description) {
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
