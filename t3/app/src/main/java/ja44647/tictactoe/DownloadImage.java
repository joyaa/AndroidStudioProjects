package ja44647.tictactoe;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DownloadImage extends AppCompatActivity {

    TextView mDownloadingMessage;
    TextView mWinnerMessage;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_image);

        //Initialize TextViews and ImageView:
        mDownloadingMessage = (TextView) findViewById(R.id.message_downloading);
        mWinnerMessage = (TextView) findViewById(R.id.message_winner);
        mImageView = (ImageView) findViewById(R.id.image);

        //Set the TextView with the downloading image status to its initial value:
        mDownloadingMessage.setText(R.string.downloading_image);
    }

}
