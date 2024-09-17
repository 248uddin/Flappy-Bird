import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //images
    Image backgroundImg;
    Image birdimg;
    Image topipImage;
    Image BttmpipImage;

    //Bird dets
    int birdX = boardWidth/4;
    int birdY = boardHeight/4;
    int birdWidth = 34;
    int birdHeight = 24; 

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }

    }
    //pipes information
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;   //scaled by 1/6
    int pipeHeight = 512;

    class pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false; 

        pipe(Image img){
            this.img = img;
        }
    }

    //logic of the bird and timer
    Bird bird;
    int velocityX = -4;// pipes speed to left
    int velocityY = 0;// move bird up and down        
    int gravity = 1; 

    ArrayList<pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0; 

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load image
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdimg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topipImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        BttmpipImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        
        //bird
        bird = new Bird(birdimg);
        pipes = new ArrayList<pipe>();

        //pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        }); 
        placePipesTimer.start();

        //timer
        gameLoop = new Timer(1000/60, this); //1000/60 = 16.6
        gameLoop.start();

    }
    public void placePipes() {
        int randompipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;               

        pipe toppipe = new pipe(topipImage);
        toppipe.y = randompipeY;
        pipes.add(toppipe);

        pipe bttmpipe = new pipe(BttmpipImage);
        bttmpipe.y = toppipe.y + pipeHeight + openingSpace;
        pipes.add(bttmpipe);

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i = 0; i < pipes.size(); i++) {
            pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
         //score
         g.setColor(Color.white);
         g.setFont(new Font("Arial", Font.PLAIN, 32));
         if(gameOver) {
             g.drawString("Game Over: " + String.valueOf((int)score), 10, 35);
         }

         else{
             g.drawString(String.valueOf((int)score), 10, 35);
         }
    }

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++){
            pipe pipe = pipes.get(i); 
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; //0.5 bc 2 pipes per screen
            }

            if (crash(bird, pipe)) {
                gameOver = true;
            }
        }

        if(bird.y > boardHeight) {
            gameOver = true;
        }

    }

    public boolean crash(Bird a, pipe b) {
        return a.x < b.x + b.width &&
             a.x + a.width > b.x &&
             a.y < b.y + b.height && 
             a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); 
        if (gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                //restart the game defaulting 
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    @Override
    public void keyTyped(KeyEvent e) {
        
    }
}
