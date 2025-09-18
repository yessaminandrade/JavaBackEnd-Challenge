# JavaBackEnd-Challenge

Small Java desktop app that reads JSON with Jackson library and writes CSV with Apache Commons CSV.  

## Tech
Java 17, Maven, Jackson, Databind, Apache Commons CSV

## Structure
- `io.JsonFileReader` – JSON reading with domain exception.
- `csv.CsvFileWriter` – CSV writer with configurable delimiter/format.
- `com.nao.sprint2.Main` – Orchestrates JSON, rows and CSV.
