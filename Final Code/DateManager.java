//Manipulates Date objects to organize dates into stickied and unstickied lists and a set of types

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DateManager {
    private List<Date> stickiedDates;
    private List<Date> unstickiedDates;
    private Set<String> customTypes;
    private Comparator<Date> dateComparator;

    public DateManager() {
        this.dateComparator = createDateComparator();
        // Load dates from database
        stickiedDates = FileDatabaseHelper.readDatesFromFile(FileDatabaseHelper.STICKIED_DATES_FILE);
        unstickiedDates = FileDatabaseHelper.readDatesFromFile(FileDatabaseHelper.UNSTICKIED_DATES_FILE);
        customTypes = FileDatabaseHelper.readCustomTypesFromFile();
        if (customTypes.isEmpty()) {
            initializeCustomTypes();
        }
    }

    // Initialize sample types
    private void initializeCustomTypes() {
        customTypes.add("holiday");
        customTypes.add("work");
        FileDatabaseHelper.writeCustomTypesToFile(customTypes);
    }

    // Creates comparator for comparing dates
    private Comparator<Date> createDateComparator() {
        return (date1, date2) -> {
            // Compare based on how close the dates are to today
            MonthDay today = MonthDay.now();
            LocalDate date1NextOccurrence = date1.getDay()
                    .atYear(today.isAfter(date1.getDay()) ? LocalDate.now().getYear() + 1 : LocalDate.now().getYear());
            LocalDate date2NextOccurrence = date2.getDay()
                    .atYear(today.isAfter(date2.getDay()) ? LocalDate.now().getYear() + 1 : LocalDate.now().getYear());

            return date1NextOccurrence.compareTo(date2NextOccurrence);
        };
    }

    // Updates the date lists with the new passed in date
    public void addOrUpdateDate(Date date) {
        // Remove the date if it already exists
        stickiedDates.remove(date);
        unstickiedDates.remove(date);

        // Add the date to the appropriate list
        if (date.isStickied()) {
            stickiedDates.add(date);
        } else {
            unstickiedDates.add(date);
        }

        // Track the custom type
        addCustomType(date.getType());

        // Reorder the lists
        reorderList(stickiedDates);
        reorderList(unstickiedDates);
        saveDates();
    }

    // Reorders the given date list
    private void reorderList(List<Date> dates) {
        dates.sort(dateComparator);
    }

    // Adds a custom type to the customTypes set
    public void addCustomType(String type) {
        customTypes.add(type.toLowerCase());
    }

    // Returns all custom types
    public Set<String> getCustomTypes() {
        return new HashSet<>(customTypes);
    }

    // Returns all dates of the passed in type
    public List<Date> getDatesOfType(String type) {
        return stickiedDates.stream()
                .filter(date -> date.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // Returns all dates in the correct order
    public List<Date> getAllDates() {
        List<Date> allDates = new ArrayList<>();
        allDates.addAll(stickiedDates);
        allDates.addAll(unstickiedDates);
        return allDates;
    }

    // Method to find a date by its string representation (e.g., "mm/dd")
    public Date findDateByString(String dateString) {
        for (Date date : getAllDates()) {
            String formatted = date.getDay().getMonthValue() + "/" + date.getDay().getDayOfMonth();
            if (formatted.equals(dateString)) {
                return date;
            }
        }
        return null; // Date not found
    }

    public List<Date> getStickiedDates() {
        return new ArrayList<>(stickiedDates);
    }

    public List<Date> getUnstickiedDates() {
        return new ArrayList<>(unstickiedDates);
    }

    public List<Date> getSortedDates() {
        List<Date> allDates = new ArrayList<>();
        allDates.addAll(stickiedDates);
        allDates.addAll(unstickiedDates);

        // Sort the combined list: stickied dates first, then by date
        allDates.sort(Comparator.comparing(Date::isStickied).reversed()
                .thenComparing(dateComparator));

        return allDates;
    }

    // Removes a given date regardless of the list they are in
    public void removeDate(Date date) {
        stickiedDates.remove(date);
        unstickiedDates.remove(date);
        saveDates();
    }

    // Save changes to the database
    public void saveDates() {
        FileDatabaseHelper.writeDatesToFile(stickiedDates, FileDatabaseHelper.STICKIED_DATES_FILE);
        FileDatabaseHelper.writeDatesToFile(unstickiedDates, FileDatabaseHelper.UNSTICKIED_DATES_FILE);
        FileDatabaseHelper.writeCustomTypesToFile(customTypes);
    }
}
