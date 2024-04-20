package model;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public interface ReadData<T> {
	public default HashMap<String, T> readData(String file) throws IOException {
		HashMap<String, T> dataMap = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader (new FileReader(file))) {
			String headerLine = reader.readLine();
			
			String line;
			
			// read each line 
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				String name = parts[0].trim();
				T value = parseValue(parts);
				
				dataMap.put(name, value);
			}
		}
		
		return dataMap;
	}
	
	T parseValue(String[] elements);

}
