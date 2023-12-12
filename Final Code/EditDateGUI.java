
//GUI responsible for allowing users to edit a chosen date's attributes
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.DateTimeException;
import java.time.MonthDay;

public class EditDateGUI extends JDialog {
    private DateUpdateListener updateListener;
    private JTextField nameField;
    private JTextField monthField;
    private JTextField dayField;
    private JCheckBox stickiedCheckBox;
    private JButton saveButton;
    private JButton selectTypeButton;
    private DateManager dateManager;
    private Date date;
    private String selectedType; // Display the current type of the date

    public EditDateGUI(Frame owner, DateManager dateManager, Date date, DateUpdateListener updateListener) {
        super(owner, "Edit Date", true);
        this.dateManager = dateManager;
        this.date = date;
        this.selectedType = date.getType(); // Initialize with the current type
        this.updateListener = updateListener;
        initializeUI();
        pack();
        setLocationRelativeTo(owner);
    }

    // Initialize the UI
    private void initializeUI() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Grid panel for the input fields and labels
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        gridPanel.add(new JLabel("Name:"));
        nameField = new JTextField(date.getName(), 10);
        gridPanel.add(nameField);

        gridPanel.add(new JLabel("Month (enter number):"));
        monthField = new JTextField(String.valueOf(date.getDay().getMonthValue()), 2);
        gridPanel.add(monthField);

        gridPanel.add(new JLabel("Day:"));
        dayField = new JTextField(String.valueOf(date.getDay().getDayOfMonth()), 2);
        gridPanel.add(dayField);

        gridPanel.add(new JLabel("Sticky on top?"));
        stickiedCheckBox = new JCheckBox("", date.isStickied());
        gridPanel.add(stickiedCheckBox);

        gridPanel.add(new JLabel("Select Type:"));

        // Type selection button
        selectTypeButton = new JButton(selectedType);
        selectTypeButton.addActionListener(this::handleSelectType);
        gridPanel.add(selectTypeButton); // Add the button in the right column of the grid

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Save button
        saveButton = new JButton("Save Changes");
        saveButton.addActionListener(this::handleSave);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Update the button text or another label to show the selected type
    public void setSelectedType(String type) {
        this.selectedType = type;
        this.selectTypeButton.setText(type);
    }

    // Allows user to select a type in the selecttypeGUI
    private void handleSelectType(ActionEvent e) {
        TypeSelectGUI typeSelectGUI = new TypeSelectGUI(this, dateManager);
        typeSelectGUI.setVisible(true);
        // Update the selected type from the type selection dialog
        selectedType = typeSelectGUI.getSelectedType();
        if (selectedType != null && !selectedType.trim().isEmpty()) {
            selectTypeButton.setText(selectedType);
        }
    }

    // Saves the changes to the system
    private void handleSave(ActionEvent e) {
        try {
            String name = nameField.getText();
            int month = Integer.parseInt(monthField.getText());
            int day = Integer.parseInt(dayField.getText());
            String type = selectedType;
            boolean stickied = stickiedCheckBox.isSelected();

            // Validate the month and day values
            MonthDay.of(month, day);

            date.setName(name);
            date.setDay(month, day);
            date.setType(type);
            date.setStickied(stickied);

            // Update the date in the date manager
            dateManager.addOrUpdateDate(date);

            // Notify the MainGUI that a date has been updated
            if (updateListener != null) {
                updateListener.onDateUpdated();
            }

            dispose();
        } catch (DateTimeException exception) {
            JOptionPane.showMessageDialog(this, "Invalid date. Please enter a valid month and day.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Please enter numeric values for month and day.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
