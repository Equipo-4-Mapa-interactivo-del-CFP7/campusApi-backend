# 🗂️ Endpoints de la API

#### Formato general de errores

Los JSON de error están estandarizados con este formato:

```JSON
{
  "status": int,
  "error": String,
  "message": String,
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

`dni` String, requerido, entre 6 y 15 caracteres.

`password` String, requerido, entre 8 y 60 caracteres.

```JSON
{
  "dni": "123456",
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
si el cuerpo de la petición no cumple las restricciones.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si el `dni` o la `password` ingresada es incorrecta.
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
  "dni": "123456",
  "rol": "ADMIN",
  "nombre": "nombre",
  "apellido": "apellido",
  "activo": true
}
```
</details>

## 🟢 Registrar usuario [solo para ADMIN]

`POST /api/usuarios/registrar`

Permite que únicamente usuarios con rol `ADMIN` pueda registrar un usuario nuevo en el sistema.
A este usuario se le asignará la contraseña `cfp + dni` (ejemplo: `cfp123456`) y el rol temporal
`CHANGE_PASSWORD` hasta que cambie su contraseña, entonces su rol pasará a ser `PERSONAL`.

🔑 Encabezados (Headers)
* `Authorization`: `Bearer <token_de_admin>`

<details>
<summary><b>📦 Cuerpo de la petición</b></summary>
<table><tr><td>

`dni` String, requerido, entre 6 y 15 caracteres.

`nombre` String, requerido, entre 1 y 100 caracteres.

`apellido` String, requerido, entre 1 y 100 caracteres.

```JSON
{
  "dni": "123456",
  "nombre": "nombre",
  "apellido": "apellido"
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
si el cuerpo de la petición no cumple las restricciones.

🔴 `401 UNAUTHORIZED` +
[JSON error](#formato-general-de-errores)
si el token es inválido.

🔴 `409 CONFLICT` +
[JSON error](#formato-general-de-errores)
si ya existe un usuario con el mismo DNI.

</td></tr></table>
</details> 

## 🟢 Listar usuarios con filtros y paginación [solo para ADMIN]

`GET /api/usuarios`

Permite que únicamente usuarios con rol `ADMIN` pueda ver la lista completa de usuarios
registrados. Además podrá filtrarlos por: `dni`, `nombre`, `apellido`, `activo`. También se utiliza
la paginación para mostrarlo, por lo que puede definir cuántos (`size`) usuarios ver por página 
(`page`).

🔑 Encabezados (Headers)
* `Authorization`: `Bearer <token_de_admin>`

🔎 Parámetros de Consulta (Query Parameters). Todos los filtros son opcionales.
Se añaden a la URL (ej. `?nombre=Juan&size=5`).
* `dni` String.
* `nombre` String.
* `apellido` String.
* `activo` Boolean - Filtro por estado del usuario (`true` o `false`).
* `page` int - Número de página, empieza en 0 (Por defecto: 0).
* `size` int - Cantidad de registros por página (Por defecto: 10).

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
      "dni": "123456",
      "rol": "ADMIN",
      "nombre": "Nombre1",
      "apellido": "Apellido1",
      "activo": true
    },
    {
      "id": 2,
      "dni": "456789",
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
si el token es inválido.

</td></tr></table>
</details>

## 🟢 Restablecer contraseña [solo para ADMIN]

`PUT /api/usuarios/{dni}/restablecer`

Permite que únicamente usuarios con el rol `ADMIN` puedan restablecer la contraseña de otro usuario.
La contraseña pasa a ser `cfp + dni` (ejemplo: `cfp123456`) y su rol cambia temporalmente a 
`CHANGE_PASSWORD` hasta que cambie su contraseña.

🔑 Encabezados (Headers)

* `Authorization`: `Bearer <token_de_admin>`

🔎 Path variable

* `dni` String, requerido, entre 6 y 15 caracteres. <br>
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

🔴 `404 NOT FOUND` Si no existe un usuario con el DNI solicitado.

</td></tr></table>
</details>

## 🟢 Cambiar estado de la cuenta [solo para ADMIN]

`PUT /api/usuarios/{dni}/cambiar-activo`

Permite que únicamente usuarios con el rol `ADMIN` puedan cambiar el flag `activo` de una cuenta.

🔑 Encabezados (Headers)

* `Authorization`: `Bearer <token_de_admin>`

🔎 Path variable

* `dni` String, requerido, entre 6 y 15 caracteres. <br>
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

* `dni` String, requerido, entre 6 y 15 caracteres. <br>
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

* `dni` String, requerido, entre 6 y 15 caracteres. <br>
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