package local.jarios.genericode;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/** Representa la identificacion de una lista de codigos genericode. */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Identification")
public class Identification {

  /** Nombre corto del catalogo. */
  @XmlElement(name = "ShortName")
  private String shortName;

  /** Nombre largo del catalogo. */
  @XmlElement(name = "LongName")
  private String longName;

  /** Version del catalogo. */
  @XmlElement(name = "Version")
  private String version;

  /** URI canonica del catalogo. */
  @XmlElement(name = "CanonicalUri")
  private String canonicalUri;

  /** URI canonica de la version del catalogo. */
  @XmlElement(name = "CanonicalVersionUri")
  private String canonicalVersionUri;

  /** URI de localizacion del catalogo. */
  @XmlElement(name = "LocationUri")
  private String locationUri;

  /** Constructor sin argumentos requerido por JAXB. */
  public Identification() {}
}
