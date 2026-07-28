# 02 — System Architecture

## Project Ghost Load

**Estado:** Actualizado para Arquitectura Hexagonal y DDD  
**Documento normativo del backend:** [10_Hexagonal_Architecture_DDD.md](./10_Hexagonal_Architecture_DDD.md)

---

## 1. Alcance

Ghost Load se implementará como una aplicación web con un backend Java organizado como
**monolito modular**. Los límites funcionales no representan microservicios ni artefactos
Maven separados.

Este documento resume la arquitectura del sistema. Las reglas detalladas de dependencias,
modelado, paquetes, pruebas y forma de trabajo están en el documento normativo enlazado
arriba.

---

## 2. Vista general

```text
Operador / Administrador
          |
          v
Frontend web (Next.js)
          |
          v
API REST (Spring Boot)
          |
          +--> PostgreSQL
          +--> almacenamiento de reportes
          +--> proveedor de correo
          +--> generador de PDF
```

Los proveedores concretos de almacenamiento, correo y PDF son decisiones pendientes.
Se conectarán mediante adaptadores cuando un caso de uso real los necesite.

No forman parte de la arquitectura base del MVP, salvo decisión explícita:

- Microservicios.
- Redis.
- Colas.
- Kubernetes.
- AWS Lambda.
- API Gateway.

---

## 3. Stack del backend

| Elemento | Decisión |
|---|---|
| Lenguaje | Java 21 |
| Build | Maven con Maven Wrapper |
| Framework | Spring Boot, versión definida por `pom.xml` |
| API | REST con contrato OpenAPI 3.1 |
| Base de datos | PostgreSQL |
| Persistencia | Spring Data JPA |
| Migraciones | Flyway |
| Arquitectura | Hexagonal / Ports and Adapters |
| Modelado | DDD táctico y pragmático |
| Despliegue lógico | Monolito modular |
| Package base | `com.ghostload.api` |

El MVP utilizará un único proyecto Maven. Los módulos se expresarán mediante packages.

---

## 4. Módulos funcionales

| Módulo | Responsabilidad |
|---|---|
| `assessment` | Operador, evaluación, calculadora, benchmark y resultados |
| `reporting` | Preparación, generación, almacenamiento y descarga del reporte |
| `outreach` | Contactos, importación CSV, campañas, invitaciones y tracking |
| `administration` | Acceso administrativo, dashboard y consultas agregadas |

`administration` será principalmente un módulo de consultas. No se forzará un modelo de
dominio complejo donde no existan reglas complejas.

---

## 5. Arquitectura interna de un módulo

```text
adapter.in
    |
    v
application.port.in
    ^
    |
application.service
    |
    +--> domain
    |
    v
application.port.out
    ^
    |
adapter.out
```

Responsabilidades:

- `domain`: entidades, value objects, agregados, invariantes y eventos; Java puro.
- `application.port.in`: casos de uso ofrecidos por el sistema.
- `application.service`: coordinación del caso de uso.
- `application.port.out`: capacidades externas requeridas por la aplicación.
- `adapter.in`: HTTP, listeners o jobs que invocan puertos de entrada.
- `adapter.out`: JPA, PDF, almacenamiento, correo u otra tecnología concreta.
- `configuration`: composition root y wiring de beans.

Reglas obligatorias:

- El dominio no depende de Spring, JPA, HTTP, AWS ni librerías de PDF.
- Una entidad JPA no es una entidad de dominio.
- Un controller nunca accede directamente a repositories o adaptadores de persistencia.
- Los puertos expresan intención de negocio, no una tecnología.
- Los DTO web no entran al dominio.
- Las dependencias apuntan hacia el interior.

---

## 6. Estructura de paquetes

```text
com.ghostload.api
├── assessment
│   ├── domain
│   ├── application
│   │   ├── port
│   │   │   ├── in
│   │   │   └── out
│   │   └── service
│   ├── adapter
│   │   ├── in
│   │   └── out
│   └── configuration
├── reporting
├── outreach
├── administration
└── shared
```

Cada módulo seguirá esta forma solo en la medida en que existan responsabilidades reales.
No se crearán carpetas, interfaces ni clases vacías para anticipar trabajo futuro.

`shared` contendrá exclusivamente conceptos genuinamente compartidos y estables; no será
un depósito general de utilidades.

---

## 7. Contrato HTTP

La fuente de verdad del contrato será:

```text
src/main/resources/openapi.yaml
```

El backend todavía no está materializado en este workspace, por lo que ese archivo deberá
crearse o migrarse desde la especificación existente antes de implementar adaptadores web.
Hasta entonces, [06_API_Specification.md](./06_API_Specification.md) es un borrador de
contrato y debe reconciliarse con los criterios de aceptación del MVP.

Flujo esperado:

```text
OpenAPI
→ DTO web
→ mapper web
→ command/query
→ puerto de entrada
→ servicio de aplicación
→ dominio
```

---

## 8. Seguridad

- Operador: email como identidad y `X-Evaluation-Token` como autorización de la
  evaluación, conforme al documento normativo.
- Administrador: Bearer JWT conforme al contrato OpenAPI definitivo.
- La extracción y validación de credenciales pertenece a adaptadores de seguridad.
- El dominio no conoce headers, tokens HTTP ni roles del framework.

La documentación anterior describe Clerk y un modelo general de roles. Esa propuesta debe
revisarse contra el flujo de token de evaluación antes de implementarse; no se considera una
autorización para agregar complejidad de autenticación al MVP.

---

## 9. Persistencia y servicios externos

- PostgreSQL se accede mediante adaptadores que implementan puertos de salida.
- Dominio y entidades JPA se modelan por separado y se conectan con mappers.
- Las transacciones cubren casos de uso, nunca reglas dentro del dominio.
- Correo, almacenamiento y PDF se incorporan detrás de puertos orientados a intención.
- No se seleccionará un proveedor o librería solo para completar la estructura.

Ejemplos:

```text
GeneratePdfPort        <- adaptador de la librería elegida
StoreReportPort        <- adaptador del almacenamiento elegido
SendEmailPort          <- adaptador del proveedor elegido
```

---

## 10. Estrategia de implementación

El backend se construirá mediante cortes verticales pequeños. Para cada corte se
identificarán antes de escribir código:

```text
Módulo
→ caso de uso
→ regla de dominio
→ puerto de entrada
→ puertos de salida necesarios
→ adaptadores
→ pruebas mínimas
```

Primer corte recomendado:

```text
Registrar operador
→ crear evaluación
→ generar token
→ persistir
→ devolver respuesta
```

No debe generarse todo el backend de una sola vez.

---

## 11. Validación arquitectónica

Una funcionalidad se considera alineada cuando:

- El negocio puede probarse sin HTTP y sin PostgreSQL.
- Las invariantes críticas tienen pruebas unitarias.
- El caso de uso depende de puertos, no de infraestructura.
- El adaptador web respeta OpenAPI.
- Ninguna entidad JPA sale por un controller.
- El proyecto compila y `mvnw test` pasa.
- Cada abstracción creada tiene un uso actual.

---

## 12. Fuentes de verdad

Ante contradicciones se aplicará este orden:

1. Criterios de aceptación aprobados del MVP.
2. `src/main/resources/openapi.yaml`, una vez incorporado al backend.
3. Decisiones registradas por el equipo.
4. [10_Hexagonal_Architecture_DDD.md](./10_Hexagonal_Architecture_DDD.md).
5. Este resumen arquitectónico.
6. Resto de documentos generales y propuestas generadas por IA.

