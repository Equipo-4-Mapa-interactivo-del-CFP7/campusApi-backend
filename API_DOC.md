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
- Si se intenta enviar en el campo `rol` los valores `OWNER` o `CHANGE_PASSWORD`.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER` / `ADMIN`).
- Si un `ADMIN` intenta registrar a otro `ADMIN` u `OWNER`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con 
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `409 CONFLICT` +
[JSON error](#formato-general-de-errores)
si el DNI ya se encuentra registrado en el sistema.

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
si el `id` en el path variable no cumple con las restricciones.

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
si no existe un usuario registrado con el `id` solicitado.

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

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si el `id` en el path variable no cumple con las restricciones.

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
si no existe un usuario registrado con el `id` solicitado.

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

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el `id` enviado en el path variable no tiene un formato numérico válido.
- Si el campo `rol` en el cuerpo está vacío o no es un rol válido del sistema.

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
si no existe un usuario con el `id` solicitado.

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
si el `id` enviado en el path variable no tiene un formato numérico válido.

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
si no existe un usuario registrado con el `id` solicitado.

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

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo de la petición no cumple las restricciones.
- Si el `id` enviado en el path variable no tiene un formato numérico válido.

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
si no existe un usuario registrado con el `id` solicitado.

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

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo de la petición no cumple las restricciones.
- Si el `id` enviado en el path variable no tiene un formato numérico válido.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee los roles permitidos (`OWNER`/`ADMIN`/`PERSONAL`).
- Si se intenta asignar el mismo `dni` que el usuario ya tiene.
- Si se intenta modificar a un usuario con el rol `CHANGE_PASSWORD`.
- Si alguien que no es `OWNER` intenta modificar a un usuario con rol `OWNER`.
- Si un `ADMIN` intenta modificar a otro con el mismo rol.
- Si un `PERSONAL` intenta modificar a alguien que no sea sí mismo.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un usuario registrado con el `id` solicitado.

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

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo de la petición no cumple las restricciones.
- Si el `id` enviado en el path variable no tiene un formato numérico válido.

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
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
`"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un usuario registrado con el `id` solicitado.

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
  "accion": String
}
```
`operadorNombre` y  `afectadoNombre` están compuestos por `nombre + apellido` de los usuarios.

<details>
<summary><b>Ejemplo cuando afecta a otro usuario</b></summary>

```JSON
{
  "id": 12,
  "fechaAccion": "2026-06-10T15:46:08.0424397",
  "operadorId": 1,
  "operadorNombre": "Usuario Operador",
  "operadorDni": "12345678",
  "operadorRol": "OWNER",
  "afectadoId": 2,
  "afectadoNombre": "Nombre Y Apellido Usuario",
  "afectadoDni": "23456789",
  "accion": "cambió el rol de un usuario"
}
```
</details>

<details>
<summary><b>Ejemplo cuando se afecta a sí mismo</b></summary>

Cuando un usuario realiza una acción sobre sí mismo, los parámetros `afectadoId`, `afectadoNombre` y
`afectadoDni` pasan a tener valores default.

```JSON
{
  "id": 12,
  "fechaAccion": "2026-06-10T15:46:08.0424397",
  "operadorId": 1,
  "operadorNombre": "Usuario Operador",
  "operadorDni": "12345678",
  "operadorRol": "ADMIN",
  "afectadoId": null,
  "afectadoNombre": "N/A (Auto-acción)",
  "afectadoDni": "N/A",
  "accion": "actualizó el número de DNI"
}
```
</details>

## 🟢 Listar historial de acciones [solo OWNER]

`GET /api/auditorias`

Permite que únicamente usuarios con el rol `OWNER` puedan ver el historial de actividad de los
usuarios.

- Podrá ordenarlos por `fechaAccion` (`desc` / `asc`).
- Podrá filtrarlos por `usuarioId` y por `accion`.
- Valores de `accion` válidos:
  - `USUARIO_CREADO`.
  - `PASSWORD_RESTABLECIDA`.
  - `ESTADO_ACTIVO_MODIFICADO`.
  - `PASSWORD_CAMBIADA`.
  - `ROL_MODIFICADO`.
  - `USUARIO_ELIMINADO`.
  - `PASSWORD_OWNER_RECUPERADA`.
  - `OWNER_TRANSFERIDO`.
  - `DNI_EDITADO`.
  - `NOMBRE_APELLIDO_EDITADO`.
  - `REPORTE_ATENDIDO`.
  - `REPORTE_CREADO`.

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
      "id": 1,
      "fechaAccion": "2026-06-24T00:22:08.719435",
      "operadorId": 2,
      "operadorNombre": "Pato Merlín",
      "operadorDni": "12345678",
      "operadorRol": "ADMIN",
      "afectadoId": null,
      "afectadoNombre": "N/A (Auto-acción)",
      "afectadoDni": "N/A",
      "accion": "actualizó los datos de nombre y apellido"
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
  "totalElements": 1,
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

# ⚠️ Reportes

#### Formato de respuesta de reporte

El JSON de respuesta de reporte sigue este patrón:

```JSON
{
  "id": Long,
  "espacioId": Long,
  "descripcion": String,
  "estadoReporte": String,
  "tipoReporte": String,
  "urlFoto": String
}
```

<details>
<summary><b>Ejemplo</b></summary>

```JSON
{
  "id": 1,
  "espacioId": 12,
  "descripcion": "La puerta de acceso al aula está bloqueada",
  "estadoReporte": "PENDIENTE",
  "tipoReporte": "ACCESO_BLOQUEADO",
  "urlFoto": "https://i.ibb.co/xyz/imagen.jpg"
}
```
</details>

---

## 🟢 Crear un reporte [acceso público]

`POST /api/reportes/reportar`

Permite que cualquier usuario, registrado o no, pueda crear un reporte de incidencia sobre un espacio.

- El reporte se creará con estado `PENDIENTE` automáticamente.
- La foto es opcional. Si no se adjunta, `urlFoto` tendrá valor `null` en la respuesta.
- No puede existir más de un reporte activo del mismo tipo para el mismo espacio. Un reporte se
  considera activo si su estado no es `RESUELTO`.

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`tipoReporte` String, requerido. Valores válidos:
- `ACCESO_BLOQUEADO`
- `PROBLEMA_SENALETICA`
- `BARRERA_FISICA`
- `DIFICULTAD_ORIENTACION`

`descripcion` String, requerido, máximo 100 caracteres.

`espacioId` Long, requerido. ID del espacio donde ocurre la incidencia.

`imagenURL` String, opcional. URL de la imagen subida previamente a un servicio externo (imgbb).

```JSON
{
  "tipoReporte": "ACCESO_BLOQUEADO",
  "descripcion": "La puerta de acceso al aula está bloqueada",
  "espacioId": 12,
  "imagenURL": "https://i.ibb.co/xyz/imagen.jpg"
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

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo JSON no cumple las restricciones estructurales (descripción vacía, tipoReporte inválido, etc.).

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un espacio registrado con el `espacioId` solicitado.

🔴 `409 CONFLICT` +
[JSON error](#formato-general-de-errores)
si ya existe un reporte activo del mismo tipo para el espacio indicado.

</td></tr></table>
</details>

---

## 🟢 Actualizar estado de un reporte [solo para ADMIN]

`PATCH /api/reportes/{id}/estado`

Permite que usuarios con el rol `ADMIN` puedan actualizar el estado de un reporte existente.

- Los estados válidos son `PENDIENTE`, `EN_REVISION` y `RESUELTO`.

<details>
<summary><b>🔑 Encabezado válido (Header)</b></summary>

* `Authorization`: `Bearer <token_de_admin>`

</details>

<details>
<summary><b>🔎 Path variable</b></summary>

* `id` Long, requerido.
  ID del reporte al que se le quiere actualizar el estado.

</details>

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`estado` String, requerido. Valores válidos:
- `PENDIENTE`
- `EN_REVISION`
- `RESUELTO`

```JSON
{
  "estado": "EN_REVISION"
}
```

</td></tr></table>
</details>

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de reporte](#formato-de-respuesta-de-reporte)
con la información actualizada del reporte.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
- Si el cuerpo JSON no cumple las restricciones (estado vacío o valor inválido).
- Si el `id` enviado en el path variable no tiene un formato numérico válido.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
- Si se intenta utilizar el endpoint sin estar logueado (falta el token).
- Si el token proporcionado está expirado, está mal formado o fue revocado por el sistema de seguridad.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
- Si el usuario logueado no posee el rol `ADMIN`.
- Si la sesión fue revocada en base de datos. Retorna el JSON de error con
  `"errorCode": "SESSION_INVALIDATED"`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un reporte registrado con el `id` solicitado.

</td></tr></table>
</details>

# 🗺️ Recorrido

- En desarrollo.