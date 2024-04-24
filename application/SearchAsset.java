package application;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import model.Asset;
import model.AssetCSVReader;

public class SearchAsset extends VBox implements LayoutHelper {
    private final String file = "asset.csv";
    private TextField assetNameField;
    private Label foundAssetLabel;
    private HashMap<String, Asset> existingAssets = new HashMap<>();

    private ArrayList<HBox> layout = new ArrayList<>();
    private final String title = "Search Asset";
    private final String line1 = "Enter Asset Name: ";

    public SearchAsset() {
        super(30); // spacing parameter 30
        super.setPadding(new Insets(40, 40, 40, 40));

        layout.add(createTitle(title));
        layout.add(createTextLine(line1, true));
        layout.add(lastLine());

        initialize(this, layout);
       

        buttonAction(layout);
        clearButtonAction(layout, 1);
        foundAssetLabel = new Label();
        this.getChildren().add(foundAssetLabel);

        // Populate existingAssets HashMap
        try {
            AssetCSVReader assetReader = new AssetCSVReader();
            existingAssets = assetReader.readData(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buttonAction(ArrayList<HBox> arg) {
    	
        ((Button)arg.get(arg.size() - 1).getChildren().get(0)).setOnAction(e -> {
            String name = ((TextField)layout.get(1).lookup("#text")).getText();
            if (name.isEmpty()) {
                // Show an error message if the name is empty
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Please enter an asset name!");
                alert.showAndWait();
            } else {
            	try {
            		AssetCSVReader assetReader = new AssetCSVReader();
                    Asset foundAsset = assetReader.searchValue(file, name);
                    if (foundAsset != null) {
                        // Show the found asset name
                        foundAssetLabel.setText("Found Asset: " + foundAsset.getName());
                        // Add delete button
                        addDeleteButton(foundAsset.getName());
                    } else {
                        // Show an error message if the asset does not exist
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error");
                        alert.setContentText("Asset not found!");
                        alert.showAndWait();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void addDeleteButton(String assetName) {
        HBox buttonBox = new HBox(10);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        editButton.setOnAction(e -> {
            // Handle edit action here
            // You can implement editing functionality
            // For example, opening a new window or dialog for editing
//            Asset target = existingAssets.get(assetName);
//            EditAsset editPage = new EditAsset(target);
//            
//            this.getChildren().clear();
//            this.getChildren().addAll(layout); // Add other layout elements if needed
//            this.getChildren().add(editPage);
        });

        deleteButton.setOnAction(e -> {
            existingAssets.remove(assetName);
            // Update existing assets HashMap and file
            try {
                writeAssetsToFile(existingAssets);
                foundAssetLabel.setText("Deleted Asset: " + assetName);
            } catch (IOException ex) {
                // Show an error message if there was a problem deleting the asset
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("There was a problem deleting the asset");
                alert.showAndWait();
            }
        });

        buttonBox.getChildren().addAll(editButton, deleteButton);
        this.getChildren().add(buttonBox);
    }
    
    public void reloadAssets() {
        existingAssets.clear();
        try {
            AssetCSVReader assetReader = new AssetCSVReader();
            existingAssets = assetReader.readData(file);
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
