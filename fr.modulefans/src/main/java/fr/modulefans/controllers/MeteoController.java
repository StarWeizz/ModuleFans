package fr.modulefans.controllers;

import fr.modulefans.models.WeatherRecord;
import fr.modulefans.services.MeteoService;
import fr.modulefans.utils.CsvReader;
import fr.modulefans.utils.NavigationManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MeteoController implements Initializable {

    @FXML private TableView<WeatherRecord> tableView;
    @FXML private TableColumn<WeatherRecord, String> colDate;
    @FXML private TableColumn<WeatherRecord, String> colVille;
    @FXML private TableColumn<WeatherRecord, String> colRegion;
    @FXML private TableColumn<WeatherRecord, Double> colTempMax;
    @FXML private TableColumn<WeatherRecord, Double> colTempMin;
    @FXML private TableColumn<WeatherRecord, Double> colPrecip;
    @FXML private TableColumn<WeatherRecord, Double> colHumidite;
    @FXML private Label lblMoyenne;
    @FXML private Label lblMin;
    @FXML private Label lblMax;
    @FXML private Label lblEcartType;
    @FXML private Label lblAnomalies;
    @FXML private Label lblHottestDay;
    @FXML private Label lblStatus;

    private final MeteoService service = new MeteoService();
    private List<WeatherRecord> records;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadDefaultData();
    }

    private void setupColumns() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        colRegion.setCellValueFactory(new PropertyValueFactory<>("region"));
        colTempMax.setCellValueFactory(new PropertyValueFactory<>("tempMax"));
        colTempMin.setCellValueFactory(new PropertyValueFactory<>("tempMin"));
        colPrecip.setCellValueFactory(new PropertyValueFactory<>("precipitation"));
        colHumidite.setCellValueFactory(new PropertyValueFactory<>("humidite"));

        // Format double columns
        colTempMax.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.1f°C", item));
            }
        });
        colTempMin.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.1f°C", item));
            }
        });
        colPrecip.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.1f mm", item));
            }
        });
        colHumidite.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.0f%%", item));
            }
        });

        // Row factory for anomaly highlighting
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(WeatherRecord item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("anomaly-row");
                if (!empty && item != null && item.isAnomalie()) {
                    getStyleClass().add("anomaly-row");
                }
            }
        });
    }

    private void loadDefaultData() {
        records = CsvReader.readMeteoFromResources();
        applyAndDisplay();
    }

    private void applyAndDisplay() {
        service.detectAnomalies(records);
        tableView.setItems(FXCollections.observableArrayList(records));
        updateStats();
        if (lblStatus != null) lblStatus.setText(records.size() + " enregistrements chargés");
    }

    private void updateStats() {
        if (records.isEmpty()) return;
        lblMoyenne.setText(String.format("%.1f°C", service.calcMoyenne(records)));
        lblMin.setText(String.format("%.1f°C", service.calcMin(records)));
        lblMax.setText(String.format("%.1f°C", service.calcMax(records)));
        lblEcartType.setText(String.format("%.2f", service.calcEcartType(records)));
        lblAnomalies.setText(String.valueOf(service.countAnomalies(records)));
        WeatherRecord hot = service.getHottestDay(records);
        if (hot != null) lblHottestDay.setText(hot.getDate() + " — " + hot.getVille() + " (" + hot.getTempMax() + "°C)");
    }

    @FXML
    public void loadFromFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Sélectionner un fichier météo CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showOpenDialog(NavigationManager.getStage());
        if (file != null) {
            records = CsvReader.readMeteoFromFile(file);
            applyAndDisplay();
        }
    }

    @FXML
    public void goBack() {
        NavigationManager.navigateTo("main_menu");
    }
}
