package ja44647.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class AndroidTicTacToe extends AppCompatActivity {

    private TicTacToeGame mGame; //lowercase 'm' at beginning of all member variables to distinguish from local parameters and variables.

    private SharedPreferences mPrefs;

    private TextView mInfoTextView;
    private boolean mGameOver;
    private int mCountHuman = 0;
    private int mCountComputer = 0;
    private int mCountTies = 0;

    static final int DIALOG_QUIT_ID = 2;
    static final int DIALOG_ABOUT_ID = 1;

    private boolean mTurn = false;
    private boolean mSoundOn = true;

    private static final String TAG = "TicTacToeGame";

    private BoardView mBoardView;

    // for all the sounds  we play
    private SoundPool mSounds;
    private int mHumanMoveSoundID;
    private int mComputerMoveSoundID;
    private int mGameLostSoundID;
    private int mGameWonSoundID;
    private int mGameTieSoundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mGame = new TicTacToeGame();

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        //Restore scores
        mCountHuman = mPrefs.getInt("mCountHuman", 0);
        mCountTies = mPrefs.getInt("mCountTies", 0);
        mCountComputer = mPrefs.getInt("mCountComputer", 0);
        displayScores();
        setDifficulty(mPrefs.getInt("difficultyLevel", 2));

        mBoardView = (BoardView) findViewById(R.id.board);

        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mInfoTextView = (TextView) findViewById(R.id.information);

        if (savedInstanceState == null) {
            startNewGame();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGame.setBoardState(savedInstanceState.getCharArray("mBoard"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mTurn = savedInstanceState.getBoolean("mTurn");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));

        displayScores();
        startComputerDelay();
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
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.reset_scores:
                resetScores();
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about_dialog:
                showDialog(DIALOG_ABOUT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_CANCELED) {
            //Apply potentially new settings
            mSoundOn = mPrefs.getBoolean("sound", true);
            String[] levels = getResources().getStringArray(R.array.list_difficulty_level);

            //Set difficulty, or use hardest if not present
            String difficultyLevel = mPrefs.getString("difficulty_level", levels[levels.length-1]);
            int i = 0;
            while(i < levels.length) {
                if(difficultyLevel.equals(levels[i])) {
                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[i]);
                    i = levels.length; //to stop loop
                }
                i++;
            }
        }
    }

    private void resetScores() {
        mCountComputer=0;
        mCountHuman=0;
        mCountTies=0;
        displayScores();
    }

    private int getDifficultyLevel() {
        TicTacToeGame.DifficultyLevel difficulty = mGame.getDifficultyLevel();
        if (difficulty == TicTacToeGame.DifficultyLevel.Easy)
            return 0;
        else if (difficulty == TicTacToeGame.DifficultyLevel.Harder)
            return 1;
        else if (difficulty == TicTacToeGame.DifficultyLevel.Expert)
            return 2;
        return 2;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_ABOUT_ID:

                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
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

    private void setDifficulty(int level) {
        if (level == 0)
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (level == 1)
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else if (level == 2)
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
    }
    //Set up the game board
    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate(); //redraws board view
        mGameOver = false;

        int first = (new Random()).nextInt(2); //0 for human, 1 for computer

        if (first == 0) {
            mInfoTextView.setText(R.string.first_human); //Human goes first
            mTurn = true;
        }
        else {
            mInfoTextView.setText(R.string.first_computer);
            mTurn = true;
            mInfoTextView.setText(R.string.turn_human);
        }
    }

    /** Sets the given players chosen location on the board
     *
     * @param player - The player making the move
     * @param location - The location of the players move
     */
    private boolean setMove(char player, int location) {
        if(mGame.setMove(player, location)) {
            if(mTurn && mSoundOn)
                mSounds.play(mHumanMoveSoundID, 1, 1, 1, 0, 1);
            //else
            //  mSounds.play(mComputerMoveSoundID, 1, 1, 1, 0, 1);
            mBoardView.invalidate();
            return true;
        }
        return false;
    }

    private void displayScores() {
        ((TextView) findViewById(R.id.human_count)).setText(Integer.toString(mCountHuman));
        ((TextView) findViewById(R.id.tie_count)).setText(Integer.toString(mCountTies));
        ((TextView) findViewById(R.id.computer_count)).setText(Integer.toString(mCountComputer));
    }

    private void computerMove() {
        mInfoTextView.setText(R.string.turn_computer);
        int move = mGame.getComputerMove();
        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
        if(mSoundOn)
            mSounds.play(mComputerMoveSoundID, 1, 1, 1, 0, 1);
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() { //????????
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (mTurn && !mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                mTurn = false;
                int winner = mGame.checkForWinner();

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_computer);
                else if (winner == 1) {
                    mCountTies++;
                    mInfoTextView.setText(R.string.result_tie);
                    TextView tv = (TextView) findViewById(R.id.tie_count);
                    tv.setText(Integer.toString(mCountTies));

                    if(mSoundOn)
                        mSounds.play(mGameTieSoundID, 1, 1, 1, 0, 1);
                    mGameOver = true;
                }
                else if (winner == 2) {
                    mCountHuman++;
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    TextView tv = (TextView) findViewById(R.id.human_count);
                    tv.setText(Integer.toString(mCountHuman));

                    if(mSoundOn)
                        mSounds.play(mGameWonSoundID, 1, 1, 1, 0, 1);
                    mGameOver = true;
                }
                if (mGameOver)
                    return false;

                mBoardView.invalidate();
                startComputerDelay();
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };
    Handler mHandler;
    Runnable mRunnable;
    private void startComputerDelay() {
        if (!mGameOver && !mTurn) {
            mHandler = new Handler();
            mRunnable = new Runnable () {
                public void run() {
                    // If no winner yet, let the computer make a move

                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        computerMove();
                    }

                    winner = mGame.checkForWinner();

                    if (winner == 0)
                        mInfoTextView.setText(R.string.turn_human);
                    else if (winner == 1) {
                        mCountTies++;
                        mInfoTextView.setText(R.string.result_tie);
                        TextView tv = (TextView) findViewById(R.id.tie_count);
                        tv.setText(Integer.toString(mCountTies));

                        if(mSoundOn)
                            mSounds.play(mGameTieSoundID, 1, 1, 1, 0, 1);
                        mGameOver = true;
                    } else if (winner == 2) {
                        mCountHuman++;
                        String defaultMessage = getResources().getString(R.string.result_human_wins);
                        mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                        TextView tv = (TextView) findViewById(R.id.human_count);
                        tv.setText(Integer.toString(mCountHuman));

                        if(mSoundOn)
                            mSounds.play(mGameWonSoundID, 1, 1, 1, 0, 1);
                        mGameOver = true;
                    } else {
                        mCountComputer++;
                        mInfoTextView.setText(R.string.result_computer_wins);
                        TextView tv = (TextView) findViewById(R.id.computer_count);
                        tv.setText(Integer.toString(mCountComputer));

                        if(mSoundOn)
                           mSounds.play(mGameLostSoundID, 1, 1, 1, 0, 1);

                        mGameOver = true;
                    }

                    mBoardView.invalidate();
                    mTurn = true;
                }
            };
            mHandler.postDelayed(mRunnable, 1200);
        }
    }

    private void endGame() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        mSoundOn = mPrefs.getBoolean("sound", true);
        mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        // 2 = maximum sounds ot play at the same time,
        // AudioManager.STREAM_MUSIC is the stream type typically used for games
        // 0 is the "the sample-rate converter quality. Currently has no effect. Use 0 for the default."
        switch (mHumanMoveSoundID = mSounds.load(this, R.raw.move_human_ding, 1)) {
        }
        //Context, id of resource, priority (currently no effect)
        switch (mComputerMoveSoundID = mSounds.load(this, R.raw.move_computer_ding, 1)) {
        }
        mGameTieSoundID = mSounds.load(this, R.raw.game_tie_buzzer, 1);
        mGameWonSoundID = mSounds.load(this, R.raw.game_won_tada, 1);
        mGameLostSoundID = mSounds.load(this, R.raw.game_lost_evillaugh, 1);
    }

    @Override
    protected void onPause () {
        super.onPause();
        Log.d(TAG, "in onPause");
        if(mSounds != null) {
            mSounds.release();
            mSounds = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stopComputerDelay();
        outState.putCharArray("mBoard", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("mTurn", mTurn);
    }

    private void stopComputerDelay() {
        if(mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Save current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mCountHuman", mCountHuman);
        ed.putInt("mCountTies", mCountTies);
        ed.putInt("mCountComputer", mCountComputer);

        ed.putInt("difficultyLevel", getDifficultyLevel());
        ed.apply();
    }
}