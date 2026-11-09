package com.app.gym.servicios;

import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;

import com.app.gym.dtos.producto.ProductoSimpleDTO;
import com.app.gym.dtos.usuario.UsuarioSimpleDTO;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.modelos.Producto;
import com.app.gym.modelos.Usuario;
import com.app.gym.modelos.Venta;
import com.app.gym.repositorios.VentaRepositorio;
import com.app.gym.utils.ListUtils;
import com.app.gym.utils.validadores.FechaValidador;

import jakarta.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.venta.VentaActualizarDto;
import com.app.gym.dtos.venta.VentaCrearDto;
import com.app.gym.dtos.venta.VentaResponseDTO;
import com.app.gym.repositorios.UsuarioRepositorio;
import com.app.gym.repositorios.ProductoRepositorio;

@Service
public class VentaServicio {

    /**
     * Repositorio para manejar las operaciones de ventas.
     */
    private final VentaRepositorio ventaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final EntityManager entityManager; 
    /**
     * Constructor del servicio de ventas.
     *
     * @param repositorio el repositorio de ventas
     */
    public VentaServicio(VentaRepositorio ventaRepositorio, UsuarioRepositorio usuarioRepositorio, ProductoRepositorio productoRepositorio, EntityManager entityManager) {
        this.ventaRepositorio = ventaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio; 
        this.productoRepositorio = productoRepositorio; 
        this.entityManager = entityManager; 

    }

    // ------------------------------------------------------------------
    // Operaciones de creación
    // ------------------------------------------------------------------

    /**
     * Registra una nueva venta en el sistema.
     *
     * @param dto los datos de la venta a registrar
     * @return la venta registrada
     */
    @Transactional
    public Venta registrarVenta(VentaCrearDto dto){
        // Verificar si el usuario existe
        Usuario usuario = usuarioRepositorio.findById(dto.getIdUsuario())
                                            .orElseThrow(()-> new RecursoNoEncontradoExcepcion("Usuario no encontrado con ID: " + dto.getIdUsuario()));
        
        // Verificar si el producto existe
        Producto producto = productoRepositorio.findById(dto.getIdProducto())
                                                .orElseThrow(()-> new RecursoNoEncontradoExcepcion("Producto no encontrado con ID: " + dto.getIdProducto()));
        
        // Si la fecha no se proporciona, usar la fecha actual:
        LocalDate fecha = dto.getFecha() != null ? dto.getFecha() : LocalDate.now();

        // Validar la fecha proporcionada
        FechaValidador.validarFecha(fecha); // Validar la fecha proporcionada
    
        // 1. Calcular rango del mes (partición)
        YearMonth yearMonth = YearMonth.from(fecha);
        LocalDate inicioMes = yearMonth.atDay(1);
        LocalDate finMes = yearMonth.atEndOfMonth();
    
        // 2. Obtener el máximo ID en ese mes
        /*  
         * Obtener el máximo ID de asistencia en el mes actual (el cual lo calcula con la fecha "inicioMes" y "finMes" dadas al metodo).
         * Esto permite que las asistencias se registren de manera secuencial por mes.
         * Si no hay asistencias, se iniciará con ID 1. 
         */
        Integer maxId = ventaRepositorio.findMaxIdVentaByMonth(inicioMes, finMes);
        int nuevoId = (maxId != null) ? maxId + 1 : 1; // Si es null, empezar en 1

        // Crear una nueva venta
        Venta venta = new Venta();
        venta.setIdVenta(nuevoId); // Asignar ID manual
        venta.setCantidad(dto.getCantidad()); // Establecer la cantidad de productos vendidos
        venta.setFecha(fecha); // Usar la fecha proporcionada o la fecha actual si no se proporciona
        venta.setUsuario(usuario); // Asociar el usuario a la venta
        venta.setProducto(producto); // Asociar el producto a la venta
        // El total se calculará automáticamente por el trigger en la base de datos

        /*
        * Usamos saveAndFlush para Guardar y forzar sincronización con la BD 
        * y asegurarnos de que la operación de guardado se sincronice al instante con la base de datos, 
        * para que el refresh pueda obtener los cambios y poder retornar los cambios actualizados para mostrarlos en el front.
        */
        Venta ventaGuardada = ventaRepositorio.saveAndFlush(venta);

        // Refrescar después de guardar
        entityManager.refresh(ventaGuardada);
        
        // Retornar la venta guardada
        return ventaGuardada;
    }

    // ------------------------------------------------------------------
    // Operaciones de actualización
    // ------------------------------------------------------------------

    /**
     * Actualiza una venta existente.
     *
     * @param idVenta el ID de la venta a actualizar
     * @param fecha la fecha de la venta a actualizar
     * @param dto los datos de actualización de la venta
     * @return la venta actualizada
     */
    @Transactional
    public Venta actualizarVenta(Integer idVenta, LocalDate fecha, VentaActualizarDto dto){
        
        Venta venta = ventaRepositorio.findByIdVentaAndFecha(idVenta, fecha)
                                    .orElseThrow(() -> new RecursoNoEncontradoExcepcion
                                    ("Venta no encontrada con ID: " + idVenta + " y fecha: " + fecha));
        
        if(dto.getFecha() != null && !dto.getFecha().equals(fecha)) {
            throw new IllegalStateException("No se puede modificar la fecha de una asistencia.");
        }

        if(dto.getCantidad() != null) {
            venta.setCantidad(dto.getCantidad());
        }

        if(dto.getAnulada() != null) {
            venta.setAnulada(dto.getAnulada());
        }

        if(dto.getUsuario() != null) {
            Usuario usuario = usuarioRepositorio.findById(dto.getUsuario())
                                                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario no encontrado con ID: " + dto.getUsuario()));
            venta.setUsuario(usuario);
        }

        if(dto.getProducto() != null) {
            Producto producto = productoRepositorio.findById(dto.getProducto())
                                                    .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Producto no encontrado con ID: " + dto.getProducto()));
            venta.setProducto(producto);
        }
        
        // Guardar y forzar sincronización con la base de datos para asegurarnos de que los cambios se reflejen inmediatamente
        // y luego refrescar la entidad para obtener los datos actualizados.
        Venta ventaActualizada = ventaRepositorio.saveAndFlush(venta);
        
        // Refrescar después de guardar
        entityManager.refresh(ventaActualizada);
        
        // Retornar la venta actualizada.
        return ventaActualizada;
    }

    // ------------------------------------------------------------------
    // Operaciones de consulta
    // ------------------------------------------------------------------

    /**
     * Busca una venta por su ID y fecha.
     *
     * @param id el ID de la venta
     * @param fecha la fecha de la venta
     * @return un Optional que contiene la venta si se encuentra, o vacío si no
     */
    @Transactional(readOnly = true)
    public Venta buscarPorIdFecha(Integer id, LocalDate fecha){
        FechaValidador.validarFecha(fecha); // Validar la fecha proporcionada
        return ventaRepositorio.findByIdVentaAndFecha(id, fecha).orElseThrow(()-> new RecursoNoEncontradoExcepcion("Venta no encontrada con ID: " + id + " y fecha: " + fecha));
    } 

    /**
     * Busca todas las ventas realizadas en una fecha específica.
     *
     * @param fecha la fecha de las ventas
     * @return una lista de ventas realizadas en esa fecha
     */
    @Transactional(readOnly = true)
    public List<Venta> buscarPorFecha (LocalDate fecha){
        FechaValidador.validarFecha(fecha); // Validar la fecha proporcionada
        return ListUtils.emptyIfNull(ventaRepositorio.findByFecha(fecha));
    }

    /**
     * Busca todas las ventas realizadas en un mes específico.
     *
     * @param year el año del mes a buscar
     * @param month el mes a buscar
     * @return una lista de ventas realizadas en ese mes
     */
    @Transactional
    public List<Venta> buscarPorMes(int year, int month){

        FechaValidador.validarYearMonth(year, month); // Validar el año y mes proporcionados
        YearMonth ym = YearMonth.of(year, month);

        return ListUtils.emptyIfNull(ventaRepositorio.findByRangoFechas(ym.atDay(1), ym.atEndOfMonth()));
    }
    
    /**
     * Busca todas las ventas realizadas en un rango de fechas.
     *
     * @param inicio la fecha de inicio del rango
     * @param fin la fecha de fin del rango
     * @return una lista de ventas realizadas en el rango de fechas
     */
    @Transactional(readOnly = true)
    public List<Venta> buscarPorRangoFechas(LocalDate inicio, LocalDate fin){

        FechaValidador.validarRangoFechas(inicio, fin);   
        return ListUtils.emptyIfNull(ventaRepositorio.findByRangoFechas(inicio, fin)); // Retorna una lista vacía si no hay ventas en el rango
    }

    /**
     * Convierte una entidad Venta a un DTO de respuesta.
     * Este método crea un DTO que contiene los detalles de la venta, incluyendo el usuario y el producto asociados.
     * @param v la entidad Venta a convertir
     * @return el DTO de respuesta correspondiente
     */
    public VentaResponseDTO toResponseDTO(Venta v){
        UsuarioSimpleDTO u = new UsuarioSimpleDTO();
        u.setIdUsuario(v.getUsuario().getIdUsuario());
        u.setNombre(v.getUsuario().getNombre());
        u.setApellido(v.getUsuario().getApellido());

        ProductoSimpleDTO p = new ProductoSimpleDTO();
        p.setIdProducto(v.getProducto().getIdProducto());
        p.setNombre(v.getProducto().getNombre());
        p.setPrecio(v.getProducto().getPrecio());

        VentaResponseDTO r = new VentaResponseDTO();
        r.setIdVenta(v.getIdVenta());
        r.setFecha(v.getFecha());
        r.setCantidad(v.getCantidad());
        r.setTotal(v.getTotal());
        r.setAnulada(v.getAnulada());
        r.setUsuario(u);
        r.setProducto(p);

        return r; // Retorna el DTO de respuesta con los datos de la venta
    }

    /**
     * Convierte una lista de entidades Venta a una lista de DTOs de respuesta.
     *
     * Este método utiliza la API de Streams de Java para transformar una lista de
     * objetos del tipo Venta en una lista de objetos VentaResponseDTO. Esta transformación
     * es útil para exponer solo los datos necesarios al cliente, manteniendo así la capa de persistencia desacoplada de la capa de presentación.
     *
     * @param ventas la lista de entidades Venta a convertir
     * @return la lista de DTOs de respuesta correspondientes
     */
    public List<VentaResponseDTO> toResponseDTO(List<Venta> ventas){

        // Inicia un Stream a partir de la lista de objetos Venta.
        // Un Stream permite operar sobre los elementos de forma funcional y más expresiva
        return ventas.stream()
                    .map(this::toResponseDTO) // Aplica una transformación (mapeo) a cada objeto Venta en el Stream.
                    .toList();  // Convierte el Stream resultante (de tipo Stream<VentaResponseDTO>) a una lista de tipo List<VentaResponseDTO>.
    }
}
