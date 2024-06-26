package application;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;
import model.Asset;
import model.AssetCSVReader;
import model.Category;
import model.Location;
import model.CategoryCSVReader;
import model.LocationCSVReader;


public class EditAsset extends VBox implements LayoutHelper{
	/**
	 * This page is VBox object. All page like this will put in StackPane.
	 * Navigation page on the left could change visibility of page on the right.
	 */
	private Asset asset = new Asset();
	private String oldAssetName = "";
	
	private HashMap<String, Category> category = new HashMap<String, Category>();
	private HashMap<String, Location> location = new HashMap<String, Location>();
	private HashMap<String, Asset> existingAssets = new HashMap<>();
	
	private final String file = "asset.csv";
	
	private ArrayList<HBox> layout;
	private final String title = "Edit Asset";
	private final String line1 = "Asset's Name: ";
	private final String line2 = "Category: ";
	private final String line3 = "Location: ";
	private final String line4 = "Purchase date: ";
	private final String line5 = "Description: ";
	private final String line6 = "Purchased Value: ";
	private final String line7 = "Warranty\nExpiration Date";
	

	public EditAsset(Asset existingAsset) {
	    super(30); // spacing parameter 30
	    super.setPadding(new Insets(40, 40, 40, 40));

	    layout = new ArrayList<HBox>();
	    setDDChoices();
	    
	    // Add layout elements to the ArrayList
	    layout.add(createTitle(title));
	    layout.add(createTextLine(line1, true));
	    layout.add(createDropdownList(line2, category));
	    layout.add(createDropdownList(line3, location));
	    layout.add(createDatePicker(line4));
	    layout.add(createTextArea(line5));
	    layout.add(createTextLine(line6));
	    layout.add(createDatePicker(line7));
	    layout.add(lastLine());
	    
	    //Purchased Value
	    ((TextField)layout.get(6).lookup("#text")).setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
	    
	    //Initial asset will have data shown 
	    this.asset = existingAsset;
	    oldAssetName = existingAsset.getName();
	    initialAsset();
	    
	    initialize(this, layout);
	    clearButtonAction(layout, 1, 2, 3, 4, 5, 6, 7);
	    buttonAction(layout);
	    
	    
	 // Populate existingAssets HashMap
        try {
            AssetCSVReader assetReader = new AssetCSVReader();
            existingAssets = assetReader.readData(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}


	// getInfo() is in working progress.
	public void getInfo() {
		
		asset.setName(((TextField) layout.get(1).lookup("#text")).getText());
		
		asset.setCategory(category.get(((ComboBox<String>) layout.get(2).lookup("#choice")).getValue()));
		
		asset.setLocation(location.get(((ComboBox<String>) layout.get(3).lookup("#choice")).getValue()));
		
		//asset.setPurchaseDate();
		//purchase date as LocalDate
		asset.setPurchaseDate(((DatePicker)getInput(layout.get(4), "date")).getValue());
		
		//description
		asset.setDesciption(((TextArea) layout.get(5).lookup("#text")).getText());
		//purchase value as Double
		 String purchaseValueText = ((TextField) layout.get(6).lookup("#text")).getText();
		    if (!purchaseValueText.isEmpty()) {
		        // If the text field is not empty, parse its content to a Double
		        asset.setPurchaseValue(Double.parseDouble(purchaseValueText));
		    } else {
		        // If the text field is empty, set the purchase value to null or any default value you prefer
		        asset.setPurchaseValue(null); // Or any default value you prefer
		    }
		//Warranty expiration date
		asset.setWarrantyExpDate(((DatePicker)getInput(layout.get(7), "date")).getValue());
		
	}
	@SuppressWarnings("unchecked")
	public <E> E getInput(HBox line, String keyword) {
		return (E)line.lookup("#" + keyword);
	}
	
	private void buttonAction(ArrayList<HBox> arg) {
		((Button)arg.get(arg.size() - 1).getChildren().get(0)).setOnAction(e -> {

			String name = ((TextField)layout.get(1).lookup("#text")).getText();
			if (name.isEmpty()) {
				// Show an error message if the name is empty
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Error");
				alert.setContentText("Asset name can not be empty!");
				alert.showAndWait();
			} else {
				// Save the category name to a .csv file
				this.getInfo();
				//save asset
				saveAssetToCsv();
				//clear
				clearTextField((TextField)arg.get(1).lookup("#text"));
				clearDropdownList((ComboBox<String>)arg.get(2).lookup("#choice"));
				clearDropdownList((ComboBox<String>)arg.get(3).lookup("#choice"));
				clearDatePicker((DatePicker)arg.get(4).lookup("#date"));
				clearTextArea((TextArea)arg.get(5).lookup("#text"));
				clearTextField((TextField)arg.get(6).lookup("#text"));
				clearDatePicker((DatePicker)arg.get(7).lookup("#date"));
			}
		});

	}
	
	private void saveAssetToCsv() {
		try {
			// Retrieve the modified asset information from the input fields
	        getInfo();
	        
	        // Update the existing asset with the modified information
	        if (asset.getName().equals(oldAssetName)) {
	            // If the name remains the same, update the existing asset
	            existingAssets.put(asset.getName(), asset);
	        } else {
	            // If the name is changed, remove the old asset and add the updated asset
	            existingAssets.remove(oldAssetName);
	            existingAssets.put(asset.getName(), asset);
	        }
	        
	        // Write the updated assets back to the CSV file
	        writeAssetsToFile(existingAssets);
			
			
			// Show a success message
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Success");
            alert.setContentText("Asset edited successfully.");
            alert.showAndWait();
			
		} catch (IOException ex) {
            // Show an error message if there was a problem saving the asset
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("There was a problem saving the edited asset.");
            alert.showAndWait();
        }
	}
	//method to get the original asset data
	public void initialAsset()
	{
		    // Populate input fields with existing asset data
		    ((TextField) layout.get(1).lookup("#text")).setText(asset.getName());
		    ((ComboBox<String>) layout.get(2).lookup("#choice")).setValue(asset.getCategory().getName());
		    ((ComboBox<String>) layout.get(3).lookup("#choice")).setValue(asset.getLocation().getName());
		    ((DatePicker) getInput(layout.get(4), "date")).setValue(asset.getPurchaseDate());
		    ((TextArea) layout.get(5).lookup("#text")).setText(asset.getDescription()); 
	/*check null*/    
		    ((TextField) layout.get(6).lookup("#text")).setText(asset.getPurchaseValue() != null ? String.valueOf(asset.getPurchaseValue()) : "");
		    ((DatePicker) getInput(layout.get(7), "date")).setValue(asset.getWarrantyExpDate());
		}

	
	private void setDDChoices() {
		CategoryCSVReader categoryReader = new CategoryCSVReader();
		LocationCSVReader locationReader = new LocationCSVReader();
		 
		try {
			category = categoryReader.readData("category.csv");
			location = locationReader.readData("location.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeAssetsToFile(HashMap<String, Asset> assets) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
        	writer.write("Asset Name,Category,Location,Purchase Date,Description,"
        			+ "Purchased Value,Warranty Expiration Date");
            for (Asset asset : assets.values()) {
            	writer.append("\n");
            	writer.append(asset.saveToCsv());
            }
        }
    }
	

}