package pe.edu.upeu.EduAndes.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.EduAndes.dto.CursoRequestDTO;
import pe.edu.upeu.EduAndes.dto.CursoResponseDTO;
import pe.edu.upeu.EduAndes.entity.Carrera;
import pe.edu.upeu.EduAndes.entity.Curso;
import pe.edu.upeu.EduAndes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.EduAndes.exception.ReglaNegocioException;
import pe.edu.upeu.EduAndes.repository.CarreraRepository;
import pe.edu.upeu.EduAndes.repository.CursoRepository;
import pe.edu.upeu.EduAndes.repository.MatriculaRepository;
import pe.edu.upeu.EduAndes.service.service.CursoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private static final Logger log = LoggerFactory.getLogger(CursoServiceImpl.class);

    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of("nombre", "creditos", "vacantes");

    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;
    private final MatriculaRepository matriculaRepository;

    @Override
    @Transactional
    public CursoResponseDTO create(CursoRequestDTO req) {
        String codigoLimpio = req.getCodigo().trim().toUpperCase();

        if (cursoRepository.existsByCodigo(codigoLimpio)) {
            throw new ReglaNegocioException("Ya existe un curso registrado con el código: " + codigoLimpio);
        }

        Carrera carrera = carreraRepository.findById(req.getCarreraId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + req.getCarreraId()));

        Curso curso = new Curso();
        curso.setCodigo(codigoLimpio);
        curso.setNombre(req.getNombre().trim());
        curso.setCreditos(req.getCreditos());
        curso.setCiclo(req.getCiclo());
        curso.setVacantes(req.getVacantes());
        curso.setEstado(req.getEstado() != null ? req.getEstado() : true);
        curso.setCarrera(carrera);

        Curso guardado = cursoRepository.save(curso);
        log.info("Curso creado exitosamente con ID: {}", guardado.getId());

        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional
    public CursoResponseDTO update(Long id, CursoRequestDTO req) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + id));

        String codigoLimpio = req.getCodigo().trim().toUpperCase();

        if (cursoRepository.existsByCodigoAndIdNot(codigoLimpio, id)) {
            throw new ReglaNegocioException("Ya existe otro curso registrado con el código: " + codigoLimpio);
        }

        Carrera carrera = carreraRepository.findById(req.getCarreraId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + req.getCarreraId()));

        curso.setCodigo(codigoLimpio);
        curso.setNombre(req.getNombre().trim());
        curso.setCreditos(req.getCreditos());
        curso.setCiclo(req.getCiclo());
        curso.setVacantes(req.getVacantes());
        curso.setEstado(req.getEstado());
        curso.setCarrera(carrera);

        Curso actualizado = cursoRepository.save(curso);
        log.info("Curso actualizado exitosamente con ID: {}", actualizado.getId());

        return mapToResponseDTO(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO read(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + id));
        return mapToResponseDTO(curso);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Curso no encontrado con ID: " + id);
        }

        if (matriculaRepository.existsByCursoIdInDetalles(id)) {
            throw new ReglaNegocioException("No se puede eliminar el curso porque tiene matrículas registradas");
        }

        cursoRepository.deleteById(id);
        log.info("Curso eliminado con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> readAll() {
        return cursoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarPorCarrera(Long carreraId) {
        if (!carreraRepository.existsById(carreraId)) {
            throw new RecursoNoEncontradoException("Carrera no encontrada con ID: " + carreraId);
        }
        return cursoRepository.findByCarreraId(carreraId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> buscar(String nombre, Long carreraId, Integer ciclo, Boolean conVacantes, String orden, String dir) {
        Specification<Curso> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.trim().toLowerCase() + "%"));
            }

            if (carreraId != null) {
                predicates.add(cb.equal(root.get("carrera").get("id"), carreraId));
            }

            if (ciclo != null) {
                predicates.add(cb.equal(root.get("ciclo"), ciclo));
            }

            if (conVacantes != null && conVacantes) {
                predicates.add(cb.greaterThan(root.get("vacantes"), 0));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        String campoOrden = (orden != null && CAMPOS_ORDEN_PERMITIDOS.contains(orden.toLowerCase()))
                ? orden.toLowerCase()
                : "nombre";

        Sort.Direction direction = (dir != null && dir.equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, campoOrden);

        return cursoRepository.findAll(spec, sort).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private CursoResponseDTO mapToResponseDTO(Curso curso) {
        return CursoResponseDTO.builder()
                .id(curso.getId())
                .codigo(curso.getCodigo())
                .nombre(curso.getNombre())
                .creditos(curso.getCreditos())
                .ciclo(curso.getCiclo())
                .vacantes(curso.getVacantes())
                .estado(curso.getEstado())
                .carreraId(curso.getCarrera() != null ? curso.getCarrera().getId() : null)
                .carreraNombre(curso.getCarrera() != null ? curso.getCarrera().getNombre() : null)
                .fechaCreacion(curso.getFechaCreacion())
                .fechaModificacion(curso.getFechaModificacion())
                .build();
    }
}
