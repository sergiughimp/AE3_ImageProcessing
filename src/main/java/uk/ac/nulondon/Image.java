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

        // Calculate energy based on the neighboring pixels of the current pixel (above, current, below)

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

    public void calculateEnergy() {
        //TODO: calculate energy for all the pixels in the image

        // Loop through all pixels in the image, excluding the boundary pixels
        for (int row = 1; row < height - 1; row++) {
            for (int col = 1; col < width - 1; col++) {
                // Access the neighboring pixels: above, current, and below
                // Using the row and column index to get the appropriate pixel from the 'rows' list
                Pixel above = rows.get((row - 1) * width + col);  // Pixel above the current one
                Pixel current = rows.get(row * width + col);      // The current pixel
                Pixel below = rows.get((row + 1) * width + col);   // Pixel below the current one

                // Calculate the energy for the current pixel based on its neighbors
                current.energy = energy(above, current, below);
            }
        }
    }

    public List<Pixel> higlightSeam(List<Pixel> seam, Color color) {
        //TODO: highlight the seam, return previous values
        return null;
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
        //TODO: Add the provided seam
    }

    private List<Pixel> getSeamMaximizing(Function<Pixel, Double> valueGetter) {
        //TODO: find the seam which maximizes total value extracted from the given pixel
        return null;
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
