package server.spring.batch.userpointJob.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class PointDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private int point;

    // 만료일자
    private LocalDateTime expiryDate;

    // 생성일자
    private LocalDateTime createDate;

    // 사용가능 여부 (만료 및 사용)
    private boolean isAvailable;

    @JoinColumn(name = "point_log_id")
    @OneToOne(cascade = CascadeType.MERGE)
    private PointLog pointLog;
}
