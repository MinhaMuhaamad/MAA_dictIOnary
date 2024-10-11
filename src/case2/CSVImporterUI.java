
package case2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class CSVImporterUI {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dictionary_database"; // Correct database URL
    private static final String USER = "root"; // MySQL username
    private static final String PASS = ""; // MySQL password

    // Main frame
    private JFrame frame;
    private JTextArea textArea;
    private JTable table;
    private DefaultTableModel tableModel;

    public CSVImporterUI() {
        // Set up the frame
        frame = new JFrame("CSV Importer with MySQL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        // Set up the UI
        JButton importButton = new JButton("Import CSV");
        JButton saveButton = new JButton("Save to Database");
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBackground(new Color(240, 248, 255));

        // Table to display CSV content
        table = new JTable();
        tableModel = new DefaultTableModel(new String[]{"Word", "Meaning", "Synonym", "Part_of_Speech"}, 0);
        table.setModel(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(false);

        // Customize the table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(100, 149, 237));
        header.setForeground(Color.WHITE);

        // Customize buttons
        importButton.setBackground(new Color(70, 130, 180));
        importButton.setForeground(Color.WHITE);
        importButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        importButton.setFocusPainted(false);
        saveButton.setBackground(new Color(60, 179, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setFocusPainted(false);

        // Add padding to text area
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add button action listeners
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importCSV();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDatabaseConnected()) {
                    saveToDatabase();
                } else {
                    JOptionPane.showMessageDialog(frame, "Could not connect to the database. Please check your connection settings.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout setup
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.add(importButton, BorderLayout.WEST);
        panel.add(saveButton, BorderLayout.EAST);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230)));
        textPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(importButton);
        buttonPanel.add(saveButton);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        frame.setLayout(new BorderLayout());
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(textPanel, BorderLayout.SOUTH);

        // Show the frame
        frame.setVisible(true);
    }

    // Function to import and display CSV file
    private void importCSV() {
        // File chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        // If a file is selected
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            displayCSVContent(selectedFile);
        }
    }

    // Function to display CSV content in the text area and table
    private void displayCSVContent(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            textArea.setText(""); // Clear previous content
            tableModel.setRowCount(0); // Clear table

            while ((line = br.readLine()) != null) {
                textArea.append(line + "\n");

                // Split the line by commas
                String[] columns = line.split(",");

                if (columns.length >= 4) {
                    // First word before the first comma -> Word
                    String word = columns[0].trim();

                    // Text between the first and second comma -> Meaning
                    String meaning = columns[1].trim();

                    // Text between the second and the last comma -> Synonym
                    StringBuilder synonyms = new StringBuilder();
                    for (int i = 2; i < columns.length - 1; i++) {
                        synonyms.append(columns[i].trim());
                        if (i < columns.length - 2) {
                            synonyms.append(", "); // Add a comma between synonyms
                        }
                    }

                    // Text after the last comma -> Part of Speech
                    String partOfSpeech = columns[columns.length - 1].trim();

                    // Add the row to the table model
                    tableModel.addRow(new Object[]{word, meaning, synonyms.toString(), partOfSpeech});
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error reading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function to save the CSV data to the database
    private void saveToDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "INSERT INTO dictionary (word, meaning, synonym, part_of_speech) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            // Loop through table rows and insert data into the database
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String word = (String) tableModel.getValueAt(i, 0);
                String meaning = (String) tableModel.getValueAt(i, 1);
                String synonyms = (String) tableModel.getValueAt(i, 2);
                String partOfSpeech = (String) tableModel.getValueAt(i, 3);

                pstmt.setString(1, word);
                pstmt.setString(2, meaning);
                pstmt.setString(3, synonyms);
                pstmt.setString(4, partOfSpeech);
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(frame, "Data saved to database!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function to check if the database connection is successful
    private boolean isDatabaseConnected() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace(); // Print the exception for debugging
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CSVImporterUI::new);
    }
}
