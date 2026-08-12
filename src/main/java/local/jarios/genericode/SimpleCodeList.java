package local.jarios.genericode;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Representa la lista simple de filas dentro de un documento genericode. */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SimpleCodeList")
public class SimpleCodeList {

  /** Filas incluidas en la lista simple. */
  @XmlElement(name = "Row")
  private List<Row> row;

  /** Constructor sin argumentos requerido por JAXB. */
  public SimpleCodeList() {}
}
