// EmergencyCallGUI.java


import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;

public class EmergencyCallGUI extends Application {
    // Main layout components
    private BorderPane mainLayout;
    private VBox chatMessages;
    private ScrollPane chatScrollPane;
    private TextField userInput;
    private Label typingIndicator;
    private VBox chatPanel;
    private CallList callList = new CallList();
    private VBox detailBox;
    private TableView<EmergencyCall> tableView;
    private boolean chattingWithAdmin = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Set up main scene and layout
        mainLayout = new BorderPane();
        Scene scene = new Scene(new StackPane(mainLayout), 1100, 600);
        // Top navigation bar
        HBox nav = new HBox(15);
        nav.setPadding(new Insets(10));
        nav.setAlignment(Pos.CENTER);
        nav.setStyle("-fx-background-color: #2c3e50;");

        // Navigation buttons
        Button home = new Button("Home");
        Button add = new Button("Add Call");
        Button view = new Button("View Calls");
        Button remove = new Button("Remove Call");

        // Button event handlers
        home.setOnAction(e -> showHome());
        add.setOnAction(e -> showAdd());
        view.setOnAction(e -> showView());
        remove.setOnAction(e -> showRemove());

        nav.getChildren().addAll(home, add, view, remove);
        mainLayout.setTop(nav);
        showHome(); // Initial view

        // Chat icon setup
        ImageView chatIcon = new ImageView(new Image("images/chat_icon.png"));
        chatIcon.setFitWidth(45);
        chatIcon.setFitHeight(45);
        StackPane.setAlignment(chatIcon, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(chatIcon, new Insets(0, 20, 20, 0));

        // Chat panel setup
        chatPanel = createChatPanel();
        chatPanel.setVisible(false);
        chatIcon.setOnMouseClicked(e -> chatPanel.setVisible(!chatPanel.isVisible()));

        ((StackPane) scene.getRoot()).getChildren().addAll(chatPanel, chatIcon);

        stage.setTitle("Emergency Call System with AI Assistant");
        stage.setScene(scene);
        stage.show();
    }

    // Creates chat panel UI and adds AI assistant intro messages
    private VBox createChatPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #ece5dd; -fx-border-color: #ccc; -fx-border-width: 1;");
        panel.setPrefWidth(360);
        panel.setTranslateX(720);
        panel.setTranslateY(50);

        Label header = new Label("🤖 AI Assistant");
        header.setFont(Font.font("Arial", 18));
        header.setTextFill(Color.DARKGREEN);

        chatMessages = new VBox(10);
        chatMessages.setPadding(new Insets(10));
        chatMessages.setPrefHeight(400);

        chatScrollPane = new ScrollPane(chatMessages);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.setStyle("-fx-background: transparent;");

        typingIndicator = new Label("");
        typingIndicator.setFont(Font.font("Arial", 12));
        typingIndicator.setTextFill(Color.GRAY);

        userInput = new TextField();
        userInput.setPromptText("Type a message...");
        userInput.setPrefWidth(220);
        userInput.setStyle("-fx-background-radius: 20; -fx-background-color: #ffffff; -fx-padding: 5 10 5 10;");
        userInput.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) handleUserInput();
        });

        Button send = new Button("Send");
        send.setStyle("-fx-background-color: #128C7E; -fx-text-fill: white; -fx-background-radius: 20;");
        send.setOnAction(e -> handleUserInput());

        HBox inputBox = new HBox(10, userInput, send);
        inputBox.setPadding(new Insets(5));
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setStyle("-fx-background-color: #ece5dd;");

        VBox.setMargin(inputBox, new Insets(10, 0, 0, 0));

        VBox chatContainer = new VBox(10, header, chatScrollPane, typingIndicator, inputBox);
        panel.getChildren().add(chatContainer);

        Platform.runLater(() -> {
            addMessage("Hi there! I'm AI assistant. I can help you with:", "assistant");
            addMessage("• Add Call\n• View Call\n• Remove Call\nOr type 'Talk to admin' to speak to an operator.", "assistant");
        });

        return panel;
    }

    // Handles user's input in the chat and simulates AI typing delay
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) return;

        addMessage(input, "user");
        userInput.clear();

        typingIndicator.setText("AI Assistant is typing...");
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            typingIndicator.setText("");
            processAIResponse(input);
        });
        pause.play();
    }

    // Adds a new message to the chat UI from user or assistant
    private void addMessage(String msg, String sender) {
        Label bubble = new Label(msg);
        bubble.setWrapText(true);
        bubble.setMaxWidth(260);
        bubble.setFont(Font.font("Arial", 13));
        bubble.setPadding(new Insets(10));
        bubble.setMinHeight(Region.USE_PREF_SIZE);

        HBox container = new HBox(bubble);
        container.setPadding(new Insets(5, 10, 5, 10));

        if (sender.equalsIgnoreCase("user")) {
            bubble.setStyle("-fx-background-color: #25D366; -fx-text-fill: white; -fx-background-radius: 15 15 0 15;");
            container.setAlignment(Pos.CENTER_LEFT);
        } else {
            bubble.setStyle("-fx-background-color: #d2e3fc; -fx-text-fill: black; -fx-background-radius: 15 15 15 0;");
            container.setAlignment(Pos.CENTER_LEFT);
        }

        Platform.runLater(() -> {
            chatMessages.getChildren().add(container);
            chatScrollPane.setVvalue(1.0);
        });
    }

    // Determines how AI responds to user queries
    private void processAIResponse(String input) {
        String lower = input.toLowerCase();

        if (chattingWithAdmin) return;

        if (lower.contains("add")) {
            addMessage("📋 To add a new emergency call:\n• Click 'Add Call'.\n• Fill in a valid name (letters only).\n• Phone: 10-11 digits.\n• Add a description.\n• Select service(s).\n• Click 'Submit'.", "assistant");
        } else if (lower.contains("view")) {
            addMessage("📑 To view calls:\n• Click 'View Calls'.\n• Use filters.\n• Click a row to view details.", "assistant");
        } else if (lower.contains("remove")) {
            addMessage("🗑️ To remove a call:\n• Click 'Remove Call'.\n• Select and click 'Remove Selected Call'.", "assistant");
        } else if (lower.contains("admin") || lower.contains("talk")) {
            addMessage("🔔 Connecting you to a live admin... Please wait.", "assistant");
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> {
                addMessage("👨‍💼 Admin: Hello User, my name is Manish. How can I help you today? Call us at 07123457892.", "assistant");
                addMessage("Please also type your emergency message in the 'Add Call' form.", "assistant");
                addMessage("You can continue chatting or click 'Add Call' to submit details.", "assistant");
            });
            pause.play();
        } else {
            addMessage("🤖 I'm here to assist with emergency calls. Try: 'Add Call', 'View Call', or 'Talk to admin'.", "assistant");
        }
    }
    // Displays the home screen with title, subtitle, and service icons
    private void showHome() {
        VBox home = new VBox(25);
        home.setAlignment(Pos.CENTER);
        home.setPadding(new Insets(30));

        Label title = new Label("\uD83D\uDEA8 Emergency Call Management System \uD83D\uDEA8");
        title.setFont(new Font("Arial", 30));
        title.setTextFill(Color.DARKRED);

        Label subtitle = new Label("Fast, Reliable & Professional Emergency Call Recording");
        subtitle.setFont(new Font("Arial", 16));
        subtitle.setTextFill(Color.DARKBLUE);

        HBox iconsRow = new HBox(50);
        iconsRow.setAlignment(Pos.CENTER);
        iconsRow.setPadding(new Insets(20));

        iconsRow.getChildren().addAll(
                createServiceBox("/images/fire.png", "Fire Service"),
                createServiceBox("/images/police.png", "Police Service"),
                createServiceBox("/images/ambulance.png", "Ambulance Service")
        );

        home.getChildren().addAll(title, subtitle, iconsRow);
        mainLayout.setCenter(home);
    }

    // Helper method to create service icon boxes
    private VBox createServiceBox(String imagePath, String label) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        icon.setFitWidth(90);
        icon.setFitHeight(90);

        icon.setOnMouseEntered(e -> {
            icon.setScaleX(1.1);
            icon.setScaleY(1.1);
            icon.setEffect(new javafx.scene.effect.DropShadow(15, Color.DARKRED));
        });
        icon.setOnMouseExited(e -> {
            icon.setScaleX(1.0);
            icon.setScaleY(1.0);
            icon.setEffect(null);
        });

        Label lbl = new Label(label);
        lbl.setFont(new Font(14));
        box.getChildren().addAll(icon, lbl);
        return box;
    }

    // Form for adding a new emergency call
    private void showAdd() {
        VBox add = new VBox(15);
        add.setPadding(new Insets(20));
        add.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Add New Emergency Call");
        title.setFont(new Font("Arial", 20));

        HBox nameRow = new HBox(10, new Label("Caller Name:"), new TextField());
        TextField nameField = (TextField) nameRow.getChildren().get(1);

        HBox phoneRow = new HBox(10, new Label("Phone (+44 not required):"), new TextField());
        TextField phoneField = (TextField) phoneRow.getChildren().get(1);

        VBox descRow = new VBox(5);
        descRow.getChildren().addAll(new Label("Description:"), new TextArea());
        TextArea descField = (TextArea) descRow.getChildren().get(1);

        CheckBox fire = new CheckBox("Fire");
        CheckBox police = new CheckBox("Police");
        CheckBox ambulance = new CheckBox("Ambulance");

        Label status = new Label();
        ProgressIndicator addProgress = new ProgressIndicator();
        addProgress.setVisible(false);

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            status.setTextFill(Color.RED);
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String desc = descField.getText().trim();

            if (!name.matches("[A-Za-z ]+")) status.setText("❌ Name must contain only letters.");
            else if (!phone.matches("\\d{10,11}")) status.setText("❌ Phone must be 10-11 digits.");
            else if (desc.isEmpty()) status.setText("❌ Description is required.");
            else if (!fire.isSelected() && !police.isSelected() && !ambulance.isSelected()) status.setText("❌ Select at least one service.");
            else {
                addProgress.setVisible(true);
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> {
                    EnumSet<Service> services = EnumSet.noneOf(Service.class);
                    if (fire.isSelected()) services.add(Service.FIRE);
                    if (police.isSelected()) services.add(Service.POLICE);
                    if (ambulance.isSelected()) services.add(Service.AMBULANCE);
                    EmergencyCall call = new EmergencyCall(name, "+44" + phone, desc, services, LocalDateTime.now());
                    callList.addCall(call);
                    nameField.clear(); phoneField.clear(); descField.clear();
                    fire.setSelected(false); police.setSelected(false); ambulance.setSelected(false);
                    addProgress.setVisible(false);
                    status.setTextFill(Color.GREEN);
                    status.setText("✅ Call successfully submitted.");
                });
                pause.play();
            }
        });

        add.getChildren().addAll(title, nameRow, phoneRow, descRow, new HBox(10, fire, police, ambulance), submit, addProgress, status);
        mainLayout.setCenter(add);
    }

    // Displays a table of emergency calls with filtering options
    private void showView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("View Emergency Calls");
        title.setFont(new Font("Arial", 18));

        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("Filter by: All", "Filter by: Fire", "Filter by: Police", "Filter by: Ambulance");
        filter.setValue("Filter by: All");

        detailBox = new VBox(10);
        detailBox.setPrefWidth(300);

        tableView = new TableView<>();
        tableView.setPrefWidth(700);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTable();

        filter.setOnAction(e -> refreshTable(filter.getValue()));
        refreshTable("Filter by: All");

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showDetails(newVal));

        HBox content = new HBox(20, tableView, detailBox);
        view.getChildren().addAll(title, filter, content);
        mainLayout.setCenter(view);
    }

    // Displays call details in side panel
    private void showDetails(EmergencyCall call) {
        detailBox.getChildren().clear();
        if (call != null) {
            detailBox.getChildren().addAll(
                    new Label("Caller: " + call.getCallerName()),
                    new Label("Phone: " + call.getPhoneNumber()),
                    new Label("Description: " + call.getDescription()),
                    new Label("Time: " + call.getTimestamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))),
                    new Label("Services: " + call.getServicesRequired().toString())
            );
        }
    }

    // Displays interface to remove selected call
    private void showRemove() {
        VBox remove = new VBox(10);
        remove.setPadding(new Insets(20));
        remove.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Remove Emergency Call");
        title.setFont(new Font("Arial", 18));

        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("Filter by: All", "Filter by: Fire", "Filter by: Police", "Filter by: Ambulance");
        filter.setValue("Filter by: All");

        tableView = new TableView<>();
        tableView.setPrefWidth(700);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTable();
        filter.setOnAction(e -> refreshTable(filter.getValue()));
        refreshTable("Filter by: All");

        ProgressIndicator removeProgress = new ProgressIndicator();
        removeProgress.setVisible(false);

        Button removeBtn = new Button("Remove Selected Call");
        Label status = new Label();

        removeBtn.setOnAction(e -> {
            EmergencyCall selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                removeProgress.setVisible(true);
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> {
                    callList.removeCall(selected);
                    status.setText("✅ Selected Call removed.");
                    removeProgress.setVisible(false);
                    refreshTable(filter.getValue());
                });
                pause.play();
            } else {
                status.setText("❌ Please select a call.");
            }
        });

        VBox layout = new VBox(10, title, filter, tableView, removeBtn, removeProgress, status);
        layout.setAlignment(Pos.CENTER);
        mainLayout.setCenter(layout);
    }

    // Configures the columns in the call table
    private void setupTable() {
        tableView.getColumns().clear();

        TableColumn<EmergencyCall, String> nameCol = new TableColumn<>("Caller");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("callerName"));

        TableColumn<EmergencyCall, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<EmergencyCall, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(call -> new ReadOnlyStringWrapper(call.getValue().getDescription()));
        descCol.setCellFactory(tc -> {
            TableCell<EmergencyCall, String> cell = new TableCell<>();
            Text text = new Text();
            text.wrappingWidthProperty().bind(descCol.widthProperty().subtract(10));
            cell.setGraphic(text);
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });

        TableColumn<EmergencyCall, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getTimestamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));

        TableColumn<EmergencyCall, String> serviceCol = new TableColumn<>("Services");
        serviceCol.setCellValueFactory(data -> {
            StringBuilder sb = new StringBuilder();
            for (Service s : data.getValue().getServicesRequired()) {
                sb.append(s).append(", ");
            }
            String result = sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
            return new ReadOnlyStringWrapper(result);
        });

        tableView.getColumns().addAll(nameCol, phoneCol, descCol, serviceCol, timeCol);
    }

    // Filters and refreshes the data shown in the call table
    private void refreshTable(String filter) {
        ArrayList<EmergencyCall> filtered;
        switch (filter) {
            case "Filter by: Fire": filtered = callList.getCallsByService(Service.FIRE); break;
            case "Filter by: Police": filtered = callList.getCallsByService(Service.POLICE); break;
            case "Filter by: Ambulance": filtered = callList.getCallsByService(Service.AMBULANCE); break;
            default: filtered = callList.getAllCalls();
        }
        tableView.setItems(FXCollections.observableArrayList(filtered));
    }
}
