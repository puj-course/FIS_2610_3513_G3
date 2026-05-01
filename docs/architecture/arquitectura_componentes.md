El diagrama de componentes del sistema EntregaYa, muestra una arquitectura en capas formada por Controller, Service, Repository, Model/DTO y Config. Esta separación permite mantener un flujo de dependencias unidireccional, donde los controladores manejan las peticiones del usuario y delegan la lógica de negocio a los servicios, que a su vez interactúan con los repositorios para el acceso a datos.

Se identifican componentes funcionales tales como autenticación (login), gestión de roles, tareas, trabajos, invitaciones y notificaciones, lo cual facilita el entendimiento modular del sistema. También se utilizan interfaces provistas y requeridas mediante notación UML (lollipop), lo cual permite visualizar claramente los contratos entre componentes.

El elemento transversal de Spring Security interviene en las peticiones antes de que lleguen al controlador, garantizando que la autenticación y autorización estén presentes en todo el sistema. También se identifican patrones como Facade, Strategy y Factory dentro de sus respectivas capas.

El diagrama también permite apreciar dependencias directas entre capas que pueden ser candidatas para una futura refactorización. Este modelo ayuda a analizar el sistema, a incorporar nuevos desarrolladores y a encontrar problemas estructurales.
