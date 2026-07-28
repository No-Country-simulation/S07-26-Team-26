# User Stories — Ghost Load

**Project:** Ghost Load
**Documento:** Historias de usuario con escenarios BDD (Gherkin)
**Versión:** 1.0

---

## US-01: Captura de email desde calculadora

**Como** operador de data center
**Quiero** ingresar mi email en la calculadora
**Para** acceder a los resultados detallados de capacidad desperdiciada

```gherkin
Scenario: Captura exitosa desde calculadora
    Given estoy en la página de la calculadora
    And he ingresado mis parámetros de facility
    When ingreso "operador@datacenter.com" en el formulario de email
    And hago clic en "Ver resultados completos"
    Then el sistema registra al operador con email "operador@datacenter.com"
    And el estado del operador es "email_capturado"
    And la fuente se registra como "calculator"
    And se envía un magic link a "operador@datacenter.com"
    And veo un mensaje "Revisa tu email para continuar"

Scenario: Email duplicado
    Given existe un operador registrado con email "operador@datacenter.com"
    When ingreso el mismo email en la calculadora
    Then el sistema no crea un duplicado
    And reenvía el magic link al email existente

Scenario: Email inválido
    When ingreso "email-invalido" en el formulario
    Then el sistema muestra "Ingresa un email válido"
    And no se envía ningún magic link
```

---

## US-02: Autenticación por magic link

**Como** operador con email capturado
**Quiero** hacer clic en el magic link que recibí por email
**Para** acceder al benchmark sin tener que crear una cuenta

```gherkin
Scenario: Magic link válido
    Given tengo un email con un magic link
    When hago clic en el enlace
    Then el sistema valida el token JWT
    And inicia mi sesión
    And me redirige al benchmark

Scenario: Magic link expirado
    Given ha pasado más de 15 minutos desde que recibí el email
    When hago clic en el enlace
    Then el sistema muestra "Este enlace ha expirado. Solicita uno nuevo."
```

---

## US-03: Calcular KPIs de capacidad desperdiciada

**Como** operador autenticado
**Quiero** ingresar los parámetros de mi facility
**Para** obtener KPIS cuantificados de mi capacidad desperdiciada

```gherkin
Scenario: Cálculo exitoso de KPIs
    Given estoy autenticado con mi magic link
    When ingreso capacidad_total: 1000, capacidad_usada: 600, costo_kwh: 0.12
    And hago clic en "Calcular"
    Then el sistema retorna:
        | KPI                    | Valor  |
        | capacidad_desperdiciada| 400    |
        | porcentaje_desperdicio | 40%    |
        | costo_anual_estimado   | $42,048|
    And los resultados se guardan asociados a mi cuenta

Scenario: Operador anónimo intenta calcular
    Given no estoy autenticado
    When intento calcular KPIs detallados
    Then el sistema me pide mi email primero
```

---

## US-04: Completar benchmark de madurez

**Como** operador autenticado
**Quiero** responder el cuestionario de madurez
**Para** conocer mi posición en la industria

```gherkin
Scenario: Benchmark completado exitosamente
    Given estoy autenticado
    When respondo las 20 preguntas del cuestionario
    And hago clic en "Enviar respuestas"
    Then el sistema calcula mi score y percentil
    And persiste las respuestas en benchmark_responses
    And se dispara automáticamente la generación de mi PDF
    And veo mi posición: "Estás en el percentil 65 — mejor que el 65% de la industria"

Scenario: Primer operador real vs datos semilla
    Given soy el primer operador real en completar el benchmark
    When el sistema calcula mi percentil
    Then lo hace contra la distribución semilla cargada
    And nunca veo "1 de 1" como resultado
```

---

## US-05: Recibir PDF institucional

**Como** operador que completó el benchmark
**Quiero** recibir mi PDF personalizado
**Para** usarlo internamente para justificar inversiones

```gherkin
Scenario: PDF generado automáticamente
    Given he completado el benchmark
    When el sistema termina de procesar mis resultados
    Then se genera un PDF institucional con:
        | Elemento                  | Incluye                          |
        | Posición en benchmark     | Percentil y score                |
        | KPIs de calculadora       | Capacidad desperdiciada en $     |
        | Logo de la empresa        | Sí                               |
        | Color forest-green        | #2D5A27                          |
        | Color gold                | #C9A84C                          |
        | Bloque de contacto        | Teléfono, LinkedIn, email, link  |
    And recibo un email con el PDF adjunto o link de descarga

Scenario: PDF no se regenera si ya existe
    Given ya tengo un PDF generado
    When intento regenerarlo
    Then el sistema devuelve el PDF existente sin volver a generarlo
```

---

## US-06: Outreach — subir lista de contactos

**Como** miembro del equipo de la startup
**Quiero** subir un archivo CSV con contactos
**Para** crear una campaña de invitación al benchmark

```gherkin
Scenario: Upload exitoso de CSV
    Given tengo un archivo CSV válido con 50 contactos
    When selecciono el archivo y hago clic en "Subir lista"
    Then el sistema crea una nueva campaña con nombre "Campaña Julio 2026"
    And los 50 contactos se registran con estado "pending"
    And veo un resumen: "50 contactos importados correctamente"

Scenario: CSV con emails duplicados
    Given el CSV contiene 2 filas con el mismo email
    When subo el archivo
    Then el sistema respeta la constraint UNIQUE
    And registra solo 1 instancia de ese email
    And notifica: "1 email duplicado ignorado"
```

---

## US-07: Outreach — enviar invitaciones

**Como** miembro del equipo
**Quiero** disparar el envío de invitaciones de una campaña
**Para** que los contactos reciban el link del benchmark sin intervención manual

```gherkin
Scenario: Envío exitoso de invitaciones
    Given una campaña con 50 contactos en estado "pending"
    When hago clic en "Enviar invitaciones"
    Then el sistema encola los 50 envíos
    And cada contacto recibe un email con su link personalizado
    And los contactos pasan a estado "invited"
    And veo el progreso: "50/50 invitaciones enviadas"

Scenario: Idempotencia — no reenviar a ya invitados
    Given 30 contactos en estado "invited" y 20 en "pending"
    When hago clic en "Enviar invitaciones"
    Then solo se envían 20 nuevos emails
    And los 20 pasan a "invited"
```

---

## US-08: Dashboard — ver respuestas acumuladas

**Como** miembro del equipo
**Quiero** ver las respuestas acumuladas del benchmark y calculadora
**Para** entender las tendencias del mercado

```gherkin
Scenario: Dashboard muestra datos agregados
    Given hay 50 operadores en el sistema
    When accedo al dashboard interno
    Then veo:
        | Métrica                | Valor                        |
        | Total operadores       | 50                           |
        | Score promedio          | 72/100                       |
        | Capacidad desperdiciada promedio | 35%                 |
        | Tasa de completitud    | 60% (30/50 completaron benchmark) |
        | Distribución de scores | Gráfico de barras            |
```

---

## US-09: Dashboard — monitorear campañas de outreach

**Como** miembro del equipo
**Quiero** ver el estado de las campañas de outreach
**Para** medir la efectividad de las invitaciones

```gherkin
Scenario: Dashboard de outreach
    Given hay 2 campañas: "Julio" (100 contactos) y "Agosto" (50 contactos)
    When accedo a la vista de outreach
    Then veo para cada campaña:
        | Campaña | Total | Invitados | Abrieron | Completaron | Tasa apertura | Tasa completitud |
        | Julio   | 100   | 100       | 65       | 30          | 65%           | 30%              |
        | Agosto  | 50    | 50        | 40       | 20          | 80%           | 40%              |
```

---

## US-10: Contactar al fundador

**Como** operador que recibió el PDF
**Quiero** ver los datos de contacto del fundador en el PDF
**Para** agendar una llamada para discutir soluciones

```gherkin
Scenario: Bloque de contacto visible en PDF
    Given tengo mi PDF generado
    When lo abro
    Then veo un bloque institucional al final con:
        - Nombre del fundador
        - Teléfono de contacto
        - LinkedIn URL
        - Email
        - Link "Agenda una llamada" (Calendly / link de calendario)
```

---

## US-11: Dashboard — acceso protegido

**Como** miembro del equipo
**Quiero** que el dashboard esté protegido con acceso más fuerte que magic link
**Para** que solo el equipo interno vea los datos acumulados

```gherkin
Scenario: Acceso denegado sin autenticación interna
    Given no estoy autenticado como miembro del equipo
    When intento acceder a /dashboard
    Then el sistema retorna 401
    And me redirige a la página de login interno

Scenario: Acceso concedido con credenciales internas
    Given soy miembro del equipo con credenciales internas
    When ingreso usuario y contraseña
    Then accedo al dashboard con todos los datos
```
