package com.app.gym.utils;

import java.util.Collections;
import java.util.List;
/*
 * Utilidades para manejar listas de manera segura y eficiente.
 * Proporciona métodos para evitar NullPointerExceptions y facilitar operaciones comunes.
 */
public class ListUtils {
    
    private ListUtils() {} // Constructor privado para evitar instanciación

    // Evita NPE y devuelve una lista vacía si la entrada es null
    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
