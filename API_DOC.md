# Endpoints de la API

<details>
<summary><b>Los JSON de error están estandarizados con este formato</b></summary>

```JSON
{
  "status": int,
  "error": String,
  "message": String,
  "timestamp": String ("2026-06-10T15:46:08.0424397")
}
```
</details>

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
## Iniciar sesión
`POST /api/auth/login`

*permite a los usuarios autenticarse en el sistema mediante su DNI y contraseña.*


<details>
<summary><b>Cuerpo de la petición</b></summary>
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
<summary><b>Respuesta del servidor</b></summary>
<table><tr><td>

`200 OK`

`accessToken` String, contiene el token del usuario.

`tokenType` String, siempre devuelve "Bearer" que es el tipo de token.

```JSON
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```
`401 UNAUTHORIZED` + JSON error.
</td></tr></table>
</details> 

---
# Control de Usuarios

<details><summary>El JSON de respuesta de usuario sigue este patrón</summary>

```JSON
{
  "id": Long,
  "dni": String,
  "nombre": String,
  "apellido": String,
  "activo": Boolean
}
```
</details>

<details><summary>Ejemplo</summary>

```JSON
{
  "id": 1,
  "dni": "123456",
  "nombre": "nombre",
  "apellido": "apellido",
  "activo": true
}
```
</details>

## Registrar usuario (solo para ADMIN)
`POST /api/usuarios/registrar`

*Permite que únicamente los usuario con rol ADMIN puedan crear usuarios.*

#### Encabezados (Headers)
* `Authorization`: `Bearer <token_de_admin>`

<details>
<summary><b>Cuerpo de la petición</b></summary>
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
<summary><b>Respuesta del servidor</b></summary>
<table><tr><td>

`201 CREATED` + JSON respuesta de usuario.

`409 CONFLICT` + JSON error, si ya existe un usuario con el mismo DNI.
</td></tr></table>
</details> 

## Listar usuarios con filtros y paginación (solo para ADMIN)
`GET /api/usuarios`

*Permite buscar usuarios de forma paginada filtrando opcionalmente por DNI, nombre, apellido o estado activo.*

#### Encabezados (Headers)
* `Authorization`: `Bearer <token_de_admin>`

#### Parámetros de Consulta (Query Parameters)
Todos los filtros son opcionales. Se añaden a la URL (ej. `?nombre=Juan&size=5`).
* `dni` String.
* `nombre` String.
* `apellido` String.
* `activo` Boolean - Filtro por estado del usuario (`true` o `false`).
* `page` int - Número de página, empieza en 0 (Por defecto: 0).
* `size` int - Cantidad de registros por página (Por defecto: 10).

<details>
<summary><b>Respuesta del servidor</b></summary>
<table><tr><td>

`200 OK` + JSON de estructura de página de Spring. 

Cada usuario tendrá el formato del JSON de respuesta de usuario.

```JSON
{
  "content": [
    {
      "id": 1,
      "dni": "123456",
      "nombre": "NombreUsuario",
      "apellido": "ApellidoUsuario",
      "activo": true
    },
    {
      "id": 2,
      "dni": "789012",
      "nombre": "OtroNombre",
      "apellido": "OtroApellido",
      "activo": false
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
</td></tr></table>
</details> 

## Restablecer contraseña (solo para ADMIN)

`PUT /api/usuarios/{dni}/restablecer`

*Permite que únicamente usuarios con el rol ADMIN puedan restablecer la contraseña de otro usuario y
les cambia el rol para obligarlos a cambiarla.*

#### Encabezados (Headers)

* `Authorization`: `Bearer <token_de_admin>`

#### Path variable

* `dni`

<details>
<summary><b>Respuesta del servidor</b></summary>
<table><tr><td>

`200 OK` + JSON respuesta de usuario.

`404 NOT FOUND` Si no existe un usuario con el DNI solicitado.
</td></tr></table>
</details> 


---
# Reportes

## Reportar incidencia (solo para USUARIOS REGISTRADOS)
POST /api/reportes/reportar

Permite que únicamente usuarios registrados en la base de datos reporten incidencias en los espacios

#### Encabezados (Headers)
* Authorization: Bearer <token_de_usuario>

<details>
<summary><b>Cuerpo de la petición</b></summary>
<table><tr><td>

tipoReporte TipoReporte, requerido, Categoría del problema reportado:
ACCESO_BLOQUEADO
BARRERA_FISICA
PROBLEMA_SENALETICA
DIFICULTAD_ORIENTACION.

descripcion String, requerido, entre 1 y 500 caracteres.

espacioId Long, requerido, ID del Espacio donde ocurrió la incidencia. El servidor valida que exista.

foto MultipartFile, opcional. Imagen adjunta. Se guarda y se crea una Imagen asociada al Reporte.

JSON
{
"tipoReporte": "ACCESO_BLOQUEADO",
"descripcion": "Acceso bloqueado en el segundo piso",
"espacioId": "2",
}

</td></tr></table>
</details> 

<details>
<summary><b>Respuesta del servidor</b></summary>
<table><tr><td>

201 CREATED

404 NOT FOUND + JSON error, si no se encontró el Espacio.
</td></tr></table>
</details>