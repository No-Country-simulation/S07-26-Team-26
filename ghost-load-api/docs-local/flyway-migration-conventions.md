# Convención de nombres para migraciones Flyway

Este documento define la convención obligatoria para nombrar y organizar las
migraciones SQL del backend de Ghost Load.

## Regla crítica para el equipo y asistentes de IA

Las migraciones existentes desde `V1.1` hasta `V1.4` están **cerradas e
inmutables**.

En particular, los archivos:

```text
V1.3__create_operators_and_evaluations_tables.sql
V1.4__add_evaluation_token_and_calculator_results.sql
```

deben conservarse exactamente como están.

Está terminantemente prohibido:

- Editarlos.
- Renombrarlos.
- Eliminarlos.
- Dividirlos en varios archivos.
- Reemplazarlos.
- Cambiar su número de versión.
- Intentar corregir retroactivamente su convención de nombres.
- Incluir su modificación en un feature futuro.

Esta regla también aplica si una persona o una IA considera que sus nombres o
su contenido podrían organizarse mejor. **No se modifica el historial de
Flyway.**

Todo feature nuevo debe avanzar desde la siguiente versión disponible y crear
archivos nuevos. Si la última migración es `V1.4`, la siguiente será `V1.5`.

Si se necesita modificar una tabla creada anteriormente, se debe agregar una
nueva migración:

```text
V1.5__alter_nombre_tabla_table.sql
```

Nunca se debe regresar a `V1.3` o `V1.4` para realizar el cambio.

## Formato obligatorio

```text
V1.X__<action>_<object_name>_<object_type>.sql
```

Ejemplo:

```text
V1.5__create_contacts_table.sql
```

Cada parte significa:

| Parte | Significado | Ejemplo |
|---|---|---|
| `V1.X` | Versión única y consecutiva de Flyway | `V1.5` |
| `__` | Dos guiones bajos después de la versión | `__` |
| `action` | Una sola acción SQL | `create` |
| `object_name` | Nombre del objeto afectado en `snake_case` | `contacts` |
| `object_type` | Tipo del objeto | `table` |
| `.sql` | Extensión obligatoria | `.sql` |

## Reglas obligatorias

1. Cada archivo debe realizar **una sola acción principal**.
2. Cada archivo debe afectar **un solo objeto principal**.
3. Se deben utilizar nombres en inglés, minúsculas y `snake_case`.
4. Después de la versión siempre deben existir exactamente dos guiones bajos.
5. El nombre debe terminar indicando el tipo del objeto.
6. No se deben utilizar palabras como `and`, `with`, `setup`, `changes` o
   descripciones que agrupen varios objetos o acciones.
7. No se debe modificar, renombrar o eliminar una migración que ya haya sido
   aplicada por Flyway.
8. `object_name` debe coincidir con el nombre real utilizado en la base de
   datos, sea singular o plural.

## Acciones acordadas

Las acciones utilizadas actualmente por el equipo son:

```text
create
alter
insert
delete
```

Debe aparecer solamente una acción en cada nombre.

Si en el futuro se necesita otra acción, por ejemplo `drop`, debe acordarse con
el equipo antes de utilizarla.

## Tipos de objetos

Algunos tipos válidos son:

```text
table
view
sp
function
index
trigger
```

El tipo debe representar el objeto principal modificado por el archivo.

## Ejemplos correctos

Ejemplos directos de la convención:

```text
V1.5__create_tabla_prueba_table.sql
V1.6__create_sp_prueba_sp.sql
V1.7__alter_tabla_prueba_table.sql
V1.8__alter_sp_prueba_sp.sql
```

### Crear una tabla

```text
V1.5__create_contacts_table.sql
```

### Alterar una tabla

```text
V1.6__alter_contacts_table.sql
```

### Insertar registros iniciales en una tabla

```text
V1.7__insert_contacts_table.sql
```

### Eliminar registros de una tabla

```text
V1.8__delete_contacts_table.sql
```

### Crear una vista

```text
V1.9__create_active_contacts_view.sql
```

### Alterar una vista

```text
V1.10__alter_active_contacts_view.sql
```

### Crear un procedimiento almacenado

```text
V1.11__create_sp_send_invitations_sp.sql
```

### Alterar un procedimiento almacenado

```text
V1.12__alter_sp_send_invitations_sp.sql
```

### Crear un índice

```text
V1.13__create_idx_contacts_email_index.sql
```

## Un archivo no debe agrupar objetos

Ejemplo incorrecto para una migración futura:

```text
V1.X__create_first_table_and_second_table.sql
```

Este nombre agrupa dos tablas diferentes. Un índice creado explícitamente
también es otro objeto y debe tener su propia migración.

Para un feature nuevo debe separarse así, utilizando versiones nuevas y
consecutivas:

```text
V1.X__create_first_table_table.sql
V1.Y__create_second_table_table.sql
V1.Z__create_idx_second_table_id_index.sql
```

Las restricciones declaradas dentro del mismo `CREATE TABLE`, como su clave
primaria o sus claves foráneas, sí pueden permanecer en la migración de esa
tabla. Un `CREATE INDEX`, `CREATE VIEW` o `CREATE TRIGGER` independiente debe
tener su propio archivo.

## Un archivo no debe agrupar acciones

Ejemplo incorrecto para una migración futura:

```text
V1.X__add_column_and_create_results.sql
```

Ese archivo realiza dos operaciones diferentes:

1. Altera una tabla.
2. Crea otra tabla.

En un feature nuevo debe separarse así:

```text
V1.X__alter_evaluations_table.sql
V1.Y__create_calculator_results_table.sql
```

También debe utilizarse `alter` en lugar de `add`, porque la acción principal
se realiza mediante `ALTER TABLE`.

## Estado definitivo de V1.3 y V1.4

Las migraciones actuales:

```text
V1.3__create_operators_and_evaluations_tables.sql
V1.4__add_evaluation_token_and_calculator_results.sql
```

se conservarán como parte del historial existente. No se abrirá una tarea para
corregir sus nombres ni para reorganizar su contenido.

Ya fueron incorporadas a `develop` y pueden haber sido aplicadas en bases de
datos locales o compartidas. Por esa razón:

- No deben renombrarse.
- No deben editarse.
- No deben eliminarse.
- No deben dividirse.
- No deben reemplazarse.
- Se conservarán como excepciones históricas.
- La convención será obligatoria desde la siguiente migración.

Si en el futuro se necesita modificar alguno de los objetos creados por esas
migraciones, se debe crear una migración nueva con la siguiente versión
disponible.

Ejemplo:

```text
V1.5__alter_evaluations_table.sql
```

## Selección de la versión

Antes de crear una migración:

1. Actualizar la rama desde `develop`.
2. Revisar `src/main/resources/db/migration`.
3. Identificar la versión más alta.
4. Utilizar la siguiente versión disponible.

Ejemplo:

```text
Última migración: V1.4
Nueva migración:  V1.5
```

Si dos ramas utilizan la misma versión, la persona que actualice su rama en
último lugar debe renumerar su migración antes de fusionarla.

## Lista de verificación para Pull Requests

Antes de subir una migración:

- [ ] El número de versión es único.
- [ ] Existen dos guiones bajos después de la versión.
- [ ] El nombre contiene una sola acción.
- [ ] El archivo modifica un solo objeto principal.
- [ ] El nombre del objeto está en inglés y `snake_case`.
- [ ] El nombre termina con el tipo del objeto.
- [ ] La migración no modifica una versión ya aplicada.
- [ ] Flyway ejecuta la migración correctamente.
- [ ] Hibernate valida el esquema correctamente.

## Regla resumida

```text
Una migración = una versión + una acción + un objeto + un tipo
```

```text
V1.X__action_object_name_object_type.sql
```
