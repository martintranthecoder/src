package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class CategoryCSVReader implements ReadData<Category> {

	
	public Category parseValue(String[] parts) {
		String name = parts[0];
		return new Category(name);
	}
	
	


}
