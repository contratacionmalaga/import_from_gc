package local.jarios.database;

import jakarta.persistence.Entity;
import local.jarios.utils.Constantes;
import local.jarios.utils.Mensajes;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import java.util.Collection;

/**
 * Description:
 * Escanea un paquete para encontrar clases anotadas con @Entity y las añade a la configuración de Hibernate.
 * Author: juan
 * Date: 28/12/2024
 * Team:
 */
@Slf4j
public class EntityScanner {

    /**
     * Escanea el paquete indicado y añade todas las entidades JPA encontradas a la configuración de Hibernate.
     *
     * @param configuration instancia de configuración de Hibernate
     * @param packageName   paquete donde buscar las entidades
     */
    public void scanAndAddEntities(Configuration configuration, String packageName) {

        // Usamos Reflections para escanear el paquete indicado
        var reflections = new Reflections(packageName);

        // Obtenemos todas las clases anotadas con @Entity
        Collection<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);

        // Log con el número de entidades encontradas
        log.info(Mensajes.ENTIDADES, entities.size(), packageName);

        // Añadimos cada entidad a la configuración de Hibernate
        for (Class<?> entityClass : entities) {
            configuration.addAnnotatedClass(entityClass);
            log.info("{}{}", Constantes.TABULADOR_1, entityClass.getName());
        }
    }
}
