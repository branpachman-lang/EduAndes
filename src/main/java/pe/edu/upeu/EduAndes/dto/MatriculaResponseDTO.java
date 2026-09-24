package pe.edu.upeu.EduAndes.dto;

import lombok.*;
import pe.edu.upeu.EduAndes.enums.EstadoMatricula;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaResponseDTO {

    private Long id;
    private LocalDateTime fecha;
    private String periodo;
    private Long estudianteId;
    private String estudianteCodigo;
    private String estudianteNombre;
    private EstadoMatricula estado;
    private Integer totalCreditos;
    private BigDecimal montoTotal;
    private List<DetalleMatriculaResponseDTO> detalles;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
