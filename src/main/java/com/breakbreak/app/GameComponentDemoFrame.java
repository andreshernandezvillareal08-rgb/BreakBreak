package main.java.com.breakbreak.app;

import main.java.com.breakbreak.components.*;
import main.java.com.breakbreak.enums.State;
import main.java.com.breakbreak.events.GameLoop;

import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.net.URL;

public class GameComponentDemoFrame extends JFrame {

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JPanel gameArea;
    private JPanel bottomPanel;
    private JLabel scoreLabel;
    private JButton playButton;
    private JButton exitButton;

    private Clip clipMusica;
    private BallComponent ball;
    private PaddleComponent paddle;
    private BorderComponent border;
    private GameLoop gameLoop;
    private Timer collisionChecker;

    private int initialBallX;
    private int initialBallY;

    public GameComponentDemoFrame() {
        setContentPane(mainPanel);
        setTitle("BreakBreak — Laboratorio de Componentes Visuales");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel.setBackground(new Color(24, 28, 36));
        if (titleLabel != null) {
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

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
                        Rectangle area = border.getInnerBounds();

                        // Si viene de perder o está en game over, reposicionamos la bola
                        if (currentState == State.GAME_OVER || ball.getY() >= area.y + area.height - 40) {
                            ball.setLocation(initialBallX, initialBallY);
                        }

                        gameLoop.start();
                        reproducirMusicaWav();
                    }
                }
            });
        }
    }

    private void cargarElementosJuego() {
        gameArea.setLayout(null);
        gameArea.setOpaque(true);

        // 1. Borde del juego con ancho ajustado (584px)
        border = new BorderComponent(new Rectangle(0, 0, 584, 520));
        gameArea.add(border);

        Rectangle area = border.getInnerBounds();

        // 2. Pelota
        int ballSize = 30;
        initialBallX = area.x + (area.width / 2) - (ballSize / 2);
        initialBallY = area.y + 100;
        ball = new BallComponent(initialBallX, initialBallY, ballSize, ballSize);
        gameArea.add(ball);

        // 3. Raqueta (Paddle)
        int paddleWidth = 120;
        int paddleHeight = 25;
        int paddleX = area.x + (area.width / 2) - (paddleWidth / 2);
        int paddleY = area.y + area.height - 40;

        paddle = new PaddleComponent(paddleX, paddleY, paddleWidth, paddleHeight, "paddle", "/images/paddle.png");
        gameArea.add(paddle);

        // 4. Imagen de fondo
        URL imgUrl = getClass().getResource("/images/background.png");
        JLabel bgLabel = null;
        if (imgUrl != null) {
            ImageIcon bgIcon = new ImageIcon(imgUrl);
            bgLabel = new JLabel(bgIcon);
            bgLabel.setBounds(0, 0, 584, 520);
            gameArea.add(bgLabel);
        } else {
            System.err.println("No se encontró el archivo /images/background.png en resources.");
        }

        // 5. Establecer orden de capas (Z-Order): Pelota al frente, Raqueta, Borde y Fondo al final
        gameArea.setComponentZOrder(ball, 0);
        gameArea.setComponentZOrder(paddle, 1);
        gameArea.setComponentZOrder(border, 2);
        if (bgLabel != null) {
            gameArea.setComponentZOrder(bgLabel, 3);
        }

        // 6. Inicialización del GameLoop
        gameLoop = new GameLoop(gameArea, ball, paddle, border, null);

        // 7. Movimiento con ratón
        gameArea.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent ev) {
                int newX = ev.getX() - (paddle.getWidth() / 2);
                if (newX < area.x) {
                    newX = area.x;
                }
                if (newX + paddle.getWidth() > area.x + area.width) {
                    newX = area.x + area.width - paddle.getWidth();
                }
                paddle.setLocation(newX, paddle.getY());
            }
        });

        gameArea.revalidate();
        gameArea.repaint();
    }

    private void iniciarMonitorDeColision() {
        collisionChecker = new Timer(16, e -> {
            if (gameLoop != null && gameLoop.getState() == State.GAME_OVER) {
                detenerMusica();
            }
        });
        collisionChecker.start();
    }

    private void reproducirMusicaWav() {
        try {
            if (clipMusica != null && clipMusica.isRunning()) {
                return;
            }
            if (clipMusica != null) {
                clipMusica.setFramePosition(0);
                clipMusica.start();
                return;
            }
            URL archivoAudio = getClass().getResource("/audio/musica.wav");
            if (archivoAudio != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoAudio);
                clipMusica = AudioSystem.getClip();
                clipMusica.open(audioStream);
                clipMusica.loop(Clip.LOOP_CONTINUOUSLY);
                clipMusica.start();
            } else {
                System.err.println("No se encontró el archivo /audio/musica.wav en resources.");
            }
        } catch (Exception e) {
            System.err.println("Error al reproducir audio: " + e.getMessage());
        }
    }

    public void detenerMusica() {
        if (clipMusica != null) {
            if (clipMusica.isRunning()) {
                clipMusica.stop();
            }
            clipMusica.setFramePosition(0);
            clipMusica.flush();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameComponentDemoFrame().setVisible(true));
    }
}