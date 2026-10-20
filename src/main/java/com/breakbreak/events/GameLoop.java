package main.java.com.breakbreak.events;

import main.java.com.breakbreak.components.*;
import main.java.com.breakbreak.enums.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLoop implements ActionListener {

    private static final int FPS = 60;
    private static final int DELAY_MS = 1000 / FPS;

    private static final int INITIAL_VX = 3;
    private static final int INITIAL_VY = -3;

    private final JComponent host;
    private final PaddleComponent paddle;
    private final BallComponent ball;
    private final BorderComponent border;

    private final Timer timer;

    private State state = State.READY;
    private int vx = INITIAL_VX;
    private int vy = INITIAL_VY;

    public GameLoop(JComponent host, BallComponent ball, PaddleComponent paddle,
                    BorderComponent border, BlockComponent[][] blocks) {
        this.host = host;
        this.ball = ball;
        this.border = border;
        this.paddle = paddle;
        this.timer = new Timer(DELAY_MS, this);
        this.timer.setCoalesce(true);
    }

    public State getState() {
        return state;
    }

    public void start() {
        // Permitimos arrancar también si está en GAME_OVER para reiniciar con el botón
        if (state == State.READY || state == State.PAUSED || state == State.GAME_OVER) {
            state = State.RUNNING;
            vx = INITIAL_VX;
            vy = INITIAL_VY;
            timer.start();
        }
    }

    public void pause() {
        if (state == State.RUNNING) {
            state = State.PAUSED;
            timer.stop();
        }
    }

    public void reset() {
        timer.stop();
        state = State.READY;
        vx = INITIAL_VX;
        vy = INITIAL_VY;
        host.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    private void update() {
        if (state != State.RUNNING) return;

        int size = ball.getWidth();
        Rectangle area = border.getInnerBounds();

        int nextX = ball.getX() + vx;
        int nextY = ball.getY() + vy;

        if (nextX <= area.x) {
            nextX = area.x;
            vx = Math.abs(vx);
        } else if (nextX + size >= area.x + area.width) {
            nextX = area.x + area.width - size; // Operador menos restaurado
            vx = -Math.abs(vx);
        }

        if (nextY <= area.y) {
            nextY = area.y;
            vy = Math.abs(vy);
        }

        ball.setLocation(nextX, nextY);

        if (vy > 0 && ball.getBounds().intersects(paddle.getBounds())) {
            int ballCenterX = ball.getX() + size / 2;
            int paddleCenterX = paddle.getX() + paddle.getWidth() / 2;
            int offset = ballCenterX - paddleCenterX;
            vx = offset / 6;
            if (vx == 0) vx = INITIAL_VX;
            vy = -Math.abs(vy);
            ball.setY(paddle.getY() - size); // Operador menos restaurado
        }

        // Si la pelota toca el fondo inferior
        if (ball.getY() + size >= area.y + area.height) {
            state = State.GAME_OVER;
            timer.stop();
        }

        host.repaint();
    }
}