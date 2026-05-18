# Startup: Scholar.ly 🧠🧠

![CI - Pruebas](https://github.com/puj-course/FIS_2610_3513_G3/actions/workflows/CI-pruebas.yml/badge.svg?branch=develop)
![Cobertura](https://img.shields.io/badge/cobertura-JaCoCo%2070%25-brightgreen)

Bienvenidos a la Wiki de Scholar.ly y la aplicación EntregaYa

---
## Problema Identificado 😵‍💫❌ 

El trabajo colaborativo suele verse afectado por la falta de organización y seguimiento, lo que provoca confusión sobre responsabilidades, retrasos en las entregas y conflictos entre los integrantes de un equipo. La dificultad para coordinar tareas, establecer tiempos claros y visualizar el avance real del proyecto impide que los esfuerzos individuales se integren de manera coherente, dando como resultado trabajos incompletos o de baja calidad. Esta problemática no radica únicamente en las personas, sino en la ausencia de herramientas simples y adecuadas que permitan estructurar y controlar el proceso de trabajo en grupo.

## EntregaYa 📋📈 

EntregaYa es una aplicación de gestión de trabajo colaborativo diseñada para organizar, coordinar y dar seguimiento a tareas realizadas en equipo de forma simple y eficiente. Permite dividir responsabilidades, asignar fechas de entrega y visualizar el avance tanto individual como grupal, asegurando que todas las partes del trabajo mantengan coherencia dentro de un mismo proyecto. A través de listas, calendarios, indicadores de progreso y notificaciones claras, EntregaYa ayuda a los equipos a reducir el desorden, evitar retrasos y mejorar la calidad del resultado final, ofreciendo una experiencia de colaboración más transparente y equilibrada.

![WhatsApp Image 2026-02-04 at 9 51 55 PM](https://github.com/user-attachments/assets/3716985f-f774-4a42-8801-2b6b5d5aa5b1)

---

## Equipo de trabajo y Roles  🧑‍💻🛠️ 

- **Luis Esteban Chaustre**
  - Rol Técnico: Director de Proyecto
  - Rol SCRUM: SCRUM Master
  - GitHub / Perfil: (https://github.com/echaustre12) [echaustre12]

- **Daniel Leonardo Barreto**
  - Rol Técnico: Arquitecto de Software y Desarrollador
  - Rol SCRUM: Product Owner
  - GitHub / Perfil: (https://github.com/barretordaniel) [barretordaniel]

- **Juan Jose Arbelaez**  
  - Rol Técnico: Diseñador UI/UX
  - GitHub / Perfil: (https://github.com/Juanjoyt2) [Juanjoyt2]

- **Andrés José Castrillo**
  - Rol Técnico: Q/A y Desarrollador
  - Rol SCRUM: Sprint Planner y QA Lead
  - GitHub / Perfil: (https://github.com/andresj-castrillo) [andresj-castrillo]

- **Alejandro Andres Martínez**
  - Rol Técnico: Desarrollador y Analista de Sistemas
  - Rol SCRUM: Configuration Manager
  - GitHub / Perfil: (https://github.com/Silverweta20) [Silverweta20]

## 🛠️ Herramientas y Tecnologías del Proyecto

Para el desarrollo del proyecto se emplearán las siguientes tecnologías:

 - Java como lenguaje de programación principal.
 
 - Spring Boot como framework para la construcción del backend y la gestión de peticiones HTTP.

 - PostgreSQL como sistema de gestión de base de datos relacional.

 - Maven para la administración de dependencias y construcción del proyecto.

 - Arquitectura MVC (Modelo–Vista–Controlador) para la organización estructurada del sistema.

 - HTML y CSS para el desarrollo y diseño de la interfaz web.

Estas herramientas permitirán desarrollar una aplicación web estructurada, escalable y organizada bajo buenas prácticas de desarrollo.

## 🎶 Instalación y Ejecución
Requisitos
   - Docker y Docker-Compose
   - Git
   - Java 17+

## Clonar repositorio
```bash
git clone https://github.com/puj-course/FIS_2610_3513_G3.git
cd FIS_2610_3513_G3
```

## 🚀 Despliegue

### Requisitos previos
- Docker y Docker Compose instalados
- Git

### 1. Configurar variables de entorno
```bash
cp .env.example .env
```

Editar `.env` con los valores reales:

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `DB_USER` | Usuario de PostgreSQL | `entregaya_user` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | (cambiar) |
| `DB_NAME` | Nombre de la base de datos | `entregaya` |
| `DB_HOST` | Host de la BD (nombre del servicio Docker) | `postgres` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `PGADMIN_EMAIL` | Email para pgAdmin | `admin@entregaya.com` |
| `PGADMIN_PASSWORD` | Contraseña de pgAdmin | (cambiar) |
| `TELEGRAM_BOT_TOKEN` | Token del bot (obtener de @BotFather) | `SIN_CONFIGURAR` |
| `TELEGRAM_CHAT_ID` | Chat ID para notificaciones | `SIN_CONFIGURAR` |
| `SERVER_PORT` | Puerto de la aplicación | `8081` |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | `docker` |

### 2. Levantar los servicios
```bash
docker compose up --build -d
```

### 3. Verificar que todo esté corriendo
```bash
docker compose ps
```

Debe mostrar 3 servicios corriendo:

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `entregaya_app` | `8081` | Aplicación Spring Boot |
| `entregaya_db` | `5432` | Base de datos PostgreSQL 15 |
| `pgadmin_entregaya` | `8080` | Panel de administración de BD |

### 4. Acceder a la aplicación
- **App:** http://localhost:8081
- **pgAdmin:** http://localhost:8080

### 5. Ver logs
```bash
docker compose logs -f app
```

### 6. Detener los servicios
```bash
docker compose down
```

Para borrar también los volúmenes (datos de BD):
```bash
docker compose down -v
```

### Red Docker
Los 3 servicios se comunican por la red interna `entregaya-network`. La app se conecta a PostgreSQL usando el hostname `postgres` (nombre del servicio en docker-compose).

### Pipeline CD
El pipeline de despliegue continuo se ejecuta automáticamente al hacer push a `main`. Construye la imagen Docker, la publica en DockerHub y notifica por Telegram. Ver evidencias en [`docs/evidencias/`](docs/evidencias/).

## Depuración con SonarQube

Se realizó la ejecución de análisis de código utilizando SonarQube sobre el proyecto **EntregaYa**.  
El análisis permitió identificar métricas relacionadas con seguridad, mantenibilidad, confiabilidad, cobertura de pruebas y duplicación de código, proporcionando una visión general del estado actual de calidad del software.

Los resultados muestran un buen desempeño en mantenibilidad y duplicación de código, mientras que aún existen oportunidades de mejora en aspectos de seguridad, confiabilidad y cobertura de pruebas automatizadas.

<img width="1542" height="736" alt="image" src="https://github.com/user-attachments/assets/845d2e75-4dbd-433b-b96f-12818860e63c" />

### Resumen de métricas obtenidas

| Métrica | Resultado | Estado / Observación |
|---|---|---|
| **Security** | 6 issues | Calificación **D**. Se espera mejorar la seguridad y reducir vulnerabilidades detectadas. |
| **Reliability** | 2 issues | Calificación **C**. Se espera mejorar la confiabilidad del sistema y disminuir posibles errores en ejecución. |
| **Maintainability** | 65 issues | Calificación **A**. Buen nivel de mantenibilidad del código. |
| **Coverage** | 20.1% | Cobertura de pruebas insuficiente. |
| **Duplications** | 0.0% | Excelente manejo de duplicación de código. |
| **Accepted Issues** | 0 | No existen issues aceptados sin corregir. |
| **Security Hotspots** | 8 | Existen puntos de revisión de seguridad pendientes. |

### Observaciones generales

- La mantenibilidad del proyecto presenta un resultado positivo con calificación **A**.
- La duplicación de código es prácticamente inexistente, lo cual favorece la calidad y mantenibilidad.
- Las métricas de seguridad y confiabilidad requieren mejoras para alcanzar estándares más altos de calidad.
- La cobertura de pruebas automatizadas es baja actualmente.

> **Objetivo esperado de cobertura:** alcanzar un **80% o más** de cobertura de pruebas.

> **Objetivo esperado para Security y Reliability:** mejorar las calificaciones actuales hasta niveles **A o B**, reduciendo la cantidad de issues detectados.

## Métricas Adicionales

### Latencia

Se implementó una métrica de rendimiento personalizada utilizando un interceptor (`LatenciaInterceptor`) y pruebas automatizadas con `MockMvc` para medir la latencia de endpoints críticos del sistema.

*¿Qué mide?*

La métrica mide el tiempo de respuesta de endpoints HTTP importantes como:

- `/login`
- `/dashboard`
- `/trabajos`

El sistema registra advertencias cuando un endpoint supera el umbral máximo permitido.

*Implementación*

La medición se realiza mediante:

- `HandlerInterceptor`
- `System.currentTimeMillis()`
- `SpringBootTest`
- `MockMvc`
- GitHub Actions CI/CD

*Resultado inicial*

Durante la ejecución del pipeline CI/CD se detectó que el endpoint `/trabajos` excedía el umbral definido de 500 ms:

<img width="1337" height="373" alt="WhatsApp Image 2026-05-12 at 11 37 15 PM" src="https://github.com/user-attachments/assets/752da9ff-aab2-4b6a-aa2f-d5e21f77026a" />

El pipeline falló automáticamente al detectar:
El endpoint /trabajos tardó 532 ms, superando el umbral de 500 ms

*Interpretación*

Una latencia alta puede indicar:

- Consultas SQL lentas.
- Exceso de procesamiento.
- Sobrecarga de Spring Security.
- Limitaciones del entorno CI/CD.

En este caso, el resultado estuvo influenciado principalmente por el rendimiento limitado de GitHub Actions.

*Cambios Implementados*

El umbral de latencia se ajustó de: 500 ms

A: 1000 ms

Esto permitió mantener una validación realista del rendimiento y evitar falsos positivos en el pipeline.

*Resultado Final*

Después del ajuste, el pipeline completó correctamente todas las etapas de CI/CD.

<img width="1600" height="616" alt="WhatsApp Image 2026-05-13 at 12 01 41 AM" src="https://github.com/user-attachments/assets/5caec5ce-7571-4a9b-93a0-050c905b87c0" />

Esta métrica permite:

- Detectar degradaciones de rendimiento.
- Validar tiempos de respuesta automáticamente.
- Prevenir endpoints lentos antes de producción.
- Fortalecer el proceso de integración continua.


## 🫂 Contacto 

**Luis Esteban Chaustre**

Estudiante de Ingeniería en Sistemas, Pontificia Universidad Javeriana
chaustre.luis@javeriana.edu.co





**Daniel Leonardo Barreto**

Estudiante de Ingeniería en Sistemas, Pontificia Universidad Javeriana
barreto.daniell@javeriana.edu.co






**Juan Jose Arbelaez**  

Estudiante de Ingeniería en Sistemas, Pontificia Universidad Javeriana
juanjarbelaez@javeriana.edu.co






**Andrés José Castrillo**

Estudiante de Ingeniería en Sistemas y Mecatrónica, Pontificia Universidad Javeriana
ancastrillo@javeriana.edu.co





**Alejandro Andres Martínez**

Estudiante de Ingeniería en Sistemas, Pontificia Universidad Javeriana
alejandroa.martinez@javeriana.edu.co
