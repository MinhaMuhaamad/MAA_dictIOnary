
package case2.service;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import case2.dao.DictionaryDAO;

public class DictionaryService {

	private DictionaryDAO dictionaryDAO;
	public DictionaryService(DictionaryDAO dictionaryDAO) {
		// TODO Auto-generated constructor stub
		this.dictionaryDAO = dictionaryDAO;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	 public void displayCSVContent(File file, JTextArea textArea, DefaultTableModel tableModel) {
	        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	            String line;
	            textArea.setText(""); // Clear previous content
	            tableModel.setRowCount(0); // Clear table

	            while ((line = br.readLine()) != null) {
	                textArea.append(line + "\n");

	                String[] columns = line.split(",");
	                if (columns.length >= 4) {
	                    String word = columns[0].trim();
	                    String meaning = columns[1].trim();
	                    StringBuilder synonyms = new StringBuilder();
	                    for (int i = 2; i < columns.length - 1; i++) {
	                        synonyms.append(columns[i].trim());
	                        if (i < columns.length - 2) {
	                            synonyms.append(", ");
	                        }
	                    }
	                    String partOfSpeech = columns[columns.length - 1].trim();

	                    tableModel.addRow(new Object[]{word, meaning, synonyms.toString(), partOfSpeech});
	                }
	            }
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }

	    public void saveToDatabase(DefaultTableModel tableModel) {
	        dictionaryDAO.saveToDatabase(tableModel);
	    }
}

