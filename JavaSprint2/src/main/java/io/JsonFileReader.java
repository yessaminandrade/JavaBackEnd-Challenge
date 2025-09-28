package io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

public final class JsonFileReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonFileReader() {
        // util class
    }

    /** Lee un JSON y lo devuelve como árbol genérico */
    public static JsonNode readTree(Path path) throws JsonReadException {
        try {
            return MAPPER.readTree(Files.newBufferedReader(path));
        } catch (NoSuchFileException e) {
            throw new JsonReadException("File not found: " + path, e);
        } catch (JsonProcessingException e) {
            throw new JsonReadException("Invalid JSON format in: " + path, e);
        } catch (IOException e) {
            throw new JsonReadException("I/O error reading: " + path, e);
        }
    }

    /** Lee un JSON y lo mapea a un POJO del tipo indicado */
    public static <T> T readObject(Path path, Class<T> type) throws JsonReadException {
        try {
            return MAPPER.readValue(Files.newBufferedReader(path), type);
        } catch (NoSuchFileException e) {
            throw new JsonReadException("File not found: " + path, e);
        } catch (JsonProcessingException e) {
            throw new JsonReadException("Invalid JSON mapping for " + type.getSimpleName() + " in: " + path, e);
        } catch (IOException e) {
            throw new JsonReadException("I/O error reading: " + path, e);
        }
    }

    /** Lee un JSON que contiene una lista de objetos del tipo indicado */
    public static <T> List<T> readList(Path path, Class<T> elementType) throws JsonReadException {
        try {
            return MAPPER.readValue(
                    Files.newBufferedReader(path),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, elementType)
            );
        } catch (NoSuchFileException e) {
            throw new JsonReadException("File not found: " + path, e);
        } catch (JsonProcessingException e) {
            throw new JsonReadException("Invalid JSON list mapping for " + elementType.getSimpleName() + " in: " + path, e);
        } catch (IOException e) {
            throw new JsonReadException("I/O error reading: " + path, e);
        }
    }

    /** Lee usando TypeReference para tipos genéricos anidados */
    public static <T> T readWithTypeRef(Path path, TypeReference<T> typeRef) throws JsonReadException {
        try {
            return MAPPER.readValue(Files.newBufferedReader(path), typeRef);
        } catch (NoSuchFileException e) {
            throw new JsonReadException("File not found: " + path, e);
        } catch (JsonProcessingException e) {
            throw new JsonReadException("Invalid JSON mapping via TypeReference in: " + path, e);
        } catch (IOException e) {
            throw new JsonReadException("I/O error reading: " + path, e);
        }
    }

    /** Excepción de dominio para centralizar errores de lectura JSON */
    public static class JsonReadException extends Exception {
        public JsonReadException(String msg, Throwable cause) { super(msg, cause); }
    }
}



