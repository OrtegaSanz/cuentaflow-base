# cuentaflow-base — Taller RxJava + Spring WebFlux + MongoDB

Proyecto base al 50 %: completa los **14 TODO** siguiendo el documento del taller.

## 1. Levantar MongoDB (elige una opción)
- **Docker:** `docker compose up -d`
- **Local:** MongoDB Community Server en `mongodb://localhost:27017`
- **Atlas:** define la variable de entorno `MONGODB_URI` con tu cadena `mongodb+srv://...`

## 2. Abrir en IntelliJ IDEA
File → Open → carpeta `cuentaflow-base`. JDK 17. Ver los TODO: View → Tool Windows → TODO.

## 3. Validar el avance (no necesita MongoDB)
Ejecuta `src/test/java/com/nttdata/cuentaflow/ValidadorTallerTest.java` o `mvn test`.
Al inicio verás 20 pruebas en rojo. Cada prueba en verde vale 1 punto: meta 20 / 20.

## 4. Ejecutar la API
Ejecuta `CuentaflowApplication` (puerto 8085). Importa `CuentaFlow.postman_collection.json` en Postman.
