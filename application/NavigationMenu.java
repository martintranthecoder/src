package application;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class NavigationMenu extends VBox{
	private Button button_NewCategory;
	private Button button_NewLocation;
	private Button button_NewAsset;
	private Button button_Search;
	private Button button_Reports;
	
	public NavigationMenu(Consumer<String> choice) {
        super(30); // spacing parameter 30
        //
        button_NewCategory = navigationButton("New Category");
    	button_NewLocation = navigationButton("New Location");
    	button_NewAsset = navigationButton("New Asset");
    	button_Search = navigationButton("Search");
    	button_Reports = navigationButton("Reports");
        
        initialize();
        buttonAction(choice);
        
    }

    private void initialize() {
        this.setStyle("-fx-background-color: rgba(72, 155, 105, 0.7); -fx-padding: 30;");
        this.setMinSize(240, 600);
        // Add buttons to the VBox
        this.getChildren().addAll(button_NewCategory, button_NewLocation, button_NewAsset, button_Search, button_Reports);
    }
    
    // set action for button
    private void buttonAction(Consumer<String> choice) {
        button_NewCategory.setOnAction(e -> choice.accept("New Category Page"));
        button_NewLocation.setOnAction(e -> choice.accept("New Location Page"));
        button_NewAsset.setOnAction(e -> choice.accept("New Asset Page"));
        button_Search.setOnAction(e -> choice.accept("Search Page"));
        button_Reports.setOnAction(e -> choice.accept("Reports Page"));
        // ... set actions for all 5 buttons ...
    }
    
    public Button navigationButton(String arg) {
		Button button = new Button(arg);

		button.setStyle("-fx-background-color: #0068B8;" + 
					 "-fx-background-radius: 25; " + 
					 "-fx-background-insets: 2;" + 
					 "-fx-text-fill: Black; " + 
					 "-fx-font-size: 20px; ");

		button.setPrefSize(180, 50);

		return button;
	}

}
