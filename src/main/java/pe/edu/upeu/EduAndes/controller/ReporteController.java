package pe.edu.upeu.EduAndes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.EduAndes.dto.reportes.MatriculadosPorCursoDTO;
import pe.edu.upeu.EduAndes.dto.reportes.RecaudacionPorCarreraDTO;
import pe.edu.upeu.EduAndes.service.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final MatriculaService matriculaService;

    @GetMapping("/matriculados-por-curso")
    public ResponseEntity<List<MatriculadosPorCursoDTO>> getMatriculadosPorCurso(
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) Long carreraId) {
        return ResponseEntity.ok(matriculaService.obtenerReporteMatriculadosPorCurso(periodo, carreraId));
    }
    @GetMapping("/recaudacion-por-carrera")
    public ResponseEntity<List<RecaudacionPorCarreraDTO>> getRecaudacionPorCarrera(
            @RequestParam(required = false) String periodo) {
        return ResponseEntity.ok(matriculaService.obtenerReporteRecaudacionPorCarrera(periodo));
    }
}
