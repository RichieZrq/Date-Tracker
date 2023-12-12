//GUI responsible for displaying all current types, allows users to add new types, and lets them select to change to a new type.

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class TypeSelectGUI extends JDialog {
    private DefaultTableModel tableModel;
    private JTable typeTable;
    private DateManager dateManager;
    private String selectedType;
    private JScrollPane scrollPane;
    private JButton addTypeButton;
    private JButton selectTypeButton;

    public TypeSelectGUI(JDialog owner, DateManager dateManager) {
        super(owner, "Select Type", true);
        this.dateManager = dateManager;

        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[] { "Types" }, 0);
        typeTable = new JTable(tableModel);
        typeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Fill the table with types
        updateTypeTable();

        scrollPane = new JScrollPane(typeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Input for new type
        JPanel newTypePanel = new JPanel();
        newTypePanel.setLayout(new BoxLayout(newTypePanel, BoxLayout.PAGE_AXIS));
        newTypePanel.add(new JLabel("Add a new type:"));
        JTextField newTypeField = new JTextField();
        newTypePanel.add(newTypeField);

        // Button to add new type
        addTypeButton = new JButton("Add Type");
        addTypeButton.addActionListener(e -> handleAddType(newTypeField));
        newTypePanel.add(addTypeButton);

        // Button to select type
        selectTypeButton = new JButton("Select Type");
        selectTypeButton.addActionListener(e -> handleSelectType());
        // Panel for the select type button
        JPanel selectButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectButtonPanel.add(selectTypeButton);

        // Combine select button panel and new type panel at the bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(selectButtonPanel, BorderLayout.NORTH);
        bottomPanel.add(newTypePanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        setSize(300, 300);
    }

    // Adds the new type to the table
    private void handleAddType(JTextField type) {
        String newType = type.getText().trim();
        if (!newType.isEmpty()) {
            dateManager.addCustomType(newType);
            type.setText("");
            updateTypeTable();
        }
    }

    // Changes the date's type to the selected type and exits the JDialog
    private void handleSelectType() {
        int selectedRow = typeTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedType = (String) tableModel.getValueAt(selectedRow, 0);
            JDialog owner = (JDialog) getOwner();
            if (owner instanceof AddDateGUI) {
                ((AddDateGUI) owner).setSelectedType(selectedType);
            } else if (owner instanceof EditDateGUI) {
                ((EditDateGUI) owner).setSelectedType(selectedType);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a type from the list.", "No Type Selected",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Changes the type table so that the new type is added to the table
    private void updateTypeTable() {
        tableModel.setRowCount(0); // Clear the table first
        for (String type : dateManager.getCustomTypes()) {
            tableModel.addRow(new Object[] { type });
        }
    }

    public String getSelectedType() {
        return selectedType;
    }
}
