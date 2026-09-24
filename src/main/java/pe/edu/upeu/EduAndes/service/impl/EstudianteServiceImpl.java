package pe.edu.upeu.EduAndes.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.EduAndes.dto.EstudianteRequestDTO;
import pe.edu.upeu.EduAndes.dto.EstudianteResponseDTO;
import pe.edu.upeu.EduAndes.entity.Carrera;
import pe.edu.upeu.EduAndes.entity.Estudiante;
import pe.edu.upeu.EduAndes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.EduAndes.exception.ReglaNegocioException;
import pe.edu.upeu.EduAndes.repository.CarreraRepository;
import pe.edu.upeu.EduAndes.repository.EstudianteRepository;
import pe.edu.upeu.EduAndes.service.service.EstudianteService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private static final Logger log = LoggerFactory.getLogger(EstudianteServiceImpl.class);

    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;

    @Override
    @Transactional
    public EstudianteResponseDTO create(EstudianteRequestDTO req) {
        String codigoLimpio = req.getCodigo().trim();
        String dniLimpio = req.getDni().trim();

        if (estudianteRepository.existsByCodigo(codigoLimpio)) {
            throw new ReglaNegocioException("Ya existe un estudiante registrado con el código: " + codigoLimpio);
        }

        if (estudianteRepository.existsByDni(dniLimpio)) {
            throw new ReglaNegocioException("Ya existe un estudiante registrado con el DNI: " + dniLimpio);
        }

        Carrera carrera = carreraRepository.findById(req.getCarreraId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + req.getCarreraId()));

        Estudiante estudiante = new Estudiante();
        estudiante.setCodigo(codigoLimpio);
        estudiante.setDni(dniLimpio);
        estudiante.setNombres(req.getNombres().trim());
        estudiante.setApellidos(req.getApellidos().trim());
        estudiante.setEmail(req.getEmail().trim().toLowerCase());
        estudiante.setEstado(req.getEstado() != null ? req.getEstado() : true);
        estudiante.setCarrera(carrera);

        Estudiante guardado = estudianteRepository.save(estudiante);
        log.info("Estudiante creado exitosamente con ID: {}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional
    public EstudianteResponseDTO update(Long id, EstudianteRequestDTO req) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));

        String codigoLimpio = req.getCodigo().trim();
        String dniLimpio = req.getDni().trim();

        if (estudianteRepository.existsByCodigoAndIdNot(codigoLimpio, id)) {
            throw new ReglaNegocioException("Ya existe otro estudiante registrado con el código: " + codigoLimpio);
        }

        if (estudianteRepository.existsByDniAndIdNot(dniLimpio, id)) {
            throw new ReglaNegocioException("Ya existe otro estudiante registrado con el DNI: " + dniLimpio);
        }

        Carrera carrera = carreraRepository.findById(req.getCarreraId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + req.getCarreraId()));

        estudiante.setCodigo(codigoLimpio);
        estudiante.setDni(dniLimpio);
        estudiante.setNombres(req.getNombres().trim());
        estudiante.setApellidos(req.getApellidos().trim());
        estudiante.setEmail(req.getEmail().trim().toLowerCase());
        estudiante.setEstado(req.getEstado());
        estudiante.setCarrera(carrera);

        Estudiante actualizado = estudianteRepository.save(estudiante);
        log.info("Estudiante actualizado exitosamente con ID: {}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteResponseDTO read(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));
        return mapToResponseDTO(estudiante);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!estudianteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id);
        }
        estudianteRepository.deleteById(id);
        log.info("Estudiante eliminado con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> readAll() {
        return estudianteRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> listarPorCarrera(Long carreraId) {
        if (!carreraRepository.existsById(carreraId)) {
            throw new RecursoNoEncontradoException("Carrera no encontrada con ID: " + carreraId);
        }
        return estudianteRepository.findByCarreraId(carreraId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private EstudianteResponseDTO mapToResponseDTO(Estudiante estudiante) {
        return EstudianteResponseDTO.builder()
                .id(estudiante.getId())
                .codigo(estudiante.getCodigo())
                .dni(estudiante.getDni())
                .nombres(estudiante.getNombres())
                .apellidos(estudiante.getApellidos())
                .email(estudiante.getEmail())
                .estado(estudiante.getEstado())
                .carreraId(estudiante.getCarrera() != null ? estudiante.getCarrera().getId() : null)
                .carreraNombre(estudiante.getCarrera() != null ? estudiante.getCarrera().getNombre() : null)
                .fechaCreacion(estudiante.getFechaCreacion())
                .fechaModificacion(estudiante.getFechaModificacion())
                .build();
    }
}
