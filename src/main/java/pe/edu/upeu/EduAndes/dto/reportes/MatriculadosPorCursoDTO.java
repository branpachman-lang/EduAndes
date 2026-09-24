package pe.edu.upeu.EduAndes.dto.reportes;

import java.math.BigDecimal;

public record MatriculadosPorCursoDTO(
        String codigo,
        String curso,
        Long matriculados,
        BigDecimal montoRecaudado
) {}
