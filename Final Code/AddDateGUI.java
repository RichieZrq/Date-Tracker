//GUI responsible for adding a date to the system

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddDateGUI extends JDialog {
    private MainGUI mainGUI;
    private JTextField nameField;
    private JTextField monthField;
    private JTextField dayField;
    private JCheckBox stickiedCheckBox;
    private JButton addButton;
    private JButton selectTypeButton;
    private DateManager dateManager;
    private String selectedType = "Select Type"; // Default Display of the type button

    public AddDateGUI(JFrame parentFrame, DateManager dateManager, MainGUI mainGUI) {
        super(parentFrame, "Add Date", true);
        this.dateManager = dateManager;
        this.mainGUI = mainGUI;
        initializeUI();
        pack();
        setLocationRelativeTo(parentFrame);
    }

    // Initialize the UI
    private void initializeUI() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Grid panel for the input fields and labels
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // rows, cols, hgap, vgap

        gridPanel.add(new JLabel("Name:"));
        nameField = new JTextField(10);
        gridPanel.add(nameField);

        gridPanel.add(new JLabel("Month (enter number):"));
        monthField = new JTextField(2);
        gridPanel.add(monthField);

        gridPanel.add(new JLabel("Day:"));
        dayField = new JTextField(2);
        gridPanel.add(dayField);

        gridPanel.add(new JLabel("Sticky on top?"));
        stickiedCheckBox = new JCheckBox();
        gridPanel.add(stickiedCheckBox);

        // Add the "Select Type" label in the left column of the grid
        gridPanel.add(new JLabel("Select Type:"));

        // Type selection button
        selectTypeButton = new JButton(selectedType);
        selectTypeButton.addActionListener(this::handleSelectType);
        gridPanel.add(selectTypeButton); // Add the button in the right column of the grid

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Add date button
        addButton = new JButton("Add Date");
        addButton.addActionListener(this::handleAddDate);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Update the button text or another label to show the selected type
    public void setSelectedType(String type) {
        selectedType = type;
        selectTypeButton.setText(type);
    }

    // Allows user to select a type in the selecttypeGUI
    private void handleSelectType(ActionEvent e) {
        TypeSelectGUI typeSelectGUI = new TypeSelectGUI(this, dateManager);
        typeSelectGUI.setVisible(true);
        selectedType = typeSelectGUI.getSelectedType();
        if (selectedType != null && !selectedType.trim().isEmpty()) {
            selectTypeButton.setText(selectedType);
        } else {
            selectTypeButton.setText("Select Type");
        }
    }

    // Adds the date to the system
    private void handleAddDate(ActionEvent e) {
        try {
            int month = Integer.parseInt(monthField.getText());
            int day = Integer.parseInt(dayField.getText());
            String name = nameField.getText();
            boolean stickied = stickiedCheckBox.isSelected();

            // Construct the new Date object and add it to the manager
            Date newDate = new Date(name, month, day, selectedType, stickied);
            dateManager.addOrUpdateDate(newDate);

            mainGUI.onDateUpdated(); // refresh the MainGUI table

            dispose(); // Close the dialog after adding
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid date values.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
