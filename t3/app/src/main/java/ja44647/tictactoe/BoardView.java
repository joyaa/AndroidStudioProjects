package ja44647.tictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.tv.TvInputManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by joyal on 2016-02-18.
 */
public class BoardView extends View {

    //Width of the board grid lines
    public static final int GRID_LINE_WIDTH = 6;
    //public static final int GRID_WIDTH = ;

    //Bitmaps to store X and O images
    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;

    private Paint mPaint;

    private TicTacToeGame mGame;

    private static final String TAG = "TicTacToeGame";

    public BoardView(Context context) {
        super(context);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void setGame(TicTacToeGame game) {
        mGame = game;
    }

    public void initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_img);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_img);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //control color & thickness of lines for game board
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Determine the width and height of the vies
        int boardHeight = getHeight();
        int boardWidth = getWidth();

        //Make thick light grey lines
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(GRID_LINE_WIDTH);

        //Draw the vertical & horizontal board lines
        //vertical
        canvas.drawLine(boardWidth / 3, 0, boardWidth / 3, boardHeight, mPaint);
        canvas.drawLine(2 * boardWidth / 3, 0, 2 * boardWidth / 3, boardHeight, mPaint);
        //horizontal
        canvas.drawLine(0, boardHeight/3, boardWidth, boardHeight/3, mPaint);
        canvas.drawLine(0, 2*boardHeight/3, boardWidth, 2*boardHeight/3, mPaint);

        int cellWidth = boardWidth/3 - 2*GRID_LINE_WIDTH;
        //Draw all the X and O images
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;

            // Define the boundaries of a destination rectangle for the image
            int xTopLeft = ((col * boardWidth) / 3) + GRID_LINE_WIDTH;
            int yTopLeft = ((row * boardWidth) / 3) + GRID_LINE_WIDTH;
            int xBottomRight = (xTopLeft + cellWidth) - (2 * GRID_LINE_WIDTH);
            int yBottomRight = (yTopLeft + cellWidth) - (2 * GRID_LINE_WIDTH);

            if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,
                        null, // src
                        new Rect(xTopLeft, yTopLeft, xBottomRight, yBottomRight), // dest
                        null);
                Log.d(TAG, "X bitmap updated for move " + (i + 1));
            } else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,
                        null, // src
                        new Rect(xTopLeft, yTopLeft, xBottomRight, yBottomRight), // dest
                        null);
                Log.d(TAG, "O bitmap updated for move " + (i + 1));
            }
        }
    }
    public int getBoardCellWidth() {
        return getWidth() / 3;
    }
    public int getBoardCellHeight() {
        return getHeight() / 3;
    }
}