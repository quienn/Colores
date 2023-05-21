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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private static boolean isProcessing = false;
  private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
  private static final Colors colorUtil = new Colors();

  public static void main(String[] args) {
    final JFrame appFrame = new JFrame("Colores");
    appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    appFrame.setPreferredSize(new Dimension(600, 170));
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

    final JButton addImageButton = new JButton("Elegir...");

    addImageButton.setFont(FONT);
    addImageButton.setAlignmentY(Component.CENTER_ALIGNMENT);
    addImageButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    final ActionListener addImageButtonAction = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final var imageFile = readImage(appFrame);

        if (imageFile != null) {
          createStatsFrame(appFrame, imageFile);
        }
      }
    };
    addImageButton.addActionListener(addImageButtonAction);

    appFrame.add(addImageButton);

    appFrame.pack();
    appFrame.setVisible(true);
  }

  private static void createStatsFrame(JFrame frame, File imageFile) {
    final JFrame statsFrame = new JFrame("Análisis de Imagen");
    statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    statsFrame.setPreferredSize(new Dimension(600, 300));
    statsFrame.setResizable(false);
    statsFrame.setLayout(new GridLayout(5, 1));

    final HashMap<String, Integer> colorNameMap = new HashMap<>();
    final HashMap<String, int[]> colorMap = new HashMap<>();

    final BufferedImage imageBuffer;
    try {
      imageBuffer = ImageIO.read(imageFile);
    } catch (Exception e) {
      // TODO Agregar un mensaje de error y cerrar ventana si no se pudo leer la
      // imagen.
      System.out.println(e);
      return;
    }

    final int width = imageBuffer.getWidth();
    final int height = imageBuffer.getHeight();

    final FastRgb fastRgb = new FastRgb(imageBuffer);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x += 1) {
        int rgb = fastRgb.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        String name = colorUtil.getColorNameFromRgb(r, g, b);
        if (colorNameMap.containsKey(name)) {
          colorNameMap.put(name, colorNameMap.get(name) + 1);
        } else {
          colorNameMap.put(name, 1);
        }

        if (colorMap.containsKey(name)) {
          int[] color = colorMap.get(name);
          colorMap.put(name, color);
        } else {
          int[] color = { r, g, b };
          colorMap.put(name, color);
        }
      }
    }

    final List<Map.Entry<String, Integer>> dominantColors = colorNameMap.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(5).toList();

    int entryPosition = 1;
    for (Map.Entry<String, Integer> colorEntry : dominantColors) { 
      int[] color = colorMap.get(colorEntry.getKey());

      // System.out.println(colorEntry.getKey() + " : " + colorEntry.getValue());
      final Color colorObject = new Color(color[0], color[1], color[2]);

      final JPanel colorPanel = new JPanel();
      colorPanel.setBackground(colorObject);
      colorPanel.setPreferredSize(new Dimension(50, 50));

      final JLabel colorLabel = new JLabel("%d. %s (%d píxeles)".formatted(entryPosition, colorEntry.getKey(), colorEntry.getValue()));
      colorLabel.setFont(FONT);
      colorLabel.setVerticalAlignment(JLabel.CENTER);
      colorLabel.setHorizontalAlignment(JLabel.CENTER);

      int d = 0;
      final double luminance = (0.299 * colorObject.getRed() + 0.587 * colorObject.getGreen() + 0.114 * colorObject.getBlue()) / 255;

      if (luminance > 0.5) {
        d = 0;
      } else {
        d = 255;
      }

      colorLabel.setForeground(new Color(d, d, d));

      colorPanel.add(colorLabel);

      statsFrame.add(colorPanel);
      entryPosition++;
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
