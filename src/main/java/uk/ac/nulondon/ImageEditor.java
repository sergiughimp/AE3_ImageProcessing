package uk.ac.nulondon;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/*APPLICATION SERVICE LAYER*/
public class ImageEditor {

    private Image image;

    private List<Pixel> highlightedSeam = null;

    private List<Pixel> highlightedSeamOldColors = null;

    private Deque<Command> commandHistory = new ArrayDeque<>();

    private List<Pixel> highlightedSeam = null;

    public void load(String filePath) throws IOException {
        File originalFile = new File(filePath);
        BufferedImage img = ImageIO.read(originalFile);
        image = new Image(img);
    }

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
        highlightedSeamOldColors = oldColors;
    }

    public void removeHighlighted() throws IOException {
        if (highlightedSeam == null) {
            System.out.println("No seam currently highlighted to remove.");
            return;
        }
        Command cmd = new RemoveSeamCommand(image, highlightedSeam, highlightedSeamOldColors);
        cmd.execute();
        commandHistory.push(cmd);
        // Clear the highlighted seam fields now that the seam has been removed.
        highlightedSeam = null;
        highlightedSeamOldColors = null;
    }

    public void highlightLowestEnergySeam() throws IOException {
        List<Pixel> seam = image.getLowestEnergySeam();
        // Highlight the seam in red and retrieve the seam's original colors.
        List<Pixel> oldColors = image.highlightSeam(seam, Color.RED);
        highlightedSeam = seam;
        highlightedSeamOldColors = oldColors;
    }

    public void undo() throws IOException {
        if (!commandHistory.isEmpty()) {
            Command lastCmd = commandHistory.pop();
            lastCmd.undo();
        }
    }

    public interface Command {
        void execute();
        void undo();
    }

    //TODO: implement Command class or interface and its subtypes
}
