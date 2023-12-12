//GUI responsible for calculating the difference between 2 days

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class CalculateDaysGUI extends JDialog {
    private JTextField firstDayField;
    private JTextField secondDayField;
    private JButton calculateButton;
    private JLabel resultLabel;

    public CalculateDaysGUI(Frame owner) {
        super(owner, "Calculate Days Between Dates", true);
        initializeUI();
        pack();
        setLocationRelativeTo(owner);
    }

    // Initialize the UI
    private void initializeUI() {

        setLayout(new BorderLayout());
        setSize(500, 300);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
        setContentPane(contentPanel);

        // Create a panel for the first date input
        JPanel firstDayPanel = new JPanel();
        firstDayPanel.setLayout(new BoxLayout(firstDayPanel, BoxLayout.LINE_AXIS));
        firstDayPanel.add(new JLabel("First day (yyyy/mm/dd):"));
        firstDayPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Add space between label and text field
        firstDayField = new JTextField(10);
        firstDayPanel.add(firstDayField);
        contentPanel.add(firstDayPanel);

        // Add space in between buttons
        contentPanel.add(Box.createVerticalStrut(15));

        // Create a panel for the second date input
        JPanel secondDayPanel = new JPanel();
        secondDayPanel.setLayout(new BoxLayout(secondDayPanel, BoxLayout.LINE_AXIS));
        secondDayPanel.add(new JLabel("Second day (yyyy/mm/dd):"));
        secondDayPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Add space between label and text field
        secondDayField = new JTextField(10);
        secondDayPanel.add(secondDayField);
        contentPanel.add(secondDayPanel);

        contentPanel.add(Box.createVerticalStrut(15));

        // Create a panel for the calculate button
        JPanel calculateButtonPanel = new JPanel();
        calculateButtonPanel.setLayout(new BoxLayout(calculateButtonPanel, BoxLayout.LINE_AXIS));
        calculateButtonPanel.add(Box.createHorizontalGlue());
        calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(this::handleCalculate);
        calculateButtonPanel.add(calculateButton);
        calculateButtonPanel.add(Box.createHorizontalGlue());
        contentPanel.add(calculateButtonPanel);

        contentPanel.add(Box.createVerticalStrut(15));

        // Create a panel for the result label
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.LINE_AXIS));
        resultPanel.add(Box.createHorizontalGlue());
        resultLabel = new JLabel("");
        resultLabel.setFont(resultLabel.getFont().deriveFont(16.0f)); // Make the font larger
        resultPanel.add(resultLabel);
        resultPanel.add(Box.createHorizontalGlue());
        contentPanel.add(resultPanel);
    }

    // Calculate the date difference
    private void handleCalculate(ActionEvent e) {
        try {
            LocalDate firstDay = LocalDate.parse(firstDayField.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            LocalDate secondDay = LocalDate.parse(secondDayField.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(firstDay, secondDay));
            resultLabel.setText(daysBetween + " Days");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter the dates in the correct format (yyyy/mm/dd).",
                    "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
