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
        return 0;
    }

    public void calculateEnergy() {
        //TODO: calculate energy for all the pixels in the image
    }

    public List<Pixel> higlightSeam(List<Pixel> seam, Color color) {
        //TODO: highlight the seam, return previous values
        return null;
    }

    public void removeSeam(List<Pixel> seam) {
        //TODO: remove the provided seam
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
