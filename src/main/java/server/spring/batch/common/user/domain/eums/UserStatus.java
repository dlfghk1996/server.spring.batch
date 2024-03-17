package server.spring.batch.common.user.domain.eums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus{
  ACTIVATED("ACTIVATED", "활성화 또는 연결 상태"),
  INACTIVATED("INACTIVATED", "연결 끊어진 상태"),
  DELETED("DELETED", "탈퇴 상태"),
  BLOCKED("BLOCKED", "회원 블락 상태"),
  SLEPT("SLEPT", "휴먼 회원");

  @JsonValue private final String code;
  private final String label;

  UserStatus(String code, String label) {
    this.code = code;
    this.label = label;
  }

  public String getCode() {
    return this.code;
  }

  public String getName() {
    return this.name();
  }

  public String getLabel() {
    return this.label;
  }
}
