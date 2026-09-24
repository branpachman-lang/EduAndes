package pe.edu.upeu.EduAndes.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthResponseDTO {

    private String status;
    private String database;
    private LocalDateTime timestamp;
}
