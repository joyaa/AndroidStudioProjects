package ja44647.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class AndroidTicTacToe extends AppCompatActivity {

    private TicTacToeGame mGame; //lowercase 'm' at beginning of all member variables to distinguish from local parameters and variables.
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private boolean mGameOver;
    private int mCountHuman = 0;
    private int mCountComputer = 0;
    private int mCountTies = 0;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        String[] numNames = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};

        for(int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int resID = getResources().getIdentifier(numNames[i], "id", getPackageName());
            mBoardButtons[i] = (Button) findViewById(resID);
        }

//        mBoardButtons[0] = (Button) findViewById(R.id.one);
//        mBoardButtons[1] = (Button) findViewById(R.id.two);
//        mBoardButtons[2] = (Button) findViewById(R.id.three);
//        mBoardButtons[3] = (Button) findViewById(R.id.four);
//        mBoardButtons[4] = (Button) findViewById(R.id.five);
//        mBoardButtons[5] = (Button) findViewById(R.id.six);
//        mBoardButtons[6] = (Button) findViewById(R.id.seven);
//        mBoardButtons[7] = (Button) findViewById(R.id.eight);
//        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);

        mGame = new TicTacToeGame();

        startNewGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                //showDialog deprecated and will show warning but will still work!
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                int selected = 0;
                TicTacToeGame.DifficultyLevel difficulty = mGame.getDifficultyLevel();
                if (difficulty == TicTacToeGame.DifficultyLevel.Easy)
                    selected = 0;
                else if (difficulty == TicTacToeGame.DifficultyLevel.Harder)
                    selected = 1;
                else if (difficulty == TicTacToeGame.DifficultyLevel.Expert)
                    selected = 2;
                // selected is the radio button that should be selected.

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); //close dialog

                                // TODO: Set the diff level of mGame based on which item was selected.
                                if (item == 0)
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                else if (item == 1)
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                else if (item == 2)
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                // Display the selected difficulty level

                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToe.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
        }
        return dialog;
    }

    //Set up the game board
    private void startNewGame() {
        mGame.clearBoard();
        mGameOver = false;

        //Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        int first = (new Random()).nextInt(2); //0 for human, 1 for computer

        if (first == 0)
            mInfoTextView.setText(R.string.first_human); //Human goes first
        else {
            mInfoTextView.setText(R.string.first_computer);
            computerMove();
            mInfoTextView.setText(R.string.turn_human);
        }
    }

    /** Sets the given players chosen location on the board
     *
     * @param player - The player making the move
     * @param location - The location of the players move
     */
    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    /** Restarts the game when buttin New Game is pressed.
     *  It is only possible to restart when a game is over.
     *  The only other cption could be to give the computer
     *  a point when the user chooses to restart the game,
     *  in most cases because the user is loosing.
     *
     * @param v
     */
    public void restartGame(View v) {
        if(!mGameOver) {
            mCountComputer++;
            mInfoTextView.setText(R.string.result_computer_wins);
            TextView tv = (TextView) findViewById(R.id.count_computer);
            tv.setText("Android: "+mCountComputer);
        }
        startNewGame();
    }

    private void computerMove() {
        mInfoTextView.setText(R.string.turn_computer);
        int move = mGame.getComputerMove();
        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
    }

    //Handles clicks on the game board buttons
    //could also set the onClick attribute of buttons to a method
    //with the same name
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if(mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                Log.d("AndroidTicTacToe", "Computer is moving to " + (location + 1));

                //If no winner yet, let computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    computerMove();
                    winner = mGame.checkForWinner();
                }

                //winner = mGame.checkForWinner();
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mCountTies++;
                    mInfoTextView.setText(R.string.result_tie);
                    TextView tv = (TextView) findViewById(R.id.count_ties);
                    tv.setText("Ties: "+mCountTies);
                    mGameOver = true;
                }
                else if (winner == 2) {
                    mCountHuman++;
                    mInfoTextView.setText(R.string.result_human_wins);
                    TextView tv = (TextView) findViewById(R.id.count_human);
                    tv.setText("Human: "+mCountHuman);
                    mGameOver = true;
                }
                else {
                    mCountComputer++;
                    mInfoTextView.setText(R.string.result_computer_wins);
                    TextView tv = (TextView) findViewById(R.id.count_computer);
                    tv.setText("Android: "+mCountComputer);
                    mGameOver = true;
                }
            }

        }

    }
}