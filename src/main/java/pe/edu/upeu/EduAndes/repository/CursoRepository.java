package pe.edu.upeu.EduAndes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.EduAndes.entity.Curso;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long>, JpaSpecificationExecutor<Curso> {

    List<Curso> findByCarreraId(Long carreraId);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByCarreraId(Long carreraId);

    Optional<Curso> findByCodigo(String codigo);
}
