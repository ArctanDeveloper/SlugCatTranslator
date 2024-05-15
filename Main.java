import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.HashMap;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static HashMap<String, Word> wordes = new HashMap<String, Word>();
    static HashMap<String, File> files = new HashMap<String, File>();

    private static void createAndShowGUI() throws IOException {
        JFrame jFrame = new JFrame("Slugcat Translator");
        jFrame.setLayout(new FlowLayout());
        jFrame.setSize(300, 300);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField label = new JTextField(32);
        JLabel info = new JLabel("Stuff");
        JButton exportation = new JButton("Export");
        exportation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (String key : wordes.keySet()) {
                    System.out.println(key + ":" + wordes.get(key).frames.length);
                }

                String text = label.getText();
                String[] words = text.split(" ");
                // grab the output image type from the first image in the sequence
                BufferedImage firstImage = wordes.get("rest").frames[0];

                if (wordes.containsKey(words[0]))
                    info.setText(wordes.get(words[0]).type == 0 ? "Passive" : "Active");

                try (
                        // create a new BufferedOutputStream with the last argument
                        ImageOutputStream output = new FileImageOutputStream(
                                new File(System.getProperty("user.dir") + "/output" + text + ".gif"))) {
                    // create a gif sequence with the type of the first image, 1 second
                    // between frames, which loops continuously
                    GifSequenceWriter writer = new GifSequenceWriter(output, firstImage.getType(), 200, true);

                    // write out the first image to our sequence...
                    try {
                        writer.writeToSequence(firstImage);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    for (String word : words) {
                        if (!wordes.containsKey(word))
                            continue;
                        for (int i = 1; i < wordes.get(word).frames.length; i++) {
                            BufferedImage nextImage = wordes.get(word).frames[i];
                            writer.writeToSequence(nextImage);
                        }

                        for (int i = 1; i < wordes.get("rest").frames.length; i++) {
                            BufferedImage nextImage = wordes.get("rest").frames[i];
                            writer.writeToSequence(nextImage);
                        }
                    }

                    for (int j = 0; j < 3; j++) {
                        for (int i = 1; i < wordes.get("rest").frames.length; i++) {
                            BufferedImage nextImage = wordes.get("rest").frames[i];
                            writer.writeToSequence(nextImage);
                        }
                    }

                    writer.close();
                    output.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        exportation.setBorder(border);
        info.setBorder(border);
        label.setBorder(border);
        label.setPreferredSize(new Dimension(150, 100));

        label.setText("Slugcat");
        label.setHorizontalAlignment(JLabel.CENTER);

        jFrame.add(label);
        jFrame.add(exportation);
        jFrame.add(info);
        jFrame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        File dir = new File(System.getProperty("user.dir"));
        System.out.println(dir);
        File[] filesa = dir.listFiles(new FileFilter() {
            public boolean accept(File dir) {
                return dir.getPath().toLowerCase().endsWith(".wrd");
            }
        });
        File[] filesb = dir.listFiles(new FileFilter() {
            public boolean accept(File dir) {
                return dir.getPath().toLowerCase().endsWith(".png");
            }
        });

        for (File file : filesa) {
            System.out.println(file);
            String strangle = file.getName().replace(".wrd", "");
            ArrayList<BufferedImage> imger = new ArrayList<BufferedImage>();
            for (File file2 : filesb) {
                String strungle = file2.getName().replace(".png", "");
                if (strungle.startsWith(strangle)) {
                    imger.add(ImageIO.read(new File(file2.getAbsolutePath())));
                }
            }
            files.put(strangle, file);
            BufferedImage[] bl = new BufferedImage[imger.size()];
            for (int i = 0; i < imger.size(); i++) {
                bl[i] = imger.get(i);
            }
            Scanner reader = new Scanner(file);
            wordes.put(strangle, new Word(bl, reader.nextInt()));
            reader.close();
        }
        createAndShowGUI();
    }
}
