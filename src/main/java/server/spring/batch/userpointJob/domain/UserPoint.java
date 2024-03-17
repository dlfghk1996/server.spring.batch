package server.spring.batch.userpointJob.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class UserPoint {

    // 누적 & 유효 & 소멸예정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private int totalPoint; // 전체 포인트 (사용 + 소멸 + 미사용)

    private int point;      // 사용 가능 포인트

    private boolean disable;

    private LocalDateTime modifyDate;

    private LocalDateTime createDate;
}
