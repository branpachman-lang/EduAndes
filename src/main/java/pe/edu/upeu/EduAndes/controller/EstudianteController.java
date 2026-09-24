package pe.edu.upeu.EduAndes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.EduAndes.dto.EstudianteRequestDTO;
import pe.edu.upeu.EduAndes.dto.EstudianteResponseDTO;
import pe.edu.upeu.EduAndes.dto.MatriculaResponseDTO;
import pe.edu.upeu.EduAndes.service.service.EstudianteService;
import pe.edu.upeu.EduAndes.service.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final MatriculaService matriculaService;

    @GetMapping
    public ResponseEntity<Iterable<EstudianteResponseDTO>> findAll() {
        return ResponseEntity.ok(estudianteService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.read(id));
    }

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> create(@Valid @RequestBody EstudianteRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estudianteService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EstudianteRequestDTO requestDTO) {
        return ResponseEntity.ok(estudianteService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        estudianteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/matriculas")
    public ResponseEntity<List<MatriculaResponseDTO>> findMatriculasByEstudiante(
            @PathVariable Long id,
            @RequestParam(required = false) String periodo) {
        return ResponseEntity.ok(matriculaService.listarPorEstudianteYPeriodo(id, periodo));
    }
}
