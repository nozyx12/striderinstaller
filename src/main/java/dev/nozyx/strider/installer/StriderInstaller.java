package dev.nozyx.strider.installer;

import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class StriderInstaller {
    public static final String VERSION = "1.0.1";

    private static final Point[] mouseClickPoint = new Point[1];

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException ignored) {}

        JFrame frame = new JFrame("StriderInstaller v" + VERSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 530);
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, 400, 530, 20, 20));
        frame.setLocationRelativeTo(null);

        URL iconURL = StriderInstaller.class.getResource("/icon.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            frame.setIconImage(icon.getImage());
        }

        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseClickPoint[0] = e.getPoint();
            }
        });
        frame.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                frame.setLocation(current.x - mouseClickPoint[0].x, current.y - mouseClickPoint[0].y);
            }
        });

        frame.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            BufferedImage logoImg = ImageIO.read(StriderInstaller.class.getResource("/logo.png"));
            Image scaledLogo = logoImg.getScaledInstance(250, 110, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
        } catch (IOException e) {
            logoLabel.setText("Logo not found");
        }

        JLabel versionLabel = new JLabel("v" + VERSION);
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(Color.DARK_GRAY);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(logoLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(versionLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel striderLabel = new JLabel("Select StriderLoader version:");
        striderLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        striderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(striderLabel);
        contentPanel.add(Box.createVerticalStrut(5));

        JComboBox<String> striderVersionBox = new JComboBox<>(new String[]{
                "0.0.1"
        });
        striderVersionBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        striderVersionBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        striderVersionBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(striderVersionBox);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel mcLabel = new JLabel("Select Launcher version:");
        mcLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mcLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(mcLabel);
        contentPanel.add(Box.createVerticalStrut(5));

        JComboBox<String> launcherVersionCombo = new JComboBox<>();
        launcherVersionCombo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        launcherVersionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        launcherVersionCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(launcherVersionCombo);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel mcVersionLabel = new JLabel("Minecraft version:");
        mcVersionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mcVersionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(mcVersionLabel);
        contentPanel.add(Box.createVerticalStrut(5));

        JTextField mcVersionField = new PlaceholderTextField("Enter Minecraft version");
        mcVersionField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mcVersionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(mcVersionField);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel launcherFolderLabel = new JLabel("Launcher folder:");
        launcherFolderLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        launcherFolderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(launcherFolderLabel);
        contentPanel.add(Box.createVerticalStrut(5));

        String defaultPath;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) defaultPath = System.getenv("APPDATA") + "\\.minecraft";
        else if (os.contains("mac")) defaultPath = System.getProperty("user.home") + "/Library/Application Support/minecraft";
        else defaultPath = System.getProperty("user.home") + "/.minecraft";

        if (!new File(defaultPath).exists()) defaultPath = "-- Default folder not found --";

        JTextField pathField = new JTextField(defaultPath);
        pathField.setEditable(false);
        pathField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton browseButton = new JButton("Browse...");
        browseButton.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BorderLayout(5, 0));
        pathPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pathPanel.add(pathField, BorderLayout.CENTER);
        pathPanel.add(browseButton, BorderLayout.EAST);
        pathPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(pathPanel);
        contentPanel.add(Box.createVerticalGlue());

        frame.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton installButton = new JButton("Install");
        installButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        installButton.addActionListener(e -> {
            String loaderVersion = (String) striderVersionBox.getSelectedItem();
            String launcherVersion = (String) launcherVersionCombo.getSelectedItem();
            String mcVersion = mcVersionField.getText();
            String launcherFolder = pathField.getText();
            install(loaderVersion, launcherVersion, mcVersion, launcherFolder, frame);
        });

        if (pathField.getText().equals("-- Default folder not found --")) {
            installButton.setEnabled(false);
            installButton.setToolTipText("Please select launcher version and launcher folder");

            launcherVersionCombo.setEnabled(false);
            launcherVersionCombo.addItem("-- Please select launcher version --");
        } else listLauncherVersions(striderVersionBox, launcherVersionCombo, installButton, pathField.getText(), mcVersionField.getText());


        striderVersionBox.addActionListener(e -> updateInstallButton(installButton, striderVersionBox, launcherVersionCombo, mcVersionField.getText()));
        launcherVersionCombo.addActionListener(e -> updateInstallButton(installButton, striderVersionBox, launcherVersionCombo, mcVersionField.getText()));
        mcVersionField.addActionListener(e -> updateInstallButton(installButton, striderVersionBox, launcherVersionCombo, mcVersionField.getText()));

        mcVersionField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateInstallButton(installButton, striderVersionBox, launcherVersionCombo, mcVersionField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateInstallButton(installButton, striderVersionBox, launcherVersionCombo, mcVersionField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(pathField.getText());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                if (!new File(chooser.getSelectedFile().getAbsolutePath()).exists()) {
                    pathField.setText("-- Folder not found --");
                    launcherVersionCombo.setEnabled(false);
                    return;
                }

                pathField.setText(chooser.getSelectedFile().getAbsolutePath());
                listLauncherVersions(striderVersionBox, launcherVersionCombo, installButton, chooser.getSelectedFile().getAbsolutePath(), mcVersionField.getText());
            }
        });

        JButton quitButton = new JButton("Exit");
        quitButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        quitButton.addActionListener(e -> {
            WindowEvent wev = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        });

        buttonsPanel.add(installButton);
        buttonsPanel.add(quitButton);
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        updateInstallButton(installButton, striderVersionBox, launcherVersionCombo, mcVersionField.getText());
    }

    private static void updateInstallButton(JButton installButton, JComboBox<String> striderVersionBox, JComboBox<String> launcherVersionCombo, String mcVersion) {
        String loaderVersion = (String) striderVersionBox.getSelectedItem();
        String launcherVersion = (String) launcherVersionCombo.getSelectedItem();

        boolean isValid = loaderVersion != null
                && launcherVersion != null
                && !launcherVersion.startsWith("--")
                && !mcVersion.isBlank();

        installButton.setEnabled(isValid);
        installButton.setToolTipText(isValid ? null : "Please select launcher version and launcher folder");
    }

    private static void listLauncherVersions(JComboBox<String> striderVersionBox, JComboBox<String> launcherVersionCombo, JButton installButton, String launcherFolder, String mcVersion) {
        launcherVersionCombo.removeAllItems();
        launcherVersionCombo.addItem("Loading...");

        File versionsDir = new File(launcherFolder, "versions");

        if (versionsDir.exists() && versionsDir.isDirectory()) {
            File[] subDirs = versionsDir.listFiles(File::isDirectory);
            if (subDirs != null) {
                List<String> validVersions = new ArrayList<>();
                for (File dir : subDirs) {
                    String dirName = dir.getName();
                    if (dirName.startsWith("striderloader-")) continue;

                    File jsonFile = new File(dir, dirName + ".json");
                    if (jsonFile.exists() && jsonFile.isFile()) validVersions.add(dirName);
                }

                validVersions.sort(String::compareTo);

                launcherVersionCombo.removeAllItems();
                if (validVersions.isEmpty()) {
                    launcherVersionCombo.addItem("-- No versions found --");
                    launcherVersionCombo.setEnabled(false);
                } else {
                    launcherVersionCombo.setEnabled(true);
                    for (String versionName : validVersions) launcherVersionCombo.addItem(versionName);
                }

                updateInstallButton(installButton, striderVersionBox, launcherVersionCombo, mcVersion);
            } else {
                launcherVersionCombo.removeAllItems();
                launcherVersionCombo.addItem("-- Could not list folder --");
                launcherVersionCombo.setEnabled(false);
            }
        } else {
            launcherVersionCombo.removeAllItems();
            launcherVersionCombo.addItem("-- Versions dir not found --");
            launcherVersionCombo.setEnabled(false);
        }
    }

    private static void install(String loaderVersion, String launcherVersion, String mcVersion, String launcherFolder, JFrame frame) {
        try {
            File launcherProfiles = new File(launcherFolder, "launcher_profiles.json");
            if (!launcherProfiles.exists()) {
                JOptionPane.showMessageDialog(null,
                        "Error : File 'launcher_profiles.json' not found in: " + launcherFolder,
                        "Install error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            File versionsDir = new File(launcherFolder, "versions");

            File striderDir = new File(versionsDir, "striderloader-" + loaderVersion + "-" + launcherVersion);

            if (!striderDir.exists()) {
                boolean created = striderDir.mkdirs();
                if (!created) {
                    JOptionPane.showMessageDialog(null,
                            "Error : Could not create version folder: " + striderDir.getAbsolutePath(),
                            "Install error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }
            } else {
                int result2 = JOptionPane.showConfirmDialog(null,
                        "Looks like StriderLoader " + loaderVersion + " with launcher version '" + launcherVersion + "' is already installed!\nReinstall it?",
                        "Install error",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (result2 == JOptionPane.NO_OPTION) return;

                try (Stream<Path> stream = Files.walk(striderDir.toPath())) {
                    stream
                            .filter(path -> !path.equals(striderDir.toPath()))
                            .sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    throw new RuntimeException("Error deleting: " + path, e);
                                }
                            });
                }
            }

            try (InputStream is = StriderInstaller.class.getResourceAsStream("/loaderJsons/" + loaderVersion + ".json")) {
                if (is == null) throw new IOException("StriderLoader's JSON version file not found: /loaderJsons/" + loaderVersion + ".json");

                String jsonTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String finalJson = jsonTemplate.replace("<mcVersion>", mcVersion).replace("<launcherVersion>", launcherVersion);
                File outputFile = new File(striderDir, "striderloader-" + loaderVersion + "-" + launcherVersion + ".json");
                Files.writeString(outputFile.toPath(), finalJson, StandardCharsets.UTF_8);
            }

            Path launcherProfilesPath = launcherProfiles.toPath();
            String launcherProfilesContent = Files.readString(launcherProfilesPath, StandardCharsets.UTF_8);

            JSONObject launcherProfilesJson = new JSONObject(launcherProfilesContent);

            JSONObject profiles = launcherProfilesJson.optJSONObject("profiles");
            if (profiles == null) {
                profiles = new JSONObject();
                launcherProfilesJson.put("profiles", profiles);
            }

            String profileKey = "striderloader-" + loaderVersion + "-" + launcherVersion;
            String nowIso = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

            JSONObject newProfile = new JSONObject();
            newProfile.put("created", nowIso);
            newProfile.put("icon", encodeImage("icon.png"));
            newProfile.put("lastUsed", nowIso);
            newProfile.put("lastVersionId", profileKey);
            newProfile.put("name", "striderloader-" + launcherVersion);
            newProfile.put("type", "custom");

            profiles.put(profileKey, newProfile);

            Files.writeString(launcherProfilesPath, launcherProfilesJson.toString(4), StandardCharsets.UTF_8);

            JOptionPane.showMessageDialog(
                    frame,
                    "Installation completed! You can now launch the Minecraft Launcher to start using StriderLoader.",
                    "Installation done",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            e.printStackTrace(System.err);

            if (e instanceof JSONException) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error : An uncaught exception occurred while parsing/writing a JSON file (Launcher version's JSON / Launcher's 'launcher_profiles.json' / StriderLoader final version's JSON) :\n" + e.getLocalizedMessage(),
                        "Install error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Error : An uncaught exception occurred while installing StriderLoader:\n" + e.getLocalizedMessage(),
                    "Install error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static String encodeImage(String resourcePath) throws IOException {
        try (InputStream is = StriderInstaller.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            byte[] bytes = is.readAllBytes();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        }
    }
}
