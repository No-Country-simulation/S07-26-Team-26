# Ghost Load API

Backend del proyecto **Ghost Load**, desarrollado para la simulación de NoCountry.

Ghost Load ayuda a operadores de data centers a identificar capacidad no productiva, evaluar su madurez operativa y obtener un reporte PDF personalizado.

## Objetivo del backend

El backend será responsable de:

- Registrar operadores.
- Crear y mantener evaluaciones.
- Calcular indicadores de capacidad.
- Procesar respuestas del benchmark.
- Calcular puntajes, niveles y percentiles.
- Generar reportes PDF.
- Gestionar contactos y campañas de outreach.
- Entregar información al dashboard administrativo.

El proyecto se desarrollará como un **monolito modular**, utilizando:

- Java 21.
- Spring Boot.
- Maven.
- PostgreSQL.
- Arquitectura hexagonal.
- Principios de Domain-Driven Design.

---

# Estado actual

Actualmente el proyecto contiene la base inicial de Spring Boot.

La arquitectura y los módulos se implementarán de manera progresiva, comenzando por funcionalidades reales y evitando crear clases o interfaces que todavía no sean necesarias.

No se pretende construir toda la arquitectura de una sola vez.

El primer objetivo es completar un flujo pequeño:

```text
Registrar operador
→ Crear evaluación
→ Guardar datos
→ Devolver identificador y token
```

A partir de ese primer flujo se repetirá la misma estructura para calculadora, benchmark, PDF y outreach.

---

# Arquitectura

Utilizaremos arquitectura hexagonal, también conocida como:

> Ports and Adapters

La idea principal es mantener la lógica del negocio independiente de tecnologías externas como:

- HTTP.
- Spring MVC.
- PostgreSQL.
- JPA.
- AWS.
- Librerías de PDF.
- Servicios de correo.

La aplicación se organiza de esta forma:

```text
Exterior
   │
   ▼
Adaptadores de entrada
   │
   ▼
Puertos de entrada
   │
   ▼
Aplicación y dominio
   │
   ▼
Puertos de salida
   │
   ▼
Adaptadores de salida
   │
   ▼
Exterior
```

## Adaptadores de entrada

Son los componentes que permiten que algo externo invoque la aplicación.

Ejemplos:

- Controladores REST.
- Listeners de eventos.
- Procesos programados.
- Pruebas que ejecutan un caso de uso.

Ejemplo:

```text
CalculatorController
→ CalculateCapacityUseCase
```

El controlador se limita a:

1. Recibir la solicitud HTTP.
2. Validar el formato.
3. Convertir el request en un comando.
4. Invocar el caso de uso.
5. Convertir el resultado en una respuesta HTTP.

El controlador no debe contener reglas del negocio ni consultas directas a la base de datos.

## Puertos de entrada

Representan las operaciones que ofrece la aplicación.

Normalmente corresponden a casos de uso.

Ejemplo:

```java
public interface CalculateCapacityUseCase {

    CapacityCalculationResult calculate(
        CalculateCapacityCommand command
    );
}
```

Otros ejemplos:

```text
RegisterEvaluationUseCase
SubmitBenchmarkUseCase
GetEvaluationResultsQuery
GenerateReportUseCase
ImportContactsUseCase
CreateCampaignUseCase
```

Los puertos de entrada no deben mencionar HTTP, REST, controladores o tecnologías específicas.

## Servicios de aplicación

Implementan los puertos de entrada y coordinan el flujo del caso de uso.

Ejemplo:

```java
public final class CalculateCapacityService
        implements CalculateCapacityUseCase {

    private final LoadEvaluationPort loadEvaluationPort;
    private final SaveEvaluationPort saveEvaluationPort;

    public CalculateCapacityService(
            LoadEvaluationPort loadEvaluationPort,
            SaveEvaluationPort saveEvaluationPort) {
        this.loadEvaluationPort = loadEvaluationPort;
        this.saveEvaluationPort = saveEvaluationPort;
    }

    @Override
    public CapacityCalculationResult calculate(
            CalculateCapacityCommand command) {

        // Cargar evaluación.
        // Ejecutar reglas del dominio.
        // Guardar resultados.
        // Devolver resultado.
        return null;
    }
}
```

El servicio de aplicación coordina el proceso, pero las reglas importantes deben mantenerse dentro del dominio cuando corresponda.

## Puertos de salida

Representan capacidades externas que la aplicación necesita.

Ejemplos:

```java
public interface LoadEvaluationPort {

    Optional<Evaluation> load(EvaluationId evaluationId);
}
```

```java
public interface SaveEvaluationPort {

    Evaluation save(Evaluation evaluation);
}
```

```java
public interface GeneratePdfPort {

    GeneratedPdf generate(ReportData reportData);
}
```

```java
public interface StoreReportPort {

    StoredReport store(GeneratedPdf report);
}
```

```java
public interface SendEmailPort {

    void send(EmailMessage message);
}
```

Los puertos de salida no deben mencionar:

- JPA.
- PostgreSQL.
- S3.
- SES.
- OpenHTMLToPDF.
- Spring Data.

Estas tecnologías pertenecen a sus adaptadores.

## Adaptadores de salida

Implementan los puertos que necesita la aplicación.

Ejemplos:

```text
EvaluationPersistenceAdapter
→ implementa LoadEvaluationPort
→ implementa SaveEvaluationPort
```

```text
OpenHtmlPdfAdapter
→ implementa GeneratePdfPort
```

```text
S3ReportStorageAdapter
→ implementa StoreReportPort
```

```text
SesEmailAdapter
→ implementa SendEmailPort
```

El núcleo de la aplicación no debe saber qué tecnología utiliza cada adaptador.

---

# Dirección de dependencias

La dirección esperada es:

```text
Controller
   │
   ▼
Puerto de entrada
   ▲
   │ implementa
Servicio de aplicación
   │
   ├── Dominio
   │
   ▼
Puerto de salida
   ▲
   │ implementa
Adaptador de persistencia, PDF, correo o almacenamiento
```

Dependencias permitidas:

```text
adapter.in → application.port.in

application.service → application.port.in
application.service → application.port.out
application.service → domain

adapter.out → application.port.out
adapter.out → domain

configuration → aplicación y adaptadores
```

Dependencias que se deben evitar:

```text
domain → Spring
domain → JPA
domain → AWS
domain → Controller

application → JpaRepository
application → S3Client
application → SesClient

controller → repository
adapter.in → adapter.out
```

---

# DDD dentro del proyecto

DDD y arquitectura hexagonal cumplen objetivos diferentes.

## Arquitectura hexagonal

Protege el negocio de las tecnologías externas.

## Domain-Driven Design

Ayuda a modelar las reglas y conceptos del negocio.

Usaremos DDD de manera práctica, sin intentar aplicar todos sus patrones desde el primer día.

Los elementos que podrán aparecer son:

- Entidades.
- Value Objects.
- Aggregate Roots.
- Servicios de dominio.
- Eventos de dominio.
- Repositorios de agregados.
- Lenguaje común entre el equipo.

No es obligatorio que cada clase sea una entidad o Value Object.

Solo se utilizarán cuando ayuden a expresar una regla del negocio.

---

# Módulos del backend

Inicialmente el backend se dividirá en cuatro módulos funcionales.

## Assessment

Responsable del proceso de evaluación.

Incluye:

- Operador.
- Evaluación.
- Calculadora.
- Benchmark.
- Puntajes.
- Niveles de madurez.
- Percentil.
- Recomendaciones.

## Reporting

Responsable de los reportes.

Incluye:

- Preparación de datos.
- Generación del PDF.
- Estado del reporte.
- Almacenamiento.
- Descarga.
- Reintentos.

## Outreach

Responsable de las invitaciones.

Incluye:

- Importación CSV.
- Contactos.
- Campañas.
- Invitaciones.
- Tokens.
- Tracking de completitud.

## Administration

Responsable del acceso y consultas internas.

Incluye:

- Login administrativo.
- Dashboard.
- Listado de operadores.
- Resultados acumulados.
- Consultas de campañas.

Estos módulos pertenecen al mismo backend. No son microservicios separados.

---

# Estructura de paquetes propuesta

La estructura se irá creando conforme se implementen las funcionalidades.

No se deben crear todas las carpetas vacías desde el comienzo.

```text
com.ghostload.api
├── assessment
│   ├── domain
│   │   ├── model
│   │   ├── service
│   │   ├── event
│   │   └── exception
│   │
│   ├── application
│   │   ├── port
│   │   │   ├── in
│   │   │   └── out
│   │   └── service
│   │
│   ├── adapter
│   │   ├── in
│   │   │   └── web
│   │   └── out
│   │       ├── persistence
│   │       └── security
│   │
│   └── configuration
│
├── reporting
│   ├── domain
│   ├── application
│   ├── adapter
│   └── configuration
│
├── outreach
│   ├── domain
│   ├── application
│   ├── adapter
│   └── configuration
│
├── administration
│   ├── application
│   ├── adapter
│   └── configuration
│
└── shared
    ├── domain
    └── configuration
```

La carpeta `shared` debe mantenerse pequeña.

No debe utilizarse como lugar general para clases que no sabemos dónde colocar.

---

# Ejemplo de estructura para una funcionalidad

La calculadora podría quedar así:

```text
assessment
├── domain
│   ├── model
│   │   ├── Evaluation.java
│   │   ├── CapacityInput.java
│   │   ├── CapacityResult.java
│   │   └── Percentage.java
│   │
│   └── service
│       └── CapacityCalculationPolicy.java
│
├── application
│   ├── port
│   │   ├── in
│   │   │   └── CalculateCapacityUseCase.java
│   │   └── out
│   │       ├── LoadEvaluationPort.java
│   │       └── SaveEvaluationPort.java
│   │
│   └── service
│       └── CalculateCapacityService.java
│
├── adapter
│   ├── in
│   │   └── web
│   │       ├── CalculatorController.java
│   │       ├── CalculatorRequest.java
│   │       ├── CalculatorResponse.java
│   │       └── CalculatorWebMapper.java
│   │
│   └── out
│       └── persistence
│           ├── EvaluationJpaEntity.java
│           ├── SpringDataEvaluationRepository.java
│           ├── EvaluationPersistenceMapper.java
│           └── EvaluationPersistenceAdapter.java
│
└── configuration
    └── AssessmentBeanConfiguration.java
```

Esta estructura es una referencia, no una obligación de crear todas las clases antes de necesitarlas.

---

# Dominio y persistencia

Las entidades del dominio y las entidades JPA estarán separadas.

## Modelo de dominio

Representa reglas y conceptos del negocio.

```java
public final class Evaluation {

    private final EvaluationId id;
    private final OperatorId operatorId;
    private EvaluationState state;

    public void completeCalculator(CapacityResult result) {
        if (state != EvaluationState.STARTED) {
            throw new InvalidEvaluationTransition(state);
        }

        state = EvaluationState.CALCULATOR_COMPLETED;
    }
}
```

## Entidad JPA

Representa cómo se guarda la información.

```java
@Entity
@Table(name = "evaluations")
class EvaluationJpaEntity {

    @Id
    private UUID id;

    private UUID operatorId;

    @Enumerated(EnumType.STRING)
    private EvaluationStateJpa state;
}
```

## Mapper de persistencia

Convierte entre ambos modelos.

```java
final class EvaluationPersistenceMapper {

    Evaluation toDomain(EvaluationJpaEntity entity) {
        // Conversión a dominio.
        return null;
    }

    EvaluationJpaEntity toEntity(Evaluation evaluation) {
        // Conversión a persistencia.
        return null;
    }
}
```

Las entidades JPA no deben devolverse directamente desde los controladores.

---

# Agregados iniciales

No se crearán agregados para cada tabla.

Los agregados iniciales posibles son:

## Assessment

```text
Evaluation
Operator
```

`Evaluation` será responsable de controlar:

- Estado de la evaluación.
- Resultado de la calculadora.
- Finalización del benchmark.
- Transiciones permitidas.

## Reporting

```text
GeneratedReport
```

## Outreach

```text
Campaign
Invitation
```

Estos modelos pueden ajustarse durante el desarrollo si el equipo encuentra una opción más sencilla o clara.

---

# Eventos

Al completar el benchmark se podrá producir un evento:

```text
BenchmarkCompleted
```

Flujo esperado:

```text
Assessment
→ BenchmarkCompleted
→ Reporting
→ GenerateReportUseCase
```

El dominio no dependerá directamente de Spring Events.

Si se utiliza `ApplicationEventPublisher`, deberá estar dentro de un adaptador que implemente un puerto de publicación de eventos.

Para el primer avance no es obligatorio implementar eventos asíncronos.

Puede comenzarse con una llamada coordinada desde el servicio de aplicación y migrarse a eventos cuando el flujo principal ya funcione.

---

# Contrato OpenAPI

El contrato HTTP se encuentra en:

```text
docs/openapi.yaml
```

OpenAPI define:

- Endpoints.
- Requests.
- Responses.
- Errores.
- Estados.
- Seguridad.
- Modelos compartidos con Frontend.

El archivo OpenAPI pertenece al exterior del sistema.

Los DTO HTTP deben permanecer en:

```text
adapter/in/web
```

Ejemplo:

```text
CreateEvaluationRequest
→ EvaluationWebMapper
→ RegisterEvaluationCommand
→ RegisterEvaluationUseCase
```

Los DTO generados desde OpenAPI no deben utilizarse directamente como entidades del dominio.

---

# Manejo de dependencias externas

Las tecnologías externas se colocarán detrás de puertos.

Ejemplos:

| Tecnología | Puerto |
|---|---|
| PostgreSQL / JPA | `LoadEvaluationPort`, `SaveEvaluationPort` |
| Generador PDF | `GeneratePdfPort` |
| Almacenamiento | `StoreReportPort` |
| Servicio de correo | `SendEmailPort` |
| Tokens | `GenerateEvaluationTokenPort` |
| Eventos | `PublishDomainEventPort` |

Esto permite probar la aplicación sin necesitar siempre una base de datos, AWS o un servicio de correo.

---

# Reglas prácticas

Estas reglas sirven como orientación durante el desarrollo.

## Evitar

```text
Controller → JpaRepository
```

```text
Controller → PersistenceAdapter
```

```text
ApplicationService → S3Client
```

```text
Domain → Spring
```

```text
Domain → JPA
```

También se debe evitar crear interfaces únicamente para terminar con:

```text
SomethingService
SomethingServiceImpl
```

Una interfaz debería representar principalmente:

- Un puerto de entrada.
- Un puerto de salida.
- Una abstracción que tenga sentido para el negocio.

## Mantener

```text
Controller
→ Input Port
→ Application Service
→ Domain
→ Output Port
→ Output Adapter
```

---

# Forma de trabajo

El objetivo es que ambos integrantes de Backend aprendan y participen en funcionalidades completas.

No se dividirá el trabajo únicamente en:

```text
Una persona crea controllers.
Otra persona crea repositories.
```

Cada integrante podrá desarrollar cortes verticales pequeños:

```text
Request
→ Controller
→ Caso de uso
→ Dominio
→ Puerto de salida
→ Persistencia
→ Response
→ Pruebas
```

Esto permite comprender el recorrido completo de una funcionalidad.

## Flujo recomendado

```text
Tarjeta de Trello
→ Rama feature
→ Desarrollo
→ Pull Request
→ Revisión del otro integrante
→ Correcciones
→ Merge a develop
```

Ejemplo de rama:

```text
feature/HU-01-operator-registration
```

Ejemplos de commits:

```text
feat(assessment): create evaluation use case
feat(persistence): save operator and evaluation
test(calculator): cover capacity formulas
fix(benchmark): reject incomplete answers
```

---

# Revisión de Pull Requests

Cada Pull Request debe indicar:

```text
Qué cambia
Tarjeta relacionada
Cómo probarlo
Endpoint agregado o modificado
Migración agregada
Pruebas ejecutadas
```

Las revisiones deben explicar los motivos de los cambios sugeridos.

Ejemplo:

```text
Esta validación puede estar en el request porque corresponde
al formato de entrada.
```

```text
Esta regla debería permanecer en el dominio porque controla
una transición de la evaluación.
```

El objetivo de la revisión es mejorar el código y compartir conocimiento, no solamente aprobar o rechazar cambios.

---

# Pruebas

No se busca una cobertura perfecta.

Se priorizarán las reglas que puedan producir errores importantes:

- Fórmulas de la calculadora.
- Capacidad productiva menor o igual que capacidad total.
- Límites de niveles de madurez.
- Puntajes por categoría.
- Validación de las doce respuestas.
- Percentil.
- Transiciones de estados.
- Generación única del PDF.
- Duplicados de contactos.
- Estados de invitación.

Los servicios de aplicación y el dominio deben poder probarse sin levantar toda la aplicación cuando sea posible.

---

# Primeros pasos

El primer corte vertical será:

```text
Registrar operador
→ Crear evaluación
→ Generar token
→ Guardar información
→ Devolver respuesta
```

Orden sugerido:

1. Definir request y response en OpenAPI.
2. Crear `RegisterEvaluationUseCase`.
3. Crear el comando y resultado del caso de uso.
4. Modelar `Operator` y `Evaluation`.
5. Definir puertos de persistencia.
6. Implementar el servicio de aplicación.
7. Crear el controlador REST.
8. Crear entidades JPA y adaptador de persistencia.
9. Crear migración.
10. Agregar pruebas.
11. Abrir Pull Request.
12. Revisar y ajustar la arquitectura.

Después se continuará con la calculadora.

No se crearán por adelantado todos los módulos, interfaces y clases del proyecto.

Cada estructura aparecerá cuando una funcionalidad real la necesite.

---

# Ejecución local

## Requisitos

- Java 21.
- PostgreSQL.
- Git.

## Ejecutar pruebas

En Windows:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

## Ejecutar la aplicación

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

---

# Principio general

La arquitectura debe ayudar a desarrollar y comprender el sistema.

Si una abstracción, interfaz o capa no aporta una separación útil, no debe agregarse solamente para que el proyecto parezca más complejo.

El objetivo es aprender una forma profesional de trabajar mientras se entrega un MVP funcional dentro del tiempo disponible.
