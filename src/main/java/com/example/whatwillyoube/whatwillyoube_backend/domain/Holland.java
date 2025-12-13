package com.example.whatwillyoube.whatwillyoube_backend.domain;

public enum Holland {

    REALISTIC("현실형", "손으로 직접 작업하는 것을 좋아합니다."),
    INVESTIGATIVE("탐구형", "문제 해결과 과학적인 탐구를 즐깁니다."),
    ARTISTIC("예술형", "자유롭고 창의적인 활동을 선호합니다."),
    SOCIAL("사회형", "사람들과의 소통과 도움 주기를 좋아합니다."),
    ENTERPRISING("진취형", "리더십과 설득력으로 조직을 이끄는 걸 좋아합니다."),
    CONVENTIONAL("관습형", "정해진 규칙과 절차에 따라 일하는 것을 선호합니다.");

    private final String displayName;
    private final String description;

    Holland(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

}
