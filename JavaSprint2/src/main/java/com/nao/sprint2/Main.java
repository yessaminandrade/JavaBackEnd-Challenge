package com.nao.sprint2;

import com.fasterxml.jackson.databind.JsonNode;
import io.JsonFileReader;
import csv.CsvFileWriter;
import csv.CsvFileWriter.Options;
import org.apache.commons.csv.QuoteMode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Path jsonIn = Path.of(args.length > 0 ? args[0] : "data/people.json");
        Path csvOut = Path.of(args.length > 1 ? args[1] : "data/out.csv");

        try {
            // Leer JSON
            JsonNode root = JsonFileReader.readTree(jsonIn);

            // Transformar a filas (name, age)
            List<List<?>> rows = new ArrayList<>();
            JsonNode array = root.isArray() ? root : root.path("people");
            if (array.isArray()) {
                for (JsonNode item : array) {
                    String name = item.path("name").asText();
                    int age = item.path("age").asInt();
                    rows.add(List.of(name, age));
                }
            }

            // Escribir CSV
            String[] headers = {"name", "age"};
            Options opts = new Options(';', QuoteMode.MINIMAL,
                    System.lineSeparator(), "", null, null, null);

            CsvFileWriter.write(csvOut, headers, rows, opts);
            System.out.println("CSV generado en: " + csvOut.toAbsolutePath());

        } catch (JsonFileReader.JsonReadException e) {
            System.err.println("Error leyendo JSON: " + e.getMessage());
        } catch (CsvFileWriter.CsvWriteException e) {
            System.err.println("Error escribiendo CSV: " + e.getMessage());
        }
    }
}


