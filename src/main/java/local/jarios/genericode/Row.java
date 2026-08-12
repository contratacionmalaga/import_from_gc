package local.jarios.genericode;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Representa una fila en una lista de codigos genericode. */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Row")
public class Row {

  /** Valores incluidos en la fila. */
  @XmlElement(name = "Value")
  private List<Value> values;

  /** Constructor sin argumentos requerido por JAXB. */
  public Row() {}
}
