package ja44647.tictactoe;

/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 *
 * This is a tic-tac-toe game that runs in the console window.  The human
 * is X and the computer is O.
 */

import android.util.Log;

import java.util.Random;

public class TicTacToeGame {

    public enum DifficultyLevel {Easy, Harder, Expert};
    //Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    private char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
    public static final int BOARD_SIZE = 9;

    private static final String TAG = "TicTacToeGame";

    //characters used to represent human, computer and open spots
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    private Random mRand;

    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
    }


    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT **/
    public void clearBoard(){
        for(int i = 0; i < BOARD_SIZE; i++)
            mBoard[i] = OPEN_SPOT;
    }

    public char getBoardOccupant(int i) {
        return mBoard[i];
    }

    public char[] getBoardState() {
        return mBoard;
    }

    /** Set the given player at the given location on the board.
     *  The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move.
     */
    public boolean setMove(char player, int location) {
        if(mBoard[location] == OPEN_SPOT){
            mBoard[location] = player;
            return true;
        }
        return false;
    }


    /** Return the best move for the computer to make.
     *  Calls setMove() to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8)
     */
    public int getComputerMove()
    {
        int move = -1;
        char[] board = mBoard;

        if(mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1) {
                mBoard = board;
                move = getRandomMove();
            }
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert) {
            //Try to win, but if that's not possible, block
            //If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1) {
                mBoard = board;
                move = getBlockingMove();
            }
            if (move == -1) {
                mBoard = board;
                move = getRandomMove();
            }
        }
        return move;
    }

    private int getWinningMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                setMove(COMPUTER_PLAYER, i);
                //mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    Log.d(TAG, "WINNING Computer is moving to " + (i + 1));
                    return i;
                } else
                    mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    public void setBoardState(char[] newBoardState) {
        mBoard = newBoardState;
    }

    public int getBlockingMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    //mBoard[i] = COMPUTER_PLAYER;
                    mBoard[i] = OPEN_SPOT;
                    setMove(COMPUTER_PLAYER, i);
                    Log.d(TAG, "BLOCKING Computer is moving to " + (i + 1));
                    return i;
                } else
                    mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    private int getRandomMove() {
        int move;
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        return move;
    }

    public DifficultyLevel getDifficultyLevel(){
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }
}