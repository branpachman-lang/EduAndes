package pe.edu.upeu.EduAndes.service.service;

import pe.edu.upeu.EduAndes.dto.EstudianteRequestDTO;
import pe.edu.upeu.EduAndes.dto.EstudianteResponseDTO;
import pe.edu.upeu.EduAndes.service.generic.CrudService;

import java.util.List;

public interface EstudianteService extends CrudService<EstudianteRequestDTO, EstudianteResponseDTO, Long> {

    List<EstudianteResponseDTO> listarPorCarrera(Long carreraId);
}
