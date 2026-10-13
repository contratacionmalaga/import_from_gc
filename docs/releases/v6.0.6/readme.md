# Release 6.0.6

- Se incorpora `app.email.enabled` en `properties/app.properties` para controlar
  los correos de exito y error, igual que en `import-from-opendata`.
- El correo esta desactivado por defecto: solo `true` habilita el envio. Si la
  clave no existe, esta vacia o tiene otro valor, no se envian notificaciones.
- Con el correo desactivado, no se usan ni validan las opciones de SMTP y una
  importacion correcta devuelve `0`. Los errores de importacion devuelven `1`.
- Para activar las notificaciones, establece `app.email.enabled=true` y configura
  `properties/mail.properties`.

## Despliegue

Sustituye el JAR del BAT por `import-from-gc-6.0.6-jar-with-dependencies.jar`,
manteniendo la configuracion externa y los ficheros GC. La clave nueva puede
anadirse como `app.email.enabled=false`; su ausencia tambien desactiva el correo.
No se necesita migrar la base de datos ni repetir la importacion.
