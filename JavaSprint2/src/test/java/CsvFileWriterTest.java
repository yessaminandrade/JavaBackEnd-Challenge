
import csv.CsvFileWriter;
import org.apache.commons.csv.QuoteMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvFileWriterTest {

    @Test
    void write_ok(@TempDir Path tmp) throws Exception {
        Path out = tmp.resolve("out.csv");
        String[] headers = {"id","name","age"};
        List<List<?>> rows = List.of(
                List.of("1","Ana",30),
                List.of("2","Luis",17)
        );
        var opts = new CsvFileWriter.Options(';', QuoteMode.MINIMAL, "\n", "", null, null, null);

        CsvFileWriter.write(out, headers, rows, opts);

        String content = Files.readString(out).trim();
        assertTrue(content.startsWith("id;name;age"));
        assertTrue(content.contains("1;Ana;30"));
    }
}

