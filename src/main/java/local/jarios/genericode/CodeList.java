package local.jarios.genericode;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/** Representa una lista de codigos con su identificacion asociada. */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
    name = "CodeList",
    namespace = "http://docs.oasis-open.org/codelist/ns/genericode/1.0/")
public class CodeList {

  /** Lista simple de codigos contenida en el documento. */
  @XmlElement(name = "SimpleCodeList")
  private SimpleCodeList simpleCodeList;

  /** Identificacion asociada a la lista de codigos. */
  @XmlElement(name = "Identification")
  private Identification identification;

  /** Constructor sin argumentos requerido por JAXB. */
  public CodeList() {}
}
