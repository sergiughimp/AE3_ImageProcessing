package uk.ac.nulondon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Image {
    private final List<Pixel> rows;

    private int width;
    private int height;


    public Image(BufferedImage img) {
        width = img.getWidth();
        height = img.getHeight();
        rows = new ArrayList<>();
        Pixel current = null;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Pixel pixel = new Pixel(img.getRGB(col, row));
                if (col == 0) {
                    rows.add(pixel);
                } else {
                    current.right = pixel;
                    pixel.left = current;
                }
                current = pixel;
            }
        }
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < height; row++) {
            Pixel pixel = rows.get(row);
            int col = 0;
            while (pixel != null) {
                image.setRGB(col++, row, pixel.color.getRGB());
                pixel = pixel.right;
            }
        }
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    double energy(Pixel above, Pixel current, Pixel below) {
        //TODO: Calculate energy based on neighbours of the current pixel

        // Check if any of the neighboring pixels (above, current, or below) are null or invalid
        // This ensures we don't encounter NullPointerException or invalid references
        if (above == null || below == null || current.left == null || current.right == null) {
            // If any of the neighbors are null, return the brightness of the current pixel as a default energy value
            return current.brightness();
        }
        // Get the brightness values of the neighboring pixels around the 'above' pixel
        double a = above.left.brightness();   // Brightness of the pixel to the left of the 'above' pixel
        double b = above.brightness();        // Brightness of the 'above' pixel itself
        double c = above.right.brightness(); // Brightness of the pixel to the right of the 'above' pixel

        // Get the brightness values of the neighboring pixels around the 'current' pixel
        double d = current.left.brightness(); // Brightness of the pixel to the left of the 'current' pixel
        double f = current.right.brightness(); // Brightness of the pixel to the right of the 'current' pixel

        // Get the brightness values of the neighboring pixels around the 'below' pixel
        double g = below.left.brightness();  // Brightness of the pixel to the left of the 'below' pixel
        double h = below.brightness();       // Brightness of the 'below' pixel itself
        double i = below.right.brightness(); // Brightness of the pixel to the right of the 'below' pixel

        // Calculate the horizontal energy component by considering the pixel's left-right neighbors
        double h_energy = (a + 2 * d + g) - (c + 2 * f + i);

        // Calculate the vertical energy component by considering the pixel's up-down neighbors
        double v_energy = (a + 2 * b + c) - (g + 2 * h + i);

        // Return the total energy by calculating the Euclidean distance between horizontal and vertical energies
        return Math.sqrt(h_energy * h_energy + v_energy * v_energy);
    }

    private Pixel getPixelAt(int row, int col) {
        Pixel p = rows.get(row);  // First pixel in row 'row'
        for (int i = 0; i < col; i++) {
            p = p.right;          // Move right 'col' times
        }
        return p;
    }

    public void calculateEnergy() {
        // Loop through all rows and columns
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Pixel current = getPixelAt(row, col);
                // For boundary pixels, use brightness (as specified)
                if (row == 0 || row == height - 1 || col == 0 || col == width - 1) {
                    current.energy = current.brightness();
                } else {
                    // Get neighboring pixels using the linked structure
                    Pixel above = getPixelAt(row - 1, col);
                    Pixel below = getPixelAt(row + 1, col);
                    current.energy = energy(above, current, below);
                }
            }
        }
    }

    public List<Pixel> highlightSeam(List<Pixel> seam, Color color) {
        // We will collect the old state (color) of each pixel in the seam
        // so that we can restore them later, if needed.
        List<Pixel> oldValues = new ArrayList<>(seam.size());

        for (Pixel p : seam) {
            // Record the pixel's old color in a new Pixel object
            // (we only care about the color here, not left/right links).
            oldValues.add(new Pixel(p.color.getRGB()));

            // Now highlight the seam pixel with the given color.
            p.color = color;
        }

        // Return the old pixel states. Each entry corresponds to the original color
        // of the seam pixel at the same index in 'seam'.
        return oldValues;
    }

    public void removeSeam(List<Pixel> seam) {
        //TODO: remove the provided seam

        // Decrease the width by 1 to account for the removed seam
        width--;

        // Process each row in the seam, updating the corresponding pixel links
        for (int row = 0; row < height; row++) {
            // Get the pixel corresponding to the current row in the seam
            Pixel pixel = seam.get(row);

            // If the pixel is not the leftmost one, update the left neighbor
            if (pixel.left != null) {
                pixel.left.right = pixel.right;
            } else {
                // For the leftmost pixel, update the first pixel in the row
                rows.set(row, pixel.right);
            }

            // If the pixel is not the rightmost one, update the right neighbor
            if (pixel.right != null) {
                pixel.right.left = pixel.left;
            }
        }
    }

    public void addSeam(List<Pixel> seam) {
        // Increase width because we are adding a vertical seam
        width++;

        // We assume 'seam' has 1 pixel per row. Each pixel’s .left and .right
        // references still point to where it was removed.
        for (int row = 0; row < height; row++) {
            Pixel pixel = seam.get(row);

            // If this pixel has a left neighbor, link it
            if (pixel.left != null) {
                pixel.left.right = pixel;
            }
            // Otherwise it’s the new leftmost pixel in this row
            else {
                rows.set(row, pixel);
            }

            // Link the pixel’s right neighbor back to this pixel if it exists
            if (pixel.right != null) {
                pixel.right.left = pixel;
            }
        }
    }

    private static <T> List<T> concat(T element, Collection<? extends T> elements){
        List<T> result = new ArrayList<>();
        result.add(element); // Add the single element first
        result.addAll(elements); // Then add all elements from the collection
        return result;
    }
    private List<Pixel> getSeamMaximizing(Function<Pixel, Double> valueGetter) {
        //TODO: find the seam which maximizes total value extracted from the given pixel

        double[] previousValues = new double[width];
        double[] currentValues = new double[width];

        List<List<Pixel>> previousSeams = new ArrayList<>();
        List<List<Pixel>> currentSeams = new ArrayList<>();

        Pixel currentPixel = rows.get(0);  // Use get(0) to access the first pixel
        int col = 0;

        // Initializing for the first row
        while (currentPixel != null) {
            previousValues[col] = valueGetter.apply(currentPixel);  // Apply the function to get the value
            previousSeams.add(concat(currentPixel, List.of()));  // Add the current pixel with an empty list (initial seam)
            col++;
            currentPixel = currentPixel.right;  // Move to the next pixel in the row
        }

        // Fill the paths array for subsequent rows
        for (int row = 1; row < height; row++) {
            currentPixel = rows.get(row);
            col = 0;

            while (currentPixel != null) {
                double maxVal = previousValues[col];
                int ref = col;

                // Compare with left, current, and right pixels for the maximum value
                if (col > 0 && previousValues[col - 1] > maxVal) {
                    maxVal = previousValues[col - 1];
                    ref = col - 1;
                }
                if (col < width - 1 && previousValues[col + 1] > maxVal) {
                    maxVal = previousValues[col + 1];
                    ref = col + 1;
                }

                // Update the current value with the maximum value from neighbors + current pixel's value
                currentValues[col] = maxVal + valueGetter.apply(currentPixel);

                // Build the seam path by concatenating the current pixel with the previous best path
                currentSeams.add(concat(currentPixel, previousSeams.get(ref)));

                col++;
                currentPixel = currentPixel.right;
            }

            // Swap the arrays and lists for the next iteration
            previousValues = currentValues;
            currentValues = new double[width];
            previousSeams = currentSeams;
            currentSeams = new ArrayList<>();
        }

        // Finding the seam with the maximum sum of values
        double maxValue = previousValues[0];
        int maxValueIndex = 0; // Should be an int, not a double
        for (int i = 1; i < width; i++) {
            if (previousValues[i] > maxValue) {
                maxValue = previousValues[i];
                maxValueIndex = i;
            }
        }

        // Return the seam with the maximum value
        return previousSeams.get(maxValueIndex);
    }

    public List<Pixel> getGreenestSeam() {
        return getSeamMaximizing(Pixel::getGreen);
        /*Or, since we haven't lectured on lambda syntax in Java, this can be
        return getSeamMaximizing(new Function<Pixel, Double>() {
            @Override
            public Double apply(Pixel pixel) {
                return pixel.getGreen();
            }
        });*/

    }

    public List<Pixel> getLowestEnergySeam() {
        calculateEnergy();
        /*
        Maximizing negation of energy is the same as minimizing the energy.
         */
        return getSeamMaximizing(pixel -> -pixel.energy);

        /*Or, since we haven't lectured on lambda syntax in Java, this can be
        return getSeamMaximizing(new Function<Pixel, Double>() {
            @Override
            public Double apply(Pixel pixel) {
                return -pixel.energy;
            }
        });
        */
    }
}
