package local.jarios.genericode;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Representa una fila en una lista de códigos.
 * Utiliza JAXB para la serialización y deserialización XML.
 * Lombok se encarga de generar los métodos getter y setter automáticamente.
 *
 * @author Juan Antonio
 * @since 04/06/2024
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Row")
public class Row {

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "Value")
	private List<Value> values;

}
