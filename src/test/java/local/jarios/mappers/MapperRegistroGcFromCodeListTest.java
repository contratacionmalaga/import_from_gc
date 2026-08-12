package local.jarios.mappers;

import local.jarios.genericode.CodeList;
import local.jarios.genericode.Row;
import local.jarios.genericode.SimpleCodeList;
import local.jarios.genericode.Value;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MapperRegistroGcFromCodeListTest {

  @Test
  void mapsRowsWithCodeAndNombreValues() {
    UUID logId = UUID.randomUUID();
    CodeList codeList = codeListWithRows(
        row(value("code", "A"), value("nombre", "Activo")),
        row(value("code", "B"), value("nombre", "Borrador")));

    var registros = MapperRegistroGcFromCodeList
        .getListRegistroGcFromCodeList(codeList, logId);

    assertThat(registros)
        .hasSize(2)
        .extracting("code", "nombre", "logId")
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple("A", "Activo", logId),
            org.assertj.core.groups.Tuple.tuple("B", "Borrador", logId));
  }

  @Test
  void ignoresNameColumnAndUnknownColumns() {
    UUID logId = UUID.randomUUID();
    CodeList codeList = codeListWithRows(
        row(
            value("code", "A"),
            value("name", "Ignored name"),
            value("unknown", "Ignored value"),
            value("nombre", "Activo")));

    var registros = MapperRegistroGcFromCodeList
        .getListRegistroGcFromCodeList(codeList, logId);

    assertThat(registros)
        .singleElement()
        .satisfies(registro -> {
          assertThat(registro.getCode()).isEqualTo("A");
          assertThat(registro.getNombre()).isEqualTo("Activo");
          assertThat(registro.getLogId()).isEqualTo(logId);
        });
  }

  @Test
  void skipsRowsWithoutRequiredValues() {
    CodeList codeList = codeListWithRows(
        row(value("code", "A")),
        row(value("nombre", "Activo")),
        row(value("code", "B"), value("nombre", "Borrador")));

    var registros = MapperRegistroGcFromCodeList
        .getListRegistroGcFromCodeList(codeList, UUID.randomUUID());

    assertThat(registros)
        .singleElement()
        .satisfies(registro -> {
          assertThat(registro.getCode()).isEqualTo("B");
          assertThat(registro.getNombre()).isEqualTo("Borrador");
        });
  }

  @Test
  void returnsEmptyListWhenCodeListHasNoRows() {
    assertThat(MapperRegistroGcFromCodeList.getListRegistroGcFromCodeList(null, UUID.randomUUID()))
        .isEmpty();
    assertThat(MapperRegistroGcFromCodeList
        .getListRegistroGcFromCodeList(new CodeList(), UUID.randomUUID()))
        .isEmpty();
  }

  private CodeList codeListWithRows(Row... rows) {
    SimpleCodeList simpleCodeList = new SimpleCodeList();
    simpleCodeList.setRow(List.of(rows));

    CodeList codeList = new CodeList();
    codeList.setSimpleCodeList(simpleCodeList);
    return codeList;
  }

  private Row row(Value... values) {
    Row row = new Row();
    row.setValues(List.of(values));
    return row;
  }

  private Value value(String columnRef, String simpleValue) {
    Value value = new Value();
    value.setColumnRef(columnRef);
    value.setSimpleValue(simpleValue);
    return value;
  }
}
