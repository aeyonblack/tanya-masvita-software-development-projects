import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * This program allows two users to play
 * checkers against each other
 *
 * The challenge is defining in a robust, logical
 * and mathematical way the rules of checkers
 *
 * Unlike mobile or web development where UI elements
 * are specified using a mark up language like html/css (web)
 * or xml (android) the user interface is manually drawn
 * by making use of the Java swing GUI library which poses more challenges
 *
 * The program fully implements object oriented design
 * principles but defines nested classes
 * for simplicity since this is a relatively small project
 *
 * The "main" function serves as the entry point to the program
 */
public class Checkers extends JPanel{

    /**
     * Entry point of the program
     * Opens the checkers window panel
     * Ensures program is run as standalone
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("Checkers");
        Checkers content = new Checkers();
        window.setContentPane(content);
        window.pack();
        window.setLocation(100,100);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);
    }

    /*-----------------------------END MAIN--------------------------------*/

    /*Starts new game when clicked*/
    private JButton newGameButton;

    /*Exits game when clicked*/
    private JButton quitGameButton;

    /*For displaying messages to the user*/
    private JLabel message;

    /**
     * Creates and manages the board with all its components
     * Uses a null layout for manual layout definition
     * Sets bounds for layout elements
     */
    public Checkers() {

        /*for manual layout definition - Define custom layout*/
        setLayout(null);

        /*All layout definitions and bounds depend on this dimension*/
        setPreferredSize(new Dimension(350,250));

        setBackground(new Color(0,150,0));

        /*Create board and its components*/

        Board board = new Board();

        add(board);
        add(newGameButton);
        add(quitGameButton);
        add(message);

        board.setBounds(20,20,164,164);
        newGameButton.setBounds(210, 60, 120,30);
        quitGameButton.setBounds(210,120,120,30);
        message.setBounds(0,200,350,30);

    }

    /*----------------------------------NESTED CLASSES------------------------------------*/

    /**
     * The CheckersData class is not a database but a simple state manager
     * It keeps track of all game states and events and responds accordingly
     * It knows what kind of piece is on each square of the checkerboard
     * and it computes available legal moves
     */
    private static class CheckersData {

        /*These constants represent all possible contents of a single square
        * on the checkerboard where RED and BLACK are player pieces*/
        
        static final int
                    EMPTY = 0,
                    RED = 1,
                    RED_KING = 2,
                    BLACK = 3,
                    BLACK_KING = 4;
        
        final int BOARD_SIZE = 8;

        /*Board has rows and columns where board[row][column] are
        * the contents of the board at position (row,column) */
        int[][] board;

        CheckersData(){
            board = new int[BOARD_SIZE][BOARD_SIZE];
            setupGame();
        }

        /**
         * Setup the board with checkers in position for
         * the beginning of a game.
         *
         * Checkers will be placed in squares/positions that satisfy
         * row % 2 == col % 2
         *
         * A correct setup requires that all such squares in the first
         * three rows contain black checkers and all such squares in
         * the last three rows contain red checkers
         */
        void setupGame() {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (row % 2 == col % 2) {
                        if (row < 3) {
                            board[row][col] = BLACK;
                        } else if (row > 4) {
                            board[row][col] = RED;
                        } else {
                            board[row][col] = EMPTY;
                        }
                    }
                }
            }
        }

        /**
         * Return the contents in the square at position (row,col)
         * @param row the row to check
         * @param col the column to check
         * @return contents of square
         */
        int pieceAt(int row, int col) {
            return board[row][col];
        }

        /**
         * Make specified move
         * @param move - legal move in checkers
         */
        void makeMove(CheckersMove move) {
            makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
        }

        /**
         * Make a move from the square in position (fromRow, fromCol)
         * to the one in position (toRow,toCol)
         * If the move is a jump, the jumped piece is removed from
         * the board.
         * If the piece moves to the last row of the opponent's side
         * The piece becomes a king
         * @param fromRow current row
         * @param fromCol current column
         * @param toRow target row
         * @param toCol target column
         */
        void makeMove(int fromRow, int fromCol, int toRow, int toCol) {

            /*IMPORTANT - Set the contents of the square at position (toRow, toCol)
            * to whatever is in the square at position (fromRow, fromCol) assuming
            * it's not empty. In other words move the piece.*/
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = EMPTY;

            if (fromRow - toRow == 2 || fromRow - toRow == -2) {
                // Remove the jumped piece from the board
                int jumpRow = (fromRow + toRow)/2; // Row of jumped piece
                int jumpCol = (fromCol + toCol)/2; // Column of the jumped piece
                board[jumpRow][jumpCol] = EMPTY;
            }
            if (toRow == 0 && board[toRow][toCol] == RED)
                board[toRow][toCol] = RED_KING;
            if (toRow == 7 && board[toRow][toCol] == BLACK)
                board[toRow][toCol] = BLACK_KING;
        }

        /**
         * Computes possible legal moves available to the player
         * and stores them in an array
         * @param player - the player piece
         * @return array of legal moves available to the player
         */
        CheckersMove[] getLegalMoves(int player) {
            if (player != RED && player != BLACK)
                return null;

            int playerKing;

            if (player == RED)
                playerKing = RED_KING;
            else
                playerKing = BLACK_KING;

            ArrayList<CheckersMove> moves = new ArrayList<>();

            /*This is almost a standard linear search with quadratic running time,
            * which should be fast enough given the size of the data to look at (8X8).
            * The goal is to look for a checker piece in each square on the
            * board and determine possible jump moves from all possible directions*/

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (canJump(player, row, col, row+1,col+1, row+2,col+2))
                            moves.add(new CheckersMove(row, col, row+2, col+2));
                        if (canJump(player, row, col, row-1,col+1,row-2, col+2))
                            moves.add(new CheckersMove(row,col,row-2,col+2));
                        if (canJump(player, row,col, row+1, col-1, row+2,col-2))
                            moves.add(new CheckersMove(row, col, row+2,col-2));
                        if (canJump(player, row, col, row-1, col-1, row-2,col-2))
                            moves.add(new CheckersMove(row,col,row-2,col-2));
                    }
                }
            }

            /*  If any jump moves were found, then the user must jump, so we don't
             add any regular moves.  However, if no jumps were found, check for
             any legal regular moves.  Look at each square on the board.
             If that square contains one of the player's pieces, look at a possible
             move in each of the four directions from that square.  If there is
             a legal move in that direction, put it in the moves ArrayList.
             */

            if (moves.size() == 0) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if (board[row][col] == player || board[row][col] == playerKing) {
                            if (canMove(player,row,col,row+1,col+1))
                                moves.add(new CheckersMove(row,col,row+1,col+1));
                            if (canMove(player,row,col,row-1,col+1))
                                moves.add(new CheckersMove(row,col,row-1,col+1));
                            if (canMove(player,row,col,row+1,col-1))
                                moves.add(new CheckersMove(row,col,row+1,col-1));
                            if (canMove(player,row,col,row-1,col-1))
                                moves.add(new CheckersMove(row,col,row-1,col-1));
                        }
                    }
                }
            }

            /*Return null if no legal moves have been found otherwise
            * create an array to store all legal moves and return it*/

            if (moves.size() == 0)
                return null;

            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);

            return moveArray;
        }

        /**
         * Computes possible legal jumps available to the player
         * and stores them in an array
         * @param player - the player piece (RED OR BLACK)
         * @return array of legal jumps available to the player
         */
        CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {

            if (player != RED && player != BLACK)
                return null;

            int playerKing; // Constant representing king piece
            if (player == RED)
                playerKing = RED_KING;
            else
                playerKing = BLACK_KING;

            /*Stores all legal jump moves*/
            ArrayList<CheckersMove> moves = new ArrayList<>();
            if (board[row][col] == player || board[row][col] == playerKing) {
                if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                    moves.add(new CheckersMove(row, col, row+2, col+2));
                if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                    moves.add(new CheckersMove(row, col, row-2, col+2));
                if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                    moves.add(new CheckersMove(row, col, row+2, col-2));
                if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                    moves.add(new CheckersMove(row, col, row-2, col-2));
            }
            if (moves.size() == 0)
                return null;
            else {
                CheckersMove[] moveArray = new CheckersMove[moves.size()];
                for (int i = 0; i < moves.size(); i++)
                    moveArray[i] = moves.get(i);
                return moveArray;
            }
        }

        /**
         * Check if player can make a jump move
         * The square between the current square the player is in and
         * the destination square should contain an opponent piece for
         * a jump move to be legal
         * @return true if player can jump
         */
        private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
            if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
                return false; // (r3,c3) is off the board

            if (board[r3][c3] != EMPTY)
                return false; // (r3,c3) already contains a checker piece

            if (player == RED) {
                if (board[r1][c1] == RED && r3 > r1)
                    return false; // Regular red piece can only move up
                return board[r2][c2] == BLACK || board[r2][c2] == BLACK_KING;
            }
            else {
                if (board[r1][c1] == BLACK && r3 < r1)
                    return false; // Regular black piece can only move down
                return board[r2][c2] == RED || board[r2][c2] == RED_KING;
            }
        }

        /**
         * Checks if the destination square is empty to make a move
         * @return true if move is possible
         */
        private boolean canMove(int player, int r1, int c1, int r2, int c2) {
            if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
                return false; // (r2,c20 is off the board

            if (board[r2][c2] != EMPTY)
                return false; // (r2,c2) already contains a piece

            if (player == RED) {
                return board[r1][c1] != RED || r2 <= r1;
            }
            else {
                return board[r1][c2] != BLACK || r2 >= r1;
            }
        }
    }

    /**
     * A CheckersMove object represents a legal move in the game
     * of checkers.
     * It holds the row and column of the piece to be moved and
     * the row and column of the square to which it is to be moved
     */
    private static class CheckersMove {
        int fromRow;
        int fromCol;
        int toRow;
        int toCol;

        CheckersMove(int r1, int c1, int r2, int c2) {
            fromRow = r1;
            fromCol = c1;
            toRow = r2;
            toCol = c2;
        }

        /**
         * In a regular move a checker piece moves one row
         * and in a jump it moves two rows
         * @return whether this move is a jump (moving two rows)
         */
        boolean isJump() {
            return (fromRow - toRow == 2 || fromRow - toRow == -2);
        }

    }

    /**
     * The Board class is responsible for the actual checkers gameplay
     * between two users.
     *
     * It displays a 160X160 checkerboard pattern with a
     * black boarder 2 pixels wide
     *
     * The size of the panel within which the board is contained
     * has a fixed size of 164X164 to compensate for the boarder
     * width across the board's perimeter
     */
    private class Board extends JPanel implements ActionListener, MouseListener {

        CheckersData board; // Generates lists of legal moves

        boolean gameInProgress;

        int currentPlayer;  // possible values include CheckersData.RED and CheckersData.BLACK

        int selectedRow;

        int selectedCol;

        CheckersMove[] legalMoves;

        /**
         * Creates buttons and label
         * Adds mouse and button click listeners and
         * starts new game
         */
        Board() {
            setBackground(Color.BLACK);
            addMouseListener(this);
            quitGameButton = new JButton("Quit");
            quitGameButton.addActionListener(this);
            newGameButton = new JButton("New Game");
            newGameButton.addActionListener(this);
            message = new JLabel("", JLabel.CENTER);
            message.setFont(new  Font("Serif", Font.BOLD, 14));
            message.setForeground(Color.green);
            board = new CheckersData();
            newGame();
        }

        /**
         * Start a new game
         */
        void newGame() {
            if (gameInProgress)
            {
                message.setText("It sucks but you've got to finish this one first!");
                return;
            }
            board.setupGame();
            currentPlayer = CheckersData.RED; // Red is player 1
            legalMoves = board.getLegalMoves(CheckersData.RED);
            selectedRow = -1;
            message.setText("RED's turn");
            gameInProgress = true;
            newGameButton.setEnabled(false);
            quitGameButton.setEnabled(true);
            repaint();
        }

        /**
         * Ends the game and the opponent wins
         */
        void quitGame() {
            if (!gameInProgress) {
                message.setText("No ongoing game to quit");
                return;
            }
            if (currentPlayer == CheckersData.RED) {
                gameOver("BLACK WINS!");
            }
            else {
                gameOver("RED WINS");
            }
        }

        void gameOver(String str) {
            message.setText(str);
            newGameButton.setEnabled(true);
            quitGameButton.setEnabled(false);
            gameInProgress = false;
        }

        /**
         * This is called by mousePressed() when a player clicks on the
         * square in the specified row and col.  It has already been checked
         * that a game is, in fact, in progress.
         */
        void doClickSquare(int row, int col) {

            /* If the player clicked on one of the pieces that the player
             can move, mark this row and col as selected and return.  (This
             might change a previous selection.)  Reset the message, in
             case it was previously displaying an error message. */

            for (CheckersMove legalMove : legalMoves)
                if (legalMove.fromRow == row && legalMove.fromCol == col) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer == CheckersData.RED)
                        message.setText("RED:  Make your move.");
                    else
                        message.setText("BLACK:  Make your move.");
                    repaint();
                    return;
                }

            /* If no piece has been selected to be moved, the user must first
             select a piece.  Show an error message and return. */

            if (selectedRow < 0) {
                message.setText("Click the piece you want to move.");
                return;
            }

            /* If the user clicked on a square where the selected piece can be
             legally moved, then make the move and return. */

            for (CheckersMove legalMove : legalMoves)
                if (legalMove.fromRow == selectedRow && legalMove.fromCol == selectedCol
                        && legalMove.toRow == row && legalMove.toCol == col) {
                    doMakeMove(legalMove);
                    return;
                }

            /* If we get to this point, there is a piece selected, and the square where
             the user just clicked is not one where that piece can be legally moved.
             Show an error message. */

            message.setText("Click the square you want to move to.");

        }  // end doClickSquare()

        /**
         * This is called when the current player has chosen the specified
         * move.  Make the move, and then either end or continue the game
         * appropriately.
         */
        void doMakeMove(CheckersMove move) {

            board.makeMove(move);

            /* If the move was a jump, it's possible that the player has another
             jump.  Check for legal jumps starting from the square that the player
             just moved to.  If there are any, the player must jump.  The same
             player continues moving.
             */

            if (move.isJump()) {
                legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
                if (legalMoves != null) {
                    if (currentPlayer == CheckersData.RED)
                        message.setText("RED:  You must continue jumping.");
                    else
                        message.setText("BLACK:  You must continue jumping.");
                    selectedRow = move.toRow;  // Since only one piece can be moved, select it.
                    selectedCol = move.toCol;
                    repaint();
                    return;
                }
            }

            /* The current player's turn is ended, so change to the other player.
             Get that player's legal moves.  If the player has no legal moves,
             then the game ends. */

            if (currentPlayer == CheckersData.RED) {
                currentPlayer = CheckersData.BLACK;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null)
                    gameOver("BLACK has no moves.  RED wins.");
                else if (legalMoves[0].isJump())
                    message.setText("BLACK:  Make your move.  You must jump.");
                else
                    message.setText("BLACK:  Make your move.");
            }
            else {
                currentPlayer = CheckersData.RED;
                legalMoves = board.getLegalMoves(currentPlayer);
                if (legalMoves == null)
                    gameOver("RED has no moves.  BLACK wins.");
                else if (legalMoves[0].isJump())
                    message.setText("RED:  Make your move.  You must jump.");
                else
                    message.setText("RED:  Make your move.");
            }

            /* Set selectedRow = -1 to record that the player has not yet selected
             a piece to move. */

            selectedRow = -1;

            /* As a courtesy to the user, if all legal moves use the same piece, then
             select that piece automatically so the user won't have to click on it
             to select it. */

            if (legalMoves != null) {
                boolean sameStartSquare = true;
                for (int i = 1; i < legalMoves.length; i++)
                    if (legalMoves[i].fromRow != legalMoves[0].fromRow
                            || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                        sameStartSquare = false;
                        break;
                    }
                if (sameStartSquare) {
                    selectedRow = legalMoves[0].fromRow;
                    selectedCol = legalMoves[0].fromCol;
                }
            }

            /* Make sure the board is redrawn in its new state. */

            repaint();

        }  // end doMakeMove();

        /**
         * Draw a checkerboard pattern in gray and lightGray.  Draw the
         * checkers.  If a game is in progress, highlight the legal moves.
         */
        public void paintComponent(Graphics g) {

            /* Turn on antialiasing to get nicer ovals. */

            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            /* Draw a two-pixel black border around the edges of the canvas. */

            g.setColor(Color.black);
            g.drawRect(0,0,getSize().width-1,getSize().height-1);
            g.drawRect(1,1,getSize().width-3,getSize().height-3);

            /* Draw the squares of the checkerboard and the checkers. */

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if ( row % 2 == col % 2 )
                        g.setColor(Color.LIGHT_GRAY);
                    else
                        g.setColor(Color.GRAY);
                    g.fillRect(2 + col*20, 2 + row*20, 20, 20);
                    switch (board.pieceAt(row,col)) {
                        case CheckersData.RED:
                            g.setColor(Color.RED);
                            g.fillOval(4 + col*20, 4 + row*20, 15, 15);
                            break;
                        case CheckersData.BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(4 + col*20, 4 + row*20, 15, 15);
                            break;
                        case CheckersData.RED_KING:
                            g.setColor(Color.RED);
                            g.fillOval(4 + col*20, 4 + row*20, 15, 15);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 7 + col*20, 16 + row*20);
                            break;
                        case CheckersData.BLACK_KING:
                            g.setColor(Color.BLACK);
                            g.fillOval(4 + col*20, 4 + row*20, 15, 15);
                            g.setColor(Color.WHITE);
                            g.drawString("K", 7 + col*20, 16 + row*20);
                            break;
                    }
                }
            }

            /* If a game is in progress, highlight the legal moves.   Note that legalMoves
             is never null while a game is in progress. */

            if (gameInProgress) {
                /* First, draw a 2-pixel cyan border around the pieces that can be moved. */
                g.setColor(Color.cyan);
                for (CheckersMove legalMove : legalMoves) {
                    g.drawRect(2 + legalMove.fromCol * 20, 2 + legalMove.fromRow * 20, 19, 19);
                    g.drawRect(3 + legalMove.fromCol * 20, 3 + legalMove.fromRow * 20, 17, 17);
                }
                /* If a piece is selected for moving (i.e. if selectedRow >= 0), then
                    draw a 2-pixel white border around that piece and draw green borders
                    around each square that that piece can be moved to. */
                if (selectedRow >= 0) {
                    g.setColor(Color.white);
                    g.drawRect(2 + selectedCol*20, 2 + selectedRow*20, 19, 19);
                    g.drawRect(3 + selectedCol*20, 3 + selectedRow*20, 17, 17);
                    g.setColor(Color.green);
                    for (CheckersMove legalMove : legalMoves) {
                        if (legalMove.fromCol == selectedCol && legalMove.fromRow == selectedRow) {
                            g.drawRect(2 + legalMove.toCol * 20, 2 + legalMove.toRow * 20, 19, 19);
                            g.drawRect(3 + legalMove.toCol * 20, 3 + legalMove.toRow * 20, 17, 17);
                        }
                    }
                }
            }

        }  // end paintComponent()

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == newGameButton)
                newGame();
            else if (src == quitGameButton)
                quitGame();
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!gameInProgress)
                message.setText("Click \"New Game\" to start a new game.");
            else {
                int col = (e.getX() - 2) / 20;
                int row = (e.getY() - 2) / 20;
                if (col >= 0 && col < 8 && row >= 0 && row < 8)
                    doClickSquare(row,col);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
