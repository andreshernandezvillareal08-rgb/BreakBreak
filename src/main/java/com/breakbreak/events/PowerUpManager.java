package main.java.com.breakbreak.events;

import main.java.com.breakbreak.components.BlockComponent;
import main.java.com.breakbreak.components.PaddleComponent;
import main.java.com.breakbreak.components.PowerUpComponent;
import main.java.com.breakbreak.components.BorderComponent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PowerUpManager {

    private final JPanel gameArea;
    private final PaddleComponent paddle;
    private final BorderComponent border;
    private final List<PowerUpComponent> activePowerUps;
    private final Random random;
    private Timer updateTimer;
    private boolean enPausa = false;

    private static final double DROP_CHANCE = 0.35; // 100% de probabilidad (puedes ajustarla)

    public PowerUpManager(JPanel gameArea, PaddleComponent paddle, BorderComponent border) {
        this.gameArea = gameArea;
        this.paddle = paddle;
        this.border = border;
        this.activePowerUps = new ArrayList<>();
        this.random = new Random();

        iniciarBucleActualizacion();
    }

    public void onBlockDestroyed(BlockComponent block) {
        if (random.nextDouble() <= DROP_CHANCE) {
            PowerUpComponent.Type[] types = PowerUpComponent.Type.values();
            PowerUpComponent.Type randomType = types[random.nextInt(types.length)];

            int x = block.getX() + (block.getWidth() / 2) - 20;
            int y = block.getY() + block.getHeight();

            PowerUpComponent powerUp = new PowerUpComponent(x, y, randomType);

            activePowerUps.add(powerUp);
            gameArea.add(powerUp);
            gameArea.setComponentZOrder(powerUp, 0);

            gameArea.revalidate();
            gameArea.repaint();
        }
    }

    public void clearAll() {
        clear();
    }

    public void clear() {
        for (PowerUpComponent powerUp : activePowerUps) {
            gameArea.remove(powerUp);
        }
        activePowerUps.clear();
        gameArea.revalidate();
        gameArea.repaint();
    }

    public void pause() {
        enPausa = true;
    }

    public void resume() {
        enPausa = false;
    }

    private void iniciarBucleActualizacion() {
        updateTimer = new Timer(20, e -> actualizarPowerUps());
        updateTimer.start();
    }

    private void actualizarPowerUps() {
        if (enPausa || activePowerUps.isEmpty()) return;

        Rectangle innerBounds = border.getInnerBounds();
        Iterator<PowerUpComponent> iterator = activePowerUps.iterator();

        while (iterator.hasNext()) {
            PowerUpComponent powerUp = iterator.next();

            powerUp.fall();

            // Colisión con la paleta
            if (powerUp.getBounds().intersects(paddle.getBounds())) {
                aplicarEfecto(powerUp.getType());
                gameArea.remove(powerUp);
                iterator.remove();
                continue;
            }

            // Si sale del área de juego por abajo
            if (powerUp.getY() > innerBounds.y + innerBounds.height) {
                gameArea.remove(powerUp);
                iterator.remove();
            }
        }

        gameArea.repaint();
    }

    private void aplicarEfecto(PowerUpComponent.Type type) {
        System.out.println("✨ ¡Power-up atrapado: " + type.name() + "!");

        switch (type) {
            case W_PINK:
                // Guardamos el ancho original que tenía la paleta antes de crecer
                int anchoOriginal = paddle.getWidth();
                int nuevoAncho = Math.min(180, anchoOriginal + 30);

                // Aplicamos el tamaño expandido
                paddle.setSize(nuevoAncho, paddle.getHeight());

                // Creamos un temporizador de 2000 ms (2 segundos) que se ejecuta una sola vez
                Timer timerEfecto = new Timer(3000, e -> {
                    // Restauramos la paleta a su ancho original
                    paddle.setSize(anchoOriginal, paddle.getHeight());
                    gameArea.revalidate();
                    gameArea.repaint();
                });
                timerEfecto.setRepeats(false); // Aseguramos que solo ocurra una vez
                timerEfecto.start();
                break;

            case L_RED:
                break;
            case E_BLUE:
                break;
            default:
                break;
        }

        gameArea.revalidate();
        gameArea.repaint();
    }
}