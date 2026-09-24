package pe.edu.upeu.EduAndes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.EduAndes.dto.CursoRequestDTO;
import pe.edu.upeu.EduAndes.dto.CursoResponseDTO;
import pe.edu.upeu.EduAndes.service.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public ResponseEntity<Iterable<CursoResponseDTO>> findAll() {
        return ResponseEntity.ok(cursoService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.read(id));
    }

    @PostMapping
    public ResponseEntity<CursoResponseDTO> create(@Valid @RequestBody CursoRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CursoRequestDTO requestDTO) {
        return ResponseEntity.ok(cursoService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cursoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CursoResponseDTO>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long carreraId,
            @RequestParam(required = false) Integer ciclo,
            @RequestParam(required = false) Boolean conVacantes,
            @RequestParam(required = false, defaultValue = "nombre") String orden,
            @RequestParam(required = false, defaultValue = "asc") String dir) {
        return ResponseEntity.ok(cursoService.buscar(nombre, carreraId, ciclo, conVacantes, orden, dir));
    }
}
