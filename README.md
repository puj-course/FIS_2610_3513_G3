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
```

## Ejecucion Docker
```bash
docker-compose up -d
```


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
