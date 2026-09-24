package pe.edu.upeu.EduAndes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.EduAndes.dto.CarreraRequestDTO;
import pe.edu.upeu.EduAndes.dto.CarreraResponseDTO;
import pe.edu.upeu.EduAndes.dto.CursoResponseDTO;
import pe.edu.upeu.EduAndes.service.service.CarreraService;
import pe.edu.upeu.EduAndes.service.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carreras")
@RequiredArgsConstructor
public class CarreraController {

    private final CarreraService carreraService;
    private final CursoService cursoService;

    @GetMapping
    public ResponseEntity<Iterable<CarreraResponseDTO>> findAll() {
        return ResponseEntity.ok(carreraService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.read(id));
    }

    @PostMapping
    public ResponseEntity<CarreraResponseDTO> create(@Valid @RequestBody CarreraRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carreraService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CarreraRequestDTO requestDTO) {
        return ResponseEntity.ok(carreraService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carreraService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cursos")
    public ResponseEntity<List<CursoResponseDTO>> findCursosByCarrera(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.listarPorCarrera(id));
    }
}
