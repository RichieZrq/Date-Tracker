//Responsible for reading data and saving changes by interacting with 3 dat files that serve as the database

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class FileDatabaseHelper {
    // Files for data storage
    public static final String STICKIED_DATES_FILE = "stickied_dates.dat";
    public static final String UNSTICKIED_DATES_FILE = "unstickied_dates.dat";
    public static final String CUSTOM_TYPES_FILE = "custom_types.dat";

    // If there are no dates in the system, add the example dates
    private static void initializeWithDefaultData(String filename) {
        List<Date> defaultDates = new ArrayList<>();
        Set<String> defaultTypes = new HashSet<>();

        // Add default types
        defaultTypes.add("holiday");
        defaultTypes.add("work");

        if (filename.equals(STICKIED_DATES_FILE)) {
            defaultDates.add(new Date("New Year", 1, 1, "holiday", true));
        } else if (filename.equals(UNSTICKIED_DATES_FILE)) {
            defaultDates.add(new Date("Christmas", 12, 25, "holiday", false));
            defaultDates.add(new Date("First Day of Class", 9, 1, "work", false));
        }

        writeDatesToFile(defaultDates, filename);

        // Save the default types to the types file
        writeCustomTypesToFile(defaultTypes);
    }

    // Saves the dates to files
    public static void writeDatesToFile(List<Date> dates, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(dates);
        } catch (IOException e) {
            System.err.println("Error writing to " + filename + ": " + e.getMessage());
        }
    }

    // Saves the types to the file
    public static void writeCustomTypesToFile(Set<String> customTypes) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CUSTOM_TYPES_FILE))) {
            oos.writeObject(customTypes);
        } catch (IOException e) {
            System.err.println("Error writing custom types to file: " + e.getMessage());
        }
    }

    // Retrieve existing types from the file
    public static Set<String> readCustomTypesFromFile() {
        File file = new File(CUSTOM_TYPES_FILE);
        if (!file.exists()) {
            return new HashSet<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Set<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading custom types from file: " + e.getMessage());
            return new HashSet<>();
        }
    }

    // Retrieve existing dates from the file
    public static List<Date> readDatesFromFile(String filename) {
        List<Date> dates = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) {
            // Initialize with default data if file not found
            initializeWithDefaultData(filename);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            dates = (List<Date>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.err.println("File not found after creation: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading from " + filename + ": " + e.getMessage());
        }

        return dates;
    }
}
