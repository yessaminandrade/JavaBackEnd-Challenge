// (sin package)

import com.fasterxml.jackson.databind.JsonNode;
import io.JsonFileReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonFileReaderTest {

    @Test
    void readTree_ok(@TempDir Path tmp) throws Exception {
        // Crear un JSON temporal
        Path file = tmp.resolve("sample.json");
        Files.writeString(file, "{\"name\":\"Ana\",\"age\":30}");

        // Leer con tu utilidad existente
        JsonNode node = JsonFileReader.readTree(file);

        // Validar
        assertEquals("Ana", node.path("name").asText());
        assertEquals(30, node.path("age").asInt());
    }
}

