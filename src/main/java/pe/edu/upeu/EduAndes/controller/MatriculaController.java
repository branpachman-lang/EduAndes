package pe.edu.upeu.EduAndes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.EduAndes.dto.MatriculaRequestDTO;
import pe.edu.upeu.EduAndes.dto.MatriculaResponseDTO;
import pe.edu.upeu.EduAndes.service.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @GetMapping
    public ResponseEntity<List<MatriculaResponseDTO>> findAll() {
        return ResponseEntity.ok(matriculaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.consultarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MatriculaResponseDTO> create(@Valid @RequestBody MatriculaRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaService.registrar(requestDTO));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MatriculaResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.anular(id));
    }
}
