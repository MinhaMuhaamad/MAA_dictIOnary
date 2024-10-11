
package case2;

import case2.dao.DictionaryDAO;
import case2.presentation.CSVImporterUI;
import case2.service.DictionaryService;
public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DictionaryDAO dictionaryDAO = new DictionaryDAO(); // DAO layer
        DictionaryService dictionaryService = new DictionaryService(dictionaryDAO); // Service layer
        new CSVImporterUI(dictionaryService); // Presentation layer

	}

}
