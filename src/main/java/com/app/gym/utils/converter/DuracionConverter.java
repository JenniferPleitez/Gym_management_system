package com.app.gym.utils.converter;

import java.time.Duration;

public class DuracionConverter {
    /**
     * Convierte cantidad y unidad a Duration para almacenamiento
     * // ENTRADA: (3, "meses")
     * // SALIDA: Duration de 90 días (3 meses × 30 días)
     */
    public static Duration toDuration(int cantidad, String unidad) {
        switch (unidad.toLowerCase()) {
            case "dias":
                return Duration.ofDays(cantidad);
            case "meses":
                return Duration.ofDays(cantidad * 30L); // Aproximación: 1 mes = 30 días
            case "años":
                return Duration.ofDays(cantidad * 365L); // Aproximación: 1 año = 365 días
            default:
                throw new IllegalArgumentException("Unidad no válida: " + unidad);
        }
    }
    
    /**
     * Convierte Duration a representación amigable
     * // ENTRADA: Duration de 90 días
     * // SALIDA: "3 meses"
     */
    public static String toRepresentacionAmigable(Duration duration) {
        long totalDias = duration.toDays();
        
        // Si es exactamente en meses (múltiplo de 30)
        if (totalDias % 30 == 0) {
            long meses = totalDias / 30;
            return meses + (meses == 1 ? " mes" : " meses");
        }
        
        // Si es exactamente en años (múltiplo de 365)
        if (totalDias % 365 == 0) {
            long años = totalDias / 365;
            return años + (años == 1 ? " año" : " años");
        }
        
        // En otro caso, mostrar en días
        return totalDias + (totalDias == 1 ? " día" : " días");
    }
    
    /**
     * Convierte Duration de vuelta a cantidad y unidad
     * // ENTRADA: Duration de 90 días
     * // SALIDA: [3, 1] donde 1 = "meses"
     */
    public static int[] fromDuration(Duration duration) {
        long totalDias = duration.toDays();
        
        // Verificar si es en años exactos
        if (totalDias % 365 == 0) {
            return new int[]{(int) (totalDias / 365), 2}; // 2 = años
        }
        
        // Verificar si es en meses exactos
        if (totalDias % 30 == 0) {
            return new int[]{(int) (totalDias / 30), 1}; // 1 = meses
        }
        
        // En otro caso, es en días
        return new int[]{(int) totalDias, 0}; // 0 = días
    }
}
