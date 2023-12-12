//The MainGUI of the system that the user sees when starting the application. Contains the main method.

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainGUI implements DateUpdateListener {
    private DateManager dateManager;
    private JFrame frame;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private JTable dateTable;
    private JButton addButton;
    private JButton viewByTypeButton;
    private JButton calculateDaysButton;
    private DefaultTableModel tableModel;
    private List<Date> currentlyDisplayedDates;
    private String currentFilterType = "All";

    public MainGUI() {
        dateManager = new DateManager();
        currentlyDisplayedDates = new ArrayList<>();
        initializeUI();
    }

    // Initialize the UI
    private void initializeUI() {
        frame = new JFrame("Date Tracker Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Table setup
        String[] columnNames = { "Name", "Date", "Days From Today", "Stickied" }; // Add "Stickied" column
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class; // The 'Stickied' column will contain Boolean values
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        dateTable = new JTable(tableModel);
        dateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set the custom renderer for the table to highlight stickied dates
        dateTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                Boolean isStickied = (Boolean) table.getModel().getValueAt(row, 3); // Get the stickied status
                if (isStickied != null && isStickied) {
                    c.setBackground(new Color(255, 230, 170));
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                }
                return c;
            }
        });

        // Hide the 'Stickied' column from view
        dateTable.removeColumn(dateTable.getColumnModel().getColumn(3));

        scrollPane = new JScrollPane(dateTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        titleLabel = new JLabel("Your Dates", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Add buttons
        addButton = new JButton("Add Date");
        addButton.addActionListener(e -> {
            AddDateGUI addDateGUI = new AddDateGUI(frame, dateManager, MainGUI.this);
            addDateGUI.setVisible(true);
        });

        viewByTypeButton = new JButton("View by Type");
        viewByTypeButton.addActionListener(e -> {
            ViewByTypeGUI viewByTypeGUI = new ViewByTypeGUI(frame, dateManager.getCustomTypes(), MainGUI.this);
            viewByTypeGUI.setVisible(true);
        });

        calculateDaysButton = new JButton("Calculate Days");
        calculateDaysButton.addActionListener(e -> {
            CalculateDaysGUI calculateDaysGUI = new CalculateDaysGUI(frame);
            calculateDaysGUI.setVisible(true);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(viewByTypeButton);
        bottomPanel.add(addButton);
        bottomPanel.add(calculateDaysButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dateManager.saveDates(); // Save dates to file
            }
        });

        // Add mouse listener for row selection
        dateTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int viewRowIndex = dateTable.getSelectedRow();
                    if (viewRowIndex >= 0) {
                        // Use the currently displayed list to get the correct Date object
                        Date selectedDate = currentlyDisplayedDates.get(viewRowIndex);
                        if (selectedDate != null) {
                            DateGUI dateGUI = new DateGUI(frame, dateManager, selectedDate, MainGUI.this);
                            dateGUI.setVisible(true);
                        }
                    }
                }
            }
        });

        updateDateTable(currentlyDisplayedDates);
        filterDatesByType(currentFilterType);
        frame.setVisible(true);
    }

    // Update the JTable model to reflect the new order
    public void onDateUpdated() {
        filterDatesByType(currentFilterType);
    }

    // Displays dates on the date table
    public void displayDates(List<Date> dates) {
        updateDateTable(dates);
    }

    // Updates the table if there are any changes
    private void updateDateTable(List<Date> dates) {
        currentlyDisplayedDates.clear();
        currentlyDisplayedDates.addAll(dates);
        tableModel.setRowCount(0);
        for (Date date : dates) {
            addDateToTable(date, date.isStickied());
        }
    }

    // Inserts a date to the table in its correct order
    private void addDateToTable(Date date, boolean isStickied) {
        LocalDate today = LocalDate.now();
        LocalDate nextOccurrence = date.getDay().atYear(today.getYear());
        if (nextOccurrence.isBefore(today)) {
            nextOccurrence = nextOccurrence.plusYears(1);
        }
        long daysFromToday = ChronoUnit.DAYS.between(today, nextOccurrence);
        String displayName = date.getName() != null ? date.getName() : "Unnamed";
        String displayDate = date.getDay().getMonthValue() + "/" + date.getDay().getDayOfMonth();

        // Add a row to the table model with the date information
        tableModel.addRow(new Object[] { displayName, displayDate, daysFromToday, isStickied });
    }

    // Filters table so it shows only the selected type
    public void filterDatesByType(String type) {
        currentFilterType = type;
        List<Date> filteredDates;
        if ("All".equals(type)) {
            filteredDates = dateManager.getAllDates();
        } else {
            filteredDates = dateManager.getAllDates().stream()
                    .filter(date -> type.equals(date.getType()))
                    .collect(Collectors.toList());
        }
        updateDateTable(filteredDates);
    }

    // main method that runs the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainGUI().frame.setVisible(true);
            }
        });
    }
}
