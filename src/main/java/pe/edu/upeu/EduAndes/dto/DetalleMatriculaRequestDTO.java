package pe.edu.upeu.EduAndes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleMatriculaRequestDTO {

    @NotNull(message = "El ID del curso es obligatorio")
    @Positive(message = "El ID del curso debe ser un valor positivo")
    private Long cursoId;
}
