package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class CategoryCSVReader implements ReadData<Category> {
	private String file = "category.csv";
	
	public Category parseValue(String[] parts) {
		String name = parts[0];
		return new Category(name);
	}
	
	public Category searchValue(String file, String search) throws IOException {
		try (BufferedReader reader = new BufferedReader (new FileReader(file))) {
			String headerLine = reader.readLine();
			
			String line;
			
			// read line to find
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				String name = parts[0].trim();
				
				//check 
				if(name.startsWith(search)) {
					return parseValue(parts);
				}
			}
		}
		return null;
		
	}


}
