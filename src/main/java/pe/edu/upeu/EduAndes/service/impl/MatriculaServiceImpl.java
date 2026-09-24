package pe.edu.upeu.EduAndes.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.EduAndes.dto.DetalleMatriculaRequestDTO;
import pe.edu.upeu.EduAndes.dto.DetalleMatriculaResponseDTO;
import pe.edu.upeu.EduAndes.dto.MatriculaRequestDTO;
import pe.edu.upeu.EduAndes.dto.MatriculaResponseDTO;
import pe.edu.upeu.EduAndes.dto.reportes.MatriculadosPorCursoDTO;
import pe.edu.upeu.EduAndes.dto.reportes.RecaudacionPorCarreraDTO;
import pe.edu.upeu.EduAndes.entity.Curso;
import pe.edu.upeu.EduAndes.entity.DetalleMatricula;
import pe.edu.upeu.EduAndes.entity.Estudiante;
import pe.edu.upeu.EduAndes.entity.Matricula;
import pe.edu.upeu.EduAndes.enums.EstadoMatricula;
import pe.edu.upeu.EduAndes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.EduAndes.exception.ReglaNegocioException;
import pe.edu.upeu.EduAndes.repository.CursoRepository;
import pe.edu.upeu.EduAndes.repository.EstudianteRepository;
import pe.edu.upeu.EduAndes.repository.MatriculaRepository;
import pe.edu.upeu.EduAndes.service.service.MatriculaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatriculaServiceImpl implements MatriculaService {

    private static final Logger log = LoggerFactory.getLogger(MatriculaServiceImpl.class);

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    @Value("${matricula.costo-credito:120.00}")
    private BigDecimal costoPorCredito;

    @Override
    @Transactional
    public MatriculaResponseDTO registrar(MatriculaRequestDTO request) {
        log.info("Iniciando registro de matrícula para el estudiante ID: {} en el periodo: {}",
                request.getEstudianteId(), request.getPeriodo());

        // 1. Buscar estudiante
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + request.getEstudianteId()));

        // RN-01: Solo se matricula un estudiante activo
        if (Boolean.FALSE.equals(estudiante.getEstado())) {
            throw new ReglaNegocioException("El estudiante '" + estudiante.getNombres() + " " + estudiante.getApellidos() + "' se encuentra inactivo");
        }

        // RN-03: Un estudiante no puede tener más de una matrícula en estado REGISTRADA en el mismo periodo
        if (matriculaRepository.existsByEstudianteIdAndPeriodoAndEstado(estudiante.getId(), request.getPeriodo(), EstadoMatricula.REGISTRADA)) {
            throw new ReglaNegocioException("El estudiante ya cuenta con una matrícula en estado REGISTRADA para el periodo: " + request.getPeriodo());
        }

        // Validar cursos duplicados en la misma petición
        Set<Long> cursoIdsUnicos = new HashSet<>();
        for (DetalleMatriculaRequestDTO detalleReq : request.getDetalles()) {
            if (!cursoIdsUnicos.add(detalleReq.getCursoId())) {
                throw new ReglaNegocioException("El curso con ID: " + detalleReq.getCursoId() + " está duplicado en la solicitud");
            }
        }

        Matricula matricula = new Matricula();
        matricula.setEstudiante(estudiante);
        matricula.setPeriodo(request.getPeriodo());
        matricula.setEstado(EstadoMatricula.REGISTRADA);
        matricula.setFecha(LocalDateTime.now());

        int totalCreditos = 0;
        BigDecimal montoTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        // 2. Procesar cada curso solicitado
        for (DetalleMatriculaRequestDTO detalleReq : request.getDetalles()) {
            Curso curso = cursoRepository.findById(detalleReq.getCursoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con ID: " + detalleReq.getCursoId()));

            // RN-01: Cursos activos
            if (Boolean.FALSE.equals(curso.getEstado())) {
                throw new ReglaNegocioException("El curso '" + curso.getNombre() + "' (" + curso.getCodigo() + ") se encuentra inactivo");
            }

            // RN-01: Cursos que pertenezcan a la carrera del estudiante
            if (estudiante.getCarrera() == null || curso.getCarrera() == null ||
                    !curso.getCarrera().getId().equals(estudiante.getCarrera().getId())) {
                throw new ReglaNegocioException("El curso '" + curso.getNombre() + "' (" + curso.getCodigo() + ") no pertenece a la carrera del estudiante");
            }

            // RN-02: No se puede matricular en un curso con cero vacantes
            if (curso.getVacantes() <= 0) {
                throw new ReglaNegocioException("El curso '" + curso.getNombre() + "' (" + curso.getCodigo() + ") no cuenta con vacantes disponibles");
            }

            // RN-02: Cada curso matriculado descuenta una vacante
            curso.setVacantes(curso.getVacantes() - 1);

            // RN-04: Cálculo de costo por curso (créditos * costo por crédito)
            BigDecimal costoCurso = costoPorCredito.multiply(BigDecimal.valueOf(curso.getCreditos()))
                    .setScale(2, RoundingMode.HALF_UP);

            totalCreditos += curso.getCreditos();
            montoTotal = montoTotal.add(costoCurso);

            // Crear DetalleMatricula con copia de créditos y costo histórico
            DetalleMatricula detalle = new DetalleMatricula();
            detalle.setCurso(curso);
            detalle.setCreditos(curso.getCreditos());
            detalle.setCosto(costoCurso);

            matricula.agregarDetalle(detalle);
        }

        // RN-04: Una matrícula no puede superar 20 créditos
        if (totalCreditos > 20) {
            throw new ReglaNegocioException("La matrícula no puede superar los 20 créditos permitidos (total solicitado: " + totalCreditos + ")");
        }

        matricula.setTotalCreditos(totalCreditos);
        matricula.setMontoTotal(montoTotal);

        Matricula guardada = matriculaRepository.save(matricula);
        log.info("Matrícula registrada exitosamente con ID: {} para estudiante: {} con {} créditos y monto: S/ {}",
                guardada.getId(), estudiante.getCodigo(), totalCreditos, montoTotal);

        return mapToResponseDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public MatriculaResponseDTO consultarPorId(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + id));
        return mapToResponseDTO(matricula);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> listarTodas() {
        return matriculaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public MatriculaResponseDTO anular(Long id) {
        log.info("Solicitud de anulación para la matrícula ID: {}", id);

        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + id));

        if (EstadoMatricula.ANULADA.equals(matricula.getEstado())) {
            throw new ReglaNegocioException("La matrícula con ID: " + id + " ya se encuentra ANULADA");
        }

        // RN-02: La anulación devuelve las vacantes a los cursos
        for (DetalleMatricula detalle : matricula.getDetalles()) {
            Curso curso = detalle.getCurso();
            if (curso != null) {
                curso.setVacantes(curso.getVacantes() + 1);
            }
        }

        matricula.setEstado(EstadoMatricula.ANULADA);
        Matricula anulada = matriculaRepository.save(matricula);
        log.info("Matrícula con ID: {} anulada y vacantes restauradas exitosamente", id);

        return mapToResponseDTO(anulada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculadosPorCursoDTO> obtenerReporteMatriculadosPorCurso(String periodo, Long carreraId) {
        log.info("Generando reporte de matriculados por curso (periodo: {}, carreraId: {})", periodo, carreraId);
        String per = (periodo != null && !periodo.trim().isEmpty()) ? periodo.trim() : null;
        return matriculaRepository.obtenerReporteMatriculadosPorCurso(per, carreraId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> listarPorEstudianteYPeriodo(Long estudianteId, String periodo) {
        if (!estudianteRepository.existsById(estudianteId)) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId);
        }

        List<Matricula> matriculas;
        if (periodo != null && !periodo.trim().isEmpty()) {
            matriculas = matriculaRepository.findByEstudianteIdAndPeriodoOrderByFechaDesc(estudianteId, periodo.trim());
        } else {
            matriculas = matriculaRepository.findByEstudianteIdOrderByFechaDesc(estudianteId);
        }

        return matriculas.stream().map(this::mapToResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecaudacionPorCarreraDTO> obtenerReporteRecaudacionPorCarrera(String periodo) {
        log.info("Generando reporte de recaudación por carrera (periodo: {})", periodo);
        String per = (periodo != null && !periodo.trim().isEmpty()) ? periodo.trim() : null;
        return matriculaRepository.obtenerReporteRecaudacionPorCarrera(per);
    }
    private MatriculaResponseDTO mapToResponseDTO(Matricula matricula) {
        List<DetalleMatriculaResponseDTO> detallesDTO = matricula.getDetalles() != null
                ? matricula.getDetalles().stream().map(d -> DetalleMatriculaResponseDTO.builder()
                .id(d.getId())
                .cursoId(d.getCurso() != null ? d.getCurso().getId() : null)
                .cursoCodigo(d.getCurso() != null ? d.getCurso().getCodigo() : null)
                .cursoNombre(d.getCurso() != null ? d.getCurso().getNombre() : null)
                .creditos(d.getCreditos())
                .costo(d.getCosto())
                .build()).toList()
                : new ArrayList<>();

        String nombreCompleto = matricula.getEstudiante() != null
                ? matricula.getEstudiante().getNombres() + " " + matricula.getEstudiante().getApellidos()
                : null;

        return MatriculaResponseDTO.builder()
                .id(matricula.getId())
                .fecha(matricula.getFecha())
                .periodo(matricula.getPeriodo())
                .estudianteId(matricula.getEstudiante() != null ? matricula.getEstudiante().getId() : null)
                .estudianteCodigo(matricula.getEstudiante() != null ? matricula.getEstudiante().getCodigo() : null)
                .estudianteNombre(nombreCompleto)
                .estado(matricula.getEstado())
                .totalCreditos(matricula.getTotalCreditos())
                .montoTotal(matricula.getMontoTotal())
                .detalles(detallesDTO)
                .fechaCreacion(matricula.getFechaCreacion())
                .fechaModificacion(matricula.getFechaModificacion())
                .build();
    }
}
