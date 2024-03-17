package server.spring.batch.basic.job.baeldung;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Coffee {

    @Id
    private String brand;
    private String origin;
    private String characteristics;
}
