package local.jarios.genericode;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa una lista de códigos con su identificación asociada.
 * Utiliza JAXB para la serialización y deserialización XML.
 * Lombok se encarga de generar los métodos getter y setter automáticamente.
 *
 * @author Juan Antonio
 * @since 04/06/2024
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CodeList", namespace = "http://docs.oasis-open.org/codelist/ns/genericode/1.0/")
public class CodeList {

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "SimpleCodeList")
	private SimpleCodeList simpleCodeList;

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "Identification")
	private Identification identification;

	/**
	 * Constructor sin argumentos requerido por JAXB.
	 * <p>
	 * Este constructor es necesario para la correcta creación de instancias
	 * durante la deserialización XML.
	 * </p>
	 */
	public CodeList() {
		// Constructor vacío requerido por JAXB
	}
}
