package com.app.gym.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.gym.modelos.AsistenciaGeneral;
import com.app.gym.modelos.AsistenciaId;

public interface AsistenciaGeneralRepositorio extends JpaRepository<AsistenciaGeneral, AsistenciaId> {

    /** Crear el id manualmente para las asistencia general en un mes específico.
     * @param inicio la fecha de inicio del mes
     * @param fin la fecha de fin del mes  
     * @return el ID máximo de venta encontrado en el mes, o 0 si no hay ventas
     */
    @Query(value = "SELECT COALESCE(MAX(a.id_asistencia), 0) FROM asistencia_general a WHERE a.fecha BETWEEN :inicio AND :fin", nativeQuery = true)
    Integer findMaxIdAsistenciabyMonth (@Param ("inicio") LocalDate inicio, @Param ("fin") LocalDate fin);
    
    /** Busca una asistencia general por su ID y fecha.
     * @param idAsistencia el ID de la asistencia
     * @param fecha la fecha de la asistencia
     * @return un Optional que contiene la asistencia si se encuentra, o vacío si no
     */
    Optional<AsistenciaGeneral> findByIdAsistenciaAndFecha (Integer idAsistencia, LocalDate fecha);

    /** Busca todas las asistencias generales realizadas en una fecha específica.
     * @param fecha la fecha de las asistencias
     * @return una lista de asistencias generales realizadas en esa fecha
     */
    List<AsistenciaGeneral> findByFecha(LocalDate fecha);

    /** Busca todas las asistencias generales realizadas en un rango de fechas.
     * @param inicio la fecha de inicio del rango
     * @param fin la fecha de fin del rango
     * @return una lista de asistencias generales realizadas en el rango de fechas
     */
    @Query("SELECT a FROM AsistenciaGeneral a WHERE a.fecha BETWEEN :inicio AND :fin")
    List<AsistenciaGeneral> findByRangoFechas (@Param ("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}