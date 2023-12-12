//GUI responsible for displaying all current types, allows users to select a type to display only dates of that type.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class ViewByTypeGUI extends JDialog {
    private Set<String> types;
    private MainGUI mainGUI;
    private JButton selectTypeButton;

    public ViewByTypeGUI(Frame owner, Set<String> types, MainGUI mainGUI) {
        super(owner, "View By Type", true);
        this.types = types;
        this.mainGUI = mainGUI;
        initializeUI();
    }

    // Initializes the UI
    private void initializeUI() {
        setSize(150, 100);
        setLayout(new BorderLayout());

        JComboBox<String> typeComboBox = new JComboBox<>();
        typeComboBox.addItem("All");
        types.forEach(typeComboBox::addItem);

        selectTypeButton = new JButton("Select Type");
        selectTypeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) typeComboBox.getSelectedItem();
                mainGUI.filterDatesByType(selectedType);
                dispose();
            }
        });

        add(typeComboBox, BorderLayout.CENTER);
        add(selectTypeButton, BorderLayout.SOUTH);

        setLocationRelativeTo(getOwner());
    }
}
