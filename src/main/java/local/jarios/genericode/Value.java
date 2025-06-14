package local.jarios.genericode;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

/**
 * Representa un valor en una fila de una lista de códigos.
 * Utiliza JAXB para la serialización y deserialización XML.
 * Lombok se encarga de generar los métodos getter y setter automáticamente.
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

}
