package Principal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class lectorCSV {
	
	public lectorCSV() {
		
	}
	public ArrayList<List<String>> leerCSV(String ruta) throws FileNotFoundException{
		ArrayList<List<String>> records = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(ruta));) {
		    while (scanner.hasNextLine()) {
		        records.add(getRecordFromLine(scanner.nextLine()));
		    }
		}
		
		return records;
	}
	
	private List<String> getRecordFromLine(String line) {
	    List<String> values = new ArrayList<String>();
	    try (Scanner rowScanner = new Scanner(line)) {
	        rowScanner.useDelimiter(";");
	        while (rowScanner.hasNext()) {
	            values.add(rowScanner.next());
	        }
	    }
	    return values;
	}
}
