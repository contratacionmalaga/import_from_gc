package local.jarios.genericode;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representa la identificación de un código en una lista de códigos.
 * Utiliza JAXB para la serialización y deserialización XML.
 * Lombok se encarga de generar los métodos getter y setter automáticamente.
 *
 * @author Juan Antonio
 * @since 04/06/2024
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Identification")
public class Identification {

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "ShortName")
	private String shortName;

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "LongName")
	private String longName;

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "Version")
	private String version;

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "CanonicalUri")
	private String canonicalUri;

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "CanonicalVersionUri")
	private String canonicalVersionUri;

	/**
	 * Valor simple asociado a este elemento.
	 */
	@XmlElement(name = "LocationUri")
	private String locationUri;
}
