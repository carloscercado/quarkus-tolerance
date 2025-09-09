# quarkus-tolerance

Este proyecto utiliza **Quarkus**, el framework supersónico y subatómico de Java, para explorar **tolerancia a fallos en servicios REST**.

Para más información sobre Quarkus, visita: [https://quarkus.io/](https://quarkus.io/).

## STATUS
![CI Build](https://github.com/carloscercado/quarkus-tolerance/actions/workflows/ci-build.yml/badge.svg)
---

## 🎯 Objetivo del proyecto

Construir una **API REST con Quarkus** que funcione como **proxy resiliente**, consumiendo la API pública **[JSONPlaceholder Users](https://jsonplaceholder.typicode.com/users)** y demostrando las funcionalidades de **tolerancia a fallos**, incluyendo:

* **Timeouts**: controlar respuestas lentas de la API externa.
* **Retry**: reintentos automáticos en caso de fallas temporales.
* **Fallback**: manejar respuestas alternativas cuando la API falla.
* **Manejo de errores inesperados**: respuestas controladas para errores no previstos.

El proyecto se levanta con **Docker**, incluye **pruebas unitarias con JUnit y RestAssured**, y muestra cómo Quarkus maneja distintos escenarios de resiliencia.

---

## 📡 Endpoints de la API

### 1. **GET `/users`**

* Consulta `https://jsonplaceholder.typicode.com/users`.
* Comportamientos configurados:

  | Mecanismo    | Configuración                                                                                      |
    | ------------ |----------------------------------------------------------------------------------------------------|
  | **Timeout**  | 3 segundos                                                                                         |
  | **Retry**    | Hasta 3 intentos si la API falla o tarda más de 3 segundos. Intetaria la nueva peticion cada 500ms |
  | **Fallback** | Devuelve un JSON alternativo con mensaje y lista vacía                                             |

**Ejemplo de respuesta de fallback:**

```json
{
  "message": "Servicio externo no disponible, intente más tarde",
  "data": []
}
```

---

### 2. **GET `/api/health`**

* Endpoint de **salud** para verificar que la API está activa, sin llamar a servicios externos.
* Útil para Docker o Kubernetes (liveness/readiness).

---

## 🧪 Pruebas unitarias

### 1. Éxito

* **Escenario:** La API externa responde correctamente.
* **Objetivo:** `/external/peoples` devuelve la lista completa de usuarios.
* **Validación:** Código HTTP 200.

### 2. Timeout

* **Escenario:** La API externa tarda más de 3 segundos en responder.
* **Objetivo:** Probar que el **timeout** funciona y activa el fallback.
* **Validación:** Código HTTP 500 con el mensaje de fallback.

### 3. Retry

* **Escenario:** Las primeras llamadas fallan por timeout o error temporal, y la tercera responde correctamente.
* **Objetivo:** Verificar que los reintentos funcionan antes de devolver el resultado correcto.
* **Validación:** Código HTTP 200 y respuesta válida del tercer intento.

### 4. Fallback total

* **Escenario:** La API externa falla en todos los intentos (timeout o error 500).
* **Objetivo:** Confirmar que el fallback se activa y devuelve respuesta controlada.
* **Validación:** Código HTTP 200 con el JSON de fallback.

### 5. Error inesperado

* **Escenario:** Se lanza una excepción inesperada desde el servicio externo.
* **Objetivo:** Evaluar que la API maneja errores imprevistos sin romper el sistema.
* **Validación:** Código HTTP 500 con mensaje claro indicando el error.

---

## 🛠️ Ejecución en modo desarrollo

Para ejecutar la aplicación en **modo dev** (live coding):

```bash
./mvnw quarkus:dev
```

* Quarkus proporciona una **Dev UI** disponible en: [http://localhost:8080/q/dev/](http://localhost:8080/q/dev/)

---

## 📦 Empaquetado y ejecución

Para empaquetar la aplicación:

```bash
./mvnw package
```

* Se genera `quarkus-run.jar` en `target/quarkus-app/`.
* Ejecutar:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```
---

## ⚙️ Endpoints de salud

Con la extensión `quarkus-smallrye-health`:

* **Liveness:** `/q/health/live` → verifica que la aplicación esté viva.
* **Readiness:** `/q/health/ready` → verifica si la aplicación está lista para atender solicitudes (conexión a servicios externos, DB, etc.).
