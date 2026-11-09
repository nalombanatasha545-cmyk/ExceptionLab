import java.io.*;
import java.util.*;
import java.util.logging.*;

public class MarksImporter {

    private static final Logger LOGGER =
            Logger.getLogger(MarksImporter.class.getName());

    // Custom exception: extends RuntimeException, so it is unchecked
    static class DuplicateStudentException extends RuntimeException {
        public DuplicateStudentException(String studentId) {
            super("Duplicate student ID: " + studentId);
        }
    }

    static void importMarks(String filename) throws IOException {
        Set<String> seen = new HashSet<>();

        // try-with-resources: the BufferedReader is declared in the parentheses,
        // so Java automatically calls br.close() when the try block ends,
        // whether it finishes normally or an exception occurs.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(",");

                    // TODO 1: check there are exactly 2 fields
                    if (parts.length != 2) {
                        throw new IllegalArgumentException(
                                "Expected 2 fields but found " + parts.length);
                    }

                    String id = parts[0].trim();

                    // TODO 2: parse mark and validate range 0..100
                    int mark = Integer.parseInt(parts[1].trim());
                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException(
                                "Mark out of range (0-100): " + mark);
                    }

                    // TODO 3: detect repeated student ID using seen
                    if (!seen.add(id)) {
                        throw new DuplicateStudentException(id);
                    }

                    // TODO 4: display accepted record
                    System.out.println("Accepted: " + id + " -> " + mark);

                } catch (NumberFormatException e) {
                    System.out.println("Line " + lineNumber
                            + ": mark is not a number. Record skipped.");
                    LOGGER.log(Level.WARNING, "NumberFormatException on line "
                            + lineNumber + ": " + e.getMessage());
                } catch (DuplicateStudentException e) {
                    System.out.println("Line " + lineNumber
                            + ": duplicate student ID. Record skipped.");
                    LOGGER.log(Level.WARNING, "DuplicateStudentException on line "
                            + lineNumber + ": " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Line " + lineNumber
                            + ": invalid record. Record skipped.");
                    LOGGER.log(Level.WARNING, "IllegalArgumentException on line "
                            + lineNumber + ": " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            importMarks("marks.txt");
        } catch (FileNotFoundException e) {
            // Must come BEFORE IOException (it is the more specific type)
            System.out.println("Marks file not found; check the file name.");
            LOGGER.log(Level.SEVERE, "FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not read the marks file. Please try again.");
            LOGGER.log(Level.SEVERE, "IOException: " + e.getMessage());
        } catch (DuplicateStudentException e) {
            System.out.println("A duplicate student was found.");
            LOGGER.log(Level.WARNING, "DuplicateStudentException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data was found in the file.");
            LOGGER.log(Level.WARNING, "IllegalArgumentException: " + e.getMessage());
        } finally {
            System.out.println("Import attempt finished");
        }
    }
}