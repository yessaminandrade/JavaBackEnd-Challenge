package model;

import org.apache.commons.csv.QuoteMode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Configuración de la app. Carga defaults, app.properties y luego argumentos CLI. */
public final class AppConfig {
    public final Path in;
    public final Path out;
    public final char delimiter;
    public final QuoteMode quoteMode;

    public AppConfig(Path in, Path out, char delimiter, QuoteMode quoteMode) {
        this.in = in;
        this.out = out;
        this.delimiter = delimiter;
        this.quoteMode = quoteMode;
    }

    public static AppConfig load(String[] args) {
        // Defaults
        Path in = Path.of("data/people.json");
        Path out = Path.of("data/out.csv");
        char delim = ';';
        QuoteMode quote = QuoteMode.MINIMAL;

        // 1) app.properties (opcional) en el directorio de trabajo
        Path propsPath = Path.of("app.properties");
        if (Files.exists(propsPath)) {
            Properties p = new Properties();
            try (InputStream is = Files.newInputStream(propsPath)) {
                p.load(is);
                if (p.getProperty("in") != null)    in    = Path.of(p.getProperty("in"));
                if (p.getProperty("out") != null)   out   = Path.of(p.getProperty("out"));
                if (p.getProperty("delimiter") != null && !p.getProperty("delimiter").isEmpty())
                    delim = p.getProperty("delimiter").charAt(0);
                if (p.getProperty("quote") != null)
                    quote = QuoteMode.valueOf(p.getProperty("quote").toUpperCase());
            } catch (IOException ignored) {}
        }

        // 2) CLI (tiene prioridad)
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.startsWith("--in="))        in = Path.of(a.substring(5));
            else if (a.equals("--in")  && i+1 < args.length)  in = Path.of(args[++i]);
            else if (a.startsWith("--out="))  out = Path.of(a.substring(6));
            else if (a.equals("--out") && i+1 < args.length)  out = Path.of(args[++i]);
            else if (a.startsWith("--delim=")) delim = a.substring(8).charAt(0);
            else if (a.equals("--delim") && i+1 < args.length) delim = args[++i].charAt(0);
            else if (a.startsWith("--quote=")) quote = QuoteMode.valueOf(a.substring(8).toUpperCase());
            else if (a.equals("--quote") && i+1 < args.length) quote = QuoteMode.valueOf(args[++i].toUpperCase());
        }
        return new AppConfig(in, out, delim, quote);
    }
}


