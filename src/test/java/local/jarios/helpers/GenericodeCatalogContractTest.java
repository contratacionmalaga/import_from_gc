package local.jarios.helpers;

import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.RegistroGc;
import local.jarios.genericode.CodeList;
import local.jarios.mappers.MapperRegistroGcFromCodeList;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GenericodeCatalogContractTest {

  @Test
  void allBundledGenericodeFilesCanBeParsedAndMapped() {
    File[] files = FileHelper.getListaFicherosFromPath("data/gc");

    assertThat(files)
        .hasSize(102)
        .allMatch(File::isFile)
        .allMatch(File::canRead);

    Log logEntity = new Log();

    Arrays.stream(files)
        .sorted(Comparator.comparing(File::getName))
        .forEach(file -> assertGenericodeFileCanBeMapped(file, logEntity));
  }

  private void assertGenericodeFileCanBeMapped(File file, Log logEntity) {
    CodeList codeList = CodeListHelper.getCodeListFromFile(file);

    assertThat(codeList.getIdentification())
        .as("identification for %s", file.getName())
        .isNotNull();

    FicheroGc ficheroGc = CodeListHelper.getFicheroGc(logEntity, codeList);

    assertThat(ficheroGc)
        .as("mapped FicheroGc for %s", file.getName())
        .isNotNull();
    assertThat(ficheroGc.getShortName())
        .as("shortName for %s", file.getName())
        .isNotBlank();

    List<RegistroGc> registros = MapperRegistroGcFromCodeList
        .getListRegistroGcFromCodeList(codeList, logEntity.getId());

    assertThat(registros)
        .as("registros for %s", file.getName())
        .isNotEmpty()
        .allSatisfy(registro -> {
          assertThat(registro.getCode()).isNotBlank();
          assertThat(registro.getNombre()).isNotBlank();
        });
  }
}
