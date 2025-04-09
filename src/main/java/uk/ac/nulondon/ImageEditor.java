package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ImageEditor {

    // Image data structure holds pixels and seam editing logic
    private Image image;

    // Reference to the currently highlighted seam
    private List<Pixel> highlightedSeam = null;

    // Stores original pixel colours
    private List<Pixel> previousSeamColors = null;

    // Stack of previously executed demands
    private final Deque<CommandControl> previousCommands = new ArrayDeque<>();

    // Loads an image from a given file path
    public void load(String filePath) throws IOException {
        File originalFile = new File(filePath);
        BufferedImage img = ImageIO.read(originalFile);
        image = new Image(img);
    }

    // Saves the current iteration of the image to a given file path
    public void save(String filePath) throws IOException {
        BufferedImage img = image.toBufferedImage();
        ImageIO.write(img, "png", new File(filePath));
    }

    // Highlights the greenest seam in green
    public void highlightGreenest() throws IOException {

        // Finds the seam with the highest green value
        List<Pixel> greenestSeam = image.getGreenestSeam();

        // Highlights the seam in green and store the previous seam colours
        List<Pixel> previousColours = image.highlightSeam(greenestSeam, Color.GREEN);

        // Store references to the greenest seam and the previous seam colours
        highlightedSeam = greenestSeam;
        previousSeamColors = previousColours;
    }

    // Highlights the seam with the lowest energy in red
    public void highlightLowestEnergySeam() throws IOException {

        // Finds the seam with the lowest energy
        List<Pixel> lowestEnergySeam = image.getLowestEnergySeam();

        // Highlights the seam in red and stores the seams previous colours
        List<Pixel> previousColours = image.highlightSeam(lowestEnergySeam, Color.RED);

        // Store references to the lowest energy seam and the previous seam colours
        highlightedSeam = lowestEnergySeam;
        previousSeamColors = previousColours;
    }

    // Removes the currently highlighted seam
    public void removeHighlighted() throws IOException {

        // Ensures a seam has been highlighted before attempting to remove a seam
        if (highlightedSeam == null) {
            System.out.println("Error: No seam highlighted");
            return;
        }

        // Creates a command to remove the highlighted seam
        CommandControl command = new SeamEditCommand(image, highlightedSeam, previousSeamColors);

        // Executes the removal of the seam
        command.execute();

        // Pushes the command to the stack to enable the removal to be undone
        previousCommands.push(command);

        // Remove references to the seam
        highlightedSeam = null;
        previousSeamColors = null;
    }

    // Enables previous edits to be undone
    public void undo() throws IOException {

        // If there have been previous commands the most recent command is undone
        if (!previousCommands.isEmpty()) {
            CommandControl previousCommand = previousCommands.pop();
            previousCommand.undo();
        }
    }

    // Interface enabling the execution of commands as well as undo functionality
    public interface CommandControl {
        void execute();
        void undo();
    }

    /*The SeamEditCommand class enables the execution of commands
     and stores previous data for the undo functionality
     */
    public static class SeamEditCommand implements CommandControl {
        private final Image image;
        private final List<Pixel> currentSeam;
        private final List<Pixel> previousColours;

        // Class Constructor
        public SeamEditCommand(Image image, List<Pixel> currentSeam, List<Pixel> previousColours) {
            this.image = image;
            this.currentSeam = currentSeam;
            this.previousColours = previousColours;
        }

        // Executes seam removal using the removeSeam method
        @Override
        public void execute() {
            // Implementation of the removeSeam method
            image.removeSeam(currentSeam);
        }

        // Undo method to undo previous seam removal
        @Override
        public void undo() {

            // Using the addSeam method to insert the seam back into the image
            image.addSeam(currentSeam);

            // Restoring the seams previous colours
            for (int i = 0; i < currentSeam.size(); i++) {
                Pixel seamPixel = currentSeam.get(i);
                Pixel original = previousColours.get(i);
                seamPixel.color = new Color(original.color.getRGB());
            }
        }
    }
}
