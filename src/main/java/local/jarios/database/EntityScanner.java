package local.jarios.database;

import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import java.util.Collection;

/**
 * Description:
 * Escanea un paquete para encontrar clases anotadas con @Entity y las añade a la configuración de Hibernate.
 * Author: juan
 * Date: 28/12/2024
 * Team: Juan Antonio
 */
@Slf4j
public class EntityScanner {

    /**
     * Constructor sin argumentos.
     */
    public EntityScanner() {
        // Constructor vacío
    }

    /**
     * Escanea el paquete indicado y añade todas las entidades JPA encontradas a la configuración de Hibernate.
     *
     * @param configuration instancia de configuración de Hibernate
     * @param packageName   paquete donde buscar las entidades
     */
    public void scanAndAddEntities(Configuration configuration, String packageName) {

        // Usamos Reflections para escanear el paquete indicado
        var reflections = new Reflections(packageName);
        log.debug("[scanAndAddEntities] Objeto Relections creado correctamente para el paquete: {}", packageName);

        // Obtenemos todas las clases anotadas con @Entity
        Collection<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
        log.debug("[scanAndAddEntities] Colección con todas las clases anotadas con @entity: {}", entities.size());

        // Añadimos cada entidad a la configuración de Hibernate
        for (Class<?> entityClass : entities) {
            configuration.addAnnotatedClass(entityClass);
            log.debug("[scanAndAddEntities] {}", entityClass.getName());
        }
    }
}
