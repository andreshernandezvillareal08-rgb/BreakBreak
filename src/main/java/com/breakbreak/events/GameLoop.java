package main.java.com.breakbreak.events;

import main.java.com.breakbreak.components.*;
import main.java.com.breakbreak.enums.State;

import javax.swing.*;
import java.awt.*;

public class GameLoop implements Runnable {
    private final JPanel gameArea;
    private final BallComponent ball;
    private final PaddleComponent paddle;
    private final BorderComponent border;
    private BlockComponent[][] blocks;
    private PowerUpManager powerUpManager;

    private State state = State.READY;
    private Thread thread;
    private int score = 0;
    private int lives = 3;

    public GameLoop(JPanel gameArea, BallComponent ball, PaddleComponent paddle, BorderComponent border, BlockComponent[][] blocks) {
        this.gameArea = gameArea;
        this.ball = ball;
        this.paddle = paddle;
        this.border = border;
        this.blocks = blocks;
    }

    public void setPowerUpManager(PowerUpManager powerUpManager) {
        this.powerUpManager = powerUpManager;
    }

    public State getState() { return state; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public void setBlocks(BlockComponent[][] blocks) { this.blocks = blocks; }

    public void start() {
        if (state == State.RUNNING) return;
        state = State.RUNNING;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        state = State.PAUSED;
    }

    public void reset() {
        state = State.READY;
        score = 0;
        lives = 3;
    }

    @Override
    public void run() {
        while (state == State.RUNNING) {
            update();
            gameArea.repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void update() {
        if (state != State.RUNNING) return;

        // Actualización de posición de la pelota usando sus velocidades (vx, vy)
        ball.setLocation(ball.getX() + ball.getVx(), ball.getY() + ball.getVy());

        Rectangle area = border.getInnerBounds();
        Rectangle ballBounds = ball.getBounds();

        // 1. Colisión con paredes izquierda y derecha
        if (ballBounds.x <= area.x) {
            ball.setLocation(area.x, ballBounds.y);
            ball.setVx(-ball.getVx());
        } else if (ballBounds.x + ballBounds.width >= area.x + area.width) {
            ball.setLocation(area.x + area.width - ballBounds.width, ballBounds.y);
            ball.setVx(-ball.getVx());
        }

        // 2. Colisión con el techo
        if (ballBounds.y <= area.y) {
            ball.setLocation(ballBounds.x, area.y);
            ball.setVy(-ball.getVy());
        }

        // 3. Colisión con el suelo (Pierde una vida)
        if (ballBounds.y + ballBounds.height >= area.y + area.height) {
            lives--;
            if (lives <= 0) {
                state = State.GAME_OVER;
            } else {
                ball.setLocation(area.x + (area.width / 2) - (ball.getWidth() / 2), area.y + 380);
                ball.setVx(3);
                ball.setVy(-3);
                state = State.READY;
            }
            return;
        }

        // 4. Colisión con la paleta (Paddle)
        if (ballBounds.intersects(paddle.getBounds())) {
            ball.setLocation(ballBounds.x, paddle.getY() - ballBounds.height - 1);

            int paddleCenter = paddle.getX() + paddle.getWidth() / 2;
            int ballCenter = ballBounds.x + ballBounds.width / 2;
            int offset = ballCenter - paddleCenter;
            double hitFactor = (double) offset / (paddle.getWidth() / 2);

            int newVx = (int) (hitFactor * 5);
            if (newVx == 0) newVx = 1;
            ball.setVx(newVx);
            ball.setVy(-Math.abs(ball.getVy()));
        }

        // 5. Colisión con los bloques
        if (blocks != null) {
            outerLoop:
            for (int r = 0; r < blocks.length; r++) {
                for (int c = 0; c < blocks[r].length; c++) {
                    BlockComponent block = blocks[r][c];
                    if (block != null && block.isVisible() && ballBounds.intersects(block.getBounds())) {
                        Rectangle blockBounds = block.getBounds();

                        int overlapLeft = (ballBounds.x + ballBounds.width) - blockBounds.x;
                        int overlapRight = (blockBounds.x + blockBounds.width) - ballBounds.x;
                        int overlapTop = (ballBounds.y + ballBounds.height) - blockBounds.y;
                        int overlapBottom = (blockBounds.y + blockBounds.height) - ballBounds.y;

                        boolean minLeftRight = Math.min(overlapLeft, overlapRight) < Math.min(overlapTop, overlapBottom);

                        if (minLeftRight) {
                            if (overlapLeft < overlapRight) {
                                ball.setLocation(blockBounds.x - ballBounds.width - 1, ballBounds.y);
                            } else {
                                ball.setLocation(blockBounds.x + blockBounds.width + 1, ballBounds.y);
                            }
                            ball.setVx(-ball.getVx());
                        } else {
                            if (overlapTop < overlapBottom) {
                                ball.setLocation(ballBounds.x, blockBounds.y - ballBounds.height - 1);
                            } else {
                                ball.setLocation(ballBounds.x, blockBounds.y + blockBounds.height + 1);
                            }
                            ball.setVy(-ball.getVy());
                        }

                        block.hit();
                        if (block.isDestroyed()) {
                            score += 100;

                            // LLAMADA AL GESTOR DE POWER-UPS AL ROMPERSE EL BLOQUE:
                            if (powerUpManager != null) {
                                powerUpManager.onBlockDestroyed(block);
                            }

                            gameArea.remove(block);
                            blocks[r][c] = null;
                        }

                        gameArea.revalidate();
                        gameArea.repaint();
                        break outerLoop;
                    }
                }
            }
        }
    }
}