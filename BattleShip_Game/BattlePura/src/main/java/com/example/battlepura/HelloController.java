package  com.example.battlepura;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class HelloController {

    @FXML
    private GridPane player1ship;
    @FXML
    private GridPane player2ship;
    @FXML
    private GridPane enemy1;
    @FXML
    private GridPane enemy2;
    @FXML
    private Label labele1;
    @FXML
    private Label labele2;
    @FXML
    private Label labelp1;
    @FXML
    private Label labelp2;

    private boolean isHorizontalPlacement = false;
    boolean hit = false;
    boolean  moved = false;
    boolean shown = false;
    String instructions = "";


    private int[] shipSizes = {3, 2, 2, 1, 1, 1}; // Sizes of the ships
    int totalShips = shipSizes.length;

    public Ship[] player1ShipList = new Ship[totalShips];
    public Ship[] player2ShipList = new Ship[totalShips];

    int placedShipsPlayer1 = 0;
    int placedShipsPlayer2 = 0;

    int player1Health = 10;
    int player2Health = 10;

    boolean winnerDecided = false;
    boolean playerTurn1 = true;
    boolean allShipsPlaced = false;

    @FXML
    private void initialize() {

        // iterate kortechi grid e jate button ke node hisebe store krte pari
        player1ship.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setText("");
                button.setOnKeyPressed(event -> {
                    if (event.getText().equalsIgnoreCase("h")) {
                        isHorizontalPlacement = true; // Set horizontal placement
                        System.out.println("Hor");
                    } else if (event.getText().equalsIgnoreCase("v")) {
                        isHorizontalPlacement = false; // Set vertical placement
                        System.out.println("Ver");
                    }
                });


                button.setOnAction(this::onclick);
            }
        });


//

        //same
        player2ship.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setText("");
                button.setOnKeyPressed(event -> {
                    if (event.getText().equalsIgnoreCase("h")) {
                        isHorizontalPlacement = true; // Set horizontal placement
                        System.out.println("Hor");
                    } else if (event.getText().equalsIgnoreCase("v")) {
                        isHorizontalPlacement = false; // Set vertical placement
                        System.out.println("Ver");
                    }
                });

                // Then set the onAction event
                button.setOnAction(this::onclick);
            }
        });

        showAlert("Start","BATTLESHIP GAME , ENJOY !!!");
        // turnvisibility();
    }

    public void onclick(ActionEvent event) {

        if(!allShipsPlaced)
            handleShipPlacement(event);
        else if(!winnerDecided)
            Attackhandle(event);


    }
    public void turnvisibility()
    {
        if(playerTurn1)
        {
            player2ship.setVisible(false);

            player1ship.setVisible(true);
            labelp2.setVisible(false);
            labele2.setVisible(false);

            labelp1.setVisible(true);
            labele1.setVisible(true);

        }
        else
        {
            player1ship.setVisible(false);

            player2ship.setVisible(true);
            labelp2.setVisible(true);
            labele2.setVisible(true);

            labelp1.setVisible(false);
            labele1.setVisible(false);
        }
    }
    private void placeShip(GridPane grid, int row, int col, int size, boolean h) {

        int p1 = 0, p2 = 0;
        Ship ship = new Ship(size);
        ship.size = size ;

        if(playerTurn1)
        {
            player1ShipList[placedShipsPlayer1] = ship;
        }
        else player2ShipList[placedShipsPlayer2] = ship;

        for (int i = 0; i < size; i++) {
            if(playerTurn1)
            {
                player1ShipList[placedShipsPlayer1].rowX[p1] = row;
                player1ShipList[placedShipsPlayer1].colY[p1] = col;
                p1++;
            }

            else
            {
                player2ShipList[placedShipsPlayer2].rowX[p2] = row;
                player2ShipList[placedShipsPlayer2].colY[p2] = col;
                p2++;
            }

            Button button = getButtonAt(grid, row, col);

            button.setStyle("-fx-background-color: #000000"); // Change color to indicate ship placement
            String shipName = String.valueOf(size);
            button.setText(shipName);
            if (h) {
                col++; // Move to the next column for horizontal placement
            } else {
                row++; // Move to the next row for vertical placement
            }
        }
    }

    private Button getButtonAt(GridPane gridPane, int row, int col) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row && node instanceof Button) {
                return (Button) node;
            }
        }
        return null ;
    }

    private boolean canPlaceShip(GridPane grid, int row, int col, int size, boolean h) {


        if (h && col + size > grid.getColumnCount()) {
            System.out.println(col + size);
            System.out.println(grid.getColumnCount());
            return false; // Ship goes out of bounds horizontally
        }
        if (!h && row + size > grid.getRowCount()) {
            System.out.println(row + size);
            System.out.println(grid.getRowCount());
            return false; // Ship goes out of bounds vertically
        }

        for (int i = 0; i < size; i++) {
            Button button = getButtonAt(grid, row, col);

            if (!button.getText().isEmpty()) {
                return false; // Ship overlaps with another ship
            }
            if (h) {
                col++; // Move to the next column for horizontal placement
            } else {
                row++; // Move to the next row for vertical placement
            }
        }
        return true; // Ship can be placed at the specified position
    }

    private void handleShipPlacement(ActionEvent event) {// ship declaration basically
        Button clickedButton = (Button) event.getSource();
        GridPane grid = (GridPane) clickedButton.getParent();
        int row = GridPane.getRowIndex(clickedButton);//row index
        int col = GridPane.getColumnIndex(clickedButton);

        String shipName = grid.getId();

        if(!allShipsPlaced && (playerTurn1 == true && shipName.equals("player1ship")) || (playerTurn1 == false && shipName.equals("player2ship")))
        {
            int shipSize = 0;

            if(playerTurn1 && placedShipsPlayer1 < totalShips)
            {
                shipSize = shipSizes[placedShipsPlayer1];// ekhne 3 size er ship ashbe placedShipsPlayer1=0 er jnno
            }
            if(!playerTurn1 && placedShipsPlayer2 < totalShips)
            {
                shipSize = shipSizes[placedShipsPlayer2];//ekhneo same
            }
            //row=rowindex ;
            //col=colmindex;

            boolean h = canPlaceShip(grid, row, col, shipSize, isHorizontalPlacement);//basically eta diye check krtechi i can move them or not !

            if(h == true)//jodi move kora jay
            {
                //ship ke placed korbo oi jaygay mane boshay felbo
                placeShip(grid, row, col, shipSize, isHorizontalPlacement);//place the ship
                if(playerTurn1 == true && placedShipsPlayer1 < totalShips) placedShipsPlayer1++;//then samner destroyer er jnno agabo
                else if(playerTurn1 == false && placedShipsPlayer2 < totalShips)placedShipsPlayer2++;//player2 er jnno

            }
            if(placedShipsPlayer1 >= totalShips)//count jodi 6 er beshi hoy ba soman hoy then r place korbo na
            {
                playerTurn1 = false;
            }
            if(placedShipsPlayer2 >= totalShips) allShipsPlaced = true;//12ta ship e placed hoiche ;
            if(allShipsPlaced) playerTurn1 = true;//somehow all ship placed  hole player 1 er kache turn jabe so that ami enemy 2 er jnno button click korte pari
        }
        else System.out.println("wrong ship");//somehow enemy board click korle but since ami 2 ta board kortechi no need for else here ...

        if(allShipsPlaced)//beffore showing the alert i set the player1turn to be true ;
        {
            showAlert("Mission", "All ship are placed now Attack!!!1");
            turnvisibility();//er kaj ektu pore describe kora mane label show korbe ..
        }

    }

    private boolean enemyAttack (GridPane gridP, GridPane gridE, int row, int col) {//attack korar jnno
        try {
            Button buttonE = getButtonAt(gridE, row, col);//je button click korbo
            Button buttonP = getButtonAt(gridP, row, col);

            if (buttonP.getText().isEmpty()) {//button er string empty thakle miss cz button tahole click e hoyni before
                System.out.println("Miss");
                buttonE.setStyle("-fx-background-color: #00FF00"); // Change color to indicate ship placement
                buttonE.setText("0");
                buttonP.setStyle("-fx-background-color: #00FF00"); // Change color to indicate ship placement
                buttonP.setText("0");
                return false;
            }
            else
            {
                //button er text e jdi 1,2,3 thake tahole jekono ship er length geche basically
                if(buttonP.getText().equals("1") || buttonP.getText().equals("2") || buttonP.getText().equals("3"))
                {
                    buttonP.setStyle("-fx-background-color: #FF0000"); // Change color to indicate ship placement
                    buttonP.setText("E");

                    buttonE.setStyle("-fx-background-color: #00FFFF"); // Change color to indicate ship placement
                    buttonE.setText("P");



                    if(player1Health == 0)//ekebare shesh e
                    {
                        winnerDecided = true;
                        System.out.println("Winner = player 2");
                        showAlert("Winner", "Player2");
                        close();
                    }
                    else if(player2Health == 0)
                    {
                        winnerDecided = true;
                        System.out.println("Winner = player 1");
                        showAlert("Winner", "Player1");
                        close();
                    }
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Get the dialog pane
        DialogPane dialogPane = alert.getDialogPane();

        // Set the preferred size (width and height)
        dialogPane.setPrefWidth(300);
        dialogPane.setPrefHeight(300);

        // Apply any other customization as needed

        alert.showAndWait();
    }


    public void moveIntact(int player, int row, int col, int x, int y)
    {
        int index = getShipIndex(player, row, col);
        if(validMovement(player, index, row, col, x, y))
        {
            moved = true;
            hit = false;
            move(player, index, row, col, x, y);
        }

    }
    public void Attackhandle(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        GridPane grid = (GridPane) clickedButton.getParent();
        int row = GridPane.getRowIndex(clickedButton);
        int col = GridPane.getColumnIndex(clickedButton);
        String shipName = grid.getId();

        if (!allShipsPlaced) {
            showAlert("Error", "All ships must be placed before attacking.");
            return;
        }

        boolean hitResult = false;
        if (shipName.equals("player1ship") && playerTurn1) {
            hitResult = enemyAttack(player2ship, grid, row, col);
        } else if (shipName.equals("player2ship") && !playerTurn1) {
            hitResult = enemyAttack(player1ship, grid, row, col);
        }
        else {
            showAlert("Error", "Shooting wrong Grid!!.");
            return;
        }

        if (hitResult) {
            if (playerTurn1) player2Health--;
            else player1Health--;

            if (player1Health == 0 || player2Health == 0) {
                winnerDecided = true;
                String winner = (player1Health == 0) ? "ENEMY" : "PLAYER";
                showAlert("Winner", winner);
                return;
                // close();
            }
        }

        // Toggle player turn
        playerTurn1 = !playerTurn1;
    }


    public boolean validMovement(int player, int index, int row, int col, int x, int y)
    {
        if(player == 1)
        {
            Button button = getButtonAt(player1ship, row, col);
            if(button.getText().isEmpty()) return false;

            for(int i = 0; i < player1ShipList[index].size; i++)
            {
                int r = player1ShipList[index].rowX[i];
                int c = player1ShipList[index].colY[i];
                button = getButtonAt(player1ship, r, c);
                if(button.getText().equals("0")) return false;

                r = r + x;
                c = c + y;
                if(r < 0 || c < 0 || r > 8 || c > 8) return false;

                button = getButtonAt(player1ship, r, c);

                if(!(button.getText().isEmpty() || getShipIndex(player, r, c) == index )) return false;
            }
        }
        else if(player == 2)
        {
            Button button = getButtonAt(player2ship, row, col);
            if(button.getText().isEmpty()) return false;

            for(int i = 0; i < player2ShipList[index].size; i++)
            {
                int r = player2ShipList[index].rowX[i];
                int c = player2ShipList[index].colY[i];
                button = getButtonAt(player2ship, r, c);
                if(button.getText().equals("0")) return false;

                r = r + x;
                c = c + y;
                if(r < 0 || c < 0 || r > 8 || c > 8) return false;

                button = getButtonAt(player2ship, r, c);
                if(!(button.getText().isEmpty() || getShipIndex(player, r, c) == index )) return false;
            }
        }
        return true;
    }
    public void move(int player,int index, int row, int col, int x, int y)
    {
        if(player == 1)
        {
            Button button = getButtonAt(player1ship, row, col);

            for(int i = 0; i < player1ShipList[index].size; i++)
            {
                int r = player1ShipList[index].rowX[i];
                int c = player1ShipList[index].colY[i];
                button = getButtonAt(player1ship, r, c);

                button.setText("");
                button.setStyle("-fx-background-color: #f4f4f4");

            }
            for(int i = 0; i < player1ShipList[index].size; i++)
            {
                int r = player1ShipList[index].rowX[i];
                int c = player1ShipList[index].colY[i];


                r = r + x;
                c = c + y;

                button = getButtonAt(player1ship, r, c);

                button.setText(String.valueOf(player1ShipList[index].size));
                button.setStyle("-fx-background-color: #000000");

                player1ShipList[index].rowX[i] = r;
                player1ShipList[index].colY[i] = c;
            }
        }
        else if(player == 2)
        {
            Button button = getButtonAt(player2ship, row, col);

            for(int i = 0; i < player2ShipList[index].size; i++)
            {
                int r = player2ShipList[index].rowX[i];
                int c = player2ShipList[index].colY[i];
                button = getButtonAt(player2ship, r, c);

                button.setText("");
                button.setStyle("-fx-background-color: #f4f4f4");

            }

            for(int i = 0; i < player2ShipList[index].size; i++)
            {
                int r = player2ShipList[index].rowX[i];
                int c = player2ShipList[index].colY[i];
                button = getButtonAt(player2ship, r, c);


                r = r + x;
                c = c + y;
                button = getButtonAt(player2ship, r, c);

                button.setText(String.valueOf(player2ShipList[index].size));
                button.setStyle("-fx-background-color: #000000");

                player2ShipList[index].rowX[i] = r;
                player2ShipList[index].colY[i] = c;
            }
        }
    }
    public int getShipIndex(int player, int row, int col)
    {
        int index = 0;

        if(player == 1)
        {
            for(int i = 0; i < player1ShipList.length; i++)
            {
                for(int j = 0; j < player1ShipList[i].size; j++)
                {
                    if(player1ShipList[i].rowX[j] == row && player1ShipList[i].colY[j] == col)
                    {
                        index = i;
                        break;
                    }
                }
            }
        }
        else if(player == 2)
        {
            for(int i = 0; i < player2ShipList.length; i++)
            {
                for(int j = 0; j < player2ShipList[i].size; j++)
                {
                    if(player2ShipList[i].rowX[j] == row && player2ShipList[i].colY[j] == col)
                    {
                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }
    //rowCordinate
    public void printInfo(int player)
    {
        if(player  == 1)
        {
            for(int i = 0; i < player1ShipList.length; i++)
            {
                System.out.println("player 1 size = " + player1ShipList[i].size);

                for(int j = 0; j < player1ShipList[i].size; j++)
                {
                    System.out.print("r " + player1ShipList[i].rowX[j]);
                    System.out.println(", c " + player1ShipList[i].colY[j]);
                }
                System.out.print("\n");
            }

        }
    }



    public void close()
    {

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished( event -> Platform.exit());
        delay.play();
    }
}


