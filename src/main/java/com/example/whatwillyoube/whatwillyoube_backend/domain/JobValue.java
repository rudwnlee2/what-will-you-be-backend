package com.example.whatwillyoube.whatwillyoube_backend.domain;

public enum JobValue {

    STABILITY("안정성", "하고 싶은 일을 계속 안정적으로 하는 것"),
    COMPENSATION("보수", "경제적 보상을 얻는 것"),
    WORK_LIFE_BALANCE("일과 삶의 균형", "일과 개인생활의 균형"),
    FUN("즐거움", "일에서 흥미와 보람을 느끼는 것"),
    BELONGING("소속감", "구성원이 되는 것을 중요하게 여김"),
    SELF_DEVELOPMENT("자기계발", "자신의 능력을 발전시키는 것"),
    CHALLENGE("도전성", "새로운 일에 도전하는 것"),
    INFLUENCE("영향력", "사람들을 이끄는 것"),
    CONTRIBUTION("사회적 기여", "다른 사람들의 행복과 복지에 기여"),
    ACHIEVEMENT("성취", "목표를 달성하는 것"),
    RECOGNITION("사회적 인정", "인정받고 존경받는 것"),
    AUTONOMY("자율성", "스스로 결정하고 선택하는 것");

    private final String displayName;
    private final String description;

    JobValue(String displayName, String description) {
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
