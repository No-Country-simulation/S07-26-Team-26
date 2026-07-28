# Arquitectura Hexagonal y DDD — Ghost Load API

> Documento de contexto técnico para el equipo Backend y para cualquier asistente de IA que participe en el proyecto.

## 1. Propósito de este documento

Este archivo define cómo aplicaremos **Arquitectura Hexagonal (Ports and Adapters)** y **Domain-Driven Design (DDD)** en el backend de Ghost Load.

No pretende que el equipo construya toda la arquitectura de una sola vez. Su función es:

- Mantener una dirección técnica común.
- Evitar una falsa arquitectura hexagonal basada solo en nombres de carpetas.
- Ayudar a una persona que está aprendiendo a comprender el recorrido completo de una funcionalidad.
- Contextualizar asistentes de IA para que no inventen estructuras, endpoints o reglas.
- Implementar el sistema progresivamente mediante cortes verticales pequeños.
- Entregar un MVP funcional dentro del tiempo disponible.

La arquitectura debe ayudar al equipo. No debe convertirse en una excusa para crear capas, interfaces o archivos que todavía no aportan valor.

---

# 2. Instrucciones para asistentes de IA

Cuando una IA reciba este documento como contexto, debe seguir estas reglas:

1. **No generar el backend completo de una sola vez.**
2. Implementar únicamente la historia, caso de uso o corte vertical solicitado.
3. Explicar brevemente la función de cada clase antes de proponerla.
4. No crear carpetas, interfaces o clases vacías “para el futuro”.
5. No inventar endpoints: el contrato HTTP está en `src/main/resources/openapi.yaml`.
6. No modificar fórmulas, estados o nombres del dominio sin indicar que se trata de una decisión pendiente.
7. No utilizar entidades JPA como entidades del dominio.
8. No conectar controllers directamente con repositories.
9. No introducir microservicios, colas, caché, Kubernetes u otras tecnologías fuera del alcance sin una necesidad aprobada.
10. Mantener el dominio independiente de Spring, JPA, HTTP, AWS y librerías de PDF.
11. Proponer pruebas junto con cada regla de negocio.
12. Favorecer código entendible para un desarrollador que está aprendiendo.
13. Evitar sobreingeniería y abstracciones sin uso real.
14. Cuando existan varias opciones válidas, explicar el intercambio y recomendar la más simple para el MVP.
15. Antes de producir código, identificar:

```text
Módulo
→ caso de uso
→ regla de dominio
→ puerto de entrada
→ puertos de salida necesarios
→ adaptadores
→ pruebas mínimas
```

Una IA no debe asumir que por tener este documento está autorizada a implementar todos los módulos. Debe avanzar únicamente sobre la tarea actual.

---

# 3. Contexto del proyecto

## 3.1 Nombre

**Ghost Load API**

## 3.2 Objetivo del producto

Ghost Load permite que un operador de data center:

1. Registre sus datos.
2. Complete una calculadora de capacidad.
3. Complete un benchmark de madurez.
4. Consulte sus resultados.
5. Obtenga un reporte PDF institucional personalizado.

También permite que un administrador:

1. Importe contactos mediante CSV.
2. Cree campañas.
3. Envíe invitaciones con enlaces únicos.
4. Consulte el avance de cada contacto.
5. Revise métricas acumuladas desde un dashboard.

## 3.3 Flujo principal

```text
Registro
→ Calculadora
→ Benchmark
→ Resultados
→ Generación automática del PDF
→ Descarga
```

## 3.4 Contexto técnico

```text
Lenguaje: Java 21
Build tool: Maven
Framework: Spring Boot
API: REST
Contrato: OpenAPI 3.1
Base de datos: PostgreSQL
Persistencia: Spring Data JPA
Migraciones: Flyway
Arquitectura: Hexagonal / Ports and Adapters
Modelado: DDD táctico y pragmático
Tipo de despliegue: Monolito modular
Package base: com.ghostload.api
```

La versión de Spring Boot será la configurada en el `pom.xml`. No debe cambiarse sin acuerdo del equipo.

## 3.5 Fuentes de verdad del proyecto

Cuando exista una contradicción, usar este orden:

1. Criterios de aceptación aprobados del MVP.
2. `src/main/resources/openapi.yaml`.
3. Decisiones registradas por el equipo.
4. Este documento.
5. README general.
6. Propuestas o ejemplos generados por una IA.

---

# 4. Qué es Arquitectura Hexagonal

Arquitectura Hexagonal, también llamada **Ports and Adapters**, separa la aplicación en:

- Un interior que contiene aplicación y dominio.
- Un exterior que contiene tecnologías y mecanismos de entrega.
- Puertos que definen conversaciones con el interior.
- Adaptadores que traducen entre una tecnología concreta y esos puertos.

El objetivo no es dibujar un hexágono ni tener seis lados. El objetivo es que el negocio pueda ejecutarse y probarse sin depender directamente de:

- Una interfaz web.
- Una base de datos real.
- Un proveedor de correo.
- Un proveedor de almacenamiento.
- Una librería específica de PDF.

Vista simplificada:

```text
EXTERIOR
   │
   ▼
ADAPTADOR DE ENTRADA
   │
   ▼
PUERTO DE ENTRADA
   │
   ▼
APLICACIÓN
   │
   ▼
DOMINIO
   │
   ▼
PUERTO DE SALIDA
   │
   ▼
ADAPTADOR DE SALIDA
   │
   ▼
EXTERIOR
```

La división importante es **interior versus exterior**, no “controller, service y repository” como capas técnicas globales.

---

# 5. Driving Side y Driven Side

## 5.1 Lado conductor: Driving Side

Es el lado que inicia una acción dentro de Ghost Load.

Ejemplos:

- Una petición HTTP.
- Un evento de benchmark completado.
- Una tarea programada.
- Una prueba automatizada.
- Un comando administrativo.

Los componentes principales son:

```text
Driving Adapter
→ Input Port
→ Application Service
```

Ejemplo:

```text
CalculatorController
→ CalculateCapacityUseCase
→ CalculateCapacityService
```

## 5.2 Lado conducido: Driven Side

Es el lado que la aplicación utiliza para completar una operación.

Ejemplos:

- Guardar una evaluación.
- Consultar preguntas.
- Generar un token.
- Crear un PDF.
- Guardar un archivo.
- Enviar un correo.
- Publicar un evento.

Los componentes principales son:

```text
Application Service
→ Output Port
→ Driven Adapter
```

Ejemplo:

```text
CalculateCapacityService
→ SaveEvaluationPort
→ EvaluationPersistenceAdapter
→ PostgreSQL
```

---

# 6. Puertos de entrada

Un puerto de entrada representa una capacidad que Ghost Load ofrece al exterior.

Normalmente corresponde a un caso de uso.

Ejemplos:

```java
public interface RegisterEvaluationUseCase {

    RegisterEvaluationResult register(
        RegisterEvaluationCommand command
    );
}
```

```java
public interface CalculateCapacityUseCase {

    CapacityCalculationResult calculate(
        CalculateCapacityCommand command
    );
}
```

```java
public interface SubmitBenchmarkUseCase {

    BenchmarkSubmissionResult submit(
        SubmitBenchmarkCommand command
    );
}
```

Características de un puerto de entrada:

- Habla en términos del negocio.
- No conoce HTTP.
- No recibe `HttpServletRequest`.
- No devuelve `ResponseEntity`.
- No recibe entidades JPA.
- No recibe `MultipartFile`.
- No conoce rutas REST.
- Puede ser invocado desde HTTP, una prueba o cualquier otro adaptador.

Nombres recomendados:

```text
RegisterEvaluationUseCase
CalculateCapacityUseCase
SubmitBenchmarkUseCase
GenerateReportUseCase
ImportContactsUseCase
CreateCampaignUseCase
SendCampaignUseCase
GetEvaluationResultsQuery
GetDashboardSummaryQuery
```

No usar nombres como:

```text
EvaluationControllerPort
RestEvaluationPort
HttpCalculatorService
```

HTTP pertenece al adaptador, no al caso de uso.

---

# 7. Servicios de aplicación

Un servicio de aplicación implementa un puerto de entrada y coordina un caso de uso.

Responsabilidades:

1. Recibir un command o query.
2. Cargar agregados mediante puertos de salida.
3. Invocar comportamiento del dominio.
4. Persistir cambios.
5. Publicar eventos si corresponde.
6. Devolver un resultado de aplicación.

Ejemplo conceptual:

```java
public final class CalculateCapacityService
        implements CalculateCapacityUseCase {

    private final LoadEvaluationPort loadEvaluationPort;
    private final SaveEvaluationPort saveEvaluationPort;
    private final Clock clock;

    public CalculateCapacityService(
            LoadEvaluationPort loadEvaluationPort,
            SaveEvaluationPort saveEvaluationPort,
            Clock clock) {
        this.loadEvaluationPort = loadEvaluationPort;
        this.saveEvaluationPort = saveEvaluationPort;
        this.clock = clock;
    }

    @Override
    public CapacityCalculationResult calculate(
            CalculateCapacityCommand command) {

        Evaluation evaluation = loadEvaluationPort
            .load(command.evaluationId())
            .orElseThrow(EvaluationNotFound::new);

        CapacityResult result = evaluation.completeCalculator(
            command.capacityInput(),
            clock.instant()
        );

        saveEvaluationPort.save(evaluation);

        return CapacityCalculationResult.from(result);
    }
}
```

El servicio de aplicación coordina. No debe convertirse en una clase con todas las fórmulas, validaciones, reglas de estados y detalles técnicos mezclados.

---

# 8. Puertos de salida

Un puerto de salida representa una capacidad que la aplicación necesita del exterior.

Ejemplos:

```java
public interface LoadEvaluationPort {

    Optional<Evaluation> load(EvaluationId evaluationId);
}
```

```java
public interface SaveEvaluationPort {

    void save(Evaluation evaluation);
}
```

```java
public interface GenerateEvaluationTokenPort {

    EvaluationToken generate(EvaluationId evaluationId);
}
```

```java
public interface GeneratePdfPort {

    GeneratedPdf generate(ReportData reportData);
}
```

```java
public interface StoreReportPort {

    StoredReport store(
        EvaluationId evaluationId,
        GeneratedPdf generatedPdf
    );
}
```

```java
public interface SendEmailPort {

    SendEmailResult send(EmailMessage message);
}
```

```java
public interface PublishDomainEventPort {

    void publish(DomainEvent event);
}
```

Características:

- Son propiedad del interior de la aplicación.
- Se definen según lo que el caso de uso necesita.
- No según lo que ofrece una librería.
- No mencionan JPA, PostgreSQL, S3, SES o una librería PDF.
- Sus implementaciones viven en adaptadores de salida.

Incorrecto:

```java
public interface JpaEvaluationPort {
}
```

```java
public interface S3FilePort {
}
```

Correcto:

```java
public interface StoreReportPort {
}
```

El puerto expresa intención. El adaptador expresa tecnología.

---

# 9. Adaptadores de entrada

Un adaptador de entrada convierte una interacción externa en una llamada a un puerto de entrada.

En Ghost Load serán principalmente:

- Controllers REST.
- Listeners de eventos.
- Jobs programados, si realmente se necesitan.
- Adaptadores de seguridad.
- Tests que llaman un caso de uso.

Ejemplo de flujo web:

```text
HTTP JSON
→ CreateEvaluationRequest
→ EvaluationWebMapper
→ RegisterEvaluationCommand
→ RegisterEvaluationUseCase
```

Responsabilidades de un controller:

- Recibir el request.
- Aplicar validaciones de formato.
- Mapear a command o query.
- Invocar un puerto de entrada.
- Mapear el resultado.
- Elegir el status HTTP adecuado.

No debe:

- Usar un `JpaRepository`.
- Ejecutar fórmulas.
- Decidir transiciones de estado.
- Generar tokens.
- Crear SQL.
- Construir PDFs.
- Enviar correos.
- Conocer detalles de almacenamiento.

Ejemplo conceptual:

```java
@RestController
@RequestMapping("/api/v1/evaluations")
final class EvaluationController {

    private final RegisterEvaluationUseCase registerEvaluationUseCase;
    private final EvaluationWebMapper mapper;

    EvaluationController(
            RegisterEvaluationUseCase registerEvaluationUseCase,
            EvaluationWebMapper mapper) {
        this.registerEvaluationUseCase = registerEvaluationUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    ResponseEntity<CreateEvaluationResponse> create(
            @Valid @RequestBody CreateEvaluationRequest request) {

        RegisterEvaluationCommand command = mapper.toCommand(request);
        RegisterEvaluationResult result =
            registerEvaluationUseCase.register(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toResponse(result));
    }
}
```

---

# 10. Adaptadores de salida

Un adaptador de salida implementa uno o más puertos de salida usando una tecnología concreta.

Ejemplos:

```text
EvaluationPersistenceAdapter
implements LoadEvaluationPort
implements SaveEvaluationPort
```

```text
OpenHtmlToPdfAdapter
implements GeneratePdfPort
```

```text
S3ReportStorageAdapter
implements StoreReportPort
```

```text
SesEmailAdapter
implements SendEmailPort
```

```text
SecureRandomEvaluationTokenAdapter
implements GenerateEvaluationTokenPort
```

El adaptador puede depender de:

- Spring Data JPA.
- PostgreSQL.
- SDK de AWS.
- Librería de PDF.
- Cliente de correo.
- Sistema de archivos.
- Framework de eventos.

El dominio y la aplicación no deben depender del adaptador.

---

# 11. Dirección de dependencias

La dirección correcta es:

```text
adapter.in
    ↓
application.port.in
    ↑
application.service
    ↓
domain
    ↓
application.port.out
    ↑
adapter.out
```

Otra forma de verlo:

```text
Controller
→ Use Case
← Application Service
→ Domain
→ Output Port
← Persistence Adapter
→ JPA Repository
```

Dependencias permitidas:

```text
adapter.in → application.port.in
adapter.in → application DTO/command/result

application.service → application.port.in
application.service → application.port.out
application.service → domain

adapter.out → application.port.out
adapter.out → domain
adapter.out → tecnología concreta

configuration → application
configuration → adapters
```

Dependencias prohibidas:

```text
domain → Spring
domain → JPA
domain → HTTP
domain → AWS
domain → PDF library

application → Controller
application → JpaRepository
application → S3Client
application → SesClient
application → MultipartFile
application → ResponseEntity

adapter.in → adapter.out
Controller → JpaRepository
Controller → PersistenceAdapter
```

---

# 12. Composition Root

Las implementaciones deben conectarse en un punto de composición.

Ubicación sugerida:

```text
<module>/configuration
```

Ejemplo:

```java
@Configuration
class AssessmentBeanConfiguration {

    @Bean
    RegisterEvaluationUseCase registerEvaluationUseCase(
            LoadOperatorPort loadOperatorPort,
            SaveOperatorPort saveOperatorPort,
            SaveEvaluationPort saveEvaluationPort,
            GenerateEvaluationTokenPort tokenPort,
            Clock clock) {

        return new RegisterEvaluationService(
            loadOperatorPort,
            saveOperatorPort,
            saveEvaluationPort,
            tokenPort,
            clock
        );
    }
}
```

Regla elegida para Ghost Load:

- El dominio permanece totalmente independiente de Spring.
- La aplicación se mantendrá independiente de Spring siempre que no complique innecesariamente el MVP.
- Los beans se construirán desde clases `@Configuration`.
- Si se usa `@Transactional` directamente en un servicio de aplicación por simplicidad, debe registrarse como una decisión pragmática y no extenderse sin criterio.

---

# 13. Qué es DDD

Domain-Driven Design ayuda a construir software usando un modelo alineado con el negocio.

DDD no significa:

- Crear muchas carpetas.
- Añadir `Entity` al nombre de todas las clases.
- Crear un servicio para cada operación.
- Convertir cada tabla en un agregado.
- Utilizar todos los patrones del libro.

Para Ghost Load se aplicará DDD de forma táctica y proporcional al MVP.

Elementos que utilizaremos cuando tengan sentido:

- Lenguaje ubicuo.
- Módulos o contextos delimitados.
- Entidades.
- Value Objects.
- Aggregate Roots.
- Reglas e invariantes.
- Servicios de dominio.
- Eventos de dominio.
- Repositorios de agregados.

---

# 14. Lenguaje ubicuo de Ghost Load

Los nombres del código, OpenAPI, base de datos y conversaciones deben mantener los mismos conceptos.

## 14.1 Operador

Persona que completa la evaluación en representación de una empresa o data center.

```text
Operator
OperatorId
Email
CompanyName
Position
```

## 14.2 Evaluación

Proceso que une registro, calculadora, benchmark, resultados y reporte.

```text
Evaluation
EvaluationId
EvaluationState
EvaluationToken
```

## 14.3 Capacidad no productiva

Capacidad contratada y disponible que no está generando carga productiva.

```text
TotalCapacity
ProductiveCapacity
NonProductiveCapacity
UtilizationPercentage
EstimatedAnnualCost
```

## 14.4 Benchmark

Evaluación de madurez de doce preguntas en cuatro categorías.

```text
BenchmarkQuestion
BenchmarkAnswer
BenchmarkResult
CategoryScore
MaturityLevel
Percentile
```

## 14.5 Reporte

Documento institucional generado con los resultados.

```text
GeneratedReport
ReportStatus
ReportData
StoredReport
```

## 14.6 Outreach

Proceso de importación, invitación y seguimiento de contactos.

```text
Contact
Campaign
Invitation
InvitationToken
InvitationStatus
ContactImport
```

Los nombres pueden estar en inglés en el código y en español en la interfaz, pero deben representar el mismo concepto.

---

# 15. Módulos y límites del dominio

Ghost Load será un **monolito modular**. No serán microservicios.

Módulos iniciales:

```text
assessment
reporting
outreach
administration
```

Estos módulos son límites funcionales y candidatos a bounded contexts. No deben interpretarse como cuatro aplicaciones separadas.

## 15.1 Assessment

Responsable de:

- Operador.
- Evaluación.
- Calculadora.
- Benchmark.
- Puntaje total.
- Puntaje por categoría.
- Nivel de madurez.
- Percentil.
- Recomendaciones.

## 15.2 Reporting

Responsable de:

- Preparar datos del reporte.
- Generar PDF.
- Guardar el archivo.
- Gestionar estado.
- Entregar enlace de descarga.
- Permitir reintentos.

## 15.3 Outreach

Responsable de:

- Importar CSV.
- Validar contactos.
- Crear campañas.
- Crear invitaciones.
- Generar enlaces únicos.
- Enviar mensajes.
- Actualizar tracking.

## 15.4 Administration

Responsable de:

- Acceso administrativo.
- Consultas del dashboard.
- Listado de operadores.
- Consulta de campañas.
- Métricas agregadas.

`administration` será principalmente orientado a consultas. No debe forzarse un modelo de dominio complejo si no existen reglas complejas.

---

# 16. Entidades

Una entidad tiene identidad y un ciclo de vida.

Ejemplo:

```java
public final class Evaluation {

    private final EvaluationId id;
    private final OperatorId operatorId;
    private EvaluationState state;
    private CapacityResult capacityResult;
    private BenchmarkResult benchmarkResult;

    // Comportamiento del dominio.
}
```

Dos evaluaciones con el mismo contenido no son la misma evaluación si tienen distinto identificador.

Candidatos iniciales:

```text
Operator
Evaluation
GeneratedReport
Campaign
Invitation
ContactImport
```

No es obligatorio que todos terminen siendo Aggregate Roots.

---

# 17. Value Objects

Un Value Object representa un concepto por su valor, no por una identidad.

Ejemplos:

```text
EvaluationId
OperatorId
Email
Capacity
Money
Percentage
EvaluationToken
BenchmarkScore
Percentile
```

Características:

- Inmutables.
- Validados al construirse.
- Comparables por valor.
- Sin setters.
- Expresan una regla o concepto.

Java 21 permite usar `record` para muchos Value Objects:

```java
public record EvaluationId(UUID value) {

    public EvaluationId {
        Objects.requireNonNull(value, "value is required");
    }

    public static EvaluationId newId() {
        return new EvaluationId(UUID.randomUUID());
    }
}
```

Ejemplo de email:

```java
public record Email(String value) {

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        if (!normalized.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        value = normalized;
    }
}
```

La validación HTTP completa puede usar Jakarta Validation. El Value Object protege la invariancia mínima del dominio.

---

# 18. Aggregate Roots

Un Aggregate Root controla consistencia y cambios dentro de un límite transaccional.

Regla:

> Los repositorios se crean para Aggregate Roots, no para cada tabla.

Candidatos iniciales:

## Assessment

```text
Operator
Evaluation
```

`Evaluation` debe controlar:

- Su estado.
- Cuándo se guarda una calculadora.
- Cuándo puede enviarse el benchmark.
- Cuándo el benchmark está completo.
- Cuándo puede iniciarse un reporte.
- Transiciones válidas.

## Reporting

```text
GeneratedReport
```

Controla:

- Estado de generación.
- Reintentos.
- Referencia de almacenamiento.
- Fecha de generación.
- Error de generación.

## Outreach

```text
Campaign
Invitation
```

Para evitar cargar cientos de contactos dentro de una campaña, `Invitation` puede mantenerse como agregado separado relacionado mediante `CampaignId`.

Estas decisiones son iniciales. Deben confirmarse al implementar el primer caso real, no mediante carpetas vacías.

---

# 19. Invariantes principales

Una invariante es una regla que siempre debe cumplirse dentro del dominio.

## 19.1 Calculadora

Entradas:

```text
totalCapacityMw > 0
productiveCapacityMw >= 0
productiveCapacityMw <= totalCapacityMw
monthlyCostPerKw >= 0
currency obligatoria
```

Fórmulas:

```text
nonProductiveCapacityMw =
    totalCapacityMw - productiveCapacityMw
```

```text
utilizationPercentage =
    productiveCapacityMw / totalCapacityMw × 100
```

```text
nonProductivePercentage =
    nonProductiveCapacityMw / totalCapacityMw × 100
```

```text
estimatedAnnualCost =
    nonProductiveCapacityMw × 1000 × monthlyCostPerKw × 12
```

Ejemplo:

```text
Total: 20 MW
Productiva: 13 MW
Costo: USD 90 por kW al mes

No productiva: 7 MW
Utilización: 65 %
No productiva: 35 %
Costo anual estimado: USD 7 560 000
```

## 19.2 Benchmark

```text
12 preguntas
4 categorías
3 preguntas por categoría
cada respuesta entre 1 y 5
no se aceptan preguntas repetidas
no se aceptan preguntas desconocidas
todas deben pertenecer a la versión activa
```

Puntaje total:

```text
((sumaRespuestas - 12) / 48) × 100
```

Puntaje por categoría:

```text
((sumaCategoria - 3) / 12) × 100
```

Niveles:

```text
0–24   INITIAL
25–49  DEVELOPING
50–74  MANAGED
75–89  ADVANCED
90–100 OPTIMIZED
```

El percentil utiliza datos de demostración durante el MVP. La respuesta y el PDF deben indicar:

```text
Comparación realizada con datos de demostración del ambiente de staging.
```

## 19.3 Identidad ligera

```text
Correo = identidad del operador
Token = autorización para continuar una evaluación
EvaluationId = identidad interna de la evaluación
```

No habrá contraseña para operadores.

## 19.4 Reporte

- Solo puede generarse para una evaluación con benchmark completo.
- La generación debe ser idempotente.
- Un reintento solo procede cuando el reporte falló.
- No se almacena una URL pública permanente.
- Se almacena la referencia interna del archivo.
- La URL de descarga se genera temporalmente.

## 19.5 Outreach

- No se duplica el mismo correo dentro de una campaña.
- Cada invitación tiene un token único.
- El correo no se expone en la URL.
- Los estados solo avanzan mediante transiciones válidas.
- Reenviar o reintentar no debe crear invitaciones duplicadas.

---

# 20. Máquinas de estados

## 20.1 Evaluación

```text
STARTED
→ CALCULATOR_COMPLETED
→ BENCHMARK_COMPLETED
→ REPORT_GENERATING
→ REPORT_COMPLETED
```

Ruta alternativa:

```text
REPORT_GENERATING
→ REPORT_FAILED
→ REPORT_GENERATING
```

`REPORT_FAILED` no es un paso posterior a `REPORT_COMPLETED`.

La evaluación no debe retroceder de estado.

## 20.2 Invitación

```text
UPLOADED
→ SENT
→ VISITED
→ STARTED
→ COMPLETED
```

Ruta alternativa de envío:

```text
UPLOADED
→ FAILED
```

El dominio debe decidir qué transiciones son válidas. El controller o el repository no debe modificar estados libremente.

Ejemplo:

```java
public void completeCalculator(
        CapacityResult result,
        Instant completedAt) {

    if (state != EvaluationState.STARTED) {
        throw new InvalidEvaluationTransition(state);
    }

    this.capacityResult = result;
    this.state = EvaluationState.CALCULATOR_COMPLETED;
    this.updatedAt = completedAt;
}
```

---

# 21. Servicios de dominio

Un servicio de dominio se usa cuando una regla:

- Es parte del negocio.
- No pertenece naturalmente a una sola entidad o Value Object.
- No depende de infraestructura.

Ejemplos posibles:

```text
BenchmarkScoringPolicy
PercentilePolicy
RecommendationPolicy
```

La calculadora puede implementarse:

- Dentro de un Value Object o entidad si la regla pertenece claramente allí.
- En `CapacityCalculationPolicy` si se quiere mantener el cálculo como política explícita.

No crear `DomainService` para operaciones simples solo por seguir un patrón.

---

# 22. Eventos de dominio

Un evento representa algo relevante que ya ocurrió.

Ejemplo:

```text
BenchmarkCompleted
ReportGenerated
ReportGenerationFailed
InvitationVisited
EvaluationCompleted
```

Ejemplo:

```java
public record BenchmarkCompleted(
        EvaluationId evaluationId,
        Instant occurredAt
) implements DomainEvent {
}
```

Flujo esperado para el PDF:

```text
Assessment
→ BenchmarkCompleted
→ Reporting
→ GenerateReportUseCase
```

El dominio no debe importar `ApplicationEventPublisher`.

Puede existir un puerto:

```java
public interface PublishDomainEventPort {

    void publish(DomainEvent event);
}
```

Y un adaptador:

```text
SpringDomainEventPublisherAdapter
```

Para el MVP, no es obligatorio empezar con eventos asíncronos. Primero puede completarse el corte vertical mediante coordinación síncrona. Después se extrae el evento cuando el flujo ya esté probado.

---

# 23. Repositorios DDD

Un repositorio representa una colección de Aggregate Roots.

En arquitectura hexagonal, el contrato del repositorio puede modelarse mediante puertos de salida:

```java
public interface LoadEvaluationPort {

    Optional<Evaluation> load(EvaluationId evaluationId);
}
```

```java
public interface SaveEvaluationPort {

    void save(Evaluation evaluation);
}
```

No se expone `JpaRepository` al interior.

Correcto:

```text
Application Service
→ LoadEvaluationPort
→ EvaluationPersistenceAdapter
→ SpringDataEvaluationRepository
```

Incorrecto:

```text
Application Service
→ SpringDataEvaluationRepository
```

---

# 24. Estructura de paquetes

La estructura se creará de forma progresiva.

```text
src/main/java/com/ghostload/api
├── assessment
│   ├── domain
│   │   ├── model
│   │   ├── service
│   │   ├── event
│   │   └── exception
│   ├── application
│   │   ├── port
│   │   │   ├── in
│   │   │   └── out
│   │   └── service
│   ├── adapter
│   │   ├── in
│   │   │   └── web
│   │   └── out
│   │       ├── persistence
│   │       └── security
│   └── configuration
│
├── reporting
│   ├── domain
│   ├── application
│   ├── adapter
│   │   ├── in
│   │   │   └── event
│   │   └── out
│   │       ├── pdf
│   │       ├── persistence
│   │       └── storage
│   └── configuration
│
├── outreach
│   ├── domain
│   ├── application
│   ├── adapter
│   │   ├── in
│   │   │   └── web
│   │   └── out
│   │       ├── persistence
│   │       ├── csv
│   │       └── email
│   └── configuration
│
├── administration
│   ├── application
│   ├── adapter
│   │   ├── in
│   │   │   └── web
│   │   └── out
│   │       └── persistence
│   └── configuration
│
└── shared
    ├── domain
    └── configuration
```

Reglas:

- No crear todas las carpetas vacías al inicio.
- Crear únicamente las carpetas requeridas por el caso de uso actual.
- `shared` debe mantenerse pequeño.
- No usar `shared` como depósito de clases sin ubicación clara.
- No crear una carpeta global de controllers y otra global de services.
- La organización principal es por módulo de negocio.

---

# 25. Ejemplo completo: Calculadora

```text
assessment
├── domain
│   ├── model
│   │   ├── Evaluation.java
│   │   ├── EvaluationId.java
│   │   ├── CapacityInput.java
│   │   ├── CapacityResult.java
│   │   ├── Capacity.java
│   │   ├── Money.java
│   │   └── Percentage.java
│   └── exception
│       └── InvalidCapacityInput.java
│
├── application
│   ├── port
│   │   ├── in
│   │   │   └── CalculateCapacityUseCase.java
│   │   └── out
│   │       ├── LoadEvaluationPort.java
│   │       └── SaveEvaluationPort.java
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

No todas estas clases deben crearse antes de implementar. Esta estructura muestra dónde pertenece cada responsabilidad cuando exista.

---

# 26. Modelo de dominio y modelo JPA

Las entidades del dominio y las entidades JPA estarán separadas.

## 26.1 Dominio

```java
public final class Evaluation {

    private final EvaluationId id;
    private final OperatorId operatorId;
    private EvaluationState state;

    public CapacityResult completeCalculator(
            CapacityInput input,
            Instant completedAt) {

        ensureCanCompleteCalculator();

        CapacityResult result = CapacityResult.calculate(input);

        this.state = EvaluationState.CALCULATOR_COMPLETED;

        return result;
    }

    private void ensureCanCompleteCalculator() {
        if (state != EvaluationState.STARTED) {
            throw new InvalidEvaluationTransition(state);
        }
    }
}
```

No lleva:

```text
@Entity
@Table
@Column
@Service
@Component
@Repository
```

## 26.2 Persistencia

```java
@Entity
@Table(name = "evaluations")
class EvaluationJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID operatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvaluationStateJpa state;
}
```

## 26.3 Mapper

```java
final class EvaluationPersistenceMapper {

    Evaluation toDomain(EvaluationJpaEntity entity) {
        // Conversión de persistencia a dominio.
        return null;
    }

    EvaluationJpaEntity toEntity(Evaluation evaluation) {
        // Conversión de dominio a persistencia.
        return null;
    }
}
```

Las entidades JPA nunca se devuelven desde controllers.

---

# 27. OpenAPI y adaptador web

El contrato HTTP está en:

```text
src/main/resources/openapi.yaml
```

Es la fuente de verdad para:

- Rutas.
- Métodos HTTP.
- Request bodies.
- Responses.
- Errores.
- Seguridad.
- Estados.
- Schemas compartidos con Frontend.

Flujo:

```text
OpenAPI schema
→ DTO web
→ Web mapper
→ Command / Query
→ Input Port
```

Ejemplo:

```text
CreateEvaluationRequest
→ EvaluationWebMapper
→ RegisterEvaluationCommand
→ RegisterEvaluationUseCase
```

Un DTO web no debe entrar al dominio.

Incorrecto:

```java
public Evaluation create(CreateEvaluationRequest request) {
}
```

Correcto:

```java
RegisterEvaluationCommand command =
    mapper.toCommand(request);

RegisterEvaluationResult result =
    useCase.register(command);
```

Si cambia el contrato HTTP:

1. Actualizar `openapi.yaml`.
2. Revisar con Frontend.
3. Actualizar DTO y mapper.
4. Mantener el dominio independiente del cambio de transporte.

---

# 28. Java 21 en el proyecto

## 28.1 Records

Usar `record` para:

- Commands.
- Queries.
- Results.
- DTO internos inmutables.
- Value Objects simples.
- Eventos de dominio.

Ejemplo:

```java
public record CalculateCapacityCommand(
        EvaluationId evaluationId,
        CapacityInput input
) {
}
```

No usar `record` automáticamente para Aggregate Roots con estado y comportamiento mutable.

## 28.2 BigDecimal

Para capacidad, costo, dinero y porcentajes usar `BigDecimal`, no `double`, dentro del dominio.

Razón:

- Evita errores de representación binaria.
- Permite definir escala y redondeo.
- Es más seguro para valores monetarios.

Ejemplo:

```java
BigDecimal utilization = productive
    .divide(total, 8, RoundingMode.HALF_UP)
    .multiply(BigDecimal.valueOf(100));
```

El equipo debe acordar:

```text
Escala interna: suficiente para cálculos
Escala de respuesta: normalmente 2 decimales
RoundingMode: HALF_UP, salvo decisión distinta
```

## 28.3 UUID

Los identificadores técnicos usarán UUID, envueltos en Value Objects cuando entren al dominio.

```java
public record EvaluationId(UUID value) {
}
```

## 28.4 Tiempo

Usar:

```text
Instant
OffsetDateTime
Clock
```

Preferencia:

- `Instant` dentro del dominio.
- `OffsetDateTime` cuando el contrato HTTP necesite offset.
- `Clock` inyectado para pruebas deterministas.

Evitar llamadas directas repetidas a:

```java
Instant.now()
```

dentro de reglas difíciles de probar.

## 28.5 Optional

Usar `Optional` principalmente como valor de retorno.

Correcto:

```java
Optional<Evaluation> load(EvaluationId id);
```

Evitar:

```text
Optional como campo de entidad
Optional como parámetro
Optional dentro de DTO JPA
```

## 28.6 Sealed types

`sealed interface` puede utilizarse cuando existe un conjunto cerrado y estable de resultados.

No es obligatorio.

Ejemplo posible:

```java
public sealed interface ReportGenerationOutcome
        permits ReportGenerated, ReportFailed {
}
```

No utilizarlo solo para demostrar una característica de Java 21.

## 28.7 Excepciones

Las excepciones de dominio deben representar problemas del negocio:

```text
EvaluationNotFound
InvalidEvaluationTransition
IncompleteBenchmark
UnknownBenchmarkQuestion
DuplicateCampaignContact
ReportNotReady
```

No crear una excepción diferente para cada línea.

El adaptador web traduce estas excepciones al error definido por OpenAPI.

---

# 29. Maven

## 29.1 Estructura estándar

```text
ghost-load-api
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn
└── src
    ├── main
    │   ├── java
    │   └── resources
    └── test
        ├── java
        └── resources
```

Usar las convenciones estándar de Maven.

## 29.2 Un solo módulo Maven

El MVP será inicialmente:

```text
Un monolito modular
→ un proyecto Maven
→ módulos representados por packages
```

No convertirlo en un proyecto Maven multi-module sin una necesidad comprobada.

Arquitectura modular no significa necesariamente múltiples artefactos Maven.

## 29.3 Maven Wrapper

Usar el wrapper incluido:

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

Validación completa:

```bash
./mvnw verify
```

## 29.4 Dependencias

Reglas:

- Añadir una dependencia solo cuando una historia real la necesite.
- No declarar versiones que ya administra Spring Boot.
- Usar scope correcto.
- No incluir SDK completos si solo se necesita un módulo.
- No añadir varias librerías para resolver la misma función.
- Documentar por qué se incorpora una dependencia importante.

## 29.5 Perfiles

Preferir perfiles de Spring y variables de entorno:

```text
application.properties
application-local.properties
application-test.properties
application-staging.properties
```

No usar perfiles Maven para secretos o configuración normal de ejecución.

## 29.6 Recursos

Ubicaciones:

```text
src/main/resources/openapi.yaml
src/main/resources/db/migration
src/main/resources/templates
src/test/resources
```

Migraciones Flyway:

```text
src/main/resources/db/migration/V1__create_initial_schema.sql
```

---

# 30. Spring Boot dentro de Hexagonal

## Dominio

Java puro.

No debe depender de:

```text
Spring
JPA
Jakarta Persistence
HTTP
AWS
librerías PDF
```

## Aplicación

Preferiblemente Java puro.

Puede depender de:

```text
domain
application.port.in
application.port.out
```

## Adaptadores

Aquí viven:

```text
@RestController
@Entity
@Repository
JpaRepository
S3Client
SES client
PDF renderer
MultipartFile
ResponseEntity
```

## Configuración

Aquí se conectan los objetos:

```text
@Configuration
@Bean
SecurityFilterChain
ObjectMapper
Clock
```

---

# 31. Persistencia y transacciones

La transacción debe cubrir el caso de uso que necesita consistencia.

Ejemplo:

```text
Cargar evaluación
→ validar estado
→ calcular
→ modificar agregado
→ guardar
→ confirmar transacción
```

No abrir transacciones dentro del dominio.

Opciones:

1. Decorador transaccional alrededor del caso de uso.
2. Configuración específica.
3. `@Transactional` en servicio de aplicación como concesión pragmática.

Para el MVP, la tercera opción puede aceptarse cuando simplifique el trabajo, siempre que:

- El dominio siga independiente.
- El servicio no dependa de JPA directamente.
- La decisión quede explícita.
- No se mezcle lógica técnica dentro del servicio.

---

# 32. Seguridad

## Operador

```text
Email identifica
Evaluation Token autoriza
```

El token llega mediante:

```http
X-Evaluation-Token
```

El adaptador de seguridad:

1. Extrae el token.
2. Lo valida.
3. Obtiene la evaluación autorizada.
4. Entrega identidad técnica al controller o caso de uso.

El dominio no conoce headers HTTP.

## Administrador

Los endpoints administrativos usan Bearer JWT según OpenAPI.

La implementación concreta pertenece a adaptadores de seguridad.

No introducir roles complejos durante el MVP.

---

# 33. Pruebas

## 33.1 Dominio

Pruebas unitarias rápidas sin Spring.

Ejemplos:

- Fórmulas.
- Validaciones.
- Estados.
- Niveles de madurez.
- Percentil.
- Recomendaciones.

```java
@Test
void shouldCalculateUnusedCapacityAndAnnualCost() {
    // Arrange
    // Act
    // Assert
}
```

## 33.2 Aplicación

Pruebas del caso de uso con fakes o mocks de puertos.

Ejemplo:

```text
FakeLoadEvaluationPort
InMemorySaveEvaluationPort
FixedClock
```

Verificar:

- Puertos invocados.
- Estado actualizado.
- Resultado correcto.
- Error correcto.

## 33.3 Adaptador web

Probar:

- Status HTTP.
- Validación del request.
- Mapeo.
- ErrorResponse.
- Seguridad.

Puede utilizarse `MockMvc`.

## 33.4 Adaptador de persistencia

Probar:

- Mapeo.
- Queries.
- Constraints.
- Relaciones.
- Migraciones.

Puede utilizarse PostgreSQL real mediante Testcontainers cuando el equipo llegue a esa etapa.

## 33.5 Integración

Probar los recorridos principales:

```text
Registro
→ calculadora
→ benchmark
→ resultados
→ reporte
```

```text
CSV
→ campaña
→ invitación
→ evaluación completada
→ dashboard
```

## 33.6 Prioridades

No se busca cobertura perfecta.

Pruebas obligatorias para reglas críticas:

- Fórmulas de capacidad.
- Capacidad productiva no mayor que total.
- Doce respuestas completas.
- Escala 1–5.
- Límites de madurez.
- Puntajes por categoría.
- Percentil.
- Transiciones de estado.
- Idempotencia del PDF.
- Duplicados de contactos.
- Estados de invitación.

---

# 34. Primer corte vertical recomendado

```text
Registrar operador
→ crear evaluación
→ generar token
→ persistir
→ devolver respuesta
```

Archivos mínimos posibles:

```text
assessment/domain/model/Operator.java
assessment/domain/model/Evaluation.java
assessment/domain/model/OperatorId.java
assessment/domain/model/EvaluationId.java
assessment/domain/model/Email.java

assessment/application/port/in/RegisterEvaluationUseCase.java
assessment/application/port/out/LoadOperatorPort.java
assessment/application/port/out/SaveOperatorPort.java
assessment/application/port/out/SaveEvaluationPort.java
assessment/application/port/out/GenerateEvaluationTokenPort.java
assessment/application/service/RegisterEvaluationService.java

assessment/adapter/in/web/EvaluationController.java
assessment/adapter/in/web/CreateEvaluationRequest.java
assessment/adapter/in/web/CreateEvaluationResponse.java
assessment/adapter/in/web/EvaluationWebMapper.java

assessment/adapter/out/persistence/OperatorJpaEntity.java
assessment/adapter/out/persistence/EvaluationJpaEntity.java
assessment/adapter/out/persistence/AssessmentPersistenceAdapter.java

assessment/adapter/out/security/SecureEvaluationTokenAdapter.java

assessment/configuration/AssessmentBeanConfiguration.java
```

Esta lista es orientativa. Antes de crearla completa, verificar cuáles archivos son realmente necesarios para el primer PR.

---

# 35. Ruta de aprendizaje para el equipo

## Etapa 1

- Levantar el proyecto.
- Ejecutar tests.
- Leer README.
- Revisar OpenAPI.
- Entender el flujo de puertos y adaptadores.

## Etapa 2

Implementar dominio puro de la calculadora:

```text
CapacityInput
CapacityResult
validaciones
fórmulas
pruebas
```

Sin controller ni base de datos.

## Etapa 3

Convertir la calculadora en un corte hexagonal:

```text
Controller
→ Input Port
→ Application Service
→ Domain
→ Output Port
→ Persistence Adapter
```

## Etapa 4

Participar en benchmark:

- Preguntas.
- Respuestas.
- Validaciones.
- Scoring.
- Pruebas.

## Etapa 5

Participar en CSV, campañas o reportes mediante pairing.

La persona que está aprendiendo debe implementar y corregir su propio código. La IA o el compañero con más experiencia no deben reemplazar ese aprendizaje escribiendo todo por ella.

---

# 36. Forma de trabajo con IA

Una petición adecuada para una IA:

```text
Contexto:
Lee hexagonal-architecture-ddd.md y openapi.yaml.

Tarea:
Implementar únicamente el dominio de la calculadora de HU-02.

Antes del código:
1. Identifica las reglas de dominio.
2. Propón el mínimo de clases.
3. Explica por qué cada clase pertenece al dominio.
4. Enumera las pruebas.
5. No implementes controller, JPA ni migraciones todavía.
```

Otra petición:

```text
Implementa el adaptador web de la calculadora usando el endpoint
definido en openapi.yaml.

No cambies el dominio.
No conectes el controller al repository.
Mapea el request a CalculateCapacityCommand.
Explica cada archivo para un desarrollador que está aprendiendo.
```

Petición incorrecta:

```text
Haz todo el backend completo con arquitectura hexagonal.
```

Ese tipo de instrucción suele producir código excesivo, inconsistencias y poca comprensión.

---

# 37. Anti-patrones que deben detectarse

## Controller conectado a JPA

```java
@RestController
class CalculatorController {

    private final CalculatorJpaRepository repository;
}
```

## Entidad de dominio con JPA

```java
@Entity
public class Evaluation {
}
```

## Puerto llamado por tecnología

```java
public interface PostgreSqlEvaluationPort {
}
```

## Servicio sin propósito

```java
public interface CalculatorService {
}

public class CalculatorServiceImpl
        implements CalculatorService {
}
```

## Request HTTP usado como modelo de dominio

```java
evaluation.completeCalculator(request);
```

## Reglas dentro del mapper

```java
response.setScore(calculateBenchmarkScore(answers));
```

## Estado modificado libremente

```java
evaluation.setState(REPORT_COMPLETED);
```

## Dependencia externa en aplicación

```java
class GenerateReportService {

    private final S3Client s3Client;
}
```

## `shared` como depósito

```text
shared/util/EverythingUtils.java
```

---

# 38. Checklist de una funcionalidad hexagonal

Antes de abrir un Pull Request:

## Dominio

- [ ] La regla principal está expresada en el dominio.
- [ ] El dominio no importa Spring o JPA.
- [ ] Las invariantes tienen pruebas.
- [ ] Los nombres coinciden con el lenguaje del proyecto.

## Aplicación

- [ ] Existe un caso de uso claro.
- [ ] El puerto de entrada representa una acción o consulta real.
- [ ] Los puertos de salida expresan necesidades, no tecnologías.
- [ ] El servicio coordina y no contiene detalles de infraestructura.

## Adaptador de entrada

- [ ] Respeta `openapi.yaml`.
- [ ] Valida formato de entrada.
- [ ] Mapea request a command.
- [ ] No consulta repositories directamente.

## Adaptador de salida

- [ ] Implementa un puerto de salida.
- [ ] Encapsula JPA o la tecnología concreta.
- [ ] Mapea dominio y persistencia.
- [ ] No filtra tipos tecnológicos hacia la aplicación.

## Pruebas

- [ ] Existe prueba del caso principal.
- [ ] Existen pruebas de invariantes relevantes.
- [ ] El proyecto compila.
- [ ] `mvnw test` pasa.

## Documentación

- [ ] OpenAPI se actualizó si cambió HTTP.
- [ ] Se agregó migración si cambió el esquema.
- [ ] El PR explica cómo probarlo.

---

# 39. Definition of Done Backend

Una historia Backend está terminada cuando:

- Cumple sus criterios de aceptación.
- Respeta el contrato OpenAPI.
- Las reglas están en el lugar correcto.
- Las pruebas principales pasan.
- No expone entidades JPA.
- No contiene secretos.
- Las migraciones son reproducibles.
- El PR fue revisado por el otro integrante.
- La rama fue integrada en `develop`.
- Frontend puede consumir el endpoint sin interpretar supuestos ocultos.

---

# 40. Decisiones que todavía pueden ajustarse

Este documento no congela detalles que aún deben validarse:

- Librería concreta de PDF.
- Proveedor concreto de almacenamiento.
- Proveedor concreto de correo.
- Implementación final de eventos.
- Escala exacta de redondeo.
- Estrategia definitiva de transacciones.
- Si algunos modelos son entidades internas o Aggregate Roots.
- Si se generarán DTO desde OpenAPI o se escribirán manualmente.
- Herramienta de pruebas de arquitectura.

Cualquier cambio debe conservar las reglas fundamentales:

```text
Dominio independiente
Puertos propiedad de la aplicación
Adaptadores tecnológicos en el exterior
Dependencias hacia adentro
Implementación progresiva
```

---

# 41. Referencias oficiales

- [Alistair Cockburn — Hexagonal Architecture, original 2005 article](https://alistair.cockburn.us/hexagonal-architecture)
- [Eric Evans — DDD Reference](https://www.domainlanguage.com/ddd/reference/)
- [Oracle Java 21 — Record Classes](https://docs.oracle.com/en/java/javase/21/language/records.html)
- [Oracle Java 21 — Sealed Classes and Interfaces](https://docs.oracle.com/en/java/javase/21/language/sealed-classes-and-interfaces.html)
- [Apache Maven — Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout)
- [Apache Maven — Getting Started Guide](https://maven.apache.org/guides/getting-started/index.html)
- [Spring Boot — Testing Spring Boot Applications](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)

---

# 42. Regla final

La arquitectura no se evalúa por la cantidad de interfaces o carpetas.

Se evalúa preguntando:

```text
¿Podemos probar el negocio sin HTTP?
¿Podemos probar el negocio sin PostgreSQL?
¿Podemos cambiar un adaptador sin reescribir el dominio?
¿Los nombres del código expresan el negocio?
¿Cada abstracción tiene una razón real?
¿La persona que implementó la funcionalidad entiende su recorrido?
```

Cuando las respuestas son afirmativas, la arquitectura está cumpliendo su propósito.
