import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final int UNIT_SIZE = 25;
    static final int NUMBER_OF_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    final int x[] = new int[NUMBER_OF_UNITS];
    final int y[] = new int[NUMBER_OF_UNITS];

    int length;
    int foodEaten;
    int foodX;
    int foodY;
    char direction;
    boolean running = false;
    boolean paused = false; // Added pause state
    Timer timer;
    Random random;
    JButton restartButton;

    public SnakeGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        // Configure the restart button
        restartButton = new CustomButton("Restart");
        restartButton.setBounds(185, 330, 155, 50);  // Set button position and size
        restartButton.setFont(new Font("Poppins", Font.PLAIN, 15));  // Set font
        restartButton.setBackground(new Color(12, 129, 238));  // Set background color
        restartButton.setForeground(Color.WHITE);  // Set text color
        restartButton.setFocusable(false);
        restartButton.setVisible(false);  // Hide the button initially
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();  // Reset the game when restart button is clicked
            }
        });

        this.setLayout(null);  // Disable layout for custom positioning
        this.add(restartButton);  // Add restart button to panel

        startGame();
    }

    public void startGame() {
        length = 5;
        foodEaten = 0;
        direction = 'D';
        running = true;
        paused = false;  // Reset pause state when the game starts

        // Initialize snake's starting position
        for (int i = 0; i < length; i++) {
            x[i] = 100 - (i * UNIT_SIZE);
            y[i] = 100;
        }

        addFood();
        timer = new Timer(150, this);  // Snake moves every 150ms
        timer.start();
    }

    public void resetGame() {
        timer.stop();
        startGame();
        restartButton.setVisible(false); // Hide the restart button during gameplay
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            drawGrid(g);  // Draw the grid if the game is running
            draw(g);      // Draw the game elements
        } else {
            gameOver(g);  // Show the Game Over screen without the grid
        }
    }

    public void drawGrid(Graphics g) {
        g.setColor(new Color(100, 100, 100));  // Grid line color
        // Draw vertical lines
        for (int i = 0; i < WIDTH; i += UNIT_SIZE) {
            g.drawLine(i, 0, i, HEIGHT);
        }
        // Draw horizontal lines
        for (int i = 0; i < HEIGHT; i += UNIT_SIZE) {
            g.drawLine(0, i, WIDTH, i);
        }
    }

    public void draw(Graphics g) {
        g.setColor(new Color(210, 115, 90));
        g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

        for (int i = 0; i < length; i++) {
            if (i == 0) {
                g.setColor(Color.WHITE);  // Head of the snake
            } else {
                g.setColor(new Color(40, 200, 150));  // Body of the snake
            }
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Sans serif", Font.BOLD, 25));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, g.getFont().getSize());

        if (paused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Sans serif", Font.BOLD, 40));
            g.drawString("Paused", (WIDTH - metrics.stringWidth("Paused")) / 2, HEIGHT / 2);
        }
    }

    public void addFood() {
        foodX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = length; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    public void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            length++;
            foodEaten++;
            addFood();
        }
    }

    public void checkCollision() {
        // Check if head collides with body
        for (int i = length; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check if head collides with walls
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Sans serif", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Sans serif", Font.BOLD, 25));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, g.getFont().getSize());

        restartButton.setVisible(true);  // Show the restart button after game over
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {  // Only move if the game is running and not paused
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:  // Space bar to toggle pause
                    paused = !paused;
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        SnakeGame gamePanel = new SnakeGame();

        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// Custom button class for consistent button styling
class CustomButton extends JButton {
    public CustomButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
    }
}
