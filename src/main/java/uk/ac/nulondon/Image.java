
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
    
            // Checking if any of the neighboring pixels are null or invalid
            if (above == null || below == null || current.left == null || current.right == null ||
                    above.left == null || above.right == null || below.left == null || below.right == null) {
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
    
    // Gets pixel at the required row and column
    private Pixel getPixelByRowCol(int row, int column) {

        // Retrieves the head of the row
        Pixel pixel = rows.get(row);

        // Iterate through the linked list to find the specified pixel
        for (int i = 0; i < column; i++) {
            pixel = pixel.right;
        }

        // Returns the pixel at the given column and row
        return pixel;
    }
    
        public void calculateEnergy() {
            // Loop through all rows and columns
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Pixel current = getPixelByRowCol(row, col);
                    // For boundary pixels, use brightness (as specified)
                    if (row == 0 || row == height - 1 || col == 0 || col == width - 1) {
                        current.energy = current.brightness();
                    } else {
                        // Get neighboring pixels using the linked structure
                        Pixel above = getPixelByRowCol(row - 1, col);
                        Pixel below = getPixelByRowCol(row + 1, col);
                        current.energy = energy(above, current, below);
                    }
                }
            }
        }
    
    
    
    
    // highlightSeam highlights a given seam with the chosen colour
    public List<Pixel> highlightSeam(List<Pixel> seam, Color color) {

        // List to store the original colour values of the seam
        List<Pixel> seamValues = new ArrayList<>(seam.size());

        // Iterates through all pixels in the seam
        for (Pixel p : seam) {

            // Adds the seams original colour values the seamValues
            seamValues.add(new Pixel(p.color.getRGB()));

            // Highlight colour applied to the seam
            p.color = color;
        }

        // Return the original colour values of the seam
        return seamValues;
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
    
        // addSeam inserts a previously removed seam back into the image
    public void addSeam(List<Pixel> seam) {
        
        // Increases the image width by 1 as we are restoring the seam
        width++;

        // Linking the seam pixels to the linked list row by row
        for (int row = 0; row < height; row++) {
            Pixel pixel = seam.get(row);

            // If the pixel to the left of the current pixel is not null this links them
            if (pixel.left != null) {
                pixel.left.right = pixel;
            }

            // If the pixel to the left is null the current pixel becomes the leftmost pixel in the row
            else {
                rows.set(row, pixel);
            }

            // If the pixel to the right of the current pixel is not null this links them
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

        // Method to find the seam that maximizes a given value using dynamic programming
        private List<Pixel> getSeamMaximizing(Function<Pixel, Double> valueGetter) {
            // Arrays to store the computed values for each column in the previous and current rows
            double[] previousValues = new double[width];
            double[] currentValues = new double[width];
    
            // Track the seams for each column and each row
            List<List<Pixel>> previousSeams = new ArrayList<>(width);
            List<List<Pixel>> currentSeams = new ArrayList<>(width);
    
            // Initialize first row: compute initial values and seams
            for (int col = 0; col < width; col++) {
                Pixel pixel = getPixelByRowCol(0, col);
                // Calculate the value for the first row using the provided function
                previousValues[col] = valueGetter.apply(pixel);

                // Initialize the seam for the first row, which is just the pixel itself
                List<Pixel> seam = new ArrayList<>();
                seam.add(pixel);
                previousSeams.add(seam);
            }
    
            // Process subsequent rows starting from row 1
            for (int row = 1; row < height; row++) {
                // Reset current seams for this row
                currentSeams.clear();
    
                for (int col = 0; col < width; col++) {
                    Pixel pixel = getPixelByRowCol(row, col);
    
                    // Find the best path from the previous row
                    double maxVal = previousValues[col];
                    int bestCol = col;

                    // Check the column to the left (if it exists)
                    if (col > 0 && previousValues[col - 1] > maxVal) {
                        maxVal = previousValues[col - 1];
                        bestCol = col - 1;
                    }

                    // Check the column to the right (if it exists)
                    if (col < width - 1 && previousValues[col + 1] > maxVal) {
                        maxVal = previousValues[col + 1];
                        bestCol = col + 1;
                    }
    
                    // Update current value for this column
                    currentValues[col] = maxVal + valueGetter.apply(pixel);

                    // Build a new seam by copying the best seam from the previous row and adding the current pixel
                    List<Pixel> newSeam = new ArrayList<>(previousSeams.get(bestCol));
                    newSeam.add(pixel);
                    currentSeams.add(newSeam);
                }
    
                // Prepare for next row by updating the previous values and seams
                System.arraycopy(currentValues, 0, previousValues, 0, width);
                List<List<Pixel>> temp = previousSeams;
                // Swap current and previous seams for the next iteration
                previousSeams = currentSeams;
                currentSeams = temp;
                currentSeams.clear();
            }

            // Find the best seam from the bottom row by looking for the column with the highest value
            int bestCol = 0;
            for (int col = 1; col < width; col++) {
                if (previousValues[col] > previousValues[bestCol]) {
                    bestCol = col;
                }
            }
            // Return the best seam from the last row
            return previousSeams.get(bestCol);
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
