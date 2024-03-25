package com.example;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Group group = new Group();
        Scene scene = new Scene(group, 800, 600);
        ArrayList<Ball> balls = new ArrayList<Ball>();
        CollisionHandler collider = new CollisionHandler();

        Ball btn = new Ball(100, 100, collider);
        group.getChildren().add(btn);

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Ball temp = new Ball(event.getSceneX(),event.getSceneY(),collider);
                balls.add(temp);
                group.getChildren().add(temp);
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
    boolean pressed;
    double[] coords;
    CollisionHandler collider;
    EventHandler<MouseEvent> clickHandler, dragHandler, releaseHandler;
    
    Ball(double x, double y, CollisionHandler collider){
        super(10);
        coords = new double[2];
        coords[0] = x;
        coords[1] = y;
        this.relocate(x, y);
        pressed = false;
        clickHandler = new BallClickHandler(this);
        dragHandler = new BallDragHandler(this);
        releaseHandler = new BallReleaseHandler(this);
        this.setOnMousePressed(clickHandler);
        this.setOnMouseDragged(dragHandler);
        this.setOnMouseReleased(releaseHandler);
        collider.addBall(this);
        this.collider = collider;
    }
}

class BallDragHandler implements EventHandler<MouseEvent>{
    Ball ball;

    BallDragHandler(Ball ball){
        this.ball = ball;
    }
    
    @Override
    public void handle(MouseEvent arg0) {
        ball.relocate(arg0.getSceneX(),arg0.getSceneY());
        ball.coords[0] = arg0.getSceneX();
        ball.coords[1] = arg0.getSceneY();
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
            for(int i = 0; i < balls.size() - 1; i++){
                for(int j = i+1; j < balls.size(); j++){
                    Ball b1 = balls.get(i), b2 = balls.get(j);
                    double
                    b1r = b1.getRadius(),
                    b2r = b2.getRadius(),
                    b1er = b1r + 0.1,
                    b2er = b2r + 0.1,
                    b1x = b1.coords[0],
                    b1y = b1.coords[1],
                    b2x = b2.coords[0],
                    b2y = b2.coords[1],
                    dist = Math.sqrt(Math.pow(b1x - b2x, 2) + Math.pow(b1y - b2y, 2)),
                    shift = b1er + b2er - dist;
                    double[] vect = new double[]{b2x-b1x, b2y-b1y};
                    if(b1r + b2r - dist > 0){
                        shifted = true;
                        double vectlen = Math.sqrt(vect[0]*vect[0] + vect[1]*vect[1]); 
                        vect[0] /= vectlen;
                        vect[1] /= vectlen;
                        if(b1.pressed){
                            b2.relocate(b2x + vect[0]*(shift), b2y + vect[1]*(shift));
                            b2.coords[0] = b2x + vect[0]*(shift);
                            b2.coords[1] = b2y + vect[1]*(shift);
                        }
                        else if(b2.pressed){
                            b1.relocate(b1x - vect[0]*(shift), b1y - vect[1]*(shift));
                            b1.coords[0] = b1x - vect[0]*(shift);
                            b1.coords[1] = b1y - vect[1]*(shift);
                        }
                        else{
                            b1.relocate(b1x + vect[0]*(shift)/2, b1y + vect[1]*(shift)/2);
                            b2.relocate(b2x - vect[0]*(shift)/2, b2y - vect[1]*(shift)/2);
                            b1.coords[0] = b1x - vect[0]*(shift)/2;
                            b1.coords[1] = b1y - vect[1]*(shift)/2; 
                            b2.coords[0] = b2x + vect[0]*(shift)/2;
                            b2.coords[1] = b2y + vect[1]*(shift)/2;    
                        }
                    }
                }
            }

            for(Ball b: balls){
                double
                br = b.getRadius(),
                er = br + 0.1,
                bx = b.coords[0],
                by = b.coords[1];
                if(bx - br < 0){
                    shifted = true;
                    b.relocate(er , by);
                    b.coords[0] = er;
                }
                else if(bx + br > 800){
                    shifted = true;
                    b.relocate(800 - er , by);
                    b.coords[0] = 800 - er;
                }

                if(by - br < 0 ){
                    shifted = true;
                    b.relocate(bx , er);
                    b.coords[1] = er;
                }
                else if(by + br > 600){
                    shifted = true;
                    b.relocate(bx , 600 - er);
                    b.coords[1] = 600 - er;
                }
            }
        }
    }
}