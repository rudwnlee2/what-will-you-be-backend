package com.example.whatwillyoube.whatwillyoube_backend.dto;

public class GenderDto {
    private final String value;
    private final String label;

    public GenderDto(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
