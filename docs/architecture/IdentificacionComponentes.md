# HU-30 Issue #342: Identificación de Componentes y Estructura de Capas

## 📋 Objetivo
Identificar todos los componentes del sistema EntregaYa y documentar la estructura de capas para servir de base al diagrama de componentes UML (Issues #343 y #344).

---

## 🏗️ ARQUITECTURA EN CAPAS

El sistema EntregaYa sigue una **arquitectura en capas** con separación estricta de responsabilidades. Las dependencias fluyen en **un solo sentido** (top-down):

```
┌─────────────────────────────────────────────────┐
│           🌐 Capa de Presentación                │
│              (Controllers)                       │
└─────────────────────────────────────────────────┘
                      ↓ usa
┌─────────────────────────────────────────────────┐
│         ⚙️  Capa de Lógica de Negocio             │
│              (Services + Facade)                 │
└─────────────────────────────────────────────────┘
                      ↓ usa
┌─────────────────────────────────────────────────┐
│         💾 Capa de Acceso a Datos                │
│              (Repositories)                      │
└─────────────────────────────────────────────────┘
                      ↓ usa
┌─────────────────────────────────────────────────┐
│         📦 Capa de Modelo / DTO                  │
│          (Entities + DTOs)                       │
└─────────────────────────────────────────────────┘

╔═════════════════════════════════════════════════╗
║   🔒 Spring Security (Componente Transversal)    ║
║         Intercepta TODAS las peticiones          ║
╚═════════════════════════════════════════════════╝
```

---

## 📂 CAPA 1: CONTROLLER (Presentación)

**Paquete:** `com.example.entregaya.controller`

**Responsabilidad:** Recibir peticiones HTTP, validar entrada, delegar a servicios y retornar respuesta/vista.

### Componentes identificados (6):

| # | Componente | Responsabilidad | Endpoint base |
|---|-----------|-----------------|---------------|
| 1 | `AuthController` | Autenticación y registro de usuarios | `/login`, `/register` |
| 2 | `TrabajoController` | CRUD de trabajos y dashboard | `/trabajos` |
| 3 | `TareaController` | CRUD de tareas y comentarios | `/trabajos/{id}/tareas` |
| 4 | `InvitacionController` | Gestión de invitaciones a trabajos | `/invitaciones` |
| 5 | `NotificacionController` | Notificaciones del usuario | `/notificaciones` |
| 6 | `PerfilController` | Perfil de usuario | `/perfil` |

**Dependencias salientes (hacia Service y DTO):**
- `CustomTrabajoDetailsService`, `CustomTareaDetailsService`, `CustomUserDetailsService`
- `CustomComentarioDetailsService`, `CustomInvitacionDetailsService`
- `DashboardFacade` (Facade)
- `Lideroeditorstrategy` (Strategy)
- DTOs: `MiembroRolDTO`, `TareaConEtiquetaDTO`

---

## 📂 CAPA 2: SERVICE (Lógica de Negocio)

**Paquete:** `com.example.entregaya.service`

**Responsabilidad:** Contener reglas de negocio, coordinar repositorios y aplicar patrones de diseño.

### Componentes identificados (5):

| # | Componente | Responsabilidad |
|---|-----------|-----------------|
| 1 | `CustomUserDetailsService` | Gestión de usuarios, implementa `UserDetailsService` de Spring Security |
| 2 | `CustomTrabajoDetailsService` | Lógica de trabajos, colaboradores y roles |
| 3 | `CustomTareaDetailsService` | Lógica de tareas con Builder, Observer y Decorator |
| 4 | `CustomComentarioDetailsService` | Lógica de comentarios |
| 5 | `CustomInvitacionDetailsService` | Lógica de invitaciones |

**Dependencias salientes (hacia Repository, Model, DTO, Patterns):**
- Repositorios: 7 componentes
- Modelos: 8 entidades
- DTOs: 3 DTOs
- Patrones: `TareaBuilder`, `TareaDecoratorFactory`, `TareaObserver`, Strategy classes

---

## 📂 CAPA 2.5: FACADE (Patrón Estructural)

**Paquete:** `com.example.entregaya.facade`

**Responsabilidad:** Proveer interfaz simplificada para operaciones complejas del dashboard.

### Componentes identificados (1):

| # | Componente | Estereotipo | Responsabilidad |
|---|-----------|-------------|-----------------|
| 1 | `DashboardFacade` | `<<Facade>>` | Unifica múltiples servicios para construir el dashboard |

**Dependencias:**
- Servicios: `CustomUserDetailsService`, `CustomTrabajoDetailsService`, `CustomTareaDetailsService`, `CustomInvitacionDetailsService`
- Repositorios: `UserRepository`, `NotificacionRepository`
- DTOs: `DashboardDTO`

---

## 📂 CAPA 3: REPOSITORY (Acceso a Datos)

**Paquete:** `com.example.entregaya.repository`

**Responsabilidad:** Abstracción de persistencia usando Spring Data JPA.

### Componentes identificados (7):

| # | Componente | Entidad | Extiende |
|---|-----------|---------|----------|
| 1 | `UserRepository` | `User` | `JpaRepository` |
| 2 | `TrabajoRepository` | `Trabajo` | `JpaRepository` |
| 3 | `TareaRepository` | `Tarea` | `JpaRepository` |
| 4 | `ComentarioRepository` | `Comentario` | `JpaRepository` |
| 5 | `InvitacionRepository` | `Invitacion` | `JpaRepository` |
| 6 | `NotificacionRepository` | `Notificacion` | `JpaRepository` |
| 7 | `ColaboradorTrabajoRepository` | `ColaboradorTrabajo` | `JpaRepository` |

**Todos son interfaces** - Spring Data genera las implementaciones automáticamente.

---

## 📂 CAPA 4: MODEL / DTO

### 4.1 Modelos (Entidades JPA)

**Paquete:** `com.example.entregaya.model`

### Componentes identificados (8):

| # | Entidad | Tabla BD | Rol |
|---|---------|----------|-----|
| 1 | `User` | `users` | Usuario del sistema |
| 2 | `Trabajo` | `trabajo` | Proyecto/trabajo grupal |
| 3 | `Tarea` | `tarea` | Tarea dentro de un trabajo |
| 4 | `Comentario` | `comentario` | Comentario en tarea |
| 5 | `Invitacion` | `invitacion` | Invitación a trabajo |
| 6 | `Notificacion` | `notificacion` | Notificación al usuario |
| 7 | `ColaboradorTrabajo` | `colaborador_trabajo` | Relación User-Trabajo con rol |
| 8 | `ColaboradorTrabajoId` | (composite key) | Clave compuesta |

### 4.2 DTOs (Data Transfer Objects)

**Paquete:** `com.example.entregaya.dto`

### Componentes identificados (3):

| # | DTO | Propósito |
|---|-----|-----------|
| 1 | `DashboardDTO` | Datos agregados del dashboard |
| 2 | `MiembroRolDTO` | Información de miembro con rol |
| 3 | `TareaEventoDTO` | Evento de tarea para Observer |

**Nota:** `TareaConEtiquetaDTO` está en paquete `decorator` (relacionado con patrón).

---

## 📂 CAPA 5: CONFIG (Configuración)

**Paquete:** `com.example.entregaya.config`

**Responsabilidad:** Configuración transversal de Spring (Security, Beans).

### Componentes identificados (2):

| # | Componente | Responsabilidad |
|---|-----------|-----------------|
| 1 | `SecurityConfig` | Configura filtros de Spring Security, autenticación, autorización |
| 2 | `PasswordConfig` | Bean de `BCryptPasswordEncoder` |

**🔒 Spring Security (Transversal):**
- Intercepta TODAS las peticiones ANTES de llegar a los controladores
- Usa `DaoAuthenticationProvider` con `CustomUserDetailsService`
- Usa `PasswordConfig.passwordEncoder()` para validar contraseñas

---

## 🎨 PATRONES DE DISEÑO (Componentes con Estereotipo)

### Patrones Estructurales

#### 1. **Facade** 
**Paquete:** `facade`
- `DashboardFacade` → `<<Facade>>`

#### 2. **Decorator**
**Paquete:** `decorator`
- `TareaInfo` → `<<interface>>`
- `TareaInfoBase` → Component base
- `ProximaDecorator`, `UrgenteDecorator`, `VencidaDecorator`, `SinFechaDecorator`, `CompletadaDecorator` → `<<Decorator>>`
- `TareaDecoratorFactory` → `<<Factory>>`
- `TareaConEtiquetaDTO` → DTO del decorator

### Patrones de Comportamiento

#### 3. **Observer**
**Paquete:** `observer`
- `TareaObserver` → `<<interface>>`
- `NotificacionObserver` → `<<Observer>>`

#### 4. **Strategy**
**Paquete:** `strategy`
- `Permisostrategy` → `<<interface>>`
- `Lideroeditorstrategy`, `Sololiderstrategy` → `<<Strategy>>`

### Patrones Creacionales

#### 5. **Builder**
**Paquete:** `builder`
- `TareaBuilder` → `<<Builder>>`

#### 6. **Prototype**
**Paquete:** `prototype`
- `TareaPrototype` → `<<interface>>`
- `TrabajoPrototype` → `<<interface>>`

---

## 🔗 INTERFACES DEL SISTEMA

### Interfaces Provistas (Internas)

| Interface | Paquete | Implementadores |
|-----------|---------|-----------------|
| `TareaInfo` | `decorator` | `TareaInfoBase` + 5 decoradores |
| `TareaObserver` | `observer` | `NotificacionObserver` |
| `Permisostrategy` | `strategy` | `Lideroeditorstrategy`, `Sololiderstrategy` |
| `TareaPrototype` | `prototype` | `Tarea` (implícito) |
| `TrabajoPrototype` | `prototype` | `Trabajo` (implícito) |

### Interfaces Requeridas (Spring Framework)

| Interface | Uso |
|-----------|-----|
| `UserDetailsService` | Implementada por `CustomUserDetailsService` |
| `JpaRepository<T, ID>` | Extendida por todos los repositorios |
| `UserDetails` | Implementada por `User` |

---

## 🔀 MAPA DE DEPENDENCIAS ENTRE CAPAS

### Sentido Único de Dependencias (Top-Down)

```
Controller ────→ Service ────→ Repository ────→ Model
    │              │               │
    │              │               └────→ (JPA)
    │              │
    │              ├────→ DTO
    │              ├────→ Builder (patrón)
    │              ├────→ Decorator Factory (patrón)
    │              ├────→ Observer (patrón)
    │              └────→ Strategy (patrón)
    │
    ├────→ DTO
    ├────→ Facade ────→ Service (múltiples)
    └────→ Strategy

Config ═══════╗
              ║ (transversal)
Security ═════╩════→ Intercepta TODO
```

### ✅ Dependencias Permitidas
- Controller → Service ✅
- Controller → Facade ✅
- Controller → DTO ✅
- Controller → Strategy ✅
- Service → Repository ✅
- Service → Model ✅
- Service → DTO ✅
- Service → Patterns (Builder, Decorator, Observer, Strategy) ✅
- Facade → Service ✅
- Repository → Model ✅

### ❌ Dependencias Prohibidas (NO existen en el código)
- Service → Controller ❌
- Repository → Service ❌
- Model → Repository ❌
- Model → Service ❌
- DTO → Service ❌

**Verificación:** Se revisaron los imports de todos los paquetes y NO existen dependencias inversas ni circulares. ✅

---

## 📊 RESUMEN DE COMPONENTES

| Capa | Cantidad | Componentes |
|------|----------|-------------|
| Controller | 6 | AuthController, TrabajoController, TareaController, InvitacionController, NotificacionController, PerfilController |
| Service | 5 | CustomUserDetailsService, CustomTrabajoDetailsService, CustomTareaDetailsService, CustomComentarioDetailsService, CustomInvitacionDetailsService |
| Facade | 1 | DashboardFacade |
| Repository | 7 | User, Trabajo, Tarea, Comentario, Invitacion, Notificacion, ColaboradorTrabajo |
| Model | 8 | User, Trabajo, Tarea, Comentario, Invitacion, Notificacion, ColaboradorTrabajo, ColaboradorTrabajoId |
| DTO | 3 | DashboardDTO, MiembroRolDTO, TareaEventoDTO |
| Config | 2 | SecurityConfig, PasswordConfig |
| **Patrones** | | |
| Builder | 1 | TareaBuilder |
| Decorator | 9 | TareaInfo, TareaInfoBase, 5 decoradores, TareaDecoratorFactory, TareaConEtiquetaDTO |
| Observer | 2 | TareaObserver, NotificacionObserver |
| Strategy | 3 | Permisostrategy, Lideroeditorstrategy, Sololiderstrategy |
| Prototype | 2 | TareaPrototype, TrabajoPrototype |
| **TOTAL** | **49** | |

---

## 📝 JUSTIFICACIÓN DE LA ARQUITECTURA EN CAPAS (150+ palabras)

La arquitectura en capas adoptada en EntregaYa organiza el sistema en cinco capas verticales con dependencias unidireccionales (top-down), complementadas por un componente transversal de seguridad. Esta decisión se fundamenta en tres principios de ingeniería de software: **separación de responsabilidades**, **bajo acoplamiento** y **alta cohesión**. Cada capa tiene un propósito único y bien definido: los Controllers manejan únicamente la interacción HTTP, los Services encapsulan toda la lógica de negocio, los Repositories abstraen el acceso a datos mediante Spring Data JPA, y los Modelos/DTOs representan las estructuras de datos sin comportamiento de negocio.

El **sentido único de dependencias** es crucial porque previene dependencias circulares que dificultarían el mantenimiento, las pruebas unitarias y la evolución del sistema. Si un Service pudiera depender de un Controller, cualquier cambio en la presentación requeriría modificar la lógica de negocio, violando el principio de inversión de dependencias. Los patrones de diseño aplicados (Facade para simplificar, Decorator para extender, Observer para desacoplar notificaciones, Strategy para intercambiar algoritmos de permisos, Builder para construcción segura de tareas) refuerzan esta arquitectura agregando flexibilidad sin romper la estructura en capas. Spring Security actúa como componente transversal que intercepta todas las peticiones antes de alcanzar los Controllers, garantizando que la autenticación y autorización sean responsabilidades separadas del código de negocio. Esta organización facilita el onboarding de nuevos desarrolladores, permite detectar violaciones arquitectónicas rápidamente mediante inspección de imports, y soporta el análisis de impacto de cambios al trazar las dependencias en una sola dirección.

---

## ✅ CRITERIOS DE ACEPTACIÓN CUMPLIDOS

- [x] Identificadas las 5 capas: Controller, Service, Repository, Model/DTO, Config
- [x] Componentes principales listados para cada capa
- [x] Dependencias entre capas documentadas (sentido único)
- [x] Verificado que no hay dependencias inversas ni circulares
- [x] Spring Security identificado como componente transversal
- [x] Patrones con rol de componente identificados (Facade, Decorator Factory, Strategy)
- [x] Interfaces relevantes listadas (TareaObserver, Permisostrategy, TareaPrototype, TrabajoPrototype)
- [x] Justificación de arquitectura en capas (>150 palabras)

---

## 🚀 PRÓXIMOS ISSUES

- **Issue #343:** Modelado del diagrama UML en PlantUML/draw.io basado en este documento
- **Issue #344:** Revisión, exportación a PNG/PDF y publicación en `docs/architecture/`

---

## 📝 COMMIT

```bash
git add docs/architecture/IdentificacionComponentes.md

git commit -m "docs: Identificar componentes y estructura de capas para diagrama UML (#342)

- 5 capas identificadas: Controller, Service, Repository, Model/DTO, Config
- 49 componentes totales documentados
- Dependencias entre capas verificadas (sentido único)
- Patrones de diseño con estereotipo UML identificados
- Spring Security documentado como componente transversal
- Justificación de arquitectura en capas (>150 palabras)

Relacionado con HU-30"
```
