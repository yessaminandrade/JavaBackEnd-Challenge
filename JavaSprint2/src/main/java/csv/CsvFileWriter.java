package csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CsvFileWriter {

    private CsvFileWriter() {}

    /** Excepción de dominio para errores al escribir CSV */
    public static class CsvWriteException extends Exception {
        public CsvWriteException(String message, Throwable cause) { super(message, cause); }
    }

    public static final class Options {
        public final char delimiter;
        public final QuoteMode quoteMode;
        public final String recordSeparator;
        public final String nullString;
        public final DateTimeFormatter dateFormatter;
        public final DateTimeFormatter dateTimeFormatter;
        public final DecimalFormat numberFormat;

        public Options(char delimiter,
                       QuoteMode quoteMode,
                       String recordSeparator,
                       String nullString,
                       DateTimeFormatter dateFormatter,
                       DateTimeFormatter dateTimeFormatter,
                       DecimalFormat numberFormat) {
            this.delimiter = delimiter;
            this.quoteMode = quoteMode;
            this.recordSeparator = recordSeparator;
            this.nullString = nullString;
            this.dateFormatter = dateFormatter;
            this.dateTimeFormatter = dateTimeFormatter;
            this.numberFormat = numberFormat;
        }

        /** Opciones por defecto: delimitador coma, quoting mínimo, ISO para fechas */
        public static Options defaults() {
            return new Options(
                    ',',
                    QuoteMode.MINIMAL,
                    System.lineSeparator(),
                    "",                                 // cómo representar nulls
                    DateTimeFormatter.ISO_LOCAL_DATE,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                    new DecimalFormat("#.##########")   // números sin notación científica
            );
        }
    }

    public static void write(Path path, String[] headers, List<? extends List<?>> rows, Options opts)
            throws CsvWriteException {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            CSVFormat fmt = CSVFormat.DEFAULT.builder()
                    .setHeader(headers)
                    .setDelimiter(opts.delimiter)
                    .setQuoteMode(opts.quoteMode)
                    .setRecordSeparator(opts.recordSeparator)
                    .setNullString(opts.nullString)
                    .build();

            try (BufferedWriter writer = Files.newBufferedWriter(
                    path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                 CSVPrinter printer = new CSVPrinter(writer, fmt)) {

                for (List<?> row : rows) {
                    List<Object> out = new ArrayList<>(row.size());
                    for (Object cell : row) {
                        out.add(formatCell(cell, opts));
                    }
                    printer.printRecord(out);
                }
            }
        } catch (IOException e) {
            throw new CsvWriteException("I/O error writing CSV: " + path, e);
        }
    }

    /** Versión simple con opciones por defecto */
    public static void write(Path path, String[] headers, List<? extends List<?>> rows)
            throws CsvWriteException {
        write(path, headers, rows, Options.defaults());
    }

    /**
     * Escribe un CSV a partir de una lista de mapas (usa las claves del primer mapa como encabezados)
     */
    public static void writeFromMaps(Path path, List<? extends Map<String, ?>> rows, Options opts)
            throws CsvWriteException {
        String[] headers = rows.isEmpty() ? new String[0] : rows.get(0).keySet().toArray(new String[0]);

        List<List<?>> ordered = new ArrayList<>(rows.size());
        for (Map<String, ?> map : rows) {
            List<Object> r = new ArrayList<>(headers.length);
            for (String h : headers) r.add(map.get(h));
            ordered.add(r);
        }
        write(path, headers, ordered, opts);
    }

    /* helpers */

    private static Object formatCell(Object v, Options opts) {
        if (v == null) return null;

        if (v instanceof Number n) {
            return opts.numberFormat != null ? opts.numberFormat.format(n) : n.toString();
        }
        if (v instanceof LocalDate d) {
            return opts.dateFormatter != null ? d.format(opts.dateFormatter) : d.toString();
        }
        if (v instanceof LocalDateTime dt) {
            return opts.dateTimeFormatter != null ? dt.format(opts.dateTimeFormatter) : dt.toString();
        }
        if (v instanceof OffsetDateTime odt) {
            return opts.dateTimeFormatter != null ? odt.format(opts.dateTimeFormatter) : odt.toString();
        }
        if (v instanceof ZonedDateTime zdt) {
            return opts.dateTimeFormatter != null ? zdt.format(opts.dateTimeFormatter) : zdt.toString();
        }
        return v.toString();
    }
}
