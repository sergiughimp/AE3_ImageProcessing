# uk.ac.nulondon:project2

This project allows users to manipulate seams of an image by highlighting and removing the greenest seam or the lowest energy seam, as well as undoing actions through a terminal-based interface.
## Features:
1. **Remove the Greenest Seam** – Removes the seam that is the "greenest" in the image.
2. **Remove the Lowest Energy Seam** – Removes the seam with the lowest energy in the image.
3. **Undo** – Undo the last seam removal.
4. **Quit** – Exit the application and save the final edited image.


## How to Use the Application
### Step 1: Run the Application
- Compile the Java program using `javac Main.java`.
- Run the application using java `uk.ac.nulondon.Main`.

### Step 2: Provide an Image Path
- Upon starting the program, you will be prompted to enter the path to the image you wish to edit.
- Example:
    ```
    Welcome! Enter file path:
    src/main/resources/beach.png
    ```
### Step 3: Interact with the Menu
Once the image is loaded, the program will display a menu with the following options:
- g – Remove the greenest seam.
- e – Remove the seam with the lowest energy.
- u – Undo the previous seam removal.
- q – Quit the program and save the final image.

### Step 4: Execute an Action
**Remove the Greenest Seam:**
- Type `g` to remove the greenest seam.
- The program will prompt for confirmation to proceed.
- If confirmed (`Y`), the greenest seam will be removed from the image.

**Remove the Lowest Energy Seam:**
- Type `e` to remove the lowest energy seam.
- The program will prompt for confirmation to proceed.
- If confirmed (`Y`), the lowest energy seam will be removed from the image.

**Undo the Last Edit:**
- Type `u` to undo the last seam removal.
- The program will prompt for confirmation to restore the removed seam.
- If confirmed (`Y`), the previous seam will be restored.

**Quit:**
- Type `q` to quit the program and save the final edited image.

### Step 5: Final Image
After exiting, the final image will be saved as `newImg.png` in the `target` directory.

## Method Descriptions
### Image Class Methods
1. `double energy(Pixel above, Pixel current, Pixel below)`
   This method calculates the energy of the current pixel based on its neighboring pixels. The energy is calculated by examining the differences between the colors of the above, current, and below pixels.
2. `private Pixel getPixelAt(int row, int col)`
   This method retrieves the pixel located at the specified row and column in the image. It returns the pixel object for further processing.
3. `public void calculateEnergy()`
   This method calculates the energy for all pixels in the image. It iterates through each pixel, computing its energy based on the neighboring pixels.
4. `public List<Pixel> highlightSeam(List<Pixel> seam, Color color)`
   This method highlights a given seam in the image by coloring the pixels along the seam with the specified color. It visually marks the seam for user interaction or editing.
5. `public void removeSeam(List<Pixel> seam)`
   This method removes the pixels that form the given seam from the image, effectively shrinking the image by one seam.
6. `public void addSeam(List<Pixel> seam)`
   This method adds the specified seam back into the image. It restores the previously removed seam by reintroducing the pixels along the seam.
7. `private List<Pixel> getSeamMaximizing(Function<Pixel, Double> valueGetter)`
   This method calculates the seam that maximizes a specific value, such as energy or greeness, based on the provided function. It returns the list of pixels that form the seam with the highest value.

### ImageEditor Class Methods
1. `public void highlightGreenest() throws IOException`
   This method identifies the greenest seam in the image and highlights it. The seam with the highest "green energy" is visually marked in green, allowing the user to preview it before removal.
2. `public void highlightLowestEnergySeam() throws IOException`
   This method identifies the seam with the lowest energy in the image and highlights it. The seam with the lowest energy is visually marked, allowing the user to see which pixels would be removed if the seam is deleted.
3. `public void removeHighlighted() throws IOException`
   This method removes the currently highlighted seam from the image. Once the seam is highlighted (greenest or lowest energy), this method removes it from the image.

### CommandControl Interface and Command Classes
1. `public interface CommandControl`
   This interface defines the basic actions that can be executed and undone, providing the framework for creating command objects that can perform operations on the image (e.g., seam removal).
  ```
  void execute() – Executes the command, performing the associated operation (e.g., removing or adding a seam).
  void undo() – Undoes the executed command, restoring the previous state (e.g., undoing a seam removal).
  ```
2. `public static class SeamRemovalCommand implements CommandControl`
   This class implements the CommandControl interface and encapsulates the command to remove a seam. It performs the following:
  ```
  execute() – Removes the highlighted seam from the image.
  undo() – Reverts the removal by adding the previously removed seam back into the image.
  ```
### Example Interaction
Welcome! Enter file path:
- /path/to/image.png

The image contains 10 seams. Please enter a command:
- g – Remove the greenest seam.
- e – Remove the seam with the lowest energy.
- u – Undo the last edit.
- q – Quit.


**Example 1: Remove the Lowest Energy Seam**

    ```
    Please enter a command:
    e
    Remove the lowest energy seam. Continue? (Y/N)
    Y
    Please enter a command:
    g - Remove the greenest seam
    e - Remove the seam with the lowest energy
    u - Undo previous edit
    q - Quit
    ```
**Example 2: Undo the Last Edit**

    ```
    Please enter a command:
    u
    Undo. Continue? (Y/N)
    Y
    Please enter a command:
    g - Remove the greenest seam
    e - Remove the seam with the lowest energy
    u - Undo previous edit
    q - Quit
    ```
**Example 3: Quit the Program**

    ```
    Please enter a command:
    q
    Thanks for playing.
    ```
After this interaction, the edited image will be saved as newImg.png in the target directory.

### Algorithm Efficiency and Performance
1. O(n) Seam Removal and Insertion
    - The `removeSeam()` and `addSeam()` methods in the Image class operate on one pixel per row (linear with image height n), so they follow `O(n)` time complexity as required.
2. Efficient Data Structure (non-2D array)
    - Instead of using a basic 2D array, we used a structure similar to a list of rows of pixels `List<List<Pixel>>` which allows better flexibility for seam editing compared to a fixed-size array.
3. Energy Recalculation in `O(n²)`
    - The `calculateEnergy()` method updates energy values for all pixels, which touches every pixel once → this is O(n²), and expected.

### Challenges and Solutions
1. Efficient Updates (Structure instead of plain array)
    - the approach we used avoids shifting entire arrays manually, as using lists or similar structures simplifies seam operations.
2. Undo Feature (Command Pattern)
    - we implemented a `CommandControl interface` with `execute()` and `undo()` methods.
    - `SeamRemovalCommand` keeps track of removed seams and can reverse them, fulfilling the undo requirement.
3. Accurate Color and Energy Update
    - After each seam change (remove or insert), energy is recalculated via `calculateEnergy()`, ensuring image data remains correct.