import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Collections;

public class TicTacToe extends Application {

	private Scene introScene, lobbyScene, errorScene, gameScene;
	private Button connect, retry, exit, play, quit, returnBtn, playAgain;
	private Button letServerGo;
	private Menu difficulty;
	private CheckMenuItem easy, medium, expert;
	private TextField ipField, portField;
	private String ipAddress, chosenDiff;
	private int port;
	private Label gameMsg;
	private ListView<String> leaderBoard;
	private Image lobbyBG = new Image("/lobbyBg.PNG");
	private Image x_img, o_img;
	private Image blank = new Image("/blank.jpg");
    private ImageView images[] = new ImageView[9];
	private Client client;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Let's Play Tic Tac Toe!!!");

		introScene = makeIntro();
		lobbyScene = makeLobby();
		gameScene = makeGameScene();
		errorScene = makeErrorScene();

		//get ip address from text field
		ipField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				ipAddress = ipField.getText();
				if(isNumeric(ipAddress) && (ipAddress.length() >= 4 && ipAddress.length() <= 15)
						&& evalIP(ipAddress)) {
					ipField.setDisable(true);
					portField.setDisable(false);
				}else{
					ipField.setText("Invalid ip address");
					ipField.setDisable(true);
				}
			}
		});

		//get port number
		portField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				if(isNumeric(portField.getText()) && (portField.getText().length() == 4)) {
					port = Integer.parseInt(portField.getText());
					portField.setDisable(true);
					connect.setDisable(false);
					retry.setDisable(false);
				}
				else{
					portField.setText("Invalid port number");
				}
			}
		});

		connect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				client = new Client( data->{ Platform.runLater( ()->{leaderBoard.getItems().add(data.toString());});}, ipAddress, port);
				client.start();

				//waits till socket thread finishes connection
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//if flag == true: client was unable to connect to server
				if(client.flag) {
					primaryStage.setScene(errorScene);

				}else{
					primaryStage.setScene(lobbyScene);  //sets scene to lobby screen
				}
			}
		});

		//lets user enter new ip/port number
		retry.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				connect.setDisable(true);
				ipField.clear();
				portField.clear();
				ipField.setDisable(false);
			}
		});

		//takes player to the actual game, and resets the GUI
		play.setOnAction(e->{playGame(); primaryStage.setScene(gameScene);
					client.setCall(data->{Platform.runLater( ()->gameMsg.setText(data.toString()));});});

		letServerGo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//sets image of X on screen and sets its new id so the same block cant be picked again
				images[client.info.serverMove].setImage(x_img);
				images[client.info.serverMove].setId(client.info.serverMove+" 1");

				//disables button
				letServerGo.setTextFill(Color.RED);
				letServerGo.setDisable(true);

				for (ImageView im: images) {
					im.setDisable(false);
				}

				//if the server made the winning move, let the player know the sad news
				//end the game and activate the play again button
				if (client.info.serverWon) {
					client.callback2.accept("You lost the game ):");
					endGame();
				}
			}
		});

		playAgain.setOnAction(e->{
			primaryStage.setScene(lobbyScene);
		});


		//handles when a position on the board has been clicked on
		images[0].setOnMouseClicked(e->boardClick(0));
		images[1].setOnMouseClicked(e->boardClick(1));
		images[2].setOnMouseClicked(e->boardClick(2));
		images[3].setOnMouseClicked(e->boardClick(3));
		images[4].setOnMouseClicked(e->boardClick(4));
		images[5].setOnMouseClicked(e->boardClick(5));
		images[6].setOnMouseClicked(e->boardClick(6));
		images[7].setOnMouseClicked(e->boardClick(7));
		images[8].setOnMouseClicked(e->boardClick(8));


		//exits from intro scene
		exit.setOnAction(e->System.exit(-1));

		//exits from lobby scene
		quit.setOnAction(e->System.exit(-1));

		primaryStage.setScene(introScene);
		primaryStage.show();
	}

	private Scene makeIntro() {

		//add game title
		Text title = new Text("   TIC TAC TOE");
		title.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
		title.setFill(Color.DARKGREY);

		//place for user to enter an ip address and port
		Label ipLabel = new Label("IP  ");
		ipLabel.setTextFill(Color.RED);
		ipLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
		ipField = new TextField();
		HBox hBox = new HBox(5);
		hBox.getChildren().addAll(ipLabel, ipField);
		hBox.setAlignment(Pos.CENTER);

		Label portLabel = new Label("PORT");
		portLabel.setTextFill(Color.RED);
		portLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 13));
		portField = new TextField();
		portField.setDisable(true);
		HBox hBox2 = new HBox(5);
		hBox2.getChildren().addAll(portLabel, portField);
		hBox2.setAlignment(Pos.CENTER);

		//buttons used for intro user decision
		//connect will start the connection between server and client
		//exit will end the program, retry allows user to renter ip/port number
		connect = new Button("Connect");
		connect.setDisable(true);
		exit = new Button("Exit");
		retry = new Button("Retry");
		VBox introLayout = new VBox(15);
		introLayout.setAlignment(Pos.CENTER);
		introLayout.getChildren().addAll(title, hBox, hBox2, connect, retry, exit);

		Background bg = new Background(new BackgroundFill(Color.PURPLE, CornerRadii.EMPTY, Insets.EMPTY));
		introLayout.setBackground(bg);

		return new Scene(introLayout, 600, 600);
	}

	private Scene makeLobby() {

		//scene layout
		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(50));

		//create background
		Background bg = new Background(new BackgroundFill(Color.LIGHTBLUE,null, null));

		Label diffLabel = new Label("Current Difficulty: Easy");
		diffLabel.setFont(Font.font(null, FontWeight.BOLD, 20));
		diffLabel.setTextFill(Color.RED);
		diffLabel.setBackground(bg);


		//sets LeadBoard
		Label lbLabel = new Label("LeaderBoard");
		lbLabel.setTextFill(Color.RED);
		lbLabel.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 30));
		leaderBoard = new ListView<>();
		leaderBoard.setMaxSize(500, 200);
		VBox top = new VBox(10);
		top.getChildren().addAll(lbLabel, leaderBoard);
		top.setBackground(bg);

		//sets buttons
		play = new Button("PLAY");
		play.setTextFill(Color.PURPLE);
		play.setBackground(bg);
		quit = new Button("QUIT");
		quit.setTextFill(Color.RED);
		quit.setBackground(bg);

		//gets which difficulty the player has chosen
		EventHandler<ActionEvent> e = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				chosenDiff = ((MenuItem)event.getSource()).getText();
				client.info.Difficulty = chosenDiff;
				diffLabel.setText("Current Difficulty: "+ client.info.Difficulty);
			}
		};

		//create difficulty settings
		difficulty = new Menu("Difficulty");
		easy = new CheckMenuItem("Easy");
		medium = new CheckMenuItem("Medium");
		expert = new CheckMenuItem("Expert");
		easy.setOnAction(e);
		medium.setOnAction(e);
		expert.setOnAction(e);
		difficulty.getItems().addAll(easy, medium, expert);
		MenuBar bar = new MenuBar();
		bar.getMenus().add(difficulty);
		bar.setBackground(bg);
		Button refresh = new Button("Refresh LeaderBoard");

		refresh.setOnAction(event->{
			leaderBoard.getItems().clear();
			if(client.info.TopScores.size() == 1) {
				leaderBoard.getItems().add(client.info.top1);
			}
			if(client.info.TopScores.size() == 2) {
				leaderBoard.getItems().add(client.info.top2);
			}
			if(client.info.TopScores.size() == 3) {
				leaderBoard.getItems().add(client.info.top3);
			}
		});

		quit = new Button("Quit");
		quit.setBackground(bg);

		HBox center = new HBox(10);
		center.getChildren().addAll(play, bar, quit, refresh);
		center.setAlignment(Pos.CENTER_LEFT);


		layout.setTop(top);
		layout.setCenter(center);
		layout.setBottom(diffLabel);

		BackgroundSize bgSize = new BackgroundSize(600, 600, false, false, false, false);
		BackgroundImage bgImage = new BackgroundImage(lobbyBG, null, null, BackgroundPosition.CENTER, bgSize);
		Background bg2 = new Background(bgImage);
		layout.setBackground(bg2);

		return new Scene(layout, 600, 600);
	}

	private Scene makeGameScene() {
        int i = 0;

        //sets images for x and o symbol
        x_img = new Image("/x.jpg");
        o_img = new Image("/o.jpg");

	    BorderPane layout = new BorderPane();
	    layout.setPadding(new Insets(80));

	    //setup board
        for (i = 0; i < 9; i++) {
            images[i] = new ImageView(blank);
            images[i].setFitWidth(100);
            images[i].setFitHeight(100);

            //sets a unique ID to each imageView  (position, availability )
			//first value represents its position in board
			//second value will be 0 if the image is blank or 1 if the image has been placed
            images[i].setId(i+" 0");
        }

        HBox row1 = new HBox(20);
        HBox row2 = new HBox(20);
        HBox row3 = new HBox(20);

        row1.getChildren().addAll(images[0], images[1], images[2]);
        row2.getChildren().addAll(images[3], images[4], images[5]);
        row3.getChildren().addAll(images[6], images[7], images[8]);

        VBox board = new VBox(20);
        board.getChildren().addAll(row1, row2, row3);
        board.setAlignment(Pos.CENTER);
        Background bg1 = new Background((new BackgroundFill(Color.GOLDENROD, null, null)));
        board.setBackground(bg1);
        board.setPadding(new Insets(10, 20, 10, 50));

        //Label used to give message to player
		gameMsg = new Label("You make the first move!");
		gameMsg.setTextFill(Color.BLUEVIOLET);
		gameMsg.setFont(Font.font(null, FontWeight.BOLD, 20));

        //playAgain button
        playAgain = new Button("Play Again");
        playAgain.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, null, null)));
        playAgain.setTextFill(Color.BLUE);
        playAgain.setFont(Font.font(null, FontWeight.BOLD, 15));
        playAgain.setAlignment(Pos.CENTER_RIGHT);

        //buttons used when client decides on their move
        letServerGo = new Button("letServerGo");
        letServerGo.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, null, null)));
        letServerGo.setTextFill(Color.RED);
        letServerGo.setFont(Font.font(null, FontWeight.BOLD, 20));
        letServerGo.setDisable(true);

        layout.setCenter(board);
        layout.setRight(playAgain);
        layout.setBottom(letServerGo);
        layout.setTop(gameMsg);

        Background bg = new Background(new BackgroundFill(Color.BLACK, null, null));
        layout.setBackground(bg);

	    return new Scene(layout, 600 ,600);
    }


	private Scene makeErrorScene() {
		Label label1 = new Label("Server does not exist. Return to main screen.");
		label1.setTextFill(Color.BLUE);
		label1.setAlignment(Pos.TOP_CENTER);
		label1.resize(10,10);
		returnBtn = new Button("Return");
		VBox vbox = new VBox(10);
		vbox.getChildren().addAll(label1, returnBtn);
		vbox.setAlignment(Pos.CENTER);

		return new Scene(vbox, 400, 200);
	}


	//this functions is used for when the player hits the play button to start the game
	private void playGame(){
	    //must clear board from previous game
	    clearBoard();

	    //board is not playable
		for (ImageView im: images) {
			im.setDisable(false);
		}

	    client.info.inGame = true;
		client.info.clientMoveCount = 0;
		client.info.ServerMoveCount = 0;
	    gameMsg.setText("You make the first move");

    }

    //updates the board based on client action, places "O" where they clicked
	private void boardClick(int i)  {

		String temp = images[i].getId();
		char last = temp.charAt(temp.length()-1);

		//checks if the block clicked on has not been picked already
		if (last == '0') {

			images[i].setImage(o_img);
			images[i].setId(i + " 1");

			int id = Character.getNumericValue(images[i].getId().charAt(0));

			//keeps track of current board set up as a string
			client.info.board[id] = "O";

			//keeps track at how many moves have been made (must not exceed 9)

			if(client.checkForWin()){
				client.callback2.accept("You Won!!");
				endGame();
				return;
			}

			client.info.clientMoveCount++;
			if (client.info.clientMoveCount == 5 && client.info.ServerMoveCount == 4) {
				client.callback2.accept("Game ends in a tie, no points gained.");
				endGame();
				return;
			}

			//send current board to server
			client.send();

			//allows player to let server make a move after they've made a move
			letServerGo.setDisable(false);
			letServerGo.setTextFill(Color.GREEN);

		}
		else if (last == '1'){
			client.callback2.accept("Can't make that move, sorry");
		}

		for (ImageView im: images) {
			im.setDisable(true);
		}

		PauseTransition pause = new PauseTransition(Duration.seconds(.2));
		pause.setOnFinished(e->letServerGo.fire());
		pause.play();
	}

	//utility to clean up board at start of new game
	private void clearBoard() {

		for (int i = 0; i < images.length; i++) {
			images[i].setImage(blank);
			images[i].setId(i+ " 0");

			client.info.board[i] = "b";
		}

    }

    private void endGame() {

		//stops the player from being able to keep playing after the game ended
		for (ImageView im: images) {
			im.setDisable(true);
		}

		client.info.inGame = false;

		letServerGo.setDisable(true);

		//enables player to play again
		playAgain.setDisable(false);
	}

	//checks that user entered a numeric bet, no alphabetic values
	boolean isNumeric(String s) {

		char alph[] = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z', ',', ';', '"', '/'};

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

	//counts the amount of dots in ip address (should only be 4)
	boolean evalIP(String ip) {

		int size = ip.length();
		int numOfDots = 0;
		for (int i = 0; i < size; i++) {
			if(ip.charAt(i) == '.') {
				numOfDots += 1;
			}
		}

		if (numOfDots != 3) {
			return false;
		}

		return true;
	}

}
