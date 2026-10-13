# Release 6.1.0

- El ejecutable con dependencias se genera como `import-from-gc-6.1.0.jar`, sin
  el sufijo `-jar-with-dependencies`.
- Se utiliza Maven Shade en `pom.xml`, igual que en `import-from-opendata`.
  El JAR ejecutable pasa a ser el artefacto Maven principal; incluye las
  dependencias, la clase de entrada y la version en el manifiesto.
- Los servicios de las dependencias se combinan al empaquetar el ejecutable.
- La release incluye el ejecutable, los JAR de sources y javadocs y este
  `readme.md`.
- El correo de exito y error sigue desactivado por defecto mediante
  `app.email.enabled=false`; su ausencia tambien desactiva las notificaciones.

## Despliegue

Utiliza `import-from-gc-6.1.0.jar` como ejecutable del BAT o del servicio de
importacion. Si el lanzador conserva un nombre fijo para el JAR, sustituye
su contenido por este archivo manteniendo el nombre que utiliza el lanzador.
Conserva los ficheros externos de `properties/` y los catalogos de `data/gc/`.

Desde la carpeta que contiene la configuracion y los datos:

```powershell
java -jar import-from-gc-6.1.0.jar
```

No se necesita migrar la base de datos ni repetir la importacion.
