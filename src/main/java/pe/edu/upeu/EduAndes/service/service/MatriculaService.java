package pe.edu.upeu.EduAndes.service.service;

import pe.edu.upeu.EduAndes.dto.MatriculaRequestDTO;
import pe.edu.upeu.EduAndes.dto.MatriculaResponseDTO;
import pe.edu.upeu.EduAndes.dto.reportes.MatriculadosPorCursoDTO;
import pe.edu.upeu.EduAndes.dto.reportes.RecaudacionPorCarreraDTO;

import java.util.List;

public interface MatriculaService {

    MatriculaResponseDTO registrar(MatriculaRequestDTO request);

    MatriculaResponseDTO consultarPorId(Long id);

    List<MatriculaResponseDTO> listarTodas();

    MatriculaResponseDTO anular(Long id);

    List<MatriculadosPorCursoDTO> obtenerReporteMatriculadosPorCurso(String periodo, Long carreraId);
    List<MatriculaResponseDTO> listarPorEstudianteYPeriodo(Long estudianteId, String periodo);
    List<RecaudacionPorCarreraDTO> obtenerReporteRecaudacionPorCarrera(String periodo);
}
