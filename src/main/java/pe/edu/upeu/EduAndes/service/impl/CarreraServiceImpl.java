package pe.edu.upeu.EduAndes.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.EduAndes.dto.CarreraRequestDTO;
import pe.edu.upeu.EduAndes.dto.CarreraResponseDTO;
import pe.edu.upeu.EduAndes.entity.Carrera;
import pe.edu.upeu.EduAndes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.EduAndes.exception.ReglaNegocioException;
import pe.edu.upeu.EduAndes.repository.CarreraRepository;
import pe.edu.upeu.EduAndes.repository.CursoRepository;
import pe.edu.upeu.EduAndes.service.service.CarreraService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarreraServiceImpl implements CarreraService {

    private static final Logger log = LoggerFactory.getLogger(CarreraServiceImpl.class);

    private final CarreraRepository carreraRepository;
    private final CursoRepository cursoRepository;

    @Override
    @Transactional
    public CarreraResponseDTO create(CarreraRequestDTO req) {
        String nombreLimpio = req.getNombre().trim();

        if (carreraRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new ReglaNegocioException("Ya existe una carrera con el nombre: " + nombreLimpio);
        }

        Carrera carrera = new Carrera();
        carrera.setNombre(nombreLimpio);
        carrera.setDescripcion(req.getDescripcion() != null ? req.getDescripcion().trim() : null);
        carrera.setEstado(req.getEstado() != null ? req.getEstado() : true);

        Carrera guardada = carreraRepository.save(carrera);
        log.info("Carrera creada exitosamente con ID: {}", guardada.getId());

        return mapToResponseDTO(guardada);
    }

    @Override
    @Transactional
    public CarreraResponseDTO update(Long id, CarreraRequestDTO req) {
        Carrera carrera = carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + id));

        String nombreLimpio = req.getNombre().trim();

        if (carreraRepository.existsByNombreIgnoreCaseAndIdNot(nombreLimpio, id)) {
            throw new ReglaNegocioException("Ya existe otra carrera con el nombre: " + nombreLimpio);
        }

        carrera.setNombre(nombreLimpio);
        carrera.setDescripcion(req.getDescripcion() != null ? req.getDescripcion().trim() : null);
        carrera.setEstado(req.getEstado());

        Carrera actualizada = carreraRepository.save(carrera);
        log.info("Carrera actualizada exitosamente con ID: {}", actualizada.getId());

        return mapToResponseDTO(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraResponseDTO read(Long id) {
        Carrera carrera = carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + id));
        return mapToResponseDTO(carrera);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!carreraRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Carrera no encontrada con ID: " + id);
        }

        if (cursoRepository.existsByCarreraId(id)) {
            throw new ReglaNegocioException("No se puede eliminar la carrera porque tiene cursos asociados");
        }

        carreraRepository.deleteById(id);
        log.info("Carrera eliminada con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> readAll() {
        return carreraRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private CarreraResponseDTO mapToResponseDTO(Carrera carrera) {
        return CarreraResponseDTO.builder()
                .id(carrera.getId())
                .nombre(carrera.getNombre())
                .descripcion(carrera.getDescripcion())
                .estado(carrera.getEstado())
                .fechaCreacion(carrera.getFechaCreacion())
                .fechaModificacion(carrera.getFechaModificacion())
                .build();
    }
}
