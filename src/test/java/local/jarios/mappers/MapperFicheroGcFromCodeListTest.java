package local.jarios.mappers;

import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.genericode.CodeList;
import local.jarios.genericode.Identification;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapperFicheroGcFromCodeListTest {

  @Test
  void mapsIdentificationFieldsToFicheroGc() {
    Log logEntity = new Log();
    CodeList codeList = codeListWithIdentification(
        "ContractCode",
        "Contract code list",
        "2.08",
        "urn:canonical",
        "urn:canonical:2.08",
        "https://example.test/ContractCode.gc");

    FicheroGc ficheroGc = MapperFicheroGcFromCodeList
        .getFicheroGcFromCodeList(logEntity, codeList);

    assertThat(ficheroGc.getLogEntity()).isSameAs(logEntity);
    assertThat(ficheroGc.getShortName()).isEqualTo("ContractCode");
    assertThat(ficheroGc.getLongName()).isEqualTo("Contract code list");
    assertThat(ficheroGc.getVersion()).isEqualTo("2.08");
    assertThat(ficheroGc.getCanonicalUri()).isEqualTo("urn:canonical");
    assertThat(ficheroGc.getCanonicalVersionUri()).isEqualTo("urn:canonical:2.08");
    assertThat(ficheroGc.getLocationUri()).isEqualTo("https://example.test/ContractCode.gc");
  }

  @Test
  void returnsNullWhenIdentificationIsMissing() {
    CodeList codeList = new CodeList();

    FicheroGc ficheroGc = MapperFicheroGcFromCodeList
        .getFicheroGcFromCodeList(new Log(), codeList);

    assertThat(ficheroGc).isNull();
  }

  @Test
  void replacesNullIdentificationFieldsWithEmptyStrings() {
    CodeList codeList = codeListWithIdentification(null, null, null, null, null, null);

    FicheroGc ficheroGc = MapperFicheroGcFromCodeList
        .getFicheroGcFromCodeList(new Log(), codeList);

    assertThat(ficheroGc.getShortName()).isEmpty();
    assertThat(ficheroGc.getLongName()).isEmpty();
    assertThat(ficheroGc.getVersion()).isEmpty();
    assertThat(ficheroGc.getCanonicalUri()).isEmpty();
    assertThat(ficheroGc.getCanonicalVersionUri()).isEmpty();
    assertThat(ficheroGc.getLocationUri()).isEmpty();
  }

  private CodeList codeListWithIdentification(
      String shortName,
      String longName,
      String version,
      String canonicalUri,
      String canonicalVersionUri,
      String locationUri) {
    Identification identification = new Identification();
    identification.setShortName(shortName);
    identification.setLongName(longName);
    identification.setVersion(version);
    identification.setCanonicalUri(canonicalUri);
    identification.setCanonicalVersionUri(canonicalVersionUri);
    identification.setLocationUri(locationUri);

    CodeList codeList = new CodeList();
    codeList.setIdentification(identification);
    return codeList;
  }
}
