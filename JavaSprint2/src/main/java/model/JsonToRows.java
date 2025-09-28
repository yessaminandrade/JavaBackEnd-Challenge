package model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

/** Mapea el JSON a filas [id, name, age, email, isAdult]. */
public final class JsonToRows {

    public static class ValidationException extends Exception {
        public ValidationException(String msg) { super(msg); }
    }

    public static List<List<?>> people(JsonNode root) throws ValidationException {
        List<List<?>> rows = new ArrayList<>();
        JsonNode arr = root.isArray() ? root : root.path("people");
        if (!arr.isArray()) {
            throw new ValidationException("Se esperaba un arreglo raíz o el nodo 'people'.");
        }

        for (JsonNode n : arr) {
            String id    = text(n, "id");
            String name  = text(n, "name");
            Integer age  = n.hasNonNull("age") ? n.path("age").asInt() : null;
            String email = n.path("contact").path("email").asText("");

            boolean isAdult = age != null && age >= 18;
            rows.add(List.of(id, name, age, email, isAdult));
        }
        return rows;
    }

    private static String text(JsonNode n, String field) {
        return n.hasNonNull(field) ? n.get(field).asText() : "";
    }

    private JsonToRows() {}
}


