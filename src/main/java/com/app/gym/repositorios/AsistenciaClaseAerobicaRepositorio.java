package com.app.gym.repositorios;

import com.app.gym.modelos.AsistenciaClaseAerobica;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AsistenciaClaseAerobicaRepositorio extends JpaRepository<AsistenciaClaseAerobica, Integer> {
    
    /**
     * Busca una asistencia de clase aeróbica por su ID.
     * @param idAsistencia el ID de la asistencia
     * @return un Optional que contiene la asistencia si se encuentra, o vacío si no
     */
    Optional<AsistenciaClaseAerobica> findByidAsistenciaClaseAerobica(Integer idAsistenciaClaseAerobica);

    /**
     * Busca una asistencia de clase aeróbica por su fecha.
     * @param fecha la fecha de la asistencia
     * @return una lista que contiene la asistencia si se encuentra.
     */
    List<AsistenciaClaseAerobica> findByFecha(LocalDate fecha);

    /**
     * Busca todas las asistencias de clase aeróbica realizadas en un rango de fechas.
     * @param inicio la fecha de inicio del rango
     * @param fin la fecha de fin del rango
     * @return una lista de asistencias de clase aeróbica realizadas en el rango de fechas
     */
    @Query("SELECT a FROM AsistenciaClaseAerobica a WHERE a.fecha BETWEEN :inicio AND :fin") // JPQL query para buscar asistencias de clases aerobicas en un cierto rango de fechas
    List<AsistenciaClaseAerobica> findByRangoFechas(LocalDate inicio, LocalDate fin);
}
