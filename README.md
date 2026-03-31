# Den Glade Bondemand

Semesterprojekt for 2. semester.

Projektet handler om lagerstyring, salgsregistrering og reservationssystem for Den Glade Bondemand.

## Projektstruktur

- `src/`: Java-kildekode
- `test/`: testkode og testnoter
- `resources/`: konfiguration og statiske filer
- `docs/`: rapportmateriale, interview, undervisningsmateriale, UML og templates
- `sql/`: DDL-scripts og seed/testdata
- `lib/`: eksterne biblioteker, hvis I senere vælger at lægge JAR-filer i repoet

## Pakker

- `dgm.app`: applikationsstart
- `dgm.ui`: Swing-brugerflade
- `dgm.ui.panel`: genbrugelige GUI-paneler
- `dgm.controller`: controllers
- `dgm.database`: databaseadgang
- `dgm.model`: domænemodel

## Eclipse

Projektet er sat op som et almindeligt Eclipse Java-projekt med:

- `.project`
- `.classpath`
- `.settings/`

Importer det i Eclipse via `File -> Import -> Existing Projects into Workspace`.

Kør applikationen via `dgm.app.App`.

## Næste naturlige skridt

1. Læg undervisningsmateriale i `docs/teaching-material/`
2. Læg AI-genererede skabeloner og noter i `docs/templates/`
3. Begynd domænemodellen i `docs/uml/`
4. Læg første SQL-udkast i `sql/ddl/`

