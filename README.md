# Sistema de Gestión de Gimnasio

Sistema de gestión para gimnasios desarrollado con **Java Spring Boot**, diseñado para hacer las operaciones más fáciles, mejorar la experiencia del cliente y 
permitir que se analise en tiempo real con una **observabilidad avanzada** con Prometheus y Grafana sobre las métricas del gym.

---

## Descripción General y alcances

Este proyecto busca dar una solución completa que permita a los gimnasios gestionar fácil y eficiente:
- **Membresías** con todo y su ciclo de vida completo
- **Asistencias** y control de acceso a los usuarios
- **Clases y horarios** con inscripciones a clases aerobicas para mayor control
- **Inventario de productos** y punto de venta
- **Reportes analíticos** con métricas de negocio
- **Seguridad basada en roles** con OAuth2
- **Monitoreo del sistema** de la aplicación en tiempo real

---

## Arquitectura Técnica

```
┌─────────────────────────────────────────────────────────┐
│                   Frontend (Web)                        │
│              (HTML/CSS/JavaScript/React)                │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│          Spring Boot REST API (Java 21)                 │
│  • Spring Security (OAuth2, JWT)                        │
│  • Spring Data JPA                                      │
│  • Spring Validation                                    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              PostgreSQL Database                        │
│              (Migrations con Flyway)                    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌──────────────────┬──────────────┬──────────────────────┐
│   Redis Cache    │  Prometheus  │    Alertmanager      │
│  (Sessions)      │  (Métricas)  │   (Notificaciones)   │
└──────────────────┴──────────────┴──────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│         Grafana (Dashboard)                             │
└─────────────────────────────────────────────────────────┘
```

---

## Stack usado

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.x** - Como framework principal
- **Spring Security** - Para la autenticación y autorización a recursos
- **Spring Data JPA / Hibernate** - ORM y la persistencia de datos
- **Validation** - Para validación de entidades

### Base de Datos & Cache
- **PostgreSQL** - Base de datos principal
- **Flyway** - Para el versionado y migración
- **Redis** - Para el manejo de cache y sesiones

### Observabilidad & Monitoreo
- **Spring Boot Actuator** - Para endpoints de salud de la app y métricas generales y especificas
- **Prometheus** - Para la recolección de las métricas
- **Alertmanager** - Para la gestión de alertas
- **Grafana** - Para tener dashboards y poder visualizarlo 

### DevOps & Herramientas
- **Docker & Docker Compose** - Para la containerización de las tecnologias usadas
- **Maven** - Como el gestor de dependencias y el build de la app

---

## Funcionalidades Principales

### 1. **Autenticación y Autorización**
- Login local con credenciales (correo y contraseña)
- Integración OAuth2 con providers externos (Google, Facebook, Apple...)
- Control de acceso basado en roles
- Tokens JWT con expiración configurables
- Recuperación de contraseña

**Roles implementados:**
- `USER` - Cliente del gimnasio
- `ADMIN` - Administrador de sucursal
- `SUPER_ADMIN` - Administrador del sistema

---

### 2. **Gestión de Usuarios**
- CRUD completo de clientes
- Perfiles de usuario con datos personales
- Hisotrial de cambios hechos
- Búsqueda y filtrado avanzado por parametros
- Desactivación lógica de usuarios

---

### 3. **Sistema de Membresías**
- Crear membresías con planes personalizados
- Monitoreo automático de vencimientos
- Renovación manual y automática
- Suspensión temporal
- Historial de cambios de estado
- Estados: `ACTIVA`, `VENCIDA`, `SUSPENDIDA`, `CANCELADA`

**Alertas integradas:**
- Notificación 7 días antes de que se vaya a vencer
- Alerta inmediata al vencer
- Recordatorio de renovación de membresía 

---

### 4. **Control de Asistencias**
- Registro automático de ingresos/salidas
- Generación de reportes
- Análisis para ver la concurrencia por horario o días
- Identificación de picos de uso
- Exportación de datos para el análisis

---

### 5. **Clases y Horarios** 
- Catálogo de clases (aerobicos)
- Creación de horarios con instructores para dar una clase aerobica
- Inscripción y cancelación de inscripciones a clases aerobicas de clientes
- Control de capacidad máxima ed la clase
- Control de capacidad minima para poder dar una clase aerbica (notificación a los inscritos en caso de qu eno se cumpla)
- Lista de espera automática
- Notificaciones de cambios de  por emergencias (como un día cerrado por falla electrica)

---

### 6. **Gestión de Productos y Ventas** 
- Inventario de productos (suplementos, galletas, camisas, etc.)
- Control de stock en tiempo real
- Alertas de stock bajo
- Historial de ventas con detalles
- Reportes de ingresos por producto

---

### 7. **Reportes y Analítica**
- Reportes mensuales de ingresos generados
- Análisis de asistencias por período X de tiempo
- Tendencias de membresías más compradas
- Productos más vendidos
- Ganancia por clase/instructor
- Exportación a CSV/PDF
- Gráficos interactivos

---

### 8. **Sistema de Notifications** (En desarrollo actualmente)
- Email de recordatorios de vencimiento
- Notificaciones push en la app
- SMS de confirmación o recordatorios
- Alertas de cambios importantes (como cambio de contraseña o correo)

---

### 9. **Monitoreo y Observabilidad** (Implementado parcialmente. Aún no está en la app completa)
- Dashboards en tiempo real con Grafana
- Alertas automáticas configurables
- Métricas de la app
- Uptime tracking (99.9% objetivo)
- Health checks automáticos

**Métricas principales monitoreadas:**
- Latencia de respuesta
- Tasa de errores HTTP
- Conexiones activas a BD
- Uso de memoria
- Disponibilidad del servicio

---

## Cómo Ejecutar el Proyecto

### Pre-requisitos
- Java 21 o superior
- Docker & Docker Compose
- Maven 3.8+
- Git

### Instalación Rápida

1. **Clonar el repositorio:**
```bash
git clone https://github.com/martkevz/Gym_management_system.git
cd gym
```

2. **Configurar variables de entorno:**
   - Crear archivo `.env` en la raíz del proyecto
   - Configurarlo siguiendo de ejemplo el archivo ".env.example" que se encuentra en la raíz del proyecto

3. **Levantar servicios con Docker:**
```bash
cd docker
docker compose --env-file ..\.env up -d
```

Este comando va a iniciar:
- PostgreSQL en el puerto 5432
- Redis en el puerto 6379
- Prometheus en el puerto 9090
- Grafana en el puerto 3000
- Alertmanager en el puerto 9093
- PostgresExporter en el puerto 9187
- CAdvisor en el 8082

4. **Compilar y ejecutar la aplicación:**
```bash
mvn clean package
mvn spring-boot:run
```

5. **Acceder a la aplicación:**
- Frontend: `http://localhost:8080`
- API: `http://localhost:8080/api/v1`
- Health Check: `http://localhost:8080/actuator/health`
- Prometheus: `http://localhost:9090`
- CAdvisor: `http://localhost:8082`
- Grafana: `http://localhost:3000` (usuario por defecto: `admin` / contraseña por defecto: `admin`)

---

## Seguridad

- **Autenticación:** Spring Security + JWT + OAuth2
- **Validación:** Validación en capas (controller/service)
- **Rate Limiting:** Control de intentos de login, cambios de contraseñas...

---

## Estructura del Proyecto

```
gym/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gym/
│   │   │       ├── application/   (Servicios de aplicación que orquestan casos de uso, coordinan servicios, publican eventos y manejan side effects)
│   │   │       ├── controllers/   (RestControllers)
│   │   │       ├── dtos/          (Para entrada/salida de datos de la API)
│   │   │       ├── events/        (Eventos de dominio y aplicación)
│   │   │       ├── listeners/     (Listeners que reaccionan a eventos para ejecutar side effects como envío de correos, logs, etc)
│   │   │       ├── mappers/       (Clases para mapear entidades, DTOs y otros modelos)
│   │   │       ├── models/        (Entidades del dominio y estructura de persistencia para la DB)
│   │   │       ├── repositories/  (Interfaces para el acceso a datos para cada entidad con JPA)
|   |   |       ├── services/      (Servicios de dominio enfocados en una sola entidad. Tienen lógica de negocio específica)
|   |   |       ├── security/      (Configuración y componentes de seguridad como JWT, filtros, auditoría y control de acceso)
|   |   |       ├── utils/         (Helpers reutilizables)
│   │   │       ├── config/        (Configuraciones generales de la aplicación)
│   │   │       └── exception/     (Excepciones y manejador de excepciones)
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── db/
│   │   │       └── migration/     (Scripts Flyway)
|   |   |       └── script/        (Scripts para creación de roles y limpieza de la DB)
│   │
│   └── test/
│       └── java/                  (Tests unitarios e integración)
├── docker/
│   └── docker-compose.yml
├── pom.xml
├── .env
├── LICENSE.md
└── README.md
```

---

## Estados del Proyecto por Módulo

| Módulo | Estado | Progreso | Próximos Pasos |
|--------|--------|----------|----------------|
| Autenticación | Completo | 100% | Falta implementar 2FA |
| Gestión de Usuarios | Completo | 100% | - |
| Membresías | Completo | 100% | Falta renovación automática |
| Asistencias | Completo | 100% | Falta análisis predictivo |
| Clases y Horarios | Completo | 100% | Fala un sistema de evaluación |
| Productos y Ventas | Completo | 100% | Falta integración de pagos |
| Reportes | Completo | 100% | Falta reportes personalizados |
| Notificaciones | En desarrollo | 60% | Falta integración WhatsApp |
| Monitoreo | Completo | 100% | Falta cubrir varias partes de la app aún |

---

## Más planes a Futuro

- [ ] **Two-Factor Authentication (2FA)** - Seguridad adicional para la app
- [ ] **Integración de Pagos** - Stripe, PayPal y cryptos.
- [ ] **App Móvil** - React Native
- [ ] **Machine Learning** - Para predicción de cancelaciones
- [ ] **WhatsApp Bot** - Para manejo de notificaciones y consultas

---

## Cómo contribuir

Las contribuciones son bienvenidas. Por favor:

1. Haz un fork el repositorio
2. Create una rama para tu feature (`git checkout -b feature/x-feature`)
3. Haz commit a tus cambios (`git commit -m 'Add X feature'`)
4. Haz push a la rama (`git push origin feature/x-feature`)
5. Por último, abre una Pull Request

---