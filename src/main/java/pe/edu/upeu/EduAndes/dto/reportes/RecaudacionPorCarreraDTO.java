package pe.edu.upeu.EduAndes.dto.reportes;

import java.math.BigDecimal;

public record RecaudacionPorCarreraDTO(
        String carrera,
        Long numeroMatriculas,
        Long totalCreditos,
        BigDecimal montoRecaudado
) {}