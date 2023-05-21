package colores;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class App {

  private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
  private static final Colors colorUtil = new Colors();

  public static void main(String[] args) {
    final JFrame appFrame = new JFrame("Colores");
    appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    appFrame.setPreferredSize(new Dimension(600, 180));
    appFrame.setResizable(false);
    appFrame.setLayout(new BoxLayout(appFrame.getContentPane(), BoxLayout.Y_AXIS));

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    appFrame.add(Box.createRigidArea(new Dimension(0, 40)));

    final JLabel addImageLabel = new JLabel("Agrega una imagen para extrar su color predominante.",
        SwingConstants.CENTER);
    addImageLabel.setFont(FONT);
    addImageLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
    addImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    appFrame.add(addImageLabel);

    appFrame.add(Box.createRigidArea(new Dimension(0, 20)));

    final JButton addImageButton = new JButton("Analizar Imagen...");
    addImageButton.setFont(FONT);
    addImageButton.setAlignmentY(Component.CENTER_ALIGNMENT);
    addImageButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    final ActionListener addImageButtonAction = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final var imageFile = readImage(appFrame);

        if (imageFile != null) {
          ArrayList<int[]> imageRgb = getImageRgb(imageFile);

          if (imageRgb != null) {
            HashMap<int[], Integer> imageColors = getImageColors(imageRgb);
            createStatsFrame(appFrame, imageColors);
          }
        }
      }
    };
    addImageButton.addActionListener(addImageButtonAction);

    appFrame.add(addImageButton);

    appFrame.pack();
    appFrame.setVisible(true);
  }

  private static ArrayList<int[]> getImageRgb(File file) {
    final ArrayList<int[]> colorData = new ArrayList<>();

    BufferedImage imageBuffer;
    try {
      imageBuffer = ImageIO.read(file);
    } catch (Exception e) {
      return null;
    }

    int width = imageBuffer.getWidth();
    int height = imageBuffer.getHeight();

    final FastRgb fastRgb = new FastRgb(imageBuffer);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int rgb = fastRgb.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        colorData.add(new int[] { r, g, b });
      }
    }

    return colorData;
  }

  private static HashMap<int[], Integer> getImageColors(List<int[]> data) {
    final HashMap<int[], Integer> colors = new HashMap<>();

    for (int[] color : data) {
      if (colors.containsKey(color)) {
        colors.put(color, colors.get(color) + 1);
      } else {
        colors.put(color, 1);
      }
    }

    return colors;
  }

  private static void createStatsFrame(JFrame frame, Map<int[], Integer> colorData) {
    final JFrame statsFrame = new JFrame("Análisis de Imagen");
    statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    statsFrame.setPreferredSize(new Dimension(600, 300));
    statsFrame.setResizable(false);
    statsFrame.setLayout(new GridLayout(1, 5));

    List<Map.Entry<int[], Integer>> dominantColors = colorData.entrySet().stream()
        .sorted(Map.Entry.<int[], Integer>comparingByValue().reversed()).limit(5).toList();

    for (Map.Entry<int[], Integer> entry : dominantColors) {
      int[] colorRgb = entry.getKey();
      int occurrences = entry.getValue();

      String colorName = colorUtil.getColorNameFromRgb(colorRgb[0], colorRgb[1], colorRgb[2]);

      System.out.println("%s : %d (%s)".formatted(colorName, occurrences, colorRgb));

      final JPanel colorPanel = new JPanel();
      colorPanel.setBackground(new Color(colorRgb[0], colorRgb[1], colorRgb[2]));
      colorPanel.setPreferredSize(new Dimension(50, 50));

      statsFrame.add(colorPanel);
    }

    statsFrame.pack();
    statsFrame.setVisible(true);
  }

  private static File readImage(JFrame frame) {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Selecciona una imagen");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setAcceptAllFileFilterUsed(false);

    final int result = fileChooser.showOpenDialog(frame);
    if (result != JFileChooser.APPROVE_OPTION) {
      JOptionPane.showMessageDialog(frame, "Necesita ingresar una imagen para poder analizarla.", "Error",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }

    return fileChooser.getSelectedFile();
  }
}
