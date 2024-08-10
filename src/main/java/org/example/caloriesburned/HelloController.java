package org.example.caloriesburned;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class HelloController {

    @FXML
    private TableView<CalorieActivity> caloriesTable;

    @FXML
    private TableColumn<CalorieActivity, String> nameColumn;

    @FXML
    private TableColumn<CalorieActivity, Integer> caloriesPerHourColumn;

    @FXML
    private TableColumn<CalorieActivity, Integer> durationColumn;

    @FXML
    private TableColumn<CalorieActivity, Integer> totalCaloriesColumn;

    @FXML
    private VBox detailsView;

    @FXML
    private Label activityName;

    @FXML
    private Label caloriesDetails;

    @FXML
    private Label durationDetails;

    @FXML
    private Label totalCaloriesDetails;

    @FXML
    private TextField searchBox;

    @FXML
    private Button searchButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {
        // Set up the table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        caloriesPerHourColumn.setCellValueFactory(new PropertyValueFactory<>("caloriesPerHour"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        totalCaloriesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCalories"));

        // Fetch data for the default activity "skiing"
        fetchData("skiing");

        // Add a listener to handle table row clicks
        caloriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetails(newSelection);
            }
        });

        // Attach search action to the button
        searchButton.setOnAction(event -> {
            String activity = searchBox.getText();
            if (!activity.isEmpty()) {
                fetchData(activity);
            }
        });
    }

    private void fetchData(String activity) {
        loadingIndicator.setVisible(true);
        new Thread(() -> {
            try {
                String apiUrl = "https://api.api-ninjas.com/v1/caloriesburned?activity=" + activity;
                HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Api-Key", "rR27C8PXsHynl7caNW0YXnkkA1Xz3rp3pukAfU5G");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                JSONArray jsonArray = new JSONArray(content.toString());
                Platform.runLater(() -> {
                    caloriesTable.getItems().clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        int caloriesPerHour = jsonObject.getInt("calories_per_hour");
                        int duration = jsonObject.getInt("duration_minutes");
                        CalorieActivity activityObj = new CalorieActivity(name, caloriesPerHour, duration);
                        caloriesTable.getItems().add(activityObj);
                    }
                    loadingIndicator.setVisible(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> loadingIndicator.setVisible(false));
            }
        }).start();
    }

    private void showDetails(CalorieActivity activity) {
        activityName.setText(activity.getName());
        caloriesDetails.setText("Calories per Hour: " + activity.getCaloriesPerHour());
        durationDetails.setText("Duration: " + activity.getDurationMinutes() + " minutes");
        totalCaloriesDetails.setText("Total Calories: " + activity.getTotalCalories());

        // Show details view and hide the table
        caloriesTable.setVisible(false);
        detailsView.setVisible(true);
    }

    @FXML
    private void onBackButtonClick() {
        // Show the table and hide the details view
        detailsView.setVisible(false);
        caloriesTable.setVisible(true);
    }
}
