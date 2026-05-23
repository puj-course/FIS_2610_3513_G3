# Reporte Sprint 14

## Resumen
- Total: 21
- Completados: 21
- Pendientes: 0
- Cumplimiento: 100.00%

## Historias de usuario

- #560 HU-51: Corregir el pipeline CI y validar Quality Gate de SonarQube Como desarrollador, quiero que el archivo CI-pruebas.yml sea un workflow válido de GitHub Actions con sus triggers correctos, y que bloquee el build si el Quality Gate de SonarQube falla...
- #555 HU-50: Implementar métrica de rendimiento en el código como desarrollador del equipo, quiero medir la latencia de los endpoints REST del sistema de forma automática, para identificar cuellos de botella...
- #568 HU-53: Resolver los 105 open issues de SonarQube Como equipo, quiero reducir los open issues críticos detectados por SonarQube para mejorar la mantenibilidad y seguridad del sistema, asegurando que ningún issue de severidad....
- #564 HU-52: Aumentar la cobertura de pruebas al 80% en SonarQube Como equipo de desarrollo, quiero que las pruebas unitarias cubran al menos el 80% del código de producción verificado en SonarQube, para que el Quality Gate pase...

## Issues completados
- #643 Sprint Review y Retrospective 14
- #633 Daily 2 Sprint 14
- #560 HU-51: Corregir el pipeline CI y validar Quality Gate de SonarQube Como desarrollador, quiero que el archivo CI-pruebas.yml sea un workflow válido de GitHub Actions con sus triggers correctos, y que bloquee el build si el Quality Gate de SonarQube falla...
- #563 TASK: Configurar dependencia between CI y CD para que el deploy bloquee si el CI falla, y documentar en README
- #555 HU-50: Implementar métrica de rendimiento en el código como desarrollador del equipo, quiero medir la latencia de los endpoints REST del sistema de forma automática, para identificar cuellos de botella...
- #558 TASK: Documentar la métrica en README con interpretación y umbral
- #571 TASK: Re-ejecutar análisis de SonarCloud, verificar calificación y actualizar README con interpretación de métricas de mantenibilidad
- #570 TASK: Corregir todos los issues Blocker y Critical identificados (vulnerabilidades de seguridad, bugs de null pointer, recursos no cerrados)
- #569 TASK: Acceder a SonarCloud, clasificar los 105 issues por severidad y documentar el inventario priorizado en un archivo issues-report.md
- #568 HU-53: Resolver los 105 open issues de SonarQube Como equipo, quiero reducir los open issues críticos detectados por SonarQube para mejorar la mantenibilidad y seguridad del sistema, asegurando que ningún issue de severidad....
- #556 TASK: Crear HandlerInterceptor que capture tiempo de inicio/fin de cada request y lo loguee
- #557 TASK: Escribir test unitario con MockMvc que valide que la latencia registrada cumple el umbral de 500 ms
- #593 Daily 1 Sprint 14
- #561 TASK: Agregar bloques name:, on: (push+pull_request) y jobs: al inicio de CI-pruebas.yml y validar sintaxis
- #562 TASK: Agregar step en Stage 5 que consulte la API de SonarCloud y falle el pipeline si Quality Gate != PASSED
- #554 Sprint Planning 14
- #564 HU-52: Aumentar la cobertura de pruebas al 80% en SonarQube Como equipo de desarrollo, quiero que las pruebas unitarias cubran al menos el 80% del código de producción verificado en SonarQube, para que el Quality Gate pase...
- #566 TASK: Escribir tests unitarios para CustomService y pdfService
- #567 TASK: Agregar tests extras para diferentes servicios hasta alcanzar el 80% global verificado en SonarCloud
- #565 TASK: Crear los tests para los diferentes models para que corran sin contexto Spring completo
- #506 TASK: Escribir tests @DataJpaTest con H2 para TrabajoRepository y TareaRepository (métodos @Query personalizados)

## Issues pendientes
- Ninguno

## Métricas por usuario

### @echaustre12
- Asignados: 8
- Completados: 8
- Pendientes: 0
- Cumplimiento: 100.00%

### @Juanjoyt2
- Asignados: 4
- Completados: 4
- Pendientes: 0
- Cumplimiento: 100.00%

### @andresj-castrillo
- Asignados: 8
- Completados: 8
- Pendientes: 0
- Cumplimiento: 100.00%

### @Silverweta20
- Asignados: 9
- Completados: 9
- Pendientes: 0
- Cumplimiento: 100.00%

### @barretordaniel
- Asignados: 8
- Completados: 8
- Pendientes: 0
- Cumplimiento: 100.00%

