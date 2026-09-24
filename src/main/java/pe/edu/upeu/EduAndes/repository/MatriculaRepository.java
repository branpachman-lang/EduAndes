package pe.edu.upeu.EduAndes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.EduAndes.dto.reportes.MatriculadosPorCursoDTO;
import pe.edu.upeu.EduAndes.dto.reportes.RecaudacionPorCarreraDTO;
import pe.edu.upeu.EduAndes.entity.Matricula;
import pe.edu.upeu.EduAndes.enums.EstadoMatricula;

import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByEstudianteIdAndPeriodoAndEstado(Long estudianteId, String periodo, EstadoMatricula estado);

    @Query("select case when count(d) > 0 then true else false end from DetalleMatricula d where d.curso.id = :cursoId")
    boolean existsByCursoIdInDetalles(@Param("cursoId") Long cursoId);

    List<Matricula> findByEstudianteIdOrderByFechaDesc(Long estudianteId);

    List<Matricula> findByEstudianteIdAndPeriodoOrderByFechaDesc(Long estudianteId, String periodo);

    @Query("""
            select new pe.edu.upeu.EduAndes.dto.reportes.MatriculadosPorCursoDTO(
                c.codigo,
                c.nombre,
                count(d.id),
                sum(d.costo)
            )
            from DetalleMatricula d
            join d.matricula m
            join d.curso c
            where m.estado = pe.edu.upeu.EduAndes.enums.EstadoMatricula.REGISTRADA
              and (:periodo is null or m.periodo = :periodo)
              and (:carreraId is null or c.carrera.id = :carreraId)
            group by c.codigo, c.nombre
            order by c.codigo asc
            """)
    List<MatriculadosPorCursoDTO> obtenerReporteMatriculadosPorCurso(
            @Param("periodo") String periodo,
            @Param("carreraId") Long carreraId);

    @Query("""
        select new pe.edu.upeu.EduAndes.dto.reportes.RecaudacionPorCarreraDTO(
            car.nombre,
            count(distinct m.id),
            sum(m.totalCreditos),
            sum(m.montoTotal)
        )
        from Matricula m
        join m.estudiante e
        join e.carrera car
        where m.estado = pe.edu.upeu.EduAndes.enums.EstadoMatricula.REGISTRADA
          and (:periodo is null or m.periodo = :periodo)
        group by car.nombre
        order by car.nombre asc
        """)
    List<RecaudacionPorCarreraDTO> obtenerReporteRecaudacionPorCarrera(@Param("periodo") String periodo);
}
