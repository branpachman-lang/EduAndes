package pe.edu.upeu.EduAndes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.EduAndes.entity.Estudiante;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findByCarreraId(Long carreraId);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);

    boolean existsByCarreraId(Long carreraId);

    Optional<Estudiante> findByCodigo(String codigo);

    Optional<Estudiante> findByDni(String dni);
}
