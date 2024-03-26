package com.example;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Group group = new Group();
        Scene scene = new Scene(group, 800, 600);
        ArrayList<Ball> balls = new ArrayList<Ball>();
        CollisionHandler collider = new CollisionHandler();
        Rectangle background = new Rectangle(0,0,800,600);
        background.setFill(Color.WHITE);
        group.getChildren().add(background);

        Ball btn = new Ball(100, 100, collider);
        group.getChildren().add(btn);

        background.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("add new ball!");
                Ball temp = new Ball(event.getSceneX(),event.getSceneY(),collider);
                balls.add(temp);
                group.getChildren().add(balls.get(balls.size()-1));
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

class Ball extends Circle {
    boolean pressed, walled;
    double x, y;
    double r;
    public static final double R = 10;
    CollisionHandler collider;
    EventHandler<MouseEvent> clickHandler, dragHandler, releaseHandler;
    
    Ball(double x, double y, CollisionHandler collider){
        super(R);
        this.r = R;
        this.x = x;
        this.y = y;
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.pressed = false;
        this.walled = false;
        this.clickHandler = new BallClickHandler(this);
        this.dragHandler = new BallDragHandler(this);
        this.releaseHandler = new BallReleaseHandler(this);
        this.setOnMousePressed(clickHandler);
        this.setOnMouseDragged(dragHandler);
        this.setOnMouseReleased(releaseHandler);
        collider.addBall(this);
        this.collider = collider;
    }

    void move(double x, double y){
        this.setLayoutX(this.x = x);
        this.setLayoutY(this.y = y);
    }
}

class BallDragHandler implements EventHandler<MouseEvent>{
    Ball ball;

    BallDragHandler(Ball ball){
        this.ball = ball;
    }
    
    @Override
    public void handle(MouseEvent arg0) {
        ball.move(arg0.getSceneX(),arg0.getSceneY());
        ball.collider.recompute();
    }
}

class BallClickHandler implements EventHandler<MouseEvent>{
    Ball ball;

    BallClickHandler(Ball ball){
        this.ball = ball;
    }
    
    @Override
    public void handle(MouseEvent arg0) { 
        ball.pressed = true;
    }
}

class BallReleaseHandler implements EventHandler<MouseEvent>{
    Ball ball;

    BallReleaseHandler(Ball ball){
        this.ball = ball;
    }
    
    @Override
    public void handle(MouseEvent arg0) { 
        ball.pressed = false;
    }
}

class CollisionHandler{
    ArrayList<Ball> balls = new ArrayList<Ball>();

    void addBall(Ball ball){
        balls.add(ball);
        this.recompute();
    }

    void recompute(){
        if(balls.size() < 2) return;
        boolean shifted = true;
        while(shifted){
            shifted = false;

            // for(Ball b: balls){
            //     double
            //     br = b.r,
            //     er = br + 0.1,
            //     bx = b.x,
            //     by = b.y;

            //     if(bx - br < 0){
            //         shifted = true;
            //         b.move(er , by);
            //     }
            //     else if(bx + br > 800){
            //         shifted = true;
            //         b.move(800 - er , by);
            //     }

            //     if(by - br < 0 ){
            //         shifted = true;
            //         b.move(bx , er);
            //     }
            //     else if(by + br > 600){
            //         shifted = true;
            //         b.move(bx , 600 - er);
            //     }
            // }

            for(int i = 0; i < balls.size() - 1; i++){
                for(int j = i+1; j < balls.size(); j++){
                    Ball b1 = balls.get(i), b2 = balls.get(j);
                    double
                    b1r = b1.r,
                    b2r = b2.r,
                    b1er = b1r + 0.1,
                    b2er = b2r + 0.1,
                    b1x = b1.x,
                    b1y = b1.y,
                    b2x = b2.x,
                    b2y = b2.y,
                    dist = Math.sqrt(Math.pow(b1x - b2x, 2) + Math.pow(b1y - b2y, 2)),
                    shift = b1er + b2er - dist;
                    double[] vect = new double[]{b2x-b1x, b2y-b1y};
                    if(b1r + b2r - dist > 0){
                        shifted = true;
                        vect[0] /= dist;
                        vect[1] /= dist;
                        if(b1.pressed){
                                b2.move(b2x + vect[0]*shift, b2y + vect[1]*shift);
                        }
                        else if(b2.pressed){
                                b1.move(b1x - vect[0]*shift, b1y - vect[1]*shift);
                        }
                        else{
                            b1.move(b1x - vect[0]*shift/2, b1y - vect[1]*shift/2);
                            b2.move(b2x + vect[0]*shift/2, b2y + vect[1]*shift/2);
                        }
                    }
                }
            }
        }
    }
}