//GUI responsible for displaying the selected date's information and offering edit/delete options

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateGUI extends JFrame {
    private DateUpdateListener updateListener;
    private DateManager dateManager;
    private Date date;
    private JLabel dateLabel;
    private JButton editButton;
    private JButton exitButton;
    private JButton deleteButton;

    public DateGUI(JFrame owner, DateManager dateManager, Date selectedDate, DateUpdateListener updateListener) {
        super("Edit Date Details");
        this.dateManager = dateManager;
        this.date = selectedDate;
        this.updateListener = updateListener;
        initializeUI();
    }

    // Initialize the UI
    private void initializeUI() {
        setSize(600, 800);
        setPreferredSize(new Dimension(300, 400));
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        dateLabel = new JLabel("", SwingConstants.CENTER);
        updateDateLabel();

        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            this.dispose();
        });

        editButton = new JButton("Edit");
        editButton.addActionListener(this::handleEdit);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::handleDelete);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(exitButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
        add(dateLabel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
    }

    // Calculates and displays the date difference from today
    private void updateDateLabel() {
        LocalDate today = LocalDate.now();
        LocalDate dateOccurrence = date.getDay().atYear(today.getYear());
        if (dateOccurrence.isBefore(today)) {
            dateOccurrence = dateOccurrence.plusYears(1);
        }
        long daysBetween = ChronoUnit.DAYS.between(today, dateOccurrence);
        String nameText = (date.getName() != null && !date.getName().trim().isEmpty()) ? date.getName() : "";

        // Create HTML content for label with centered text
        // HTML content with styling for different sizes
        String htmlContent = "<html><div style='text-align: center;'>";

        if (!nameText.isEmpty()) {
            htmlContent += "<h1>" + nameText + "</h1>";
        }

        htmlContent += "<h1>" + daysBetween + "</h1>";
        htmlContent += "<h3> Days from today </h3>";
        htmlContent += "</div></html>";

        dateLabel.setText(htmlContent);
    }

    // Lets the user edit the date if they click on the edit button
    private void handleEdit(ActionEvent e) {
        // Create and show the EditDateGUI
        EditDateGUI editDateGUI = new EditDateGUI(this, dateManager, date, new DateUpdateListener() {
            @Override
            public void onDateUpdated() {

                dateUpdated(date); // Update the DateGUI with the new date
                if (updateListener != null) {
                    updateListener.onDateUpdated(); // Inform the MainGUI
                }
            }
        });
        editDateGUI.setVisible(true);
    }

    // Method to be called to update the date if it's been edited
    public void dateUpdated(Date date) {
        this.date = date;
        updateDateLabel();
    }

    // Lets the user delete the date if they click on the delete button
    private void handleDelete(ActionEvent e) {
        // Ask user to confirm deletion
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this date?", "Confirm Deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Delete the date
            dateManager.removeDate(date);

            // Inform the main GUI to update the table
            if (updateListener != null) {
                updateListener.onDateUpdated();
            }

            dispose();
        }
    }
}
