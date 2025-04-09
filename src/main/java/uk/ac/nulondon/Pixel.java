package uk.ac.nulondon;

import java.awt.*;

public class Pixel {
    Pixel left;
    Pixel right;

    double energy;

    Color color;

    public Pixel(int rgb) {
        this.color = new Color(rgb);
    }

    public Pixel(Color color) {
        this.color = color;
    }

    public double brightness() {
        //TODO: implement brightness calculation

        // Calculate the brightness of the pixel based on its RGB color components.
        // Get the blue, green, and red color components of the pixel
        int blue = color.getBlue();   // Blue component of the pixel
        int green = color.getGreen(); // Green component of the pixel
        int red = color.getRed();     // Red component of the pixel

        // Calculate the brightness by averaging the RGB values
        // This gives a simple approximation of the pixel's brightness based on its color components
        return (double) (blue + green + red) / 3;
    }

    public double getGreen() {
        return color.getGreen();
    }
}
