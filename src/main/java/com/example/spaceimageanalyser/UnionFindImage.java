package com.example.spaceimageanalyser;
import java.util.HashMap;

public class UnionFindImage {
        private HashMap<Point, Point> parent;
        private HashMap<Point, Integer> rank;
        private int numSets;
        private int[][] image;
        private int width;
        private int height;
        private int numPixels;

        public UnionFindImage(int[][] image) {
            this.parent = new HashMap<>();
            this.rank = new HashMap<>();
            this.numSets = 0;
            this.image = image;
            this.width = image[0].length;
            this.height = image.length;
            this.numPixels = this.width * this.height;

            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    if (this.image[y][x] != 0) {
                        Point p = new Point(x, y);
                        this.parent.put(p, p);
                        this.rank.put(p, 0);
                        this.numSets++;
                    }
                }
            }
        }

        private Point find(Point p) {
            if (!this.parent.get(p).equals(p)) {
                this.parent.put(p, this.find(this.parent.get(p)));
            }
            return this.parent.get(p);
        }

        public void union(Point p, Point q) {
            Point rootP = this.find(p);
            Point rootQ = this.find(q);

            if (rootP.equals(rootQ)) {
                return;
            }

            if (this.rank.get(rootP) < this.rank.get(rootQ)) {
                this.parent.put(rootP, rootQ);
            } else if (this.rank.get(rootP) > this.rank.get(rootQ)) {
                this.parent.put(rootQ, rootP);
            } else {
                this.parent.put(rootQ, rootP);
                this.rank.put(rootP, this.rank.get(rootP) + 1);
            }

            this.numSets--;
        }

        public int getNumSets() {
            return this.numSets;
        }

        public int[][] getLabels() {
            int[][] labels = new int[this.height][this.width];
            int label = 1;

            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    if (this.image[y][x] != 0) {
                        Point p = new Point(x, y);
                        Point root = this.find(p);
                        if (root.equals(p)) {
                            labels[y][x] = label;
                            label++;
                        } else {
                            Point rootValue = this.parent.get(root);
                            labels[y][x] = labels[rootValue.y][rootValue.x];
                        }
                    }
                }
            }

            return labels;
        }

        private static class Point {
            public int x;
            public int y;

            public Point(int x, int y) {
                this.x = x;
                this.y = y;
            }

            @Override
            public int hashCode() {
                return this.x + this.y * 31;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null || !(obj instanceof Point)) {
                    return false;
                }
                Point other = (Point) obj;
                return this.x == other.x && this.y == other.y;
            }
        }
    }
