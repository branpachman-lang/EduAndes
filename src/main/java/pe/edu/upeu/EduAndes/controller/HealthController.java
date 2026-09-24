package pe.edu.upeu.EduAndes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.EduAndes.dto.HealthResponseDTO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    @GetMapping
    public ResponseEntity<HealthResponseDTO> checkHealth() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return ResponseEntity.ok(
                        HealthResponseDTO.builder()
                                .status("UP")
                                .database("UP")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                        HealthResponseDTO.builder()
                                .status("DOWN")
                                .database("DOWN")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                    HealthResponseDTO.builder()
                            .status("DOWN")
                            .database("DOWN")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}
