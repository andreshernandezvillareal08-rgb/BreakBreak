package main.java.com.breakbreak.app;

import main.java.com.breakbreak.components.*;
import main.java.com.breakbreak.enums.State;
import main.java.com.breakbreak.events.GameLoop;
import main.java.com.breakbreak.events.PowerUpManager;

import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameComponentDemoFrame extends JFrame {

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JPanel gameArea;
    private JPanel bottomPanel;

    private JButton playButton;
    private JButton exitButton;

    JPanel RecordPanel;
    JPanel ScorePanel;
    JPanel LivePanel;
    JPanel LevelPanel;

    private NumberComponent scoreDisplay;
    private NumberComponent recordDisplay;
    private NumberComponent levelDisplay;
    private LivesComponent livesDisplay;

    private Clip clipMusica;
    private long posicionMusicaActual = 0;
    private BallComponent ball;
    private PaddleComponent paddle;
    private BorderComponent border;
    private GameLoop gameLoop;
    private Timer collisionChecker;

    private int initialBallX;
    private int initialBallY;
    private List<BlockComponent> blockList = new ArrayList<>();
    private BlockComponent[][] blockArray;
    private PowerUpManager powerUpManager;

    private int score = 0;
    private int record = 0;
    private int lives = 3;
    private int level = 1;

    public GameComponentDemoFrame() {
        setContentPane(mainPanel);
        setTitle("BreakBreak — Laboratorio de Componentes Visuales");
        setSize(865, 595);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        Color azulCyan = new Color(0, 130, 160);

        mainPanel.setBackground(azulCyan);

        if (bottomPanel != null) {
            bottomPanel.setOpaque(true);
            bottomPanel.setBackground(azulCyan);
        }

        if (titleLabel != null) {
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        configurarPanelesHUDForm();
        cargarElementosJuego();
        iniciarMonitorDeColision();

        if (exitButton != null) {
            exitButton.addActionListener(ev -> System.exit(0));
        }

        if (playButton != null) {
            playButton.addActionListener(ev -> {
                if (gameLoop != null) {
                    State currentState = gameLoop.getState();

                    if (currentState == State.READY || currentState == State.PAUSED || currentState == State.GAME_OVER) {
                        if (currentState == State.GAME_OVER) {
                            gameLoop.reset();
                            reiniciarBloques();

                            ball.setLocation(initialBallX, initialBallY);
                            ball.setVx(3);
                            ball.setVy(-3);
                            paddle.setSize(50, paddle.getHeight());


                            Rectangle area = border.getInnerBounds();
                            int paddleX = area.x + (area.width / 2) - (paddle.getWidth() / 2);
                            int paddleY = area.y + area.height - 40;
                            paddle.setLocation(paddleX, paddleY);

                            if (powerUpManager != null) {
                                powerUpManager.clearAll();
                            }

                            score = 0;
                            lives = 3;
                            level = 1;
                            actualizarHUDDirecto();

                            reiniciarMusicaDesdeCero();
                        }

                        gameLoop.start();

                        if (currentState != State.GAME_OVER) {
                            reanudarOIniciarMusica();
                        }

                        if (powerUpManager != null) {
                            powerUpManager.resume();
                        }

                        playButton.setText("Pausa");
                    }
                    else if (currentState == State.RUNNING) {
                        gameLoop.pause();
                        pausarMusica();

                        if (powerUpManager != null) {
                            powerUpManager.pause();
                        }

                        playButton.setText("Jugar");
                    }
                }
            });
        }
    }

    private void configurarPanelesHUDForm() {
        if (RecordPanel != null) {
            configurarPanelConFondo(RecordPanel, "/image/record.png", 268, 47);
            recordDisplay = new NumberComponent(6, 134, 11, 117, 25);
            recordDisplay.setValue(record);
            RecordPanel.add(recordDisplay);
            RecordPanel.setComponentZOrder(recordDisplay, 0);
        }

        if (ScorePanel != null) {
            configurarPanelConFondo(ScorePanel, "/image/score.png", 268, 135);
            scoreDisplay = new NumberComponent(6, 18, 62, 232, 60);
            scoreDisplay.setValue(score);
            ScorePanel.add(scoreDisplay);
            ScorePanel.setComponentZOrder(scoreDisplay, 0);
        }

        if (LivePanel != null) {
            configurarPanelConFondo(LivePanel, "/image/live.png", 201, 133);
            livesDisplay = new LivesComponent(lives, 16, 62, 168, 58);
            LivePanel.add(livesDisplay);
            LivePanel.setComponentZOrder(livesDisplay, 0);
        }

        if (LevelPanel != null) {
            configurarPanelConFondo(LevelPanel, "/image/level.png", 145, 132);
            levelDisplay = new NumberComponent(2, 16, 62, 113, 55);
            levelDisplay.setValue(level);
            LevelPanel.add(levelDisplay);
            LevelPanel.setComponentZOrder(levelDisplay, 0);
        }
    }

    private void configurarPanelConFondo(JPanel panel, String rutaImagen, int anchoDefecto, int altoDefecto) {
        panel.setLayout(null);
        panel.setOpaque(false);
        panel.removeAll();
        panel.setPreferredSize(new Dimension(anchoDefecto, altoDefecto));

        URL url = getClass().getResource(rutaImagen);
        if (url == null && rutaImagen.startsWith("/image/")) {
            url = getClass().getResource(rutaImagen.replace("/image/", "/images/"));
        }

        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(anchoDefecto, altoDefecto, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(img);

            JLabel bgLabel = new JLabel(scaledIcon);
            bgLabel.setBounds(0, 0, anchoDefecto, altoDefecto);
            panel.add(bgLabel);
            panel.setComponentZOrder(bgLabel, panel.getComponentCount() - 1);
        } else {
            System.err.println("¡ADVERTENCIA! No se pudo encontrar la imagen de fondo: " + rutaImagen);
        }
    }

    private void actualizarHUDDirecto() {
        if (scoreDisplay != null) scoreDisplay.setValue(score);
        if (livesDisplay != null) livesDisplay.setLives(lives);
        if (levelDisplay != null) levelDisplay.setValue(level);
        if (recordDisplay != null && score > record) {
            record = score;
            recordDisplay.setValue(record);
        }
    }

    private void cargarElementosJuego() {
        gameArea.setLayout(null);
        gameArea.setOpaque(true);

        if (gameArea instanceof JComponent) {
            ((JComponent) gameArea).setDoubleBuffered(true);
        }

        URL imgUrl = getClass().getResource("/images/background.png");
        JLabel bgLabel = null;
        if (imgUrl != null) {
            ImageIcon bgIcon = new ImageIcon(imgUrl);
            bgLabel = new JLabel(bgIcon);
            bgLabel.setBounds(0, 0, 584, 520);
            gameArea.add(bgLabel);
        }

        border = new BorderComponent(new Rectangle(0, 0, 584, 520));
        gameArea.add(border);

        Rectangle area = border.getInnerBounds();

        int paddleWidth = 50;
        int paddleHeight = 10;
        int paddleX = area.x + (area.width / 2) - (paddleWidth / 2);
        int paddleY = area.y + area.height - 40;

        paddle = new PaddleComponent(paddleX, paddleY, paddleWidth, paddleHeight, "paddle", "/images/paddle.png");
        gameArea.add(paddle);

        int ballSize = 25;
        initialBallX = area.x + (area.width / 2) - (ballSize / 2);
        initialBallY = area.y + 380;
        ball = new BallComponent(initialBallX, initialBallY, ballSize, ballSize);
        ball.setVx(3);
        ball.setVy(-3);
        gameArea.add(ball);

        int rows = 8;
        int cols = 8;
        int blockHeight = 20;
        int startX = area.x;
        int startY = area.y;

        blockArray = new BlockComponent[rows][cols];
        blockList.clear();

        Random random = new Random();
        String[] blockImages = {
                "/images/block-blue.png", "/images/block-green.png",
                "/images/block-orange.png", "/images/block-yellow.png",
                "/images/block-cyan.png", "/images/block-aqua.png"
        };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x1 = startX + (c * area.width) / cols;
                int x2 = startX + ((c + 1) * area.width) / cols;
                int blockWidth = x2 - x1;

                int bx = x1;
                int by = startY + (r * blockHeight);
                String blockImage = blockImages[random.nextInt(blockImages.length)];
                int durezaAleatoria = random.nextInt(3) + 1;

                BlockComponent block = new BlockComponent(bx, by, blockWidth, blockHeight, "block", blockImage, durezaAleatoria);
                blockArray[r][c] = block;
                blockList.add(block);
                gameArea.add(block);
            }
        }

        powerUpManager = new PowerUpManager(gameArea, paddle, border);
        gameLoop = new GameLoop(gameArea, ball, paddle, border, blockArray);
        gameLoop.setPowerUpManager(powerUpManager);

        gameArea.setComponentZOrder(ball, 0);
        for (BlockComponent block : blockList) {
            gameArea.setComponentZOrder(block, 1);
        }
        gameArea.setComponentZOrder(paddle, blockList.size() + 1);
        gameArea.setComponentZOrder(border, blockList.size() + 2);
        if (bgLabel != null) {
            gameArea.setComponentZOrder(bgLabel, blockList.size() + 3);
        }

        gameArea.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent ev) {
                if (gameLoop != null && gameLoop.getState() == State.RUNNING) {
                    int newX = ev.getX() - (paddle.getWidth() / 2);
                    if (newX < area.x) newX = area.x;
                    if (newX + paddle.getWidth() > area.x + area.width) {
                        newX = area.x + area.width - paddle.getWidth();
                    }
                    paddle.setLocation(newX, paddle.getY());
                    paddle.repaint();
                }
            }
        });

        gameArea.revalidate();
        gameArea.repaint();
    }

    private void reiniciarBloques() {
        for (BlockComponent block : blockList) {
            gameArea.remove(block);
        }
        blockList.clear();

        Rectangle area = border.getInnerBounds();
        int rows = blockArray.length;
        int cols = blockArray[0].length;
        int blockHeight = 20;
        int startX = area.x;
        int startY = area.y;

        Random random = new Random();
        String[] blockImages = {
                "/images/block-blue.png", "/images/block-green.png",
                "/images/block-orange.png", "/images/block-yellow.png",
                "/images/block-cyan.png", "/images/block-aqua.png"
        };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x1 = startX + (c * area.width) / cols;
                int x2 = startX + ((c + 1) * area.width) / cols;
                int blockWidth = x2 - x1;

                int bx = x1;
                int by = startY + (r * blockHeight);
                String blockImage = blockImages[random.nextInt(blockImages.length)];
                int durezaAleatoria = random.nextInt(3) + 1;

                BlockComponent block = new BlockComponent(bx, by, blockWidth, blockHeight, "block", blockImage, durezaAleatoria);
                blockArray[r][c] = block;
                blockList.add(block);
                gameArea.add(block);
                gameArea.setComponentZOrder(block, 1);
            }
        }

        if (gameLoop != null) {
            gameLoop.setBlocks(blockArray);
        }
        gameArea.revalidate();
        gameArea.repaint();
    }

    private void iniciarMonitorDeColision() {
        collisionChecker = new Timer(16, e -> {
            if (gameLoop != null) {
                score = gameLoop.getScore();
                int vidasActuales = gameLoop.getLives();

                // Detectar si el jugador perdió una vida
                if (vidasActuales < lives) {
                    lives = vidasActuales;
                    pausarMusica();

                    // Reposicionar paleta a su posición inicial cuando pierde vida
                    if (border != null && paddle != null) {
                        Rectangle area = border.getInnerBounds();
                        int paddleX = area.x + (area.width / 2) - (paddle.getWidth() / 2);
                        int paddleY = area.y + area.height - 40;
                        paddle.setLocation(paddleX, paddleY);
                    }

                    if (powerUpManager != null) {
                        powerUpManager.pause();
                    }

                    if (playButton != null) {
                        playButton.setText("Jugar");
                    }
                }

                actualizarHUDDirecto();

                if (gameLoop.getState() == State.GAME_OVER) {
                    pausarMusica();
                    if (powerUpManager != null) {
                        powerUpManager.pause();
                    }
                    if (playButton != null) {
                        playButton.setText("Jugar");
                    }
                }
            }
        });
        collisionChecker.start();
    }

    private void reanudarOIniciarMusica() {
        try {
            if (clipMusica == null) {
                URL archivoAudio = getClass().getResource("/audio/musica.wav");
                if (archivoAudio != null) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoAudio);
                    clipMusica = AudioSystem.getClip();
                    clipMusica.open(audioStream);
                }
            }
            if (clipMusica != null) {
                clipMusica.setMicrosecondPosition(posicionMusicaActual);
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
                clipMusica.start();
            }
        } catch (Exception e) {
            System.err.println("Error al reproducir audio: " + e.getMessage());
        }
    }

    private void reiniciarMusicaDesdeCero() {
        try {
            if (clipMusica == null) {
                URL archivoAudio = getClass().getResource("/audio/musica.wav");
                if (archivoAudio != null) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoAudio);
                    clipMusica = AudioSystem.getClip();
                    clipMusica.open(audioStream);
                }
            }
            if (clipMusica != null) {
                clipMusica.stop();
                clipMusica.setMicrosecondPosition(0);
                posicionMusicaActual = 0;
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
                clipMusica.start();
            }
        } catch (Exception e) {
            System.err.println("Error al reiniciar audio: " + e.getMessage());
        }
    }

    private void pausarMusica() {
        if (clipMusica != null && clipMusica.isRunning()) {
            posicionMusicaActual = clipMusica.getMicrosecondPosition();
            clipMusica.stop();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameComponentDemoFrame().setVisible(true));
    }
}