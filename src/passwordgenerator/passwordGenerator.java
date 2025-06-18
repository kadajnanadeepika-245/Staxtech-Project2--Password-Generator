package passwordgenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.security.SecureRandom;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class passwordGenerator extends JFrame implements ActionListener {
    private JTextField lengthField;
    private JCheckBox upperCaseCheck, lowerCaseCheck, numberCheck, specialCharCheck;
    private JTextArea outputArea;
    private JButton generateButton, copyButton;
    private JLabel strengthLabel;

    public passwordGenerator() {
        setTitle("🔐 Secure Password Generator");
        setSize(550, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("🔐 Secure Password Generator", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        centerPanel.setBackground(new Color(236, 240, 241));

        JLabel lengthLabel = new JLabel("Enter Password Length:");
        lengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        centerPanel.add(lengthLabel);
        centerPanel.add(Box.createVerticalStrut(5));

        lengthField = new JTextField();
        styleTextField(lengthField);
        centerPanel.add(lengthField);
        centerPanel.add(Box.createVerticalStrut(20));

        upperCaseCheck = new JCheckBox("Include Uppercase Letters (A-Z)");
        lowerCaseCheck = new JCheckBox("Include Lowercase Letters (a-z)");
        numberCheck = new JCheckBox("Include Numbers (0-9)");
        specialCharCheck = new JCheckBox("Include Special Characters (!@#$%)");

        JCheckBox[] checks = { upperCaseCheck, lowerCaseCheck, numberCheck, specialCharCheck };
        for (JCheckBox cb : checks) {
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cb.setBackground(new Color(236, 240, 241));
            centerPanel.add(cb);
            centerPanel.add(Box.createVerticalStrut(5));
        }

        centerPanel.add(Box.createVerticalStrut(15));

        generateButton = new JButton("✨ Generate Password");
        styleButton(generateButton);
        generateButton.addActionListener(this);
        centerPanel.add(generateButton);

        add(centerPanel, BorderLayout.CENTER);

        // Output Panel
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        outputPanel.setBackground(new Color(236, 240, 241));
        outputPanel.setBorder(new EmptyBorder(10, 40, 30, 40));

        outputArea = new JTextArea(3, 20);
        outputArea.setFont(new Font("Consolas", Font.BOLD, 18));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(255, 255, 255));
        outputArea.setBorder(BorderFactory.createTitledBorder("Generated Password"));
        outputPanel.add(outputArea);

        strengthLabel = new JLabel(" ");
        strengthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        strengthLabel.setForeground(Color.DARK_GRAY);
        strengthLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        outputPanel.add(strengthLabel);

        copyButton = new JButton("📋 Copy to Clipboard");
        styleButton(copyButton);
        copyButton.addActionListener(e -> {
            String text = outputArea.getText().trim();
            if (!text.isEmpty()) {
                StringSelection selection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
                JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        outputPanel.add(Box.createVerticalStrut(10));
        outputPanel.add(copyButton);

        add(outputPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    public void actionPerformed(ActionEvent e) {
        try {
            int length = Integer.parseInt(lengthField.getText().trim());
            if (length <= 0) throw new NumberFormatException();

            String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lower = "abcdefghijklmnopqrstuvwxyz";
            String digits = "0123456789";
            String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";

            StringBuilder charPool = new StringBuilder();
            if (upperCaseCheck.isSelected()) charPool.append(upper);
            if (lowerCaseCheck.isSelected()) charPool.append(lower);
            if (numberCheck.isSelected()) charPool.append(digits);
            if (specialCharCheck.isSelected()) charPool.append(special);

            if (charPool.length() == 0) {
                outputArea.setText("⚠ Please select at least one character set.");
                strengthLabel.setText(" ");
                return;
            }

            SecureRandom rand = new SecureRandom();
            StringBuilder password = new StringBuilder();

            for (int i = 0; i < length; i++) {
                int index = rand.nextInt(charPool.length());
                password.append(charPool.charAt(index));
            }

            String generatedPassword = password.toString();
            outputArea.setText(generatedPassword);
            strengthLabel.setText("Strength: " + getStrength(generatedPassword));

        } catch (NumberFormatException ex) {
            outputArea.setText("⚠ Please enter a valid number for password length.");
            strengthLabel.setText(" ");
        }
    }

    private String getStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=[\\]{}|;:,.<>?].*")) score++;

        return switch (score) {
            case 5, 4 -> "Strong 💪";
            case 3 -> "Moderate ⚠";
            default -> "Weak ❌";
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(passwordGenerator::new);
    }
}
