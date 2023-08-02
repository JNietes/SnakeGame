
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import java.util.Collections;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.Point;

public class SnakeGame extends Application {
   
   @Override
   public void start(Stage primaryStage) {
      int bx = 500;
      int by = 500;
      
      BorderPane pane = new BorderPane();
      
      Text message = new Text("Press arrow keys to start");
      HBox mHBox = new HBox();
      
      message.setFont(Font.font(16));
      
      mHBox.getChildren().add(message);
      mHBox.setAlignment(Pos.CENTER);
      pane.setBottom(mHBox);
      
      SnakePane snake = new SnakePane(bx, by);
      pane.setCenter(snake);
      
      snake.setOnKeyPressed(e -> {
         
         if (message.getText().equals("Press arrow keys to start")){
            mHBox.getChildren().clear();
            message.setText("not the instructions");
            mHBox.getChildren().add(snake.getMessageBoard());
            snake.setMessageBoard("0");
         }
         
         if (snake.getAlive() == true) {
            snake.startAnimation();
         }
      
         if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) {
            snake.setLastDirection(snake.getDirection());
            snake.setDirection('u');
            }
         
         else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) {
            snake.setLastDirection(snake.getDirection());
            snake.setDirection('d');
            }
         
         else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
            snake.setLastDirection(snake.getDirection());
            snake.setDirection('r');
            }

         else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
            snake.setLastDirection(snake.getDirection());
            snake.setDirection('l');
            }
      });
      
      MenuBar menuBar = new MenuBar();
      Menu options = new Menu("Options");
      Menu snakeSpeed = new Menu("Snake Speed");
      MenuItem restart = new MenuItem("Restart");
      MenuItem exit = new MenuItem("Exit");
      MenuItem slow = new MenuItem("Slow");
      MenuItem medium = new MenuItem("Medium");
      MenuItem fast = new MenuItem("Fast");
      
      exit.setOnAction(e -> System.exit(0));
      restart.setOnAction(e -> snake.resetSnake());
      slow.setOnAction(e -> snake.setSnakeSpeed(200));
      medium.setOnAction(e -> snake.setSnakeSpeed(100));
      fast.setOnAction(e -> snake.setSnakeSpeed(50));
      
      menuBar.getMenus().add(options);
      menuBar.getMenus().add(snakeSpeed);
      options.getItems().add(restart);
      options.getItems().add(exit);
      snakeSpeed.getItems().add(slow);
      snakeSpeed.getItems().add(medium);
      snakeSpeed.getItems().add(fast);
      
      pane.setTop(menuBar);
      
      Scene scene = new Scene(pane, bx, by+25); // Create a scene and place it in the stage
      primaryStage.setTitle("Snake Game"); // Set the stage title
      primaryStage.setScene(scene); // Place the scene in the stage
      primaryStage.show(); // Display the stage  
      
      snake.requestFocus(); // IDK why but this is needed to accept keyboard input into lambda expression above
   }
   
   private class SnakePane extends Pane {
      private int pw;
      private int ph;
      private Timeline animation;
      private ArrayList<Point2D> snakeList = new ArrayList<>(); // Using a list and .contains() for colision detection
      private static Group snakeLines = new Group();
      private char direction;
      private char lastDirection;
      private int snakeLength = 8;
      private int segLength = 25;
      private Point oldHead;
      private Point newHead;
      private static Group snakeFood = new Group();
      private boolean isAlive = true; // Prevents immortal snake
      private Text messageBoard = new Text("");
      private int score = 0;
      private double snakeSpeed = 100;
      
      
      public SnakePane(int pw, int ph) {
         messageBoard.setFont(Font.font(16));
         this.pw = pw;
         this.ph = ph;
         
         this.setOldHead(new Point(pw/2, ph/2));    
         this.setNewHead(new Point(pw/2, ph/2));  
         snakeList.add(oldHead);
         this.getChildren().add(snakeLines);
         this.getChildren().add(snakeFood);
         
         snakeFood.getChildren().add(createSnakeFood());
         startSnake();  
      }  
      
      public void setOldHead(Point p) {
         this.oldHead = p;
      }
      
      public void setNewHead(Point p) {
         this.newHead = p;
      }      
      
      protected void moveSnake() {
         
         if (direction == 'u') {
            this.newHead = new Point((int)oldHead.getX(), (int)oldHead.getY()-segLength);
         }
         else if (direction == 'd') {
            this.newHead = new Point((int)oldHead.getX(), (int)oldHead.getY()+segLength);
         }
         else if (direction == 'r') {
            this.newHead = new Point((int)oldHead.getX()+segLength, (int)oldHead.getY());
         }
         else if (direction == 'l') {
            this.newHead = new Point((int)oldHead.getX()-segLength, (int)oldHead.getY());
         }
         else {
            direction = lastDirection;
         }
         
         if (newHead.getX() == 0 || newHead.getX() == pw || newHead.getY() == 0 || newHead.getY() == ph) {
            animation.stop();
            isAlive = false;
            showDeathMessage();        
            }
            
         if (!(newHead.equals(oldHead))) {   // Insipred by search sort if statement
            snakeList.add(newHead);
         }
         
         if (snakeLines.getChildren().size() >= snakeLength) {
            snakeList.remove(0);
            snakeLines.getChildren().remove(0);
         }
         
         if (snakeList.subList(0, snakeList.size()-1).contains(newHead)) {
            animation.stop(); 
            isAlive = false;
            showDeathMessage(); 
         }

         snakeLines.getChildren().add(new Line((int)oldHead.getX(), (int)oldHead.getY(), (int)newHead.getX(), (int)newHead.getY()));
         this.oldHead = newHead;
         
         if (snakeFood.contains(newHead.getX(), newHead.getY())) {
            snakeLength+=2;
            snakeFood.getChildren().add(createSnakeFood());
            snakeFood.getChildren().remove(0); 
            score++;
            setMessageBoard(score);
         }
      }
      
      private Point createNewHead(char c) {
         Point newHead = new Point();
         if (c == 'u') {        
            newHead = new Point((int)oldHead.getX(), (int)oldHead.getY()-segLength);
         }
         else if (c == 'd') {
            newHead = new Point((int)oldHead.getX(), (int)oldHead.getY()+segLength);
         }
         else if (c == 'r') {
            newHead = new Point((int)oldHead.getX()+segLength, (int)oldHead.getY());
         }
         else if (c == 'l') {
            newHead = new Point((int)oldHead.getX()-segLength, (int)oldHead.getY());
         }
         return newHead;
      }
      
      private Circle createSnakeFood(){
         Circle food = new Circle((int)(Math.random()*pw/segLength)*segLength, (int)(Math.random()*ph/segLength)*segLength, 5);
         while (snakeList.contains(new Point((int)food.getCenterX(), (int)food.getCenterY())) 
               || food.getCenterX() == 0 || food.getCenterX() == pw || food.getCenterY() == 0 || food.getCenterY() == ph) {
            food = new Circle((int)(Math.random()*pw/segLength)*segLength, (int)(Math.random()*ph/segLength)*segLength, 5);
         }
         return food;
      }
      public void resetSnake(){
         
         this.isAlive = true;
         snakeList.clear();
         snakeLines.getChildren().clear();
         
         this.setOldHead(new Point(pw/2, ph/2));    
         this.setNewHead(new Point(pw/2, ph/2));  
         snakeList.add(oldHead);
         this.setDirection('\0');
         this.snakeLength = 8;
         setScore(0);
         setMessageBoard(score);
         
         startSnake();
      }
      
      public void startSnake(){
         animation = new Timeline(new KeyFrame(Duration.millis(snakeSpeed), e -> moveSnake()));
         animation.setCycleCount(Timeline.INDEFINITE);
      }
      
      public void showDeathMessage() {
         setMessageBoard("The Snake Is Dead :'( " + " Your Score: " + String.valueOf(score));
      }
      
      public boolean getAlive() {
         return this.isAlive;
      }
      
      public void startAnimation(){
         animation.play();         
      }
      
      public static Group getSnakeLines() {
         return snakeLines;
      }
      
      public int getScore() {
         return score;
      }
      
      public void setScore(int i) {
         this.score = i;
      }
      
      public void setMessageBoard(int i) {
         messageBoard.setText(String.valueOf(i));
      }
      
      public void setMessageBoard(String s) {
         messageBoard.setText(s);
      }
      
      public Text getMessageBoard() {
         return messageBoard;
      }
      
      public void setSnakeSpeed(double i) {
         this.snakeSpeed = i;
      }
      
      public void setLastDirection(char c) {
         this.lastDirection = c;
      }
      
      public char getLastDirection() {
         return lastDirection;
      }
            
      // Took me the longest to figure this out
      public void setDirection(char c) {
         
         if (snakeList.size() > 2 && snakeList.get(snakeList.size()-2).equals(createNewHead(c))) {
            this.direction = lastDirection;
         }
         else
            this.direction = c;
      }
      
      public char getDirection() {
         return direction;  
      }
   }     
}