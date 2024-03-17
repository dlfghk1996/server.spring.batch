package server.spring.batch.userpointJob.job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import server.spring.batch.common.user.domain.eums.UserStatus;

@Slf4j
@Getter
@NoArgsConstructor
public class CreateDateJobParameter {
    // Enum, Long, String의 타입은 직접 필드로 받아도 형변환 가능
    @Value("#{jobParameters[userStatus]}")
    private UserStatus userStatus;

    // 테스트 반복을 위한 파라미터
    private LocalDateTime uniqueParam = LocalDateTime.now();

    private LocalDate createDate;

    // LocalDate와 같이 자동 형변환이 안되서
    // Setter에 @Value를 사용하여 문자열로 받은 후, LocalDate로 형변환 한다.
    @Value("#{jobParameters[createDate]}") // (2)
    public void setCreateDate(String createDate) {
        this.createDate = LocalDate.parse(createDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
