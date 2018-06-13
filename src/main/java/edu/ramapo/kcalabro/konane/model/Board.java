//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project:  Konane AI Search - Project 2                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/6/18                                            *
//     ************************************************************

package edu.ramapo.kcalabro.konane.model;

/**
 * Created by KyleCalabro on 1/17/18.
 */

import java.util.Random;
import java.util.Vector;

public class Board
{
    //------------------------Data Members------------------------

    public final static char WHITE_STONE = 'W';

    public final static char BLACK_STONE = 'B';

    public final static char OPEN_SLOT = 'O';

    // The dimensions of the board.
    private int boardSize;

    // The number of white stones that have been captured.
    private int numWhiteStonesCaptured;

    // The number of black stones that have been captured.
    private int numBlackStonesCaptured;

    // The board itself, a two-dimensional array of characters. 'W', 'B'.
    private char board[][];

    // Indicates if the user is making a multiple jump with the same stone.
    private Boolean multiHop = false;

    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Board class.
     *
     * @param boardSize Integer representing the size of the two-dimensional board.
     */

    public Board(int boardSize)
    {
        this.boardSize = boardSize;

        numBlackStonesCaptured = 0;
        numWhiteStonesCaptured = 0;

        initializeBoard();
    }

    /**
     * To get the current board.
     *
     * @return Array of characters representing the current board.
     */

    public char[][] getBoard()
    {
        return board;
    }

    /**
     * To set the char at a certain row and column position of the board.
     *
     * @param row Row position of the board.
     * @param col Column position of the board.
     * @param indicator Char which to set the board position to.
     */

    public void setBoardAtPosition(int row, int col, char indicator)
    {
        board[row][col] = indicator;
    }

    /**
     * To initialize and populate the board utilized in the Konane game.
     *
     * Algorithm:
     *      ; If the row is even and the column is even -> black.
     *      ; If the row is even and the column is odd -> white.
     *
     *      ; If the row is odd and the column is even -> white.
     *      ; If the row is odd and the column is odd -> black.
     */

    public void initializeBoard()
    {
        board = null;

        // Initialize the board to the specified size.
        board = new char[boardSize][boardSize];

        // Define row and column counters and set them to zero.
        int row = 0;
        int col = 0;

        // Iterate through the rows.
        for (row = 0; row < boardSize; row++)
        {
            // Iterate through the columns.
            for (col = 0; col < boardSize; col++)
            {
                // Populate the board with alternating black and white stones.
                if (row % 2 == 0)
                {
                    if (col % 2 == 0)
                    {
                        board[row][col] = BLACK_STONE;
                    }
                    else
                    {
                        board[row][col] = WHITE_STONE;
                    }
                }
                else
                {
                    if (col % 2 == 1)
                    {
                        board[row][col] = BLACK_STONE;
                    }
                    else
                    {
                        board[row][col] = WHITE_STONE;
                    }
                }
            }
        }
    }

    /**
     * To initially remove a black and white stone at the beginning of a game.
     *
     * Algorithm:
     *      ; If the row is even and the column is odd -> White stone.
     *      ; If the row is odd and the column is odd -> Black stone.
     */

    public void removeInitialStones()
    {
        Random random = new Random();

        // Generate a random even number.
        int randomEvenNum_1 = 0 + random.nextInt((6)/2) *2;
        int randomEvenNum_2 = 0 + random.nextInt((6)/2) *2;

        System.out.println(randomEvenNum_1 + " " + randomEvenNum_2);

        // Generate two random odd numbers.
        int randomOddNum_1 = 1 + random.nextInt((6)/2) * 2;
        int randomOddNum_2 = 1 + random.nextInt((6)/2) * 2;

        System.out.println(randomOddNum_1 + " " + randomOddNum_2);

        // Removes a white stone from the board.
        board[randomEvenNum_1][randomOddNum_1] = OPEN_SLOT;

        // Removes a black stone from the board.
        board[randomOddNum_1][randomOddNum_2] = OPEN_SLOT;
    }

    /**
     * To determine if a given move is valid.
     *
     * @param stonePosition Array of integers containing the position of the stone to be moved, [0] = row, [1] = col.
     * @param vacantPositions Vector of positions containing the positions of the vacant pukas to be moved to, [0] = row, [1] = col.
     * @return Boolean value indicating if the move is valid.
     *
     * Algorithm:
     *      ; If the row positions are the same for the stone and vacant positions, it is a lateral move.
     *      ; If the column positions are the same for the stone and vacant positions, it is a vertical move.
     *      ; Otherwise, the user is attempting to make a diagonal move, which is not allowed.
     */

    public boolean isMoveValid(Boolean isBlack, int[] stonePosition, Vector<Position> vacantPositions)
    {
        // Get the coordinates of the stone in the array.
        int stoneRowPosition = stonePosition[0];
        int stoneColPosition = stonePosition[1];

        Boolean isValidMove = false;

        int attemptedScoreGain = 0;

        for(int pos = 0; pos < vacantPositions.size(); pos++)
        {
            // Get the coordinates of the vacant position from the vector.
            int vacantRowPosition = vacantPositions.elementAt(pos).getRowPosition();
            int vacantColPosition = vacantPositions.elementAt(pos).getColPosition();

            int[] vacantPosition = new int[]{vacantRowPosition, vacantColPosition};

            if (stoneRowPosition == vacantRowPosition)
            {
                isValidMove = isLateralMoveValid(isBlack, stonePosition, vacantPosition);
            }
            else if (stoneColPosition == vacantColPosition)
            {
                isValidMove = isVerticalMoveValid(isBlack, stonePosition, vacantPosition);
            }

            // Diagonal moves not allowed.
            else
            {
                return false;
            }

            if(!isValidMove)
            {
                if(isBlack)
                {
                    numWhiteStonesCaptured -= attemptedScoreGain;
                }
                else
                {
                    numBlackStonesCaptured -= attemptedScoreGain;
                }
                multiHop = false;
                return false;
            }

            attemptedScoreGain++;

            stoneRowPosition = vacantRowPosition;
            stoneColPosition = vacantColPosition;
            stonePosition[0] = vacantRowPosition;
            stonePosition[1] = vacantColPosition;

            multiHop = true;
        }

        multiHop = false;
        return isValidMove;
    }

    /**
     * To determine if a given player can make a move.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @return Boolean value indicating if the player can make a move.
     *
     * Algorithm:
     *      ; For lateral moves:
     *          ; Iterate through the board from left to right and right to left:
     *              ; If the adjacent position is the opponents stone AND
     *              ; the next position from that is open, the player can make a move.
     *      ; For vertical moves:
     *          ; Iterate through the board from north to south and south to north:
     *              ; If the adjacent position is the opponents stone AND
     *              ; the next position from that is open, the player can make a move.
     *      ; Otherwise, there are no moves to be made and the player must pass.
     */

    public boolean canMakePlay(boolean isBlack)
    {
        char stoneToCapture;
        char currentStoneColor;

        // If the current player is using black stones, you must capture white stones.
        if(isBlack)
        {
            currentStoneColor = BLACK_STONE;
            stoneToCapture = WHITE_STONE;
        }

        // Otherwise, the current player is using white stones, and must capture black stones.
        else
        {
            currentStoneColor = WHITE_STONE;
            stoneToCapture = BLACK_STONE;
        }

        // Check if there are any lateral moves to be made.
        for(int row = 0; row < boardSize; row++)
        {
            // Check if any moves to the right can be made.
            for (int col = 0; col < boardSize - 2; col++)
            {
                if(board[row][col] == currentStoneColor)
                {
                    if (board[row][col + 1] == stoneToCapture && board[row][col + 2] == OPEN_SLOT)
                    {
                        return true;
                    }
                }
            }

            // Check if any moves to the left can be made.
            for(int col = boardSize - 1; col > 1; col--)
            {
                if(board[row][col] == currentStoneColor)
                {
                    if (board[row][col - 1] == stoneToCapture && board[row][col - 2] == OPEN_SLOT)
                    {
                        return true;
                    }
                }
            }
        }

        // Check if there are any vertical moves to be made.
        for(int col = 0; col < boardSize; col++)
        {
            // Check if any moves to the north can be made.
            for (int row = 0; row < boardSize - 2; row++)
            {
                if(board[row][col] == currentStoneColor)
                {
                    if (board[row + 1][col] == stoneToCapture && board[row + 2][col] == OPEN_SLOT)
                    {
                        return true;
                    }
                }
            }

            // Check if any moves to the south can be made.
            for (int row = boardSize - 1; row > 1; row--)
            {
                if(board[row][col] == currentStoneColor)
                {
                    if (board[row - 1][col] == stoneToCapture && board[row - 2][col] == OPEN_SLOT)
                    {
                        return true;
                    }
                }
            }
        }

        // Otherwise, no moves can be made for the player, and they must pass.
        return false;
    }

    /**
     * To determine the color of a stone at a given position on the board.
     *
     * @param row The row number of position for the board.
     * @param col The column number of position for the board.
     * @return A character representing the color of stone in the desired position, 'W', 'B'.
     */

    public char getStoneColorAtPosition(int row, int col)
    {
        return board[row][col];
    }

    /**
     * To determine if a given position on the board is occupied by a black stone.
     *
     * @param row The row position on the board to check.
     * @param col The column position on the board to check.
     * @return Boolean indicating if the position is occupied by a black stone.
     */

    public boolean isPositionBlack(int row, int col)
    {
        if(row < 0 || row > boardSize - 1 || col < 0 || col > boardSize - 1)
        {
            return false;
        }
        else if(getStoneColorAtPosition(row, col) == BLACK_STONE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * To determine if a given position on the board is occupied by a white stone.
     *
     * @param row The row position on the board to check.
     * @param col The column position on the board to check.
     * @return Boolean indicating if the position is occupied by a white stone.
     */

    public boolean isPositionWhite(int row, int col)
    {
        if(row < 0 || row > boardSize - 1 || col < 0 || col > boardSize - 1)
        {
            return false;
        }
        else if(getStoneColorAtPosition(row, col) == WHITE_STONE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * To determine if a given position on the board is vacant.
     *
     * @param row The row position on the board to check.
     * @param col The column position on the board to check.
     * @return Boolean indicating if the position is vacant.
     */

    public boolean isPositionOpen(int row, int col)
    {
        if(row < 0 || row > boardSize - 1 || col < 0 || col > boardSize - 1)
        {
            return false;
        }
        else if(getStoneColorAtPosition(row, col) == OPEN_SLOT)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * To modify the board in any given direction.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param stonePosition Array of integers containing the position of the stone to be moved.
     * @param vacantPositions Vector of positions containing the position of the vacant positions to be occupied.
     */

    public void modifyBoard(Boolean isBlack, int[] stonePosition, Vector<Position> vacantPositions)
    {

        int stoneRowPosition = stonePosition[0];
        int stoneColPosition = stonePosition[1];

        for(int pos = 0; pos < vacantPositions.size(); pos++)
        {
            // Get the coordinates of the vacant position from the vector.
            int vacantRowPosition = vacantPositions.elementAt(pos).getRowPosition();
            int vacantColPosition = vacantPositions.elementAt(pos).getColPosition();

            int[] vacantPosition = new int[]{vacantRowPosition, vacantColPosition};

            // If the row position of the stone and vacant spot are the same, the move is lateral.
            if (stoneRowPosition == vacantRowPosition)
            {
                // Modify the board laterally.
                modifyBoardLaterally(isBlack, stonePosition, vacantPosition);
            }

            // Otherwise, it is a vertical move.
            else if (stoneColPosition == vacantColPosition)
            {
                // Modify the board vertically.
                modifyBoardVertically(isBlack, stonePosition, vacantPosition);
            }

            stoneRowPosition = vacantRowPosition;
            stoneColPosition = vacantColPosition;
            stonePosition[0] = vacantRowPosition;
            stonePosition[1] = vacantColPosition;
        }
    }

    /**
     * Getter function for the number of white stones that have been captured.
     *
     * @return Integer value representing number of white stones that have been captured.
     */

    public int getNumWhiteStonesCaptured()
    {
        return numWhiteStonesCaptured;
    }

    /**
     * Getter function for the number of black stones that have been captured.
     *
     * @return Integer value representing number of black stones that have been captured.
     */

    public int getNumBlackStonesCaptured()
    {
        return numBlackStonesCaptured;
    }

    /**
     * Setter function for the number of black stones that have been captured.
     *
     * @param numBlackStonesCaptured Number of black stones that have been captured.
     */

    public void setNumBlackStonesCaptured(int numBlackStonesCaptured)
    {
        this.numBlackStonesCaptured = numBlackStonesCaptured;
    }

    /**
     * Setter function for the number of white stones that have been captured.
     *
     * @param numWhiteStonesCaptured Number of white stones that have been captured.
     */

    public void setNumWhiteStonesCaptured(int numWhiteStonesCaptured)
    {
        this.numWhiteStonesCaptured = numWhiteStonesCaptured;
    }

    /**
     * To determine if a move to the north is available for a given position.
     *
     * @param isBlack Boolean value indicating if the current player is black.
     * @param oldPosition The position from which to make a move from.
     * @param northPosition The potential position to make a move to.
     * @return Boolean value indicating if the move is valid.
     */

    public boolean isNorthAvailable(Boolean isBlack, Position oldPosition, Position northPosition)
    {
        if(northPosition.getRowPosition() < 0 || northPosition.getRowPosition() > Game.BOARD_SIZE - 1)
        {
            return false;
        }
        else
        {
            if(isBlack && isPositionBlack(oldPosition.getRowPosition(), oldPosition.getColPosition()))
            {
                if(isPositionOpen(northPosition.getRowPosition(), northPosition.getColPosition()))
                {
                    if(isPositionWhite(oldPosition.getRowPosition() - 1, oldPosition.getColPosition()))
                    {
                        return true;
                    }
                }
            }
            if(!isBlack && isPositionWhite(oldPosition.getRowPosition(), oldPosition.getColPosition()))
            {
                if(isPositionOpen(northPosition.getRowPosition(), northPosition.getColPosition()))
                {
                    if(isPositionBlack(oldPosition.getRowPosition() - 1, oldPosition.getColPosition()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * To determine if a move to the south is available for a given position.
     *
     * @param isBlack Boolean value indicating if the current player is black.
     * @param oldPosition The position from which to make a move from.
     * @param southPosition The potential position to make a move to.
     * @return Boolean value indicating if the move is valid.
     */

    public boolean isSouthAvailable(Boolean isBlack, Position oldPosition, Position southPosition)
    {
        if(southPosition.getRowPosition() < 0 || southPosition.getRowPosition() > Game.BOARD_SIZE - 1)
        {
            return false;
        }
        else
        {
            if(isBlack && isPositionBlack(oldPosition.getRowPosition(), oldPosition.getColPosition()))
            {
                if(isPositionOpen(southPosition.getRowPosition(), southPosition.getColPosition()))
                {
                    if(isPositionWhite(oldPosition.getRowPosition() + 1, oldPosition.getColPosition()))
                    {
                        return true;
                    }
                }
            }
            if(!isBlack && isPositionWhite(oldPosition.getRowPosition(), oldPosition.getColPosition()))
            {
                if(isPositionOpen(southPosition.getRowPosition(), southPosition.getColPosition()))
                {
                    if(isPositionBlack(oldPosition.getRowPosition() + 1, oldPosition.getColPosition()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * To determine if a move to the east is available for a given position.
     *
     * @param isBlack Boolean value indicating if the current player is black.
     * @param oldPosition The position from which to make a move from.
     * @param eastPosition The potential position to make a move to.
     * @return Boolean value indicating if the move is valid.
     */

    public boolean isEastAvailable(Boolean isBlack, Position oldPosition, Position eastPosition)
    {
        if(eastPosition.getColPosition() < 0 || eastPosition.getColPosition() > Game.BOARD_SIZE - 1)
        {
            return false;
        }
        else
        {
            if(isBlack && isPositionBlack(oldPosition.getRowPosition(), oldPosition.getColPosition()))
            {
                if(isPositionOpen(eastPosition.getRowPosition(), eastPosition.getColPosition()))
                {
                    if(isPositionWhite(oldPosition.getRowPosition(), oldPosition.getColPosition() + 1))
                    {
                        return true;
                    }
                }
            }
            if(!isBlack && isPositionWhite(oldPosition.getRowPosition(), oldPosition.getColPosition()))
            {
                if(isPositionOpen(eastPosition.getRowPosition(), eastPosition.getColPosition()))
                {
                    if(isPositionBlack(oldPosition.getRowPosition(), oldPosition.getColPosition() + 1))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * To determine if a move to the west is available for a given position.
     *
     * @param isBlack Boolean value indicating if the current player is black.
     * @param oldPosition The position from which to make a move from.
     * @param westPosition The potential position to make a move to.
     * @return Boolean value indicating if the move is valid.
     */

    public boolean isWestAvailable(Boolean isBlack, Position oldPosition, Position westPosition)
    {
        if(westPosition.getColPosition() < 0 || westPosition.getColPosition() > Game.BOARD_SIZE - 1)
        {
            return false;
        }
        else
        {
            if(isBlack && isPositionBlack(oldPosition.getRowPosition(), oldPosition.getColPosition()))
            {
                if(isPositionOpen(westPosition.getRowPosition(), westPosition.getColPosition()))
                {
                    if(isPositionWhite(oldPosition.getRowPosition(), oldPosition.getColPosition() - 1))
                    {
                        return true;
                    }
                }
            }
            if(!isBlack && isPositionWhite(oldPosition.getRowPosition(), oldPosition.getColPosition()))
            {
                if(isPositionOpen(westPosition.getRowPosition(), westPosition.getColPosition()))
                {
                    if(isPositionBlack(oldPosition.getRowPosition(), oldPosition.getColPosition() - 1))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * To determine if a given lateral move is valid.
     *
     * @param stonePosition Array of integers containing the position of the stone to be moved, [0] = row, [1] = col.
     * @param vacantPosition Array of integers containing the position of the vacant puka to be moved to, [0] = row, [1] = col.
     * @return Boolean value indicating if the move is valid.
     */

    private boolean isLateralMoveValid(Boolean isBlack, int[] stonePosition, int[] vacantPosition)
    {
        int stoneColPosition = stonePosition[1];
        int vacantColPosition = vacantPosition[1];

        if(stoneColPosition < vacantColPosition)
        {
            return isRightMoveValid(isBlack, stonePosition, vacantPosition);
        }
        else
        {
            return isLeftMoveValid(isBlack, stonePosition, vacantPosition);
        }
    }

    /**
     * To determine if a given move is valid.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param whiteStoneCount The number of white stones jumped over.
     * @param blackStoneCount The number of black stones jumped over.
     * @param openPositionCount The number of open spots jumped over.
     * @return Boolean value indicating if the move was valid.
     *
     * Algorithm:
     *      ; For the player using black stones:
     *          ; The number of open spots jumped in the move most equal the number
     *          ; of white stones jumped in the move.
     *
     *      ; For the player using white stones:
     *          ; The number of open spots jumped in the move must equal the number
     *          ; of black stones jumped in the move.
     */

    private boolean verifyStoneCount(boolean isBlack, int whiteStoneCount, int blackStoneCount, int openPositionCount)
    {
        if(multiHop)
        {
            openPositionCount--;
        }

        if(isBlack)
        {
            if(whiteStoneCount == openPositionCount)
            {
                numWhiteStonesCaptured += whiteStoneCount;
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if(blackStoneCount == openPositionCount)
            {
                numBlackStonesCaptured += blackStoneCount;
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * To determine if a given move to the right is valid.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param stonePosition Array of integers containing the position of the stone to be moved.
     * @param vacantPosition Array of integers containing the coordinates of the vacant position.
     * @return Boolean valud indicating if the move was valid.
     */

    private boolean isRightMoveValid(Boolean isBlack, int[] stonePosition, int[] vacantPosition)
    {
        int row = stonePosition[0];

        int stoneColPosition = stonePosition[1];
        int vacantColPosition = vacantPosition[1];

        int whiteStoneCount = 0;
        int blackStoneCount = 0;
        int openPositionCount = 0;

        if(vacantColPosition > Game.BOARD_SIZE - 1)
        {
            return false;
        }

        for(int col = stoneColPosition; col <= vacantColPosition; col++)
        {
            if(isPositionOpen(row, col))
            {
                openPositionCount++;
            }
            else if(isPositionWhite(row, col))
            {
                whiteStoneCount++;
            }
            else
            {
                blackStoneCount++;
            }
        }

        return verifyStoneCount(isBlack, whiteStoneCount, blackStoneCount, openPositionCount);
    }

    /**
     * To determine if a given move to the left is valid.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param stonePosition Array of integers containing the position of the stone to be moved.
     * @param vacantPosition Array of integers containing the coordinates of the vacant position.
     * @return Boolean value indicating if the move was valid.
     */

    private boolean isLeftMoveValid(Boolean isBlack, int[] stonePosition, int[] vacantPosition)
    {
        int row = stonePosition[0];

        int stoneColPosition = stonePosition[1];
        int vacantColPosition = vacantPosition[1];

        int whiteStoneCount = 0;
        int blackStoneCount = 0;
        int openPositionCount = 0;

        if(vacantColPosition < 0)
        {
            return false;
        }

        for(int col = stoneColPosition; col >= vacantColPosition; col--)
        {
            if(isPositionOpen(row, col))
            {
                openPositionCount++;
            }
            else if(isPositionWhite(row, col))
            {
                whiteStoneCount++;
            }
            else
            {
                blackStoneCount++;
            }
        }

        return verifyStoneCount(isBlack, whiteStoneCount, blackStoneCount, openPositionCount);
    }

    /**
     * To determine if a given horizontal move is valid.
     *
     * @param stonePosition Array of integers containing the position of the stone to be moved, [0] = row, [1] = col.
     * @param vacantPosition Array of integers containing the position of the vacant puka to be moved to, [0] = row, [1] = col.
     * @return Boolean value indicating if the move is valid.
     */

    private boolean isVerticalMoveValid(Boolean isBlack, int[] stonePosition, int[] vacantPosition)
    {
        int stoneRowPosition = stonePosition[0];
        int vacantRowPosition = vacantPosition[0];

        if(stoneRowPosition > vacantRowPosition)
        {
            return isNorthMoveValid(isBlack, stonePosition, vacantPosition);
        }
        else
        {
            return isSouthMoveValid(isBlack, stonePosition, vacantPosition);
        }
    }

    /**
     * To determine if a given move to the north is valid.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param stonePosition Array of integers containing the position of the stone to be moved.
     * @param vacantPosition Array of integers containing the coordinates of the vacant position.
     * @return Boolean valud indicating if the move was valid.
     */

    private Boolean isNorthMoveValid(Boolean isBlack, int[] stonePosition, int[] vacantPosition)
    {
        int col = stonePosition[1];

        int stoneRowPosition = stonePosition[0];
        int vacantRowPosition = vacantPosition[0];

        int whiteStoneCount = 0;
        int blackStoneCount = 0;
        int openPositionCount = 0;

        if(vacantRowPosition < 0)
        {
            return false;
        }

        for(int row = stoneRowPosition; row >= vacantRowPosition; row--)
        {
            if(isPositionOpen(row, col))
            {
                openPositionCount++;
            }
            else if(isPositionWhite(row, col))
            {
                whiteStoneCount++;
            }
            else
            {
                blackStoneCount++;
            }
        }

        return verifyStoneCount(isBlack, whiteStoneCount, blackStoneCount, openPositionCount);
    }

    /**
     * To determine if a given move to the south is valid.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param stonePosition Array of integers containing the position of the stone to be moved.
     * @param vacantPosition Array of integers containing the coordinates of the vacant position.
     * @return Boolean valud indicating if the move was valid.
     */

    private Boolean isSouthMoveValid(Boolean isBlack, int[] stonePosition, int[] vacantPosition)
    {
        int col = stonePosition[1];

        int stoneRowPosition = stonePosition[0];
        int vacantRowPosition = vacantPosition[0];

        int whiteStoneCount = 0;
        int blackStoneCount = 0;
        int openPositionCount = 0;

        if(vacantRowPosition > Game.BOARD_SIZE - 1)
        {
            return false;
        }

        for(int row = stoneRowPosition; row <= vacantRowPosition; row++)
        {
            if(isPositionOpen(row, col))
            {
                openPositionCount++;
            }
            else if(isPositionWhite(row, col))
            {
                whiteStoneCount++;
            }
            else
            {
                blackStoneCount++;
            }
        }

        return verifyStoneCount(isBlack, whiteStoneCount, blackStoneCount, openPositionCount);
    }

    /**
     * To modify the board in any given lateral direction.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param stonePosition Array of integers containing the position of the stone to be moved.
     * @param vacantPosition Array of integers containing the position of the vacant position to be occupied.
     *
     * Algorithm:
     *      ; If the column position of the stone is less than that of the vacant position:
     *          ; The stone is being moved to the right.
     *      ; Otherwise, the stone is being moved to the left.
     */

    private void modifyBoardLaterally(Boolean isBlack, int[] stonePosition, int[] vacantPosition)
    {
        int stoneColPosition = stonePosition[1];
        int stoneRowPosition = stonePosition[0];
        int vacantRowPosition = vacantPosition[0];
        int vacantColPosition = vacantPosition[1];

        // Iterate through the board, setting the positions properly.
        if(stoneColPosition < vacantColPosition)
        {
            for(int col = stoneColPosition; col < vacantColPosition; col++)
            {
                board[stoneRowPosition][col] = OPEN_SLOT;
            }
        }
        else
        {
            for(int col = stoneColPosition; col > vacantColPosition; col--)
            {
                board[stoneRowPosition][col] = OPEN_SLOT;
            }
        }

        // If the current player is using black stones, set the newly occupied position to a black stone.
        if(isBlack)
        {
            board[vacantRowPosition][vacantColPosition] = BLACK_STONE;
        }
        // Otherwise, set the newly occupied position to a white stone.
        else
        {
            board[vacantRowPosition][vacantColPosition] = WHITE_STONE;
        }
    }

    /**
     * To modify the board in any given vertical direction.
     *
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param stonePosition Array of integers containing the position of the stone to be moved.
     * @param vacantPosition Array of integers containing the position of the vacant position to be occupied.
     *
     * Algorithm:
     *      ; If the row position of the stone is greater than that of the vacant position:
     *          ; The stone is being moved to the south.
     *      ; Otherwise, the stone is being moved to the north.
     */

    private void modifyBoardVertically(Boolean isBlack, int[] stonePosition, int[] vacantPosition)
    {
        int stoneColPosition = stonePosition[1];
        int stoneRowPosition = stonePosition[0];
        int vacantRowPosition = vacantPosition[0];
        int vacantColPosition = vacantPosition[1];

        // Iterate through the board setting the positions properly.
        if(stoneRowPosition > vacantRowPosition)
        {
            for(int row = stoneRowPosition; row > vacantRowPosition; row--)
            {
                board[row][stoneColPosition] = OPEN_SLOT;
            }
        }
        else
        {
            for(int row = stoneRowPosition; row < vacantRowPosition; row++)
            {
                board[row][stoneColPosition] = OPEN_SLOT;
            }
        }

        // If the current player is using black stones, set the newly occupied position to a black stone.
        if(isBlack)
        {
            board[vacantRowPosition][vacantColPosition] = BLACK_STONE;
        }

        // Otherwise, set the position to a white stone.
        else
        {
            board[vacantRowPosition][vacantColPosition] = WHITE_STONE;
        }
    }
}
