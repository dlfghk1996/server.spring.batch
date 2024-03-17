package server.spring.batch.userpointJob.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import server.spring.batch.userpointJob.domain.enums.PointLogType;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="pointDetail")
@Getter
@Setter
@Entity
public class PointLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;


    @Enumerated(EnumType.STRING)
    private PointLogType type;

    private int point;

    private String content;

    private LocalDateTime createDate;

}
