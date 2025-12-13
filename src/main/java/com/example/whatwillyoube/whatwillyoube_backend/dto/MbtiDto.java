package com.example.whatwillyoube.whatwillyoube_backend.dto;

public class MbtiDto {
    private final String value;
    private final String description;

    public MbtiDto(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
