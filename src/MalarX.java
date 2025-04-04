import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class MalarX extends JFrame {
    private static final int SCREEN_WIDTH = 375; // emulate phone width and height
    private static final int SCREEN_HEIGHT = 667;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 30;
    private static final int PADDING = 30;
    private static final int IMAGE_SIZE = SCREEN_WIDTH - PADDING * 2;

    private BufferedImage selectedImage;
    private File selectedFile;

    public static BufferedImage loadImage(String path) {
        BufferedImage image = null;
        try {
            File imageFile = new File(path);
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public MalarX() {
        setTitle("MalarX");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        JFileChooser fileChooser = new JFileChooser();

        JButton processButton = new JButton("Process File");
        processButton.setBounds(SCREEN_WIDTH / 2 - BUTTON_WIDTH / 2, SCREEN_HEIGHT - 100, BUTTON_WIDTH, BUTTON_HEIGHT);
        processButton.addActionListener(e -> {
            if (selectedFile != null) {
                String result = processImage(selectedFile);
                JOptionPane.showMessageDialog(null, "Processing Result: " + result, "Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No file selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(processButton);

        JButton fileSelectButton = new JButton("Pick");
        fileSelectButton.setBounds(50, SCREEN_HEIGHT - 100, 66, BUTTON_HEIGHT);
        fileSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();

                    // Error check: Ensure the file is a PNG
                    if (!filePath.toLowerCase().endsWith(".png")) {
                        JOptionPane.showMessageDialog(null, "Please select a PNG file.", "Invalid File", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    selectedImage = loadImage(filePath);
                    if (selectedImage != null) {
                        JLabel picLabel = new JLabel(new ImageIcon(selectedImage.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH)));
                        picLabel.setBounds(PADDING, 66, IMAGE_SIZE, IMAGE_SIZE);
                        panel.add(picLabel);
                        panel.revalidate();
                        panel.repaint();
                    }
                }
            }
        });
        panel.add(fileSelectButton);

        this.getContentPane().add(panel);
        setVisible(true);
    }

    private String processImage(File imageFile) {
        String result = "Error"; // default
        try (Socket socket = new Socket("0.0.0.0", 12345); // connect to server
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();
             DataOutputStream dos = new DataOutputStream(out);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            // send image file to server
            byte[] fileBytes = new byte[(int) imageFile.length()];
            FileInputStream fis = new FileInputStream(imageFile);
            fis.read(fileBytes);
            fis.close();

            dos.writeInt(fileBytes.length);
            dos.write(fileBytes);
            dos.flush();

            // receive result
            result = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MalarX::new);
    }
}

