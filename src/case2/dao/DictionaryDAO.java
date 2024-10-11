
package case2.dao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DictionaryDAO {

	private static final String DB_URL = "jdbc:mysql://localhost:3306/dictionary_database";
    private static final String USER = "root";
    private static final String PASS = "";


    public void saveToDatabase(DefaultTableModel tableModel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "INSERT INTO dictionary (word, meaning, synonym, part_of_speech) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

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

            JOptionPane.showMessageDialog(null, "Data saved to database!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
	public DictionaryDAO() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
















