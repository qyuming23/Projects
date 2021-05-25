import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TicTacToe extends Application {

	private Scene introScene, mainScene;
	private TextField portField;
	private int port;
	private Label invalidLabel, numPlayers;
	private Button startBtn, exitBtn, closeServer;
	private ListView<String> gameDisplay;
	private Server server;

	public static void main(String[] args) {

		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("TIC_TAC_TOE server");

		introScene = makeIntro();
		mainScene = makeMainScene();

		//gets port number
		this.portField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(isNumeric(portField.getText()) && portField.getText().length() == 4) {
					invalidLabel.setVisible(false);
					port = Integer.parseInt(portField.getText());
					portField.setDisable(true);
					startBtn.setDisable(false);
				}else{
					portField.clear();
					invalidLabel.setVisible(true);
				}
			}
		});

		this.startBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				primaryStage.setScene(mainScene);
				server = new Server(data->{
					Platform.runLater(
							()->{gameDisplay.getItems().add(data.toString());});}, port);

				server.setCall(data->{Platform.runLater(()->numPlayers.setText(data.toString()));});

			}
		});


		//exits from intro scene
		exitBtn.setOnAction(e->System.exit(-1));

		//exits from server scene
		closeServer.setOnAction(e->System.exit(-1));

		primaryStage.setScene(introScene);
		primaryStage.show();
	}

	private Scene makeIntro() {

		Label portLabel = new Label("ENTER PORT:");
		portField = new TextField();
		invalidLabel = new Label("Invalid port");
		invalidLabel.setTextFill(Color.RED);
		invalidLabel.setVisible(false);
		startBtn = new Button("Start Server");
		startBtn.setDisable(true);
		HBox portLayout = new HBox(10);
		portLayout.getChildren().addAll(portLabel, portField, startBtn);
		portLayout.setAlignment(Pos.CENTER);
		exitBtn = new Button("Exit");
		VBox introLayout = new VBox(20);
		introLayout.getChildren().addAll(portLayout, invalidLabel, exitBtn);
		introLayout.setAlignment(Pos.CENTER);

		return new Scene(introLayout, 600, 600);
	}

	private Scene makeMainScene() {

		gameDisplay = new ListView<String>();
		gameDisplay.setEditable(false);
		gameDisplay.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
		closeServer = new Button("Stop Server");
		numPlayers = new Label("Number of clients: 0");

		BorderPane mainLayout = new BorderPane();
		mainLayout.setPadding(new Insets(50));
		mainLayout.setCenter(gameDisplay);
		mainLayout.setBottom(closeServer);
		mainLayout.setTop(numPlayers);

		return new Scene(mainLayout, 600, 600);
	}

	//checks that user entered a numeric bet, no alphabetic values
	public boolean isNumeric(String s) {

		char alph[] = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z'};

		for (int i = 0; i < alph.length; i++) {
			if (s.contains(Character.toString(alph[i]))) {
				return false;
			}
			if ( s.contains( Character.toString( alph[i] ).toUpperCase() ) ) {
				return false;
			}

		}
		return true;
	}

}
