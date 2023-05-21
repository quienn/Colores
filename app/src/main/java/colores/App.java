package colores;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

public class App {
  private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

  public static void main(String[] args) {
    final JFrame frame = new JFrame("Colores");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(600, 180));
    frame.setResizable(false);
    frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

    frame.add(Box.createRigidArea(new Dimension(0, 40)));

    final JLabel addImageLabel = new JLabel("Agrega una imagen para extrar su color predominante.", SwingConstants.CENTER);
    addImageLabel.setFont(FONT);
    addImageLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
    addImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    frame.add(addImageLabel);

    frame.add(Box.createRigidArea(new Dimension(0, 20)));

    final JButton addImageButton = new JButton("Analizar Imagen...");
    addImageButton.setFont(FONT);
    addImageButton.setAlignmentY(Component.CENTER_ALIGNMENT);
    addImageButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    final ActionListener addImageButtonAction = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final var imageFile = readImage(frame);

        if (imageFile != null) {
          System.out.println(imageFile.getPath());
        }
      }
    };

    addImageButton.addActionListener(addImageButtonAction);

    frame.add(addImageButton);

    frame.pack();
    frame.setVisible(true);
  }

  private static File readImage(JFrame frame) {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Selecciona una imagen");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setAcceptAllFileFilterUsed(false);

    final int result = fileChooser.showOpenDialog(frame);
    if (result != JFileChooser.APPROVE_OPTION) {
      JOptionPane.showMessageDialog(frame, "Necesita ingresar una imagen para poder analizarla.", "Error", JOptionPane.ERROR_MESSAGE);
      return null;
    }

    return fileChooser.getSelectedFile();
  }
}
