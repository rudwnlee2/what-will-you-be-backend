package com.example.whatwillyoube.whatwillyoube_backend.domain;

public enum MBTI {

    INTJ("용의주도한 전략가"),
    INTP("논리적인 사색가"),
    ENTJ("대담한 통솔자"),
    ENTP("뜨거운 논쟁을 즐기는 변론가"),
    INFJ("통찰력 있는 선지자"),
    INFP("열정적인 중재자"),
    ENFJ("정의로운 사회운동가"),
    ENFP("재기발랄한 활동가"),
    ISTJ("청렴결백한 논리주의자"),
    ISFJ("용감한 수호자"),
    ESTJ("엄격한 관리자"),
    ESFJ("사교적인 외교관"),
    ISTP("만능 재주꾼"),
    ISFP("호기심 많은 예술가"),
    ESTP("모험을 즐기는 사업가"),
    ESFP("자유로운 영혼의 연예인");

    private final String description;

    MBTI(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
