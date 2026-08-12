package local.jarios.database;

import jakarta.persistence.Entity;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

/** Escanea entidades JPA y las registra en la configuracion de Hibernate. */
@Slf4j
public class EntityScanner {

  /** Constructor sin argumentos. */
  public EntityScanner() {}

  /**
   * Escanea el paquete indicado y anade todas las entidades JPA encontradas.
   *
   * @param configuration instancia de configuracion de Hibernate
   * @param packageName paquete donde buscar las entidades
   */
  public void scanAndAddEntities(Configuration configuration, String packageName) {
    var reflections = new Reflections(packageName);
    log.debug(
        "[scanAndAddEntities] Objeto Reflections creado correctamente para el paquete: {}",
        packageName);

    Collection<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
    log.debug(
        "[scanAndAddEntities] Coleccion con todas las clases anotadas con @Entity: {}",
        entities.size());

    for (Class<?> entityClass : entities) {
      configuration.addAnnotatedClass(entityClass);
      log.debug("[scanAndAddEntities] {}", entityClass.getName());
    }
  }
}
