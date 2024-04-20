package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SearchAsset extends VBox implements LayoutHelper {
    private final String file = "category.csv";
    private TextField AssetNameField;
    private Label foundAssetLabel;

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
        foundAssetLabel = new Label();
        this.getChildren().add(foundAssetLabel);
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
                    Set<String> existingAssets = getExistingAssets();
                    if (existingAssets.contains(name)) {
                        // Show the found asset name
                        foundAssetLabel.setText("Found Asset: " + name);
                        // Add delete button
                        addDeleteButton(name);
                    } else {
                        // Show an error message if the asset does not exist
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error");
                        alert.setContentText("Asset not found!");
                        alert.showAndWait();
                    }
                } catch (IOException ex) {
                    // Show an error message if there was a problem reading the assets
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error");
                    alert.setContentText("There was a problem reading existing assets");
                    alert.showAndWait();
                }
            }
        });
    }

    private Set<String> getExistingAssets() throws IOException {
        Set<String> assets = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming each line contains a single asset name
                assets.add(line.trim());
            }
        }
        return assets;
    }

    private void addDeleteButton(String assetName) {
    	HBox buttonBox = new HBox(10);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        editButton.setOnAction(e -> {
            // Handle edit action here
            // You can implement editing functionality
            // For example, opening a new window or dialog for editing
            System.out.println("Edit action for asset: " + assetName);
        });

        deleteButton.setOnAction(e -> {
            try {
                Set<String> existingAssets = getExistingAssets();
                existingAssets.remove(assetName);
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

    private void writeAssetsToFile(Set<String> assets) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            for (String asset : assets) {
                writer.write(asset + "\n");
            }
        }
    }
}