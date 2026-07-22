# 🗂️ Endpoints de la API

#### Formato general de errores

El `errorCode` será siempre `null`por default. En caso de que se requiera que la sesión del usuario
sea finalizada, tendrá el valor `SESSION_INVALIDATED`.

Los JSON de error están estandarizados con este formato:

```JSON
{
  "status": int,
  "error": String,
  "message": String,
  "errorCode": String,
  "timestamp": String ("2026-06-10T15:46:08.0424397")
}
```

<details>
<summary><b>Ejemplo</b></summary>

```JSON
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Credenciales incorrectas (DNI o contraseña inválidos)",
  "errorCode": null,
  "timestamp": "2026-06-10T15:46:08.0424397"
}
```
</details>

---

# 🔐 Iniciar sesión [acceso público]

`POST /api/auth/login`

Permite a los usuarios autenticarse en el sistema mediante su DNI y contraseña.

- En caso de que el `dni` sea de 7 caracteres, podrá ingresar usando tanto el `dni` de forma normal
como con el `0` (cero) al comienzo. Ejemplo: `dni 1234567`, formas de ingresarlo aceptadas:
`1234567` o `01234567`.

**En caso de loguear un usuario con `dni` de 7 caracteres con contraseña `cfp+dni` (únicamente cuando se tiene
el rol `CHANGE_PASSWORD`) podrá usar las siguientes
formas:**

| `dni`      | `password`    |
|------------|---------------|
| `1234567`  | `cfp1234567`  |
| `1234567`  | `cfp01234567` |
| `01234567` | `cfp1234567`  |
| `01234567` | `cfp01234567` |

Estas combinaciones aceptadas con y sin `0` (cero) al inicio son para que el usuario no tenga
dificultades a la hora de loguear si no sabe qué `dni` usar, si el suyo o el que viene con `0`.

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`dni` String, requerido, entre 7 y 9 caracteres numéricos.

`password` String, requerido, entre 8 y 60 caracteres.

```JSON
{
  "dni": "12345678",
  "password": "admin123"
}
```
</td></tr></table>
</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK`

`accessToken` String, contiene el token del usuario.

`tokenType` String, siempre devuelve "Bearer" que es el tipo de token.

```JSON
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si el cuerpo JSON no cumple las restricciones estructurales (dni vacío, etc.).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si el dni o la password son incorrectos.
- Si la cuenta se encuentra temporalmente desactivada.

</td></tr></table>
</details> 

---

# 👥 Control de Usuarios

#### Nombres y apellidos reservadados por el sistema

| `nombre`  | `apellido`  |
|-----------|-------------|
| `USUARIO` | `ELIMINADO` |
| `SISTEMA` | `PROCESO`   |


#### Formato de respuesta de usuarios

El JSON de respuesta de usuario sigue este patrón:

```JSON
{
  "id": Long,
  "dni": String,
  "rol": String,
  "nombre": String,
  "apellido": String,
  "activo": Boolean
}
```

<details><summary>Ejemplo</summary>

```JSON
{
  "id": 1,
  "dni": "12345678",
  "rol": "ADMIN",
  "nombre": "nombre",
  "apellido": "apellido",
  "activo": true
}
```
</details>

## 🟢 Registrar un usuario [solo para OWNER o ADMIN]

`POST /api/usuarios/registrar`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `USUARIO_CREADO`

Permite que usuarios con el rol `OWNER` o `ADMIN` puedan registrar un usuario nuevo en el sistema.

- Para registrarlo se ingresará su `dni`, `nombre`, `apellido` y `rol`.
- El `nombre` y `apellido` serán normalizados poniendo la primera letra de cada palabra en mayúscula y
quitando los espacios adicionales.
- El `dni` será normalizado en caso de tener 7 caracteres y se le asignará un `0` (cero) al comienzo.
Ejemplo: `01234567`.
- El usuario creado tendrá rol `CHANGE_PASSWORD` y no obtendrá su rol real hasta que cambie su
contraseña.
- Al usuario creado se le asignará como contraseña `cfp + dni`. Ejemplo: `cfp12345678`.
- `OWNER` puede crear usuarios con los roles `ADMIN` y `PERSONAL`.
- `ADMIN` puede crear usuarios con el rol `PERSONAL`.

<details>
<summary><b>🔑 Encabezados válidos (Headers)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`
* `Authorization`: `Bearer <token_de_admin>`

</details> 

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`dni` String, requerido, entre 7 y 9 caracteres numéricos.

`nombre` String, requerido, 2 a 50 caracteres (solo letras, espacios, `-` y `'`), debe contener al
menos una letra.

`apellido` String, requerido, 2 a 50 caracteres (solo letras, espacios, `-` y `'`), debe contener al
menos una letra.

`rol` String, requerido, solo se admite `ADMIN` y `PERSONAL`.

```JSON
{
  "dni": "12345678",
  "nombre": "nombre",
  "apellido": "apellido",
  "rol": "PERSONAL"
}
```

</td></tr></table>
</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `201 CREATED` + 
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
con la información del usuario creado.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo JSON no cumple las restricciones estructurales (dni vacío, etc.).
- Si se intenta enviar en el campo `rol` los valores `OWNER`, `CHANGE_PASSWORD` o `SYSTEM`.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER` / `ADMIN`).
- Si un `ADMIN` intenta registrar a otro `ADMIN` u `OWNER`.
- [nombre o apellido](#nombres-y-apellidos-reservadados-por-el-sistema)
está reservado por el sistema.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con 
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `409 CONFLICT` +
[JSON error](#formato-general-de-errores)
si el `dni` ya se encuentra registrado en el sistema.

</td></tr></table>
</details>

## 🟢 Listar usuarios con filtros y paginación [solo para OWNER o ADMIN]

`GET /api/usuarios`

Permite que usuarios con el rol `OWNER` o `ADMIN` puedan ver la lista completa de usuarios
registrados.

- Podrá filtrarlos por: `dni`, `nombre`, `apellido`, `activo`, `rol`.
- Se utiliza la paginación para mostrarlo, por lo que se puede definir cuántos (`size`) usuarios ver
por página (`page`).

<details>
<summary><b>🔑 Encabezados válidos (Headers)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`
* `Authorization`: `Bearer <token_de_admin>`

</details> 

<details>
<summary><b>❓ Parámetros de Consulta (Query Parameters)</b></summary>

**Todos los filtros son opcionales**.
Se añaden a la URL (ej. `?nombre=Juan&size=5`).
* `dni` String - Filtro por coincidencia parcial.
* `nombre` String - Filtro por coincidencia parcial (no distingue mayúsculas/minúsculas).
* `apellido` String - Filtro por coincidencia parcial (no distingue mayúsculas/minúsculas).
* `activo` Boolean - Filtro exacto (`true` o `false`).
* `rol`: String - Filtro exacto (`OWNER`, `ADMIN`, `PERSONAL`, `CHANGE_PASSWORD`).
* `page` int - Número de página, empieza en 0 (Por defecto: 0).
* `size` int - Cantidad de registros por página (Por defecto: 10).

</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` + JSON de estructura de página de Spring. 

Cada usuario tendrá el formato del
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios).

<details>
<summary><b>🧾 JSON de estructura de página de Spring</b></summary>

```JSON
{
  "content": [
    {
      "id": 1,
      "dni": "12345678",
      "rol": "ADMIN",
      "nombre": "Nombre1",
      "apellido": "Apellido1",
      "activo": true
    },
    {
      "id": 2,
      "dni": "23456789",
      "rol": "PERSONAL",
      "nombre": "Nombre2",
      "apellido": "Apellido2",
      "activo": true
    }
  ],
  "empty": false,
  "first": true,
  "last": true,
  "number": 0,
  "numberOfElements": 2,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 10,
    "paged": true,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "unpaged": false
  },
  "size": 10,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "totalElements": 2,
  "totalPages": 1
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si los tipos de datos enviados en los parámetros son incompatibles (ejemplo: `?activo=hola`).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER` / `ADMIN`).
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

</td></tr></table>
</details>

## 🟢 Restablecer contraseña de otro usuario [solo para OWNER o ADMIN]

`PUT /api/usuarios/{id}/restablecer`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `PASSWORD_RESTABLECIDA`

Permite que usuarios con el rol `OWNER` o `ADMIN` puedan restablecer la contraseña de otro usuario
a `cfp + dni` (ejemplo: usuario con `dni=12345678` obtiene contraseña `cfp12345678`) y le asigna el
rol `CHANGE_PASSWORD` hasta que cambie su propia contraseña.

- Un usuario con rol `OWNER` puede restablecer la contraseña de `ADMIN` y `PERSONAL`.
- Un usuario con rol `ADMIN` solo puede restablecer la contraseña de `PERSONAL`.

<details>
<summary><b>🔑 Encabezados válidos (Headers)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`
* `Authorization`: `Bearer <token_de_admin>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido. <br>
ID del usuario al que se le va a restablecer la contraseña.

</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
con la información del usuario al que se le cambió la contraseña.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el `id` en el path variable no cumple con las restricciones.
- Si se intenta modificar a un usuario `eliminado` (ofuscado).
- Si se intenta modificar al usuario con rol `SYSTEM`.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER` / `ADMIN`).
- Si se intentó modificar al usuario con rol `OWNER` o `CHANGE_PASSWORD`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
- Si no existe un usuario registrado con el `id` solicitado.
- Si se intenta modificar al usuario con rol `SYSTEM`.

</td></tr></table>
</details>

## 🟢 Cambiar estado de la cuenta de otro usuario [solo para OWNER o ADMIN]

`PUT /api/usuarios/{id}/cambiar-activo`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `ESTADO_ACTIVO_MODIFICADO`

Permite que usuarios con el rol `OWNER` o `ADMIN` puedan cambiar el flag `activo` de otra cuenta.

- Un usuario con rol `OWNER` puede cambiar el estado `activo` de `ADMIN` y `PERSONAL`.
- Un usuario con el rol `ADMIN` puede cambiar el estado `activo` de `PERSONAL`.

<details>
<summary><b>🔑 Encabezados válidos (Headers)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`
* `Authorization`: `Bearer <token_de_admin>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido. <br>
ID del usuario al que se le va a cambiar el estado `activo` de la cuenta.

</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario al que se le cambió el flag `activo`.

<details>
<summary><b>🧾 JSON de detalles de auditoría</b></summary>

[JSON respuesta de auditoria](#formato-respuesta-de-auditoria)

```JSON
{
  "usuarioAfectadoId": Long,
  "activoAnterior": Boolean,
  "activoNuevo": Boolean
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el `id` en el path variable no cumple con las restricciones.
- Si se intenta modificar a un usuario `eliminado` (ofuscado).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER` / `ADMIN`).
- Si se intenta modificar a un usuario con rol `OWNER`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
- Si no existe un usuario registrado con el `id` solicitado.
- Si se intenta modificar al usuario con rol `SYSTEM`.

</td></tr></table>
</details> 

## 🟢 Cambiar contraseña propia [usuarios logueados]

`PUT /api/usuarios/me/password`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `PASSWORD_CAMBIADA`

Permite que únicamente un usuario logueado pueda cambiar su propia contraseña. Si el usuario tenía
el rol `CHANGE_PASSWORD` entonces recuperará su rol normal.

- La nueva contraseña no puede ser su `dni`.
- La nueva contraseña no puede ser la contraseña por defecto `cfp + dni`. En caso de que el `dni`
sea de 7 caracteres, esta restricción aplica tanto para la versión con el `0` (cero) al comienzo 
como sin él (ejemplo: se prohíbe tanto `cfp1234567` como `cfp01234567`).
- Si el usuario tiene rol `CHANGE_PASSWORD` entonces en su contraseña actual `cfp+dni` podrá
ingresar el `dni` con o sin `0` al comienzo (ejemplo: `cfp1234567` / `cfp01234567`).

<details>
<summary><b>🔑 Encabezados válidos (Headers)</b></summary>

* `Authorization`: `Bearer <token_de_usuario_logueado>`

</details>

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`oldPassword` String, requerido, entre 8 y 60 caracteres.

`newPassword` String, requerido, entre 8 y 60 caracteres.

```JSON
{
  "oldPassword": "passwordActual",
  "newPassword": "passwordNueva"
}
```

</td></tr></table>
</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` (sin body)

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo de la petición no cumple las restricciones.
- Si la nueva contraseña es el `dni` o `cfp + dni`.
- Si la `oldPassword` es incorrecta.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

</td></tr></table>
</details>

## 🟢 Cambiar rol de otro usuario [solo para OWNER]

`PUT /api/usuarios/{id}/cambiar-rol`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `ROL_MODIFICADO`

Permite que únicamente usuarios con el rol `OWNER` puedan cambiar el `rol` de otro usuario.

- Solo puede cambiar el rol de usuarios con el rol `ADMIN` o `PERSONAL`.
- Solo puede asignar los roles `ADMIN` y `PERSONAL`.
- No puede asignar el mismo rol que el otro usuario ya tiene.

<details>
<summary><b>🔑 Encabezado (Header)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido. <br>
ID del usuario al que se le va a cambiar el `rol`.

</details>

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`rol` String, requerido, solo se admite `ADMIN` y `PERSONAL`.

```JSON
{
  "rol": "ADMIN"
}
```

</td></tr></table>
</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario al que se le cambió el rol.

<details>
<summary><b>🧾 JSON de detalles de auditoría</b></summary>

[JSON respuesta de auditoria](#formato-respuesta-de-auditoria)

```JSON
{
  "usuarioAfectadoId": Long,
  "rolAnterior": String,
  "rolNuevo": String,
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el `id` enviado en el path variable no tiene un formato numérico válido.
- Si el campo `rol` en el cuerpo está vacío o no es un rol válido del sistema.
- Si se intenta modificar a un usuario `eliminado` (ofuscado).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`.
- Si se intenta modificar a un usuario que es `OWNER` o se encuentra en estado `CHANGE_PASSWORD`.
- Si el usuario ya cuenta con el rol que se está intentando asignar.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
- Si no existe un usuario con el `id` solicitado.
- Si se intenta modificar al usuario con rol `SYSTEM`.

</td></tr></table>
</details>

## 🟢 Obtener mi propio perfil [usuario logueado]

`GET /api/usuarios/me`

Permite que únicamente un usuario logueado pueda ver su propio perfil.

- Los usuarios con el rol `CHANGE_PASSWORD` tendrán error 403 al intentar usar el endpoint.

<details>
<summary><b>🔑 Encabezados válidos (Headers)</b></summary>

* `Authorization`: `Bearer <token_de_usuario_owner>`
* `Authorization`: `Bearer <token_de_usuario_admin>`
* `Authorization`: `Bearer <token_de_usuario_personal>`

</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario logueado.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario tiene rol `CHANGE_PASSWORD`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

</td></tr></table>
</details>

## 🟢 Obtener el perfil de otro usuario [solo para OWNER o ADMIN]

`GET /api/usuarios/{id}`

Permite que usuarios con el rol `OWNER` o `ADMIN` puedan revisar el perfil de otros usuarios.

- El perfil de un `OWNER` solo puede ser visto por un usuario con rol `OWNER`.

<details>
<summary><b>🔑 Encabezados válidos (Headers)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`
* `Authorization`: `Bearer <token_de_admin>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido. <br>
ID del usuario al que se le quiere ver el perfil.

</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario solicitado.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si el `id` enviado en el path variable no tiene un formato numérico válido.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER` / `ADMIN`).
- Si un `ADMIN` intenta ver el perfil de un `OWNER`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un usuario registrado con el `id` solicitado.

</td></tr></table>
</details>

## 🟢 Eliminar un usuario (ofuscar) [solo para OWNER]

`POST /api/usuarios/{id}/eliminar`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `USUARIO_ELIMINADO`

Permite que únicamente usuarios con el rol `OWNER` puedan "eliminar" un usuario.

- El usuario eliminado no es borrado de la base de datos para conservar su historial.
- Es imposible recuperar los datos del usuario eliminado para cumplir con la **Ley 25.326 de
Protección de Datos Personales** de Argentina. 
- Al usuario eliminado se le ofuscan los datos da la siguiente manera:
  - `dni` = `00000000`.
  - `nombre` = `USUARIO`.
  - `apellido` = `ELIMINADO`.
  - `activo` = `false`.
  - `eliminado` = `true`.
- En los registros de la auditoría, el `dni`, `nombre` y `apellido` del usuario pasan a estar ofuscados.

<details>
<summary><b>🔑 Encabezado (Header)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido. <br>
  ID del usuario que se quiere eliminar.

</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `204 NO CONTENT` (sin body).

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el `id` enviado en el path variable no tiene un formato numérico válido.
- Si se intenta modificar a un usuario `eliminado` (ofuscado).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`.
- Si se intenta eliminar un usuario con el rol `OWNER`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
- Si no existe un usuario registrado con el `id` solicitado.
- Si se intenta modificar al usuario con rol `SYSTEM`.

</td></tr></table>
</details>

## 🟢 Recuperar contraseña del OWNER [acceso público]

`POST /api/usuarios/recuperar-owner`


📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `PASSWORD_OWNER_RECUPERADA`

Permite que un `OWNER` pueda recuperar su contraseña utilizando una clave secreta que se encuentra
oculta en las variables de entorno, por lo que debe tenerla anotada en forma física.

- Al ingresar su `dni`, si este tiene 7 caracteres, podrá ingresarlo con los 7 o con un `0` (cero) al
comienzo. Ejemplo de valores válidos: `1234567`/`01234567`.
- La nueva contraseña no puede ser su propio `dni`.
- La nueva contraseña no puede ser la default `cfp + dni` (ejemplo: `cfp12345678`).

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`dni` String, requerido, entre 7 y 9 caracteres numéricos.

`recoveryPassword` String, requerido, entre 8 y 60 caracteres.

`nuevaPassword` String, requerido, entre 8 y 60 caracteres.

```JSON
{
  "dni": "12345678",
  "recoveryPassword": "contraseña secreta de recuperacion",
  "nuevaPassword": "nueva_password"
}
```

</td></tr></table>
</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `204 NO CONTENT` (sin body).

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo de la petición no cumple las restricciones.
- Si la nueva contraseña es el `dni` o `cfp + dni`.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si la `recoveryPassword` es incorrecta.
- Si el `dni` no pertenece a un `OWNER`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un usuario registrado con el `dni` solicitado.

</td></tr></table>
</details> 

## 🟢 Transferir OWNER [solo para OWNER]

`POST /api/usuarios/{id}/transferir-owner`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `OWNER_TRANSFERIDO`

Permite que un `OWNER` pueda transferir su rol a otro usuario registrado en el sistema.

- El usuario objetivo no debe tener rol `CHANGE_PASSWORD`.
- El `OWNER` que transfiere su rol pasa a tener rol `ADMIN`.
- Para realizar la acción, el `OWNER` debe escribir su propia `password` para verificar que es él.

<details>
<summary><b>🔑 Encabezado (Header)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido. <br>
ID del usuario que se le quiere asignar el rol `OWNER`.

</details>

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`password` String, requerido, entre 8 y 60 caracteres.

```JSON
{
  "password": "contraseña"
}
```

</td></tr></table>
</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` (sin body).

<details>
<summary><b>🧾 JSON de detalles de auditoría</b></summary>

[JSON respuesta de auditoria](#formato-respuesta-de-auditoria)

```JSON
{
  "usuarioAfectadoId": Long,
  "rolAnterior": String,
  "rolNuevo": String,
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo de la petición no cumple las restricciones.
- Si el `id` enviado en el path variable no tiene un formato numérico válido.
- Si se intenta modificar a un usuario `eliminado` (ofuscado).
- Si se intenta modificar a un usuario con `activo = false`.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`.
- Si el usuario objetivo tiene el rol `CHANGE_PASSWORD`.
- Si se intenta transferirse el rol a sí mismo.
- Si la `password` en el cuerpo de la petición es incorrecta. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
- Si no existe un usuario registrado con el `id` solicitado.
- Si se intenta modificar al usuario con rol `SYSTEM`.

</td></tr></table>
</details>

## 🟢 Cambiar DNI [Solo OWNER, ADMIN o PERSONAL]

`PUT /api/usuarios/{id}/cambiar-dni`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `DNI_EDITADO`

Permite que usuarios con el rol `OWNER`, `ADMIN` o `PERSONAL` puedan cambiar el `dni` de sí mismos u
otros:

- `OWNER` puede cambiar su propio `dni`, el de `ADMIN` y el de `PERSONAL`.
- `ADMIN` puede cambiar su propio `DNI` pero no el de otro `ADMIN`, también puede cambiar el de
`PERSONAL`.
- `PERSONAL` solo puede cambiar su propio `dni`.
- Nadie puede cambiar el `dni` de usuarios con rol `CHANGE_PASSWORD`.
- El nuevo `dni` no puede ser el mismo que ya tiene el usuario.
- Si el nuevo `dni` es de 7 caracteres, podrá ingresarlo tanto en su forma normal como con el
`0` (cero) al comienzo. Ejemplo: `1234567`/`01234567`.

<details>
<summary><b>🔑 Encabezados válidos (Header)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`
* `Authorization`: `Bearer <token_de_admin>`
* `Authorization`: `Bearer <token_de_personal>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido. <br>
ID del usuario que se le quiere cambiar el `dni`.

</details>

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`dni` String, requerido, entre 7 y 9 caracteres numéricos.

```JSON
{
  "dni": "12345678"
}
```

</td></tr></table>
</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario al que se le cambió el `dni`.

<details>
<summary><b>🧾 JSON de detalles de auditoría</b></summary>

[JSON respuesta de auditoria](#formato-respuesta-de-auditoria)

```JSON
{
  "usuarioAfectadoId": Long,
  "dniAnterior": String,
  "dniNuevo": String,
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo de la petición no cumple las restricciones.
- Si el `id` enviado en el path variable no tiene un formato numérico válido.
- Si se intenta modificar a un usuario `eliminado` (ofuscado).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER`/`ADMIN`/`PERSONAL`).
- Si se intenta asignar el mismo `dni` que el usuario ya tiene.
- Si se intenta asignar un `dni` ya registrado en el sistema.
- Si se intenta modificar a un usuario con el rol `CHANGE_PASSWORD`.
- Si alguien que no es `OWNER` intenta modificar a un usuario con rol `OWNER`.
- Si un `ADMIN` intenta modificar a otro con el mismo rol.
- Si un `PERSONAL` intenta modificar a alguien que no sea sí mismo.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
- Si no existe un usuario registrado con el `id` solicitado.
- Si se intenta modificar al usuario con rol `SYSTEM`.

</td></tr></table>
</details>

## 🟢 Cambiar nombre y apellido [Solo OWNER, ADMIN o PERSONAL]

`PUT /api/usuarios/{id}/cambiar-nombre-apellido`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `NOMBRE_APELLIDO_EDITADO`

Permite que usuarios con el rol `OWNER`, `ADMIN` o `PERSONAL` puedan cambiar el `nombre` y `apellido`
de sí mismos u otros:

- `OWNER` puede cambiar su propio `nombre` y `apellido`, el de `ADMIN` y el de `PERSONAL`.
- `ADMIN` puede cambiar su propio `nombre` y `apellido` pero no el de otro `ADMIN`, también puede
cambiar el de `PERSONAL`.
- `PERSONAL` solo puede cambiar su propio `nombre` y `apellido`.
- Nadie puede cambiar el `nombre` y `apellido` de usuarios con el rol `CHANGE_PASSWORD`.
- El nuevo `nombre` y `apellido` no pueden ser ambos al mismo tiempo los mismos que ya poseía.
- El `nombre` y `apellido` será normalizado poniendo la primera letra de cada palabra en mayúscula y
quitando los espacios adicionales.

<details>
<summary><b>🔑 Encabezados válidos (Header)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`
* `Authorization`: `Bearer <token_de_admin>`
* `Authorization`: `Bearer <token_de_personal>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido. <br>
ID del usuario que se le quiere cambiar el `nombre` y `apellido`.

</details>

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`nombre`: String, requerido, 2 a 50 caracteres (solo letras, espacios, `-` y `'`), debe contener al menos una letra.

`apellido`: String, requerido, 2 a 50 caracteres (solo letras, espacios, `-` y `'`), debe contener al menos una letra.

```JSON
{
  "nombre": "Nombre Persona",
  "apellido": "Apellido De La Misma"
}
```

</td></tr></table>
</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario al que se le cambió el `nombre` y `apellido`.

<details>
<summary><b>🧾 JSON de detalles de auditoría</b></summary>

[JSON respuesta de auditoria](#formato-respuesta-de-auditoria)

Solo aparecerán los datos cambiados, ya sea solo nombre, solo apellido o ambos.

```JSON
{
  "usuarioAfectadoId": Long,
  "nombreAnterior": String,
  "nombreNuevo": String,
  "apellidoAnterior": String,
  "apellidoNuevo": String,
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo de la petición no cumple las restricciones.
- Si el `id` enviado en el path variable no tiene un formato numérico válido.
- Si se intenta modificar a un usuario `eliminado` (ofuscado).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER`/`ADMIN`/`PERSONAL`).
- Si se intenta asignar el mismo `nombre` y `apellido` (ambos) que el usuario ya tiene.
- Si se intenta modificar a un usuario con el rol `CHANGE_PASSWORD`.
- Si alguien que no es `OWNER` intenta modificar a un usuario con rol `OWNER`.
- Si un `ADMIN` intenta modificar a otro con el mismo rol.
- Si un `PERSONAL` intenta modificar a alguien que no sea sí mismo.
- [nombre o apellido](#nombres-y-apellidos-reservadados-por-el-sistema)
  está reservado por el sistema.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
- Si no existe un usuario registrado con el `id` solicitado.
- Si se intenta modificar al usuario con rol `SYSTEM`.

</td></tr></table>
</details>

---

# 📓 Auditoría

#### Formato respuesta de auditoria

Los JSON de respuesta de auditoría están estandarizados con este formato:

```JSON
{
  "id": Long,
  "fechaAccion": String ("2026-06-10T15:46:08.0424397"),
  "operadorId": Long,
  "operadorNombre": String,
  "operadorDni": String,
  "operadorRol": String,
  "afectadoId": Long,
  "afectadoNombre": String,
  "afectadoDni": String,
  "reporteId": Long,
  "reporteEspacioId": Long,
  "reporteTipo": String,
  "accion": String,
  "detalles": Object (JSON dinámico)
}
```
`operadorNombre` y  `afectadoNombre` están compuestos por `nombre + apellido` de los usuarios.

<details>
<summary><b>Ejemplo cuando afecta a otro usuario - con detalles 'JSON'</b></summary>

- En este ejemplo el `OWNER` le cambia el `nombre` y `apellido` a otro usuario.
- Se genera un `JSON` con los `detalles` históricos del cambio.

```JSON
{
  "id": 1,
  "fechaAccion": "2026-07-06T20:38:39.055161",
  "operadorId": 2,
  "operadorNombre": "Dueño Total",
  "operadorDni": "11112222",
  "operadorRol": "OWNER",
  "afectadoId": 4,
  "afectadoNombre": "Pepe Ortega",
  "afectadoDni": "23456789",
  "reporteId": null,
  "reporteEspacioId": null,
  "reporteTipo": null,
  "accion": "NOMBRE_APELLIDO_EDITADO",
  "detalles": {
    "usuarioAfectadoId": 4,
    "nombreAnterior": "Personal",
    "nombreNuevo": "Pepe",
    "apellidoAnterior": "Institucional",
    "apellidoNuevo": "Ortega"
  }
}
```
</details>

<details>
<summary><b>Ejemplo cuando no afecta a otro usuario - sin detalles</b></summary>

- En este ejemplo el `SYSTEM` cierra un reporte de forma automática.


```JSON
{
  "id": 2,
  "fechaAccion": "2026-07-15T09:18:43.059593",
  "operadorId": 1,
  "operadorNombre": "SISTEMA PROCESO",
  "operadorDni": "SYSTEM01",
  "operadorRol": "SYSTEM",
  "afectadoId": null,
  "afectadoNombre": "N/A (Auto-acción)",
  "afectadoDni": "N/A",
  "reporteId": 1,
  "reporteEspacioId": 1,
  "reporteTipo": "ACCESO_BLOQUEADO",
  "accion": "REPORTE_CERRADO_AUTOMATICO",
  "detalles": null
}
```

</details>

## 🟢 Listar historial de acciones [solo OWNER]

`GET /api/auditorias`

Permite que únicamente usuarios con el rol `OWNER` puedan ver el historial de actividad de los
usuarios.

- Podrá ordenarlos por `fechaAccion` con el parámetro `sort` (`desc` / `asc`).
- Podrá filtrarlos por `usuarioId`, `accion` (se puede seleccionar más de una), `reporteId`.


- Valores de `accion` válidos:
  - `USUARIO_CREADO`
  - `PASSWORD_RESTABLECIDA`
  - `ESTADO_ACTIVO_MODIFICADO`
  - `PASSWORD_CAMBIADA`
  - `ROL_MODIFICADO`
  - `USUARIO_ELIMINADO`
  - `PASSWORD_OWNER_RECUPERADA`
  - `OWNER_TRANSFERIDO`
  - `DNI_EDITADO`
  - `NOMBRE_APELLIDO_EDITADO`
  - `REPORTE_CREADO`
  - `REPORTE_ATENDIDO`
  - `REPORTE_MODIFICADO`
  - `REPORTE_TIEMPO_ELIMINADO`
  - `REPORTE_CERRADO`
  - `REPORTE_CERRADO_AUTOMATICO`

<details>
<summary><b>🔑 Encabezado (Header)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`

</details>

<details>
<summary><b>❓ Parámetros de Consulta (Query Parameters)</b></summary>

**Todos los filtros son opcionales**.
Se añaden a la URL (ej. `?usuarioId=2&size=5`).
* `usuarioId` int - Filtro exacto (`id` del usuario del cual se busca el historial).
* `accion` String - Filtro exacto (solo acepta los valores listados).
* `reporteId` Long - Filtro exacto.
* `page` int - Número de página, empieza en 0 (Por defecto: 0).
* `size` int - Cantidad de registros por página (Por defecto: 20).
* `sort` String - Orden de la lista, si no se añade `asc` (ascendente) siempre será por defecto
`desc` (descendente).

</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` + JSON de estructura de página de Spring.

Cada acción tendrá el formato del 
[JSON respuesta de auditoria](#formato-respuesta-de-auditoria)

<details>
<summary><b>🧾 JSON de estructura de página de Spring</b></summary>

```JSON
{
  "content": [
    {
      "id": 2,
      "fechaAccion": "2026-07-15T09:18:43.059593",
      "operadorId": 1,
      "operadorNombre": "SISTEMA PROCESO",
      "operadorDni": "SYSTEM01",
      "operadorRol": "SYSTEM",
      "afectadoId": null,
      "afectadoNombre": "N/A (Auto-acción)",
      "afectadoDni": "N/A",
      "reporteId": 1,
      "reporteEspacioId": 1,
      "reporteTipo": "ACCESO_BLOQUEADO",
      "accion": "REPORTE_CERRADO_AUTOMATICO",
      "detalles": null
    },
    {
      "id": 1,
      "fechaAccion": "2026-07-06T20:38:39.055161",
      "operadorId": 2,
      "operadorNombre": "Dueño Total",
      "operadorDni": "11112222",
      "operadorRol": "OWNER",
      "afectadoId": 4,
      "afectadoNombre": "Pepe Ortega",
      "afectadoDni": "23456789",
      "reporteId": null,
      "reporteEspacioId": null,
      "reporteTipo": null,
      "accion": "NOMBRE_APELLIDO_EDITADO",
      "detalles": {
        "usuarioAfectadoId": 4,
        "nombreAnterior": "Personal",
        "nombreNuevo": "Pepe",
        "apellidoAnterior": "Institucional",
        "apellidoNuevo": "Ortega"
      }
    }
  ],
  "empty": false,
  "first": true,
  "last": true,
  "number": 0,
  "numberOfElements": 3,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 20,
    "paged": true,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "unpaged": false
  },
  "size": 20,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "totalElements": 3,
  "totalPages": 1
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si los tipos de datos enviados en los parámetros son incompatibles (ejemplo: `?usuarioId=alfanumerico`).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

</td></tr></table>
</details>

---

# ⚠️ Reportes

#### Formato de respuesta de reporte

El JSON de respuesta de reporte sigue este patrón:

```JSON
{
  "id": Long,
  "atendidoPorId": Long,
  "atendidoPorNombre": String,
  "espacioId": Long,
  "nombreEspacio": String,
  "descripcion": String,
  "estadoReporte": String,
  "tipoReporte": String,
  "minutosEstimados": Integer,
  "fechaVencimiento": String ("2026-06-10T15:46:08.0424397"),
  "fechaCreacion": String ("2026-06-10T15:46:08.0424397")
}
```

<details>
<summary><b>Ejemplo</b></summary>

```JSON
{
  "id": 1,
  "espacioId": 1,
  "nombreEspacio": "Laboratorio de Computación 1",
  "descripcion": "Suelo mojado",
  "estadoReporte": "RESUELTO",
  "tipoReporte": "ACCESO_BLOQUEADO",
  "minutosEstimados": null,
  "fechaVencimiento": null,
  "fechaCreacion": "2026-07-06T20:44:30.055448"
}
```
</details>

## 🟢 Crear un reporte [Solo OWNER, ADMIN o PERSONAL]

`POST /api/reportes`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `REPORTE_CREADO`

Permite que un usuario logueado como `OWNER`, `ADMIN` o `PERSONAL` pueda crear un reporte de
incidencia sobre un espacio.

- No puede existir más de un reporte activo del mismo tipo para el mismo espacio. Un reporte se
considera activo si su estado no es `RESUELTO`.
- El reporte se creará con estado `PENDIENTE` automáticamente, a menos que se cree con minutos de
cierre, en cuyo caso su estado será `EN_REVISION`.

<details>
<summary><b>🔑 Encabezados válidos (Header)</b></summary>

- `Authorization`: `Bearer <token_de_owner>`
- `Authorization`: `Bearer <token_de_admin>`
- `Authorization`: `Bearer <token_de_personal>`

</details>

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`tipoReporte` String, requerido. Valores válidos:
- `ACCESO_BLOQUEADO`
- `PROBLEMA_SENALETICA`
- `BARRERA_FISICA`
- `DIFICULTAD_ORIENTACION`
- `OTROS`

`descripcion` String, opcional, máximo 100 caracteres.

`espacioId` Long, requerido. `id` del espacio donde ocurre la incidencia.

`minutosEstimados` Integer, opcional. Minutos antes que el reporte se cierre solo.

```JSON
{
  "tipoReporte": "ACCESO_BLOQUEADO",
  "descripcion": "La puerta de acceso al aula está bloqueada",
  "espacioId": 12,
  "minutosEstimados": 10
}
```

</td></tr></table>
</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `201 CREATED` +
[JSON respuesta de reporte](#formato-de-respuesta-de-reporte)
con la información del reporte creado.

<details>
<summary><b>🧾 Posible JSON de detalles de auditoría</b></summary>

[JSON respuesta de auditoria](#formato-respuesta-de-auditoria)


```JSON
{
  "minutosEstimados":  Integer,
  "fechaVencimientoNueva": String (LocalDateTime)
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo JSON no cumple las restricciones estructurales (descripción vacía, tipoReporte inválido, etc.).
- Si el `tipoReporte` es `OTROS` y no tiene una `descripcion`.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`, `ADMIN` o `PERSONAL`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un espacio registrado con el `espacioId` solicitado.

🔴 `409 CONFLICT` +
[JSON error](#formato-general-de-errores)
si ya existe un reporte activo del mismo tipo para el espacio indicado.

</td></tr></table>
</details>

## 🟢 Atender un reporte [solo OWNER, ADMIN o PERSONAL]

`POST /api/reportes/{id}/atender`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion` posibles: 
- `REPORTE_ATENDIDO`
- `REPORTE_MODIFICADO`
- `REPORTE_TIEMPO_ELIMINADO`

Permite que un usuario logueado como `OWNER`, `ADMIN` o `PERSONAL` pueda atender un reporte. Al
atender un reporte se puede:
- Únicamente marcarlo como atendido.
- Agregarle, quitarle o editarle el tiempo de espera para su cierre automático.
- Cambiarle la descripción.

<details>
<summary><b>🔑 Encabezados válidos (Header)</b></summary>

- `Authorization`: `Bearer <token_de_owner>`
- `Authorization`: `Bearer <token_de_admin>`
- `Authorization`: `Bearer <token_de_personal>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

- `id` Long, requerido.<br>
  ID del reporte que se quiere editar.

</details> 

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`minutosEstimados` Integer, opcional, valor mínimo `1`.

`quitarContador` Boolean, opcional.

`descripcion` String, opcional, máximo 100 caracteres.

```JSON
{
  "minutosEstimados": 5,
  "quitarContador": false,
  "descripcion": "Piso secándose"
}
```

</td></tr></table>
</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de reporte](#formato-de-respuesta-de-reporte)
con la información del reporte creado.

<details>
<summary><b>🧾 Posible JSON de detalles de auditoría</b></summary>

[JSON respuesta de auditoria](#formato-respuesta-de-auditoria)

```JSON
{
  "minutosEstimados":  Integer,
  "descripcionAnterior":  String,
  "descripcionNueva": String,
  "fechaVencimientoAnterior": String (LocalDateTime),
  "fechaVencimientoNueva": String (LocalDateTime)
}
```

</details>


🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si se intenta modificar el tiempo (`minutosEstimados`) y quitar el contador (`quitarContador`) al
mismo tiempo.
- Si se intenta quitar el contador (`quitarContador`) a un reporte que no tenía contador.
- Si no se realiza ningún cambio.
- Si el `id` enviado en el path variable no tiene un formato numérico válido.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`, `ADMIN` o `PERSONAL`.
- Si se intenta modificar un reporte que no está en `PENDIENTE` ni `EN_REVISION`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un reporte con el `id` solicitado en el path variable.

</td></tr></table>
</details> 

## 🟢 Resolver un reporte [solo OWNER, ADMIN o PERSONAL]

`POST /api/reportes/{id}/resolver`

📋 [Genera Auditoria](#formato-respuesta-de-auditoria) | `accion`: `REPORTE_CERRADO`

Permite que un usuario logueado como `OWNER`, `ADMIN` o `PERSONAL` pueda marcar un reporte como
`RESUELTO`.

<details>
<summary><b>🔑 Encabezados válidos (Header)</b></summary>

- `Authorization`: `Bearer <token_de_owner>`
- `Authorization`: `Bearer <token_de_admin>`
- `Authorization`: `Bearer <token_de_personal>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

- `id` Long, requerido.<br>
  ID del reporte que se quiere resolver.

</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de reporte](#formato-de-respuesta-de-reporte)
con la información del reporte resuelto.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el reporte ya se encuentra con estado `RESUELTO`.
- Si el `id` enviado en el path variable no tiene un formato numérico válido.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`, `ADMIN` o `PERSONAL`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un reporte con el `id` solicitado en el path variable.

</td></tr></table>
</details> 

## 🟢 Obtener un reporte [solo OWNER, ADMIN o PERSONAL]

`GET /api/reportes/{id}`

Permite que un usuario logueado como `OWNER`, `ADMIN` o `PERSONAL` pueda obtener un reporte por su
`id`.

<details>
<summary><b>🔑 Encabezados válidos (Header)</b></summary>

- `Authorization`: `Bearer <token_de_owner>`
- `Authorization`: `Bearer <token_de_admin>`
- `Authorization`: `Bearer <token_de_personal>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

- `id` Long, requerido.<br>
  ID del reporte que se quiere obtener.

</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de reporte](#formato-de-respuesta-de-reporte)
con la información del reporte requerido.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si el `id` enviado en el path variable no tiene un formato numérico válido.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un reporte con el `id` solicitado en el path variable.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`, `ADMIN` o `PERSONAL`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

</td></tr></table>
</details> 

## 🟢 Listar reportes con filtros [solo OWNER, ADMIN o PERSONAL]

`GET /api/reportes`

Permite que un usuario logueado como `OWNER`, `ADMIN` o `PERSONAL` puedan obtener una lista
filtrable con todos los reportes.

- Podrá filtrarlos por `espacioId`, `estado`, `tipoReporte`, `page`, `size`.
- Se pueden seleccionar múltiples `estado` y `tipoReporte`.

<details>
<summary><b>🔑 Encabezados válidos (Header)</b></summary>

- `Authorization`: `Bearer <token_de_owner>`
- `Authorization`: `Bearer <token_de_admin>`
- `Authorization`: `Bearer <token_de_personal>`

</details>

<details>
<summary><b>❓ Parámetros de Consulta (Query Parameters)</b></summary>

**Todos los filtros son opcionales**. Se añaden a la URL (ej: `?espacioId=2`)

- `espacioId` int - Filtro exacto (`id` del espacio al que pertenece el reporte).
- `estado` String - Filtro exacto (estado del reporte que puede ser `PENDIENTE` / `EN_REVISION` / 
`RESUELTO`)
- `tipoReporte` String - Filtro exacto (puede ser del tipo `ACCESO_BLOQUEADO` / 
`PROBLEMA_SENALETICA` / `BARRERA_FISICA` / `DIFICULTAD_ORIENTACION` / `OTROS`).
- `page` int - Número de página, empieza en 0 (Por defecto: 0).
- `size` int - Cantidad de registros por página (Por defecto: 10).

</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` + JSON de estructura de página de Spring.


Cada reporte tendrá el formato del
[JSON respuesta de reporte](#formato-de-respuesta-de-reporte)

<details>
<summary><b>🧾 JSON de estructura de página de Spring</b></summary>

```JSON
{
  "content": [
    {
      "id": 1,
      "espacioId": 1,
      "nombreEspacio": "Laboratorio de Computación 1",
      "descripcion": "No funciona la luz",
      "estadoReporte": "RESUELTO",
      "tipoReporte": "ACCESO_BLOQUEADO",
      "minutosEstimados": null,
      "fechaVencimiento": null,
      "fechaCreacion": "2026-07-06T20:44:30.055448"
    }
  ],
  "empty": false,
  "first": true,
  "last": true,
  "number": 0,
  "numberOfElements": 1,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 10,
    "paged": true,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "unpaged": false
  },
  "size": 10,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "totalElements": 1,
  "totalPages": 1
}
```

</details>

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si los tipos de datos enviados en los parámetros son incompatibles 
(ejemplo: ?usuarioId=alfanumerico).

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`, `ADMIN` o `PERSONAL`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

</td></tr></table>
</details>

## 🟢 Obtener conteo de reportes activos [solo OWNER, ADMIN o PERSONAL]

`GET /api/reportes/conteo`

Permite que un usuario logueado como `OWNER`, `ADMIN` o `PERSONAL` pueda ver el total de cada tipo
de reporte activo.

<details>
<summary><b>🔑 Encabezados válidos (Header)</b></summary>

- `Authorization`: `Bearer <token_de_owner>`
- `Authorization`: `Bearer <token_de_admin>`
- `Authorization`: `Bearer <token_de_personal>`

</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` + **JSON de conteo**

En caso de no haber ninguno, el valor será `0`.

```JSON
{
  "accesoBloqueado": int,
  "problemaSenaletica": int,
  "barreraFisica": int,
  "dificultadOrientacion": int,
  "otros": int
}
```

<details>
<summary><b>Ejemplo</b></summary>

```JSON
{
  "accesoBloqueado": 3,
  "problemaSenaletica": 1,
  "barreraFisica": 0,
  "dificultadOrientacion": 0,
  "otros": 1
}
```

</details>

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`, `ADMIN` o `PERSONAL`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
  `"errorCode": "SESSION_INVALIDATED"`.

</td></tr></table>
</details> 

---

# 📊 Métricas

### Formato JSON de metricas

```JSON
{
  "fechaDesde": String (LocalDate),
  "fechaHasta": String (LocalDate),
  "cuentas": {
    "creados": long,
    "eliminados": long,
    "passwordRestablecidas": long,
    "passwordCambiadas": long,
    "estadosModificados": long,
    "rolesModificados": long,
    "ownerRecuperaciones": long,
    "ownerTransferencias": long
  },
  "datos":  {
    "cambiosDni":  {
      "total": long,
      "siMismo": long,
      "otros": long
    },
    "cambiosNombreApellido": {
      "total": long,
      "siMismo": long,
      "otros": long
    } 
  },
  "reportes": {
    "creados": long,
    "atentidos": long,
    "modificados": long,
    "cerradosManual": long,
    "cerradosAutomatico": long,
    "rendimientoPorTipo": [
      {
        "tipo": String,
        "creados": long,
        "cerrados": long,
        "promedioMinutos": long,
        "maximoMinutos": long
      }
    ],
    "espaciosCriticosPorTipo": [
      {
        "tipo": String,
        "zonas": [
          {
            "espacioId": Long,
            "nombre": String,
            "cantidad": long
          }
        ]
      }
    ]
  },
  "usuariosReportes": {
    "topUsuarios": [
      {
        "tipoAccion": String,
        "topUsuarios": [
          {
            "id": Long,
            "nombre": String,
            "cantidad": long
          }
        ]
      }
    ],
    "topRoles": [
      {
        "rol": String,
        "tipoAccion": String,
        "cantidad": Long
      }
    ]
  },
  "busquedasMapa": {
    "totalConsultas": long,
    "topOrigenes": [
      {
        "espacioId": Long,
        "nombreEspacio": String,
        "cantidad": long
      }
    ],
    "topDestinos": [
      {
        "espacioId": Long,
        "nombreEspacio": String,
        "cantidad": long
      }
    ],
    "topRutas": [
      {
        "desdeId": Long,
        "nombreDesde": String,
        "hastaId": Long,
        "nombreHasta": String,
        "cantidad": long
      }
    ]
  }  
}
```

Los siguientes tops se pueden desactivar mandando los valores `null` o `0`:
- En reportes:
  - espaciosCriticosPorTipo
- En usuariosReportes:
  - topUsuarios
  - topRoles
- En busquedasMapa
  - topOrigenes
  - topDestinos
  - topRutas

<details>
<summary><b>EJEMPLO JSON con todos los top 2</b></summary>

```json
{
  "fechaDesde": "2026-07-14",
  "fechaHasta": "2026-07-21",
  "cuentas": {
    "creados": 0,
    "eliminados": 0,
    "passwordRestablecidas": 0,
    "passwordCambiadas": 1,
    "estadosModificados": 0,
    "rolesModificados": 0,
    "ownerRecuperaciones": 0,
    "ownerTransferencias": 0
  },
  "datos": {
    "cambiosDni": {
      "total": 0,
      "siMismo": 0,
      "otros": 0
    },
    "cambiosNombreApellido": {
      "total": 0,
      "siMismo": 0,
      "otros": 0
    }
  },
  "reportes": {
    "creados": 8,
    "atentidos": 1,
    "modificados": 0,
    "cerradosManual": 6,
    "cerradosAutomatico": 2,
    "rendimientoPorTipo": [
      {
        "tipo": "ACCESO_BLOQUEADO",
        "creados": 3,
        "cerrados": 3,
        "promedioMinutos": 1,
        "maximoMinutos": 1
      },
      {
        "tipo": "PROBLEMA_SENALETICA",
        "creados": 1,
        "cerrados": 1,
        "promedioMinutos": 1,
        "maximoMinutos": 1
      },
      {
        "tipo": "BARRERA_FISICA",
        "creados": 2,
        "cerrados": 2,
        "promedioMinutos": 2,
        "maximoMinutos": 2
      },
      {
        "tipo": "DIFICULTAD_ORIENTACION",
        "creados": 1,
        "cerrados": 1,
        "promedioMinutos": 3,
        "maximoMinutos": 3
      },
      {
        "tipo": "OTROS",
        "creados": 1,
        "cerrados": 1,
        "promedioMinutos": 4,
        "maximoMinutos": 4
      }
    ],
    "espaciosCriticosPorTipo": [
      {
        "tipo": "ACCESO_BLOQUEADO",
        "zonas": [
          {
            "espacioId": 1,
            "nombre": "Electricidad",
            "cantidad": 2
          },
          {
            "espacioId": 2,
            "nombre": "Herreria",
            "cantidad": 1
          }
        ]
      },
      {
        "tipo": "PROBLEMA_SENALETICA",
        "zonas": [
          {
            "espacioId": 1,
            "nombre": "Electricidad",
            "cantidad": 1
          }
        ]
      },
      {
        "tipo": "BARRERA_FISICA",
        "zonas": [
          {
            "espacioId": 2,
            "nombre": "Herreria",
            "cantidad": 1
          },
          {
            "espacioId": 3,
            "nombre": "Climatizacion",
            "cantidad": 1
          }
        ]
      },
      {
        "tipo": "DIFICULTAD_ORIENTACION",
        "zonas": [
          {
            "espacioId": 1,
            "nombre": "Electricidad",
            "cantidad": 1
          }
        ]
      },
      {
        "tipo": "OTROS",
        "zonas": [
          {
            "espacioId": 5,
            "nombre": "Bano Sector 1 (grande)",
            "cantidad": 1
          }
        ]
      }
    ]
  },
  "usuariosReportes": {
    "topUsuarios": [
      {
        "tipoAccion": "REPORTE_ATENDIDO",
        "topUsuarios": [
          {
            "id": 2,
            "nombre": "Martín Fernández",
            "cantidad": 1
          }
        ]
      },
      {
        "tipoAccion": "REPORTE_CERRADO",
        "topUsuarios": [
          {
            "id": 2,
            "nombre": "Martín Fernández",
            "cantidad": 3
          },
          {
            "id": 4,
            "nombre": "Diego Alejandro Gómez",
            "cantidad": 2
          }
        ]
      },
      {
        "tipoAccion": "REPORTE_CREADO",
        "topUsuarios": [
          {
            "id": 4,
            "nombre": "Diego Alejandro Gómez",
            "cantidad": 6
          },
          {
            "id": 2,
            "nombre": "Martín Fernández",
            "cantidad": 2
          }
        ]
      }
    ],
    "topRoles": [
      {
        "rol": "OWNER",
        "tipoAccion": "REPORTE_ATENDIDO",
        "cantidad": 1
      },
      {
        "rol": "OWNER",
        "tipoAccion": "REPORTE_CERRADO",
        "cantidad": 3
      },
      {
        "rol": "PERSONAL",
        "tipoAccion": "REPORTE_CERRADO",
        "cantidad": 2
      },
      {
        "rol": "PERSONAL",
        "tipoAccion": "REPORTE_CREADO",
        "cantidad": 6
      },
      {
        "rol": "OWNER",
        "tipoAccion": "REPORTE_CREADO",
        "cantidad": 2
      }
    ]
  },
  "busquedasMapa": {
    "totalConsultas": 5,
    "topOrigenes": [
      {
        "espacioId": 3,
        "nombreEspacio": "Climatizacion",
        "cantidad": 3
      },
      {
        "espacioId": 1,
        "nombreEspacio": "Electricidad",
        "cantidad": 2
      }
    ],
    "topDestinos": [
      {
        "espacioId": 1,
        "nombreEspacio": "Electricidad",
        "cantidad": 2
      },
      {
        "espacioId": 2,
        "nombreEspacio": "Herreria",
        "cantidad": 1
      }
    ],
    "topRutas": [
      {
        "desdeId": 3,
        "nombreDesde": "Climatizacion",
        "hastaId": 1,
        "nombreHasta": "Electricidad",
        "cantidad": 2
      },
      {
        "desdeId": 1,
        "nombreDesde": "Electricidad",
        "hastaId": 2,
        "nombreHasta": "Herreria",
        "cantidad": 1
      }
    ]
  }
}
```

</details>

<details>
<summary><b>EJEMPLO JSON con todos los top desactivados</b></summary>

```json
{
  "fechaDesde": "2026-07-14",
  "fechaHasta": "2026-07-21",
  "cuentas": {
    "creados": 0,
    "eliminados": 0,
    "passwordRestablecidas": 0,
    "passwordCambiadas": 1,
    "estadosModificados": 0,
    "rolesModificados": 0,
    "ownerRecuperaciones": 0,
    "ownerTransferencias": 0
  },
  "datos": {
    "cambiosDni": {
      "total": 0,
      "siMismo": 0,
      "otros": 0
    },
    "cambiosNombreApellido": {
      "total": 0,
      "siMismo": 0,
      "otros": 0
    }
  },
  "reportes": {
    "creados": 8,
    "atentidos": 1,
    "modificados": 0,
    "cerradosManual": 6,
    "cerradosAutomatico": 2,
    "rendimientoPorTipo": [
      {
        "tipo": "ACCESO_BLOQUEADO",
        "creados": 3,
        "cerrados": 3,
        "promedioMinutos": 1,
        "maximoMinutos": 1
      },
      {
        "tipo": "PROBLEMA_SENALETICA",
        "creados": 1,
        "cerrados": 1,
        "promedioMinutos": 1,
        "maximoMinutos": 1
      },
      {
        "tipo": "BARRERA_FISICA",
        "creados": 2,
        "cerrados": 2,
        "promedioMinutos": 2,
        "maximoMinutos": 2
      },
      {
        "tipo": "DIFICULTAD_ORIENTACION",
        "creados": 1,
        "cerrados": 1,
        "promedioMinutos": 3,
        "maximoMinutos": 3
      },
      {
        "tipo": "OTROS",
        "creados": 1,
        "cerrados": 1,
        "promedioMinutos": 4,
        "maximoMinutos": 4
      }
    ],
    "espaciosCriticosPorTipo": null
  },
  "usuariosReportes": {
    "topUsuarios": null,
    "topRoles": null
  },
  "busquedasMapa": {
    "totalConsultas": 5,
    "topOrigenes": null,
    "topDestinos": null,
    "topRutas": null
  }
}
```

</details>

## 🟢 Obtener métricas entre fechas [solo OWNER]

`GET /api/metricas`

Permite que únicamente un usuario con el rol `OWNER` pueda obtener un resumen de lo sucedido entre
dos fechas.

- En el JSON se encuentran varios tops que pueden ser desactivados mandando los valores `0` o `null`.

El resumen está compuesto por distintas secciones:
- **cuentas**: muestra un conteo de todas las acciones referidas a cuentas de usuarios (menos cambio
de `dni` y cambio de `nombre`/`apellido`).
- **datos**: muestra la cantidad total de cambios de `dni`, `nombre`/`apellido` que se hicieron, y también
cuántos fueron realizados a otros usuarios y cuántos a uno mismo.
- **reportes**: 
  - Muestra un conteo de todas las acciones referidas al menos de reportes. 
  - También incluye por cada tipo de reporte: cuántos fueron creados/cerrados, el promedio de tiempo en cerrarse
  y cuánto fue que se tardó el reporte que más se tardó en cerrarse.
  - Incluye un top de zonas en las que se crearon más reportes de cada tipo.
- **usuariosReportes**: devuelve dos top de usuarios:
  - topUsuarios: muestra un top de usuarios en 3 categorías: más reportes creados/atendidos/cerrados.
  - topRoles: muestra un top de roles que crearon/atendieron/cerraron más reportes.
- **busquedasMapa**: muestra el total de veces que se consultó el mapa. También contiene 3 tops:
  - top de desde dónde se hicieron las consultas.
  - top de hacia dónde se hicieron las consultas.
  - top de rutas más buscadas, incluye desde dónde hacia dónde.

<details>
<summary><b>🔑 Encabezado (Header)</b></summary>

* `Authorization`: `Bearer <token_de_owner>`

</details>

<details>
<summary><b>❓ Parámetros de Consulta (Query Parameters)</b></summary>

- Ejemplo con omisión: `?desde=2026-07-14&hasta=2026-07-21`
- Ejemplo completo: `?desde=2026-07-14&hasta=2026-07-21&topEspaciosCriticos=0&topUsuarios=1&topRoles=1&topBusquedas=1`


- `desde` String (localDate), requerido. (ejemplo: `2026-07-14`).
- `hasta` String (localDate), requerido.
- `topEspaciosCriticos` long, opcional. (`omitir` o valores `0`/`null` para desactivar).
- `topUsuarios` long, opcional. (`omitir` o valores `0`/`null` para desactivar).
- `topRoles` long, opcional. (`omitir` o valores `0`/`null` para desactivar).
- `topBusquedas` long, opcional. (`omitir` o valores `0`/`null` para desactivar).

</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON de metricas](#formato-json-de-metricas)

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si los tipos de datos enviados en los parámetros son incompatibles.
- Si la fecha de inicio es superior a la de finalización.
- Si la fecha de finalización es superior a la fecha actual.
- (raro) Si se intenta ejecutar el endpoint mientras ya se estaba ejecutando.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `OWNER`
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

</td></tr></table>
</details> 

---

# 🚦 Espacios más buscados

## 🟢 Registrar búsqueda [acceso público]

`POST /api/busqueda/registrar`

Permite registrar los recorridos de forma asíncrona para métricas futuras. Diseñado para no lanzar
erroes y devolver de forma automática un código `202` así no retrasa al mapa.

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

Ambas variables deberían ser obligatorias, pero se mantienen como String opcionales para evitar que
se lance algún tipo de error y mantener este endpoint lo más ágil posible.

- `desdeId` String, opcional.
- `hastaId` String, opcional.

```json
{
  "desdeId": "1",
  "hastaId": "2"
}
```

</td></tr></table>
</details> 

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `202 ACCEPTED`

</td></tr></table>
</details> 

---

# 🗺️ Recorrido

- En desarrollo.