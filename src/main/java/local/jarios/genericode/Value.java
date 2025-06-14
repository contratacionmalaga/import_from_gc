package local.jarios.genericode;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

/**
 * Representa un valor en una fila de una lista de códigos.
 * Utiliza JAXB para la serialización y deserialización XML.
 * Lombok se encarga de generar los métodos getter y setter automáticamente.
 * <p>
 * Esta clase requiere un constructor sin argumentos explícito para la correcta
 * serialización y deserialización con JAXB.
 * </p>
 *
 * @author Juan Antonio
 * @since 04/06/2024
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Value")
public class Value {

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "SimpleValue")
	private String simpleValue;

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlAttribute(name = "ColumnRef")
	private String columnRef;

	/**
	 * Constructor sin argumentos requerido por JAXB.
	 * <p>
	 * Este constructor es necesario para la correcta creación de instancias
	 * durante la deserialización XML.
	 * </p>
	 */
	public Value() {
		// Constructor vacío requerido por JAXB
	}

}
