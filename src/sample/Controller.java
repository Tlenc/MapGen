package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;


public class Controller {


    public Button GenButton;
    public Button SaveButton;
    public Canvas MapCanvas;
    public Pane StackPane;
    public Button SpawnIcon;
    public ChoiceBox<String> SelectMapType;
    public ObservableList<String> MapTypeValues = FXCollections.observableArrayList("OpneSimplex Noise","test");
    public String selectedChoice;

    public Pane MainPane;
    private static final int WIDTH = 565;
    private static final int HEIGHT = 500;
    private static boolean isResizing;
    private static double resizeX = 50;
    private static double resizeY = 50;


    public int Id = 0;
    Random rand = new Random();

    public void initialize(){
        SelectMapType.setItems(MapTypeValues);


    }


    public void OnSpawnIcon() {
        Image img = new Image("sample\\Icon_town.png");
        ImageView imgV = new ImageView(img);

        imgV.setId(Integer.toString(Id));
        StackPane.getChildren().add(imgV);
        Id++;
        imgV.setFitHeight(50);
        imgV.setFitWidth(50);
        Draggable(imgV);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem("Resize");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                imgV.setCursor(Cursor.E_RESIZE);
                isResizing = true;

                imgV.setOnMouseDragged(new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent dr) {
                        if (isResizing == true) {
                            imgV.setFitWidth(dr.getX());
                            imgV.setFitHeight(dr.getX());
                            resizeX =imgV.getFitWidth();
                            resizeY = imgV.getFitHeight();
                        }
                    }
                });
                imgV.setOnMouseReleased(new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent dr) {
                        isResizing = false;
                        imgV.setCursor(Cursor.DEFAULT);
                    }
                });

            }
        });

        MenuItem item2 = new MenuItem("Delete");
        item2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                StackPane.getChildren().remove(imgV);
            }
        });


        contextMenu.getItems().addAll(item1, item2);

        imgV.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

            @Override
            public void handle(ContextMenuEvent event) {

                contextMenu.show(imgV, event.getScreenX(), event.getScreenY());
            }
        });


    }

    public void Draggable(Node imgV) {


        imgV.setCursor(Cursor.HAND);


        StackPane.setOnMouseDragged(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent me) {

                Bounds bound = new BoundingBox(0, 0, WIDTH-resizeX, HEIGHT-resizeY);
                Point2D currentPointer = new Point2D(me.getX(), me.getY());
                if (me.isPrimaryButtonDown() && !isResizing) {
                    if (bound.contains(currentPointer)) {

                        imgV.setLayoutX(me.getX());
                        imgV.setLayoutY(me.getY());
                    }

                }
            }

        });


    }

    public void OnSaveButton() {

        File file = new File("CanvasImage.png");
        WritableImage image = StackPane.snapshot(new SnapshotParameters(), null);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (Exception s) {
        }

    }


    public void OnGenButton() {



        selectedChoice = SelectMapType.getValue();
        System.out.print(selectedChoice);



        long seed = rand.nextLong();

        OpenSimplexNoise n1 = new OpenSimplexNoise(seed);
        OpenSimplexNoise n2 = new OpenSimplexNoise(seed);
        OpenSimplexNoise n3 = new OpenSimplexNoise(seed);
        OpenSimplexNoise n4 = new OpenSimplexNoise(seed);
        OpenSimplexNoise n5 = new OpenSimplexNoise(seed);

        GraphicsContext gc = MapCanvas.getGraphicsContext2D();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                double value = (n1.eval(x / 48.0, y / 48.0, 0.5) + n2.eval(x / 24.0, y / 24.0, 0.5) * .5 + n3.eval(x / 12.0, y / 12.0, 0.5) * .25 + n4.eval(x / 12.0, y / 12.0, 0.5 + n5.eval(x / 12.0, y / 12.0, 0.5) * .25) * .25) / (1 + .5 + .25 + .25 + 0.5);

                double elevation = n1.eval(x / 24.0, y / 24.0, 0.5);
                double temperature = n2.eval(x / 24.0, y / 24.0, 0.5);
                double precipitation = n3.eval(x / 24.0, y / 24.0, 0.5);


                int rgb = 0x010101 * (int) ((value + 1) * 127.5);
                int HeightNoise = ((rgb >> 16) & 255);
                if (HeightNoise <= 91) {
                    int r = 11;
                    int g = 0;
                    int b = 230;
                    gc.getPixelWriter().setColor(x, y, Color.rgb(r, g, b));
                } else if (HeightNoise >= 91 && HeightNoise <= 100) {
                    int r = 211;
                    int g = 191;
                    int b = 135;
                    gc.getPixelWriter().setColor(x, y, Color.rgb(r, g, b));
                } else if (HeightNoise >= 100 && HeightNoise <= 120) {
                    int r = 28;
                    int g = 99;
                    int b = 78;
                    gc.getPixelWriter().setColor(x, y, Color.rgb(r, g, b));

                } else if (HeightNoise >= 120 && HeightNoise <= 140) {
                    int r = 128;
                    int g = 201;
                    int b = 93;
                    gc.getPixelWriter().setColor(x, y, Color.rgb(r, g, b));

                } else if (HeightNoise >= 140 && HeightNoise <= 160) {
                    int r = 93;
                    int g = 158;
                    int b = 111;
                    gc.getPixelWriter().setColor(x, y, Color.rgb(r, g, b));
                } else {
                    int r = 255;
                    int g = 255;
                    int b = 255;
                    gc.getPixelWriter().setColor(x, y, Color.rgb(r, g, b));
                }
            }
        }
    }

}

