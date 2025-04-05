package uk.ac.nulondon;

import java.awt.*;

public class Pixel {
    Pixel left;
    Pixel right;

    double energy;

    final Color color;

    public Pixel(int rgb) {
        this.color = new Color(rgb);
    }

    public Pixel(Color color) {
        this.color = color;
    }

    public double brightness() {
        //TODO: implement brightness calculation
        return 0;
    }

    public double getGreen() {
        return color.getGreen();
    }
}
