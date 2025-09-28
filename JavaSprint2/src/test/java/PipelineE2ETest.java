import com.fasterxml.jackson.databind.JsonNode;
import io.JsonFileReader;
import csv.CsvFileWriter;
import model.JsonToRows;
import org.apache.commons.csv.QuoteMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineE2ETest {

    @Test
    void endToEnd_ok(@TempDir Path tmp) throws Exception {
        // 1) Preparo JSON temp
        Path json = tmp.resolve("people.json");
        String data = """
        [
          {"id":"1","name":"Ana","age":30,"contact":{"email":"ana@example.com"}},
          {"id":"2","name":"Luis","age":17}
        ]
        """;
        Files.writeString(json, data);

        // 2) Leo
        JsonNode root = JsonFileReader.readTree(json);

        // 3) Transformo
        var rows = JsonToRows.people(root);
        String[] headers = {"id","name","age","email","isAdult"};

        // 4) Escribo
        Path out = tmp.resolve("out.csv");
        var opts = new CsvFileWriter.Options(';', QuoteMode.MINIMAL, "\n", "", null, null, null);
        CsvFileWriter.write(out, headers, rows, opts);

        // 5) Verifico
        String csv = Files.readString(out);
        assertTrue(csv.contains("id;name;age;email;isAdult"));
        assertTrue(csv.contains("1;Ana;30;ana@example.com;true"));
        assertTrue(csv.contains("2;Luis;17;;false"));
    }
}
