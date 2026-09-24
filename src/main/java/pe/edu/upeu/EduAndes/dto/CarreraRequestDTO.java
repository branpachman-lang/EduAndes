package pe.edu.upeu.EduAndes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarreraRequestDTO {

    @NotBlank(message = "El nombre de la carrera es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
