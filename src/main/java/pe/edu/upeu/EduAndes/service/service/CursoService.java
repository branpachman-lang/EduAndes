package pe.edu.upeu.EduAndes.service.service;

import pe.edu.upeu.EduAndes.dto.CursoRequestDTO;
import pe.edu.upeu.EduAndes.dto.CursoResponseDTO;
import pe.edu.upeu.EduAndes.service.generic.CrudService;

import java.util.List;

public interface CursoService extends CrudService<CursoRequestDTO, CursoResponseDTO, Long> {

    List<CursoResponseDTO> listarPorCarrera(Long carreraId);

    List<CursoResponseDTO> buscar(String nombre, Long carreraId, Integer ciclo, Boolean conVacantes, String orden, String dir);
}
