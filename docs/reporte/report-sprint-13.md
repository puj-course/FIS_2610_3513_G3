# Reporte Sprint 13

## Resumen
- Total: 18
- Completados: 18
- Pendientes: 0
- Cumplimiento: 100.00%

## Historias de usuario

- #482 HU-47:  Como equipo de desarrollo de EntregaYa, quiero configurar JaCoCo en el proyecto Maven para medir la cobertura de código de forma automatizada, para que el pipeline de CI rechace builds cuya cobertura sea inferior al 70%....
- #503 HU-48: Ampliar pruebas unitarias a servicios y repositorios
- #492 HU-49: Como QA Lead del equipo,quiero implementar pruebas unitarias con JUnit 5 (org.junit.jupiter.api.*) para los controladores TrabajoController, TareaController y AuthController, instanciándolos directamente e inyectando sus dependencias...

## Issues completados
- #482 HU-47:  Como equipo de desarrollo de EntregaYa, quiero configurar JaCoCo en el proyecto Maven para medir la cobertura de código de forma automatizada, para que el pipeline de CI rechace builds cuya cobertura sea inferior al 70%....
- #491 TASK: Documentar resultados (Security Rating, Duplication%) en README con captura e interpretación
- #483 TASK: Agregar jacoco-maven-plugin al pom.xml con goals report y check
- #484 TASK: Definir reglas de exclusión (config, dto, Application) e inclusión (service, controller)
- #487 R1: Configurar SonarQube para análisis de seguridad y duplicidad de código
- #488 TASK: Crear cuenta en SonarCloud, vincular el repositorio y obtener SONAR_TOKEN
- #503 HU-48: Ampliar pruebas unitarias a servicios y repositorios
- #505 TASK: Escribir tests @Test JUnit 5 para CustomTareaDetailsService con @SpringBootTest/H2 (mínimo 5 casos, incluir validaciones de fecha)
- #504 TASK: Escribir tests @Test JUnit 5 para CustomTrabajoDetailsService con @SpringBootTest/H2 (mínimo 6 casos)
- #485 TASK: Crear stage 'Cobertura' en CI-pruebas.yml con mvn verify y upload de artefacto HTML
- #519 Daily 3 Sprint 13
- #507 Daily 2 Sprint 13
- #490 TASK: Añadir job 'SonarQube Analysis' al pipeline CI con sonarcloud-github-action y secrets
- #489 TASK: Crear sonar-project.properties y configurar exclusiones (tests, generated, static)
- #502 Daily 1 Sprint 13
- #486 TASK: Documentar resultados en README con interpretación de métricas y captura de pantalla
- #481 Sprint Planning 13
- #492 HU-49: Como QA Lead del equipo,quiero implementar pruebas unitarias con JUnit 5 (org.junit.jupiter.api.*) para los controladores TrabajoController, TareaController y AuthController, instanciándolos directamente e inyectando sus dependencias...

## Issues pendientes
- Ninguno

## Métricas por usuario

### @barretordaniel
- Asignados: 9
- Completados: 9
- Pendientes: 0
- Cumplimiento: 100.00%

### @echaustre12
- Asignados: 9
- Completados: 9
- Pendientes: 0
- Cumplimiento: 100.00%

### @Silverweta20
- Asignados: 7
- Completados: 7
- Pendientes: 0
- Cumplimiento: 100.00%

### @Juanjoyt2
- Asignados: 4
- Completados: 4
- Pendientes: 0
- Cumplimiento: 100.00%

### @andresj-castrillo
- Asignados: 5
- Completados: 5
- Pendientes: 0
- Cumplimiento: 100.00%

