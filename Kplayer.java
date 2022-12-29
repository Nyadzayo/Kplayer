import javax.media.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Kplayer {
    private static Player player;
    private static List<File> videoFiles = new ArrayList<>();
    private static int currentVideoIndex = 0;
    private static JFrame frame;
    private static JSlider volumeSlider;
    private static JButton playButton;
    private static JButton pauseButton;
    private static JButton nextButton;
    private static JButton previousButton;
    private static JButton openButton;
    private static JFileChooser fileChooser;

    public static void main(String[] args) {
        // Check if the JMF is installed
        if (!Manager.getSupportedFormats().hasNext()) {
            System.out.println("JMF is not installed on this system");
            return;
        }

        // Create the frame to display the video
        frame = new JFrame("Video Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);

        // Create the file chooser
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        // Create the buttons
        playButton = new JButton("Play");
        playButton.addActionListener(e -> play());
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> pause());
        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> next());
        previousButton = new JButton("Previous");
        previousButton.addActionListener(e -> previous());
        openButton = new JButton("Open");
        openButton.addActionListener(e -> openFile());

        // Create the volume slider
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.addChangeListener(e -> setVolume(volumeSlider.getValue()));

        // Add the buttons and volume slider to the frame
        JPanel controlPanel = new JPanel();
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(nextButton);
        controlPanel.add(previousButton);
        controlPanel.add(openButton);
        controlPanel.add(volumeSlider);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Show the frame
        frame.setVisible(true);
    }

    private static void play() {
        if (player == null) {
            // Create a player for the current video file if it does not exist
            try {
                player = Manager
                        .createRealizedPlayer(new MediaLocator(videoFiles.get(currentVideoIndex).toURI().toURL()));
            } catch (Exception e) {
                System.out.println("Error creating player for the video file");
                e.printStackTrace();
                return;
            }

            // Add the player's visual component to the frame
            frame.add(player.getVisualComponent());

            // Update the frame layout
            frame.validate();
        }
        player.start();
    }

    private static void pause() {
        if (player != null) {
            player.stop();
        }
    }

    private static void next() {
        if (player != null) {
            player.stop();
            player = null;
        }
        currentVideoIndex++;
        if (currentVideoIndex >= videoFiles.size()) {
            currentVideoIndex = 0;
        }
        play();
    }

    private static void previous() {
        if (player != null) {
            player.stop();
            player = null;
        }
        currentVideoIndex--;
        if (currentVideoIndex < 0) {
            currentVideoIndex = videoFiles.size() - 1;
        }
        play();
    }

    private static void setVolume(int volume) {
        if (player != null) {
            float gain = volume / 100.0f;
            player.getGainControl().setLevel(gain);
        }
    }

    private static void openFile() {
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            videoFiles.add(selectedFile);
            if (player != null) {
                player.stop();
                player = null;
            }
            currentVideoIndex = videoFiles.size() - 1;
            play();
        }
    }
}
