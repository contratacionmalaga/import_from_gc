package local.jarios.genericode;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/** Representa un valor dentro de una fila genericode. */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Value")
public class Value {

  /** Valor textual de la celda. */
  @XmlElement(name = "SimpleValue")
  private String simpleValue;

  /** Referencia de columna asociada al valor. */
  @XmlAttribute(name = "ColumnRef")
  private String columnRef;

  /** Constructor sin argumentos requerido por JAXB. */
  public Value() {}
}
