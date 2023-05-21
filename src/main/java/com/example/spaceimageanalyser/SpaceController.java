package com.example.spaceimageanalyser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;


import javafx.stage.FileChooser;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;


public class SpaceController {
    private int width1;
    private int height1;
    private int[] parent;
    private int[] rank;
    private boolean[] isBlack;
    private boolean[] isWhite;

    @FXML
    ImageView imageView;

    @FXML
    ImageView blackWhiteImage;

    @FXML
    private Button button1;

    @FXML
    private int imageWidth, imageHeight;



    public void displayFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a File");
        File file = fileChooser.showOpenDialog(null);
        Image image = new Image(file.toURI().toString());
        Image image1 = convertToBlackAndWhite(image);
        imageView.setImage(image);
        blackWhiteImage.setImage(image1);

        // if file not found then display the error message
        if (file != null) {
            System.out.println("Path of the file : " + file.getPath());

        } else {
            // display the path of the file
            System.out.println("Failed to open the file");
        }
    }


    public static Image convertToBlackAndWhite(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage outputImage = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = reader.getColor(x, y);
                double luminance = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
                Color gray = Color.gray(luminance);
                writer.setColor(x, y, gray);
            }
        }
        return outputImage;
    }


    public ImageView getImageView() {
        return blackWhiteImage;
    }


    public void UnionFindImage(Image image) {
        width1 = (int) image.getWidth();
        height1 = (int) image.getHeight();
        parent = new int[width1 * height1];
        rank = new int[width1 * height1];
        isBlack = new boolean[width1 * height1];
        isWhite = new boolean[width1 * height1];

        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int index = y * width1 + x;
                isBlack[index] = reader.getColor(x, y).grayscale().getRed() == 0;
                isWhite[index] = reader.getColor(x, y).grayscale().getRed() == 1;
                parent[index] = index;
            }
        }
    }

    public void union(int p, int q) {
        int parentP = find(p);
        int parentQ = find(q);
        if (parentP == parentQ) {
            return;
        }
        if (isBlack[p] || isBlack[q]) {
            return;
        }
        if (rank[parentP] > rank[parentQ]) {
            parent[parentQ] = parentP;
        } else if (rank[parentP] < rank[parentQ]) {
            parent[parentP] = parentQ;
        } else {
            parent[parentQ] = parentP;
            rank[parentP]++;
        }
    }

    public int find(int p) {
        while (p != parent[p]) {
            parent[p] = parent[parent[p]];
            p = parent[p];
        }
        return p;
    }

    public boolean isBlack(int x, int y) {
        if (x < 0 || x >= imageWidth || y < 0 || y >= imageHeight) {
            // base case: coordinates are out of bounds, return false
            return false;
        }
        return isBlack(x, y);
    }

    public boolean isWhite(int x, int y) {
        if (x < 0 || x >= imageWidth || y < 0 || y >= imageHeight) {
            // base case: coordinates are out of bounds, return false
            return false;
        }
        return isWhite(x, y);
    }

    public int countBlackComponents() {
        int count = 0;
        for (int i = 0; i < parent.length; i++) {
            if (parent[i] == i && isBlack[i]) {
                count++;
            }
        }
        return count;
    }


    private List<List<Integer>> findWhiteComponents() {
        List<List<Integer>> whiteComponents = new ArrayList<>();

        // Get the image as a 2D array of grayscale values
        Image image = blackWhiteImage.getImage();
        int[][] pixels = new int[(int) image.getWidth()][(int) image.getHeight()];
        PixelReader pixelReader = image.getPixelReader();
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                pixels[x][y] = (int) (pixelReader.getColor(x, y).getBrightness() * 255);
            }
        }

        // Find the white components using a lower threshold value
        int threshold = 140;
        boolean[][] visited = new boolean[pixels.length][pixels[0].length];
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                if (pixels[x][y] > threshold && !visited[x][y]) {
                    List<Integer> component = new ArrayList<>();
                    Queue<Point> queue = new LinkedList<>();
                    queue.add(new Point(x, y));
                    visited[x][y] = true;
                    while (!queue.isEmpty()) {
                        Point p = queue.remove();
                        int px = p.x;
                        int py = p.y;
                        component.add(py * pixels.length + px);
                        if (px > 0 && pixels[px - 1][py] > threshold && !visited[px - 1][py]) {
                            queue.add(new Point(px - 1, py));
                            visited[px - 1][py] = true;
                        }
                        if (px < pixels.length - 1 && pixels[px + 1][py] > threshold && !visited[px + 1][py]) {
                            queue.add(new Point(px + 1, py));
                            visited[px + 1][py] = true;
                        }
                        if (py > 0 && pixels[px][py - 1] > threshold && !visited[px][py - 1]) {
                            queue.add(new Point(px, py - 1));
                            visited[px][py - 1] = true;
                        }
                        if (py < pixels[x].length - 1 && pixels[px][py + 1] > threshold && !visited[px][py + 1]) {
                            queue.add(new Point(px, py + 1));
                            visited[px][py + 1] = true;
                        }
                    }
                    if (!component.isEmpty()) {
                        whiteComponents.add(component);
                    }
                }
            }
        }

        return whiteComponents;
    }



    private void union(int i, int j, int[] parent) {
        int rootI = find(i, parent);
        int rootJ = find(j, parent);
        parent[rootJ] = rootI;
    }

    private int find(int i, int[] parent) {
        if (parent[i] != i) {
            parent[i] = find(parent[i], parent);
        }
        return parent[i];
    }

    private boolean isWhitePixel(int index) {
        int x = index % (int) blackWhiteImage.getImage().getWidth();
        int y = index / (int) blackWhiteImage.getImage().getWidth();
        return blackWhiteImage.getImage().getPixelReader().getColor(x, y).equals(Color.WHITE);
    }



    @FXML
    public void detectStars(ActionEvent actionEvent) {
        Platform.runLater(() -> { //sets up the UI
            // Find the pixels belonging to each white component
            List<List<Integer>> whiteComponents = findWhiteComponents();

            // Draw circles around the centroids of the white components
            double radius = 30.0;
            int imageWidth = (int) blackWhiteImage.getImage().getWidth();
            int imageHeight = (int) blackWhiteImage.getImage().getHeight();
            Canvas canvas = new Canvas(imageWidth, imageHeight);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(blackWhiteImage.getImage(),0,0);
            gc.setLineWidth(2.0);
            gc.setStroke(Color.RED);
            List<Point2D> centroids = new ArrayList<>();
            for (List<Integer> component : whiteComponents) {
                int numPixels = component.size();
                double sumX = 0.0;
                double sumY = 0.0;
                for (int index : component) {
                    int x = index % imageWidth;
                    int y = index / imageWidth;
                    sumX += x;
                    sumY += y;
                }
                double centerX = sumX / numPixels;
                double centerY = sumY / numPixels;
                Point2D centroid = new Point2D(centerX, centerY);
                boolean overlap = false;
                for (Point2D otherCentroid : centroids) {
                    if (centroid.distance(otherCentroid) < radius * 2) {
                        overlap = true;
                        break;
                    }
                }
                if (!overlap) {
                    gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                    centroids.add(centroid);
                }
            }

            // Display the result in a dialog box
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Star Detection Result");
            alert.setHeaderText(null);
            alert.setContentText("Number of stars: " + centroids.size());
            alert.showAndWait();

            // Display the canvas in an ImageView
            Image resultImage = canvas.snapshot(null, null);
            blackWhiteImage.setImage(resultImage);
        });
    }

    @FXML
    public void reset(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            // Clear the canvas
            Canvas canvas = new Canvas(blackWhiteImage.getImage().getWidth(), blackWhiteImage.getImage().getHeight());
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(imageView.getImage(), 0, 0);

            // Display the canvas in an ImageView
            Image resultImage = canvas.snapshot(null, null);
            blackWhiteImage.setImage(resultImage);
        });
    }
}

