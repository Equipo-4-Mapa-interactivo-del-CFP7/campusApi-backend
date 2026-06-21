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

# 🔐 Iniciar sesión

`POST /api/auth/login`

Permite a los usuarios autenticarse en el sistema mediante su DNI y contraseña.

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`dni` String, requerido, entre 7 y 20 caracteres alfanuméricos.

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

## 🟢 Registrar usuario [solo para OWNER o ADMIN]

`POST /api/usuarios/registrar`

Permite que usuarios con el rol `OWNER` o `ADMIN` puedan registrar un usuario nuevo en el sistema.

- Para registrarlo se ingresará su `dni`, `nombre`, `apellido` y `rol`.
- El nombre y apellido será normalizado poniendo la primera letra de cada palabra en mayúscula y
quitando los espacios adicionales.
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

`dni` String, requerido, entre 7 y 20 caracteres alfanuméricos.

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
* `dni` String - Filtro por coincidencia parcial (no distingue mayúsculas/minúsculas).
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

## 🟢 Restablecer contraseña [solo para OWNER o ADMIN]

`PUT /api/usuarios/{dni}/restablecer`

Permite restablecer la contraseña de otro usuario a su propio `dni` y le asigna el rol
`CHANGE_PASSWORD` hasta que cambie su propia contraseña.
- Un usuario con rol `OWNER` puede restablecer la contraseña de `ADMIN` y `PERSONAL`.
- Un usuario con rol `ADMIN` solo puede restablecer la contraseña de un usuario `PERSONAL`.
- Ningún rol puede restablecer la contraseña del rol `OWNER`.

🔑 Encabezados (Headers)

* `Authorization`: `Bearer <token_de_owner/admin>`

🔎 Path variable

* `dni` String, requerido, Exactamente 8 caracteres numéricos. <br>
DNI del usuario al que se le va a restablecer la contraseña.

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
con la información del usuario al que se le cambió la contraseña.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si el path variable no cumple con las restricciones.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si el token es inválido.

🔴 `403 FORBIDDEN` +
[JSON error](#formato-general-de-errores)
si los permisos del token no coinciden con la base de datos o si se intentó modificar al usuario
con rol `OWNER`.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un usuario con el DNI solicitado.

</td></tr></table>
</details>

## 🟢 Cambiar estado de la cuenta [solo para ADMIN]

`PUT /api/usuarios/{dni}/cambiar-activo`

Permite que únicamente usuarios con el rol `ADMIN` puedan cambiar el flag `activo` de una cuenta.

🔑 Encabezados (Headers)

* `Authorization`: `Bearer <token_de_admin>`

🔎 Path variable

* `dni` String, requerido, Exactamente 8 caracteres numéricos. <br>
DNI del usuario al que se le va a cambiar el flag `activo`.

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario al que se le cambió el flag `activo`.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si el path variable no cumple con las restricciones.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si el token es inválido.

🔴 `404 NOT FOUND` Si no existe un usuario con el DNI solicitado.

</td></tr></table>
</details> 

## 🟢 Cambiar contraseña

`PUT /api/usuarios/me/password`

Permite que únicamente un usuario logueado pueda cambiar su propia contraseña. Si el usuario tenía
el rol `CHANGE_PASSWORD` entonces recuperará su rol normal.

🔑 Encabezados (Headers)

* `Authorization`: `Bearer <token_de_usuario_logueado>`

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`oldPassword` String, requerido, entre 8 y 60 caracteres.

`newPassword` String, requerido, entre 8 y 60 caracteres.

```JSON
{
  "oldPassword": "passwordVieja",
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
si el cuerpo de la petición no cumple las restricciones.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si `oldPassword` no coincide con la contraseña actual.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si el token es inválido.

</td></tr></table>
</details>

## 🟢 Cambiar rol [solo para ADMIN]

`PUT /api/usuarios/{dni}/cambiar-rol`

Permite que únicamente los usuarios con el rol `ADMIN` puedan cambiar el rol de otro usuario. Los
va intercambiando entre los roles `ADMIN` y `PERSONAL`.

🔑 Encabezados (Headers)

* `Authorization`: `Bearer <token_de_admin>`

🔎 Path variable

* `dni` String, requerido, Exactamente 8 caracteres numéricos. <br>
DNI del usuario al que se le va a cambiar el rol.

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario al que se le cambió el rol.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si el path variable no cumple con las restricciones.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si el token es inválido.

🔴 `404 NOT FOUND` Si no existe un usuario con el DNI solicitado.

</td></tr></table>
</details>

## 🟢 Obtener mi propio perfil

`GET /api/usuarios/me`

Permite que únicamente un usuario logueado pueda ver su propio perfil.

🔑 Encabezados (Headers)

* `Authorization`: `Bearer <token_de_usuario_logueado>`

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario logueado.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si el token es inválido.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
(Raro que ocurra) Ocurre si el usuario fue eliminado de la base de datos mientras
su token JWT aún seguía activo.

</td></tr></table>
</details>

## 🟢 Obtener cualquier perfil [solo para ADMIN]

`GET /api/usuarios/{dni}`

Permite que únicamente usuarios con el rol `ADMIN` puedan obtener información de cualquier perfil
de usuarios a través de su DNI.

🔑 Encabezados (Headers)

* `Authorization`: `Bearer <token_de_admin>`

🔎 Path variable

* `dni` String, requerido, Exactamente 8 caracteres numéricos. <br>
DNI del usuario del cual se quiere obtener el perfil.

<details>
<summary><b>🔄 Respuesta del servidor</b></summary>
<table><tr><td>

🟢 `200 OK` +
[JSON respuesta de usuario](#formato-de-respuesta-de-usuarios)
del usuario solicitado.

🔴 `400 BAD REQUEST` +
[JSON error](#formato-general-de-errores)
si el path variable no cumple con las restricciones.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si el token es inválido.

🔴 `404 NOT FOUND` +
[JSON error](#formato-general-de-errores)
si no existe un usuario con el DNI solicitado.

</td></tr></table>
</details>