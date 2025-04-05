import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int GAME_SPEED = 100;  // milliseconds
    private static final int INITIAL_LENGTH = 3;

    private LinkedList<Point> snake;  // The snake's body
    private Point food;  // Food location
    private char direction = 'R';  // Initial direction (Right)
    private boolean gameOver = false;
    private Timer timer;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) return;

                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP && direction != 'D') {
                    direction = 'U';
                } else if (keyCode == KeyEvent.VK_DOWN && direction != 'U') {
                    direction = 'D';
                } else if (keyCode == KeyEvent.VK_LEFT && direction != 'R') {
                    direction = 'L';
                } else if (keyCode == KeyEvent.VK_RIGHT && direction != 'L') {
                    direction = 'R';
                }
            }
        });

        // Initialize the snake and food
        snake = new LinkedList<>();
        for (int i = INITIAL_LENGTH - 1; i >= 0; i--) {
            snake.add(new Point(i, 0));
        }

        spawnFood();

        // Start the game timer
        timer = new Timer(GAME_SPEED, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            return;
        }

        // Move the snake
        Point head = snake.getFirst();
        Point newHead = null;

        switch (direction) {
            case 'U':
                newHead = new Point(head.x, head.y - 1);
                break;
            case 'D':
                newHead = new Point(head.x, head.y + 1);
                break;
            case 'L':
                newHead = new Point(head.x - 1, head.y);
                break;
            case 'R':
                newHead = new Point(head.x + 1, head.y);
                break;
        }

        // Check for collisions with boundaries
        if (newHead.x < 0 || newHead.x >= WIDTH / TILE_SIZE || newHead.y < 0 || newHead.y >= HEIGHT / TILE_SIZE || snake.contains(newHead)) {
            gameOver = true;
            repaint();
            return;
        }

        // Move the snake
        snake.addFirst(newHead);

        // Check if the snake ate the food
        if (newHead.equals(food)) {
            spawnFood();  // Generate new food
        } else {
            snake.removeLast();  // Remove the tail if no food was eaten
        }

        repaint();  // Redraw the screen
    }

    private void spawnFood() {
        Random rand = new Random();
        int x = rand.nextInt(WIDTH / TILE_SIZE);
        int y = rand.nextInt(HEIGHT / TILE_SIZE);

        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over! Press R to Restart", 200, HEIGHT / 2);
            return;
        }

        // Draw the snake
        g.setColor(Color.GREEN);
        for (Point segment : snake) {
            g.fillRect(segment.x * TILE_SIZE, segment.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Draw the food
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Restart the game when 'R' is pressed
        game.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R && game.gameOver) {
                    game.restartGame();
                }
            }
        });
    }

    // Restart the game
    private void restartGame() {
        snake.clear();
        for (int i = INITIAL_LENGTH - 1; i >= 0; i--) {
            snake.add(new Point(i, 0));
        }
        direction = 'R';
        gameOver = false;
        spawnFood();
        timer.start();
        repaint();
    }
}
