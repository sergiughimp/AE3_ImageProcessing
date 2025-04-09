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

    private Image image;
    // Holds the currently highlighted seam (one pixel per row)
    private List<Pixel> highlightedSeam = null;
    // Also store the original pixel colors for that seam (to be able to undo highlighting)
    private List<Pixel> previousSeamColors = null;

    // Command history for undo functionality.
    private final Deque<CommandControl> previousCommands = new ArrayDeque<>();

    // Loads the image.
    public void load(String filePath) throws IOException {
        File originalFile = new File(filePath);
        BufferedImage img = ImageIO.read(originalFile);
        image = new Image(img);
    }

    // Saves the current image.
    public void save(String filePath) throws IOException {
        BufferedImage img = image.toBufferedImage();
        ImageIO.write(img, "png", new File(filePath));
    }


    public void highlightGreenest() throws IOException {
        List<Pixel> seam = image.getGreenestSeam();
        // Highlight the seam in green and retrieve the seam's original colors.
        List<Pixel> oldColors = image.highlightSeam(seam, Color.GREEN);
        // Store for later removal or undo.
        highlightedSeam = seam;
        previousSeamColors = oldColors;
    }


    public void highlightLowestEnergySeam() throws IOException {
        List<Pixel> seam = image.getLowestEnergySeam();
        // Highlight the seam in red and retrieve the seam's original colors.
        List<Pixel> oldColors = image.highlightSeam(seam, Color.RED);
        highlightedSeam = seam;
        previousSeamColors = oldColors;
    }


    public void removeHighlighted() throws IOException {
        if (highlightedSeam == null) {
            System.out.println("No seam currently highlighted.");
            return;
        }
        CommandControl command = new SeamRemovalCommand(image, highlightedSeam, previousSeamColors);
        command.execute();
        previousCommands.push(command);
        // Clear the highlighted seam fields now that the seam has been removed.
        highlightedSeam = null;
        previousSeamColors = null;
    }


    public void undo() throws IOException {
        if (!previousCommands.isEmpty()) {
            CommandControl lastCommand = previousCommands.pop();
            lastCommand.undo();
        }
    }


    public interface CommandControl {
        void execute();
        void undo();
    }


    public static class SeamRemovalCommand implements CommandControl {
        private final Image image;
        private final List<Pixel> seam;
        private final List<Pixel> oldColors;

        public SeamRemovalCommand(Image image, List<Pixel> seam, List<Pixel> oldColors) {
            this.image = image;
            this.seam = seam;
            this.oldColors = oldColors;
        }


        @Override
        public void execute() {
            image.removeSeam(seam);
        }


        @Override
        public void undo() {
            image.addSeam(seam);
            // Restore the original colors into the seam pixels.
            for (int i = 0; i < seam.size(); i++) {
                Pixel seamPixel = seam.get(i);
                Pixel original = oldColors.get(i);
                seamPixel.color = new Color(original.color.getRGB());
            }
        }
    }
}

