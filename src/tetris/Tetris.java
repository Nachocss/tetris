/*@Documentation
* Los métodos nuevos o modificados marcados como 'aqui'
* Controles: 
*   R - Restart
*   C/V - Caída libre
*   Espacio - Caída rápida
*   Flechas - Mover y rotar
*/

/* Cambios:
* Aumento gradual de velocidad segun la puntuación
* Correción de la rotación de las fichas 'L' y 'J'
* Botón de Restart
* Mensaje de GAME OVER (como .png)
* Mejoras en el sistema de caída de piezas (dropdowns)
*con caída directa y caída rápida.
* https://javaconceptoftheday.com/how-to-sort-a-text-file-in-java/ sort highscore
*/
package tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.ImageIcon;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JPanel {

	private static final long serialVersionUID = -8715353373678321308L;

	private final Point[][][] Tetraminos = {
			// I-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
			},
			
			// J-Piece
			{
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
                                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },			
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) },
                                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) }

                        },
			// L-Piece
			{
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
                            		{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) },
                                        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) }
			},                        
                        
			// O-Piece
			{
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
			},
			
			// S-Piece
			{
				{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
				{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
			},
			
			// T-Piece
			{
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
			},
			
			// Z-Piece
			{
				{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
				{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
			}
	};
	
	private final Color[] tetraminoColors = {
		Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
	};
	
	private Point pieceOrigin;
	private int currentPiece;
	private int rotation;
	private ArrayList<Integer> nextPieces = new ArrayList<Integer>();

	private long score = 0;
	private Color[][] well;
	
	// Creates a border around the well and initializes the dropping piece
	private void init() {
            
                score = 0;
            
		well = new Color[12][24];
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				if (i == 0 || i == 11 || j == 22) {
					well[i][j] = Color.GRAY;
				} else {
					well[i][j] = Color.BLACK;
				}
			}
		}	//aqui, NO VA   
		if( !collidesAt(5,2,1))newPiece(); 

	}

	// Put a new, random piece into the dropping position
	public void newPiece() {
            
        	pieceOrigin = new Point(5, 2);
		rotation = 0;
		if (nextPieces.isEmpty()) {
			Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
			Collections.shuffle(nextPieces);
		}
		currentPiece = nextPieces.get(0);
		nextPieces.remove(0);
                
                
	}
	
	// Collision test for the dropping piece
	private boolean collidesAt(int x, int y, int rotation) {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			if (well[p.x + x][p.y + y] != Color.BLACK) {
				return true;
			}
		}
		return false;
	}
	
	// Rotate the piece clockwise or counterclockwise
	public void rotate(int i) {
		int newRotation = (rotation + i) % 4;
		if (newRotation < 0) {
			newRotation = 3;
		}
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
			rotation = newRotation;
		}
		repaint();
	}
	
	// Move the piece left or right
	public void move(int i) {
		if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
			pieceOrigin.x += i;	
		}
		repaint();
	}
	
	// Drops the piece one line or fixes it to the well if it can't drop
	public void dropDown() {
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		} else {
			fixToWell();
		}	
		repaint();
	}
	//aqui Los 2 siguientes metodos son propios
	public void fastDropDown() {
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 3, rotation)) {
			pieceOrigin.y += 3;
		}
                else if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 2, rotation)) {
			pieceOrigin.y += 2;
		}                
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		}                
                else {
			fixToWell();
		}	
		repaint();
	}
       
        public void fullDropDown() throws IndexOutOfBoundsException{
            
            int n = 0;
            
            for( int i = n; i < well[1].length; i++ ){
                
                if (!collidesAt(pieceOrigin.x, pieceOrigin.y+i, rotation)){
                    n++;
                }               
                else if(collidesAt(pieceOrigin.x, pieceOrigin.y+i, rotation)){
                    break;
                }
            }
            
            pieceOrigin.y += (n-1);
                if(collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)){
                    fixToWell();
                }
                repaint();            
            
        }
        
        
        
        
	// Make the dropping piece part of the well, so it is available for
	// collision detection.
	public void fixToWell() {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
		}
		clearRows();
		newPiece();
	}
	
	public void deleteRow(int row) {
		for (int j = row-1; j > 0; j--) {
			for (int i = 1; i < 11; i++) {
				well[i][j+1] = well[i][j];
			}
		}
	}
	
	// Clear completed rows from the field and award score according to
	// the number of simultaneously cleared rows.
	public void clearRows() {
		boolean gap;
		int numClears = 0;
		
		for (int j = 21; j > 0; j--) {
			gap = false;
			for (int i = 1; i < 11; i++) {
				if (well[i][j] == Color.BLACK) {
					gap = true;
					break;
				}
			}
			if (!gap) {
				deleteRow(j);
				j += 1;
				numClears += 1;
			}
		}
		
		switch (numClears) {
		case 1:
			score += 100;
			break;
		case 2:
			score += 300;
			break;
		case 3:
			score += 500;
			break;
		case 4:
			score += 800;
			break;
		}
	}
	
	// Draw the falling piece
	private void drawPiece(Graphics g) {		
		g.setColor(tetraminoColors[currentPiece]);
		for (Point p : Tetraminos[currentPiece][rotation]) {
			g.fillRect((p.x + pieceOrigin.x) * 26, 
					   (p.y + pieceOrigin.y) * 26, 
					   25, 25);
		}
	}
	
	@Override 
	public void paintComponent(Graphics g)
	{
		// Paint the well
		g.fillRect(0, 0, 26*12, 26*23);
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				g.setColor(well[i][j]);
				g.fillRect(26*i, 26*j, 25, 25);
			}
		}
		
		// Display the score
		g.setColor(Color.WHITE);
		g.drawString("" + score, 19*12, 25);
		
		// Draw the currently falling piece
		drawPiece(g);
                
                //aqui
                    Image imagenInterna = new ImageIcon(
                    getClass().getResource("gameover.png")
                    ).getImage();                
                
                
                if( collidesAt(5,2,1)){
                    g.drawString("Press R to restart", 30,20);
                    //System.exit(1);
                    

                    
                    g.drawImage(imagenInterna, 84, 50, Color.black, this);
                    
                }
                
                
                
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Tetris");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(12*26+10, 26*23+25);
		f.setVisible(true);
		
		final Tetris game = new Tetris();
		game.init();
		f.add(game);
		
		// Keyboard controls
		f.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					game.rotate(-1);
					break;
				case KeyEvent.VK_DOWN:
					game.rotate(+1);
					break;
				case KeyEvent.VK_LEFT:
					game.move(-1);
					break;
				case KeyEvent.VK_RIGHT:
					game.move(+1);
					break;
				case KeyEvent.VK_SPACE:
					game.fastDropDown();
					game.score += 1;
					break;
                                        
                                //aqui restart
                                case KeyEvent.VK_R:
                                    	game.init();
                                        f.add(game);
                                        break;
                                //aqui fulldropdown
                                case KeyEvent.VK_C:
                                        game.fullDropDown();
                                        break;
                                // correctDropdown        
                                case KeyEvent.VK_V:
                                        game.fullDropDown();
                                        break;
                                }
			}
			
			public void keyReleased(KeyEvent e) {
			}
		});
		
		// Make the falling piece drop every second
		new Thread() {
			@Override public void run() {                             
                           while(true){     
                                if( game.score < 600){
                                	try {
                                            Thread.sleep(500);
                                            game.dropDown();
                                            } catch ( InterruptedException e ) {}                                      
                                }
                                else if( game.score < 1300){
                                	try {
                                            Thread.sleep(410);
                                            game.dropDown();
                                            } catch ( InterruptedException e ) {}                                      
                                }                                
                                else if( game.score < 1900){
                                	try {
                                            Thread.sleep(290);
                                            game.dropDown();
                                            } catch ( InterruptedException e ) {}                                      
                                }                                 
                                else if( game.score < 2600){
                                	try {
                                            Thread.sleep(200);
                                            game.dropDown();
                                            } catch ( InterruptedException e ) {}                                      
                                }                                 
                                else if( game.score < 3200){
                                	try {
                                            Thread.sleep(150);
                                            game.dropDown();
                                            } catch ( InterruptedException e ) {}                                      
                                }
                                else if( game.score < 4000){
                                	try {
                                            Thread.sleep(100);
                                            game.dropDown();
                                            } catch ( InterruptedException e ) {}                                      
                                }    
                                else {
                                	try {
                                            Thread.sleep(50);
                                            game.dropDown();
                                            } catch ( InterruptedException e ) {}                                      
                                }                                
                           }
			}
		}.start();
	}
}




// TIENE BUGS
    /*    public void fullDropDown() throws IndexOutOfBoundsException{

                for( int i = well[1].length-1; i > 0; i-- ){
                    
                    try{
                    if (!collidesAt(pieceOrigin.x, pieceOrigin.y+i, rotation)){
                        pieceOrigin.y += i;
                    }
                    }
                    
                    catch(IndexOutOfBoundsException ex){
                        i -= 5;
                    }
                }
                
                if(collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)){
                    fixToWell();
                }
                repaint();
        }*/

                          
			/*	STANDART DE VELOCIDAD
                                while (true){
					try {
						Thread.sleep(500);
						game.dropDown();
					} catch ( InterruptedException e ) {}
				}*/
                        /*      AUMENTO DE VELOCIDAD POR TIEMPO        
                                for( int i = 0; i < 10; i++){
                                					try {
						Thread.sleep(500);
						game.dropDown();
					} catch ( InterruptedException e ) {}    
                                }
                                
                                for( int i = 0; i < 10; i++){
                                					try {
						Thread.sleep(200);
						game.dropDown();
					} catch ( InterruptedException e ) {}    
                                }  */ 