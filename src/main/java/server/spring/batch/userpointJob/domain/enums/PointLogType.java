package server.spring.batch.userpointJob.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PointLogType {
    ADD("ADD", "적립"),

    USED("USED", "사용완료"),

    EXPIRED("EXPIRED", "기간만료");

    @JsonValue
    private String code;
    private String label;

    PointLogType(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
