//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project:  Konane AI Search - Project 2                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/6/18                                            *
//     ************************************************************

package edu.ramapo.kcalabro.konane.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.ramapo.kcalabro.konane.R;

/**
 * Created by KyleCalabro on 1/25/18.
 */

public class EndGameActivity extends AppCompatActivity
{
    //------------------------Data Members------------------------

    private TextView whiteStoneScoreView;
    private TextView blackStoneScoreView;
    private TextView winnerView;

    private int blackStonesScore;
    private int whiteStonesScore;

    //------------------------Member Functions------------------------

    /**
     * onCreate function for the EndRoundActivity class.
     *
     * @param savedInstanceState Bundle containing pertinent data.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_endgame);
        Bundle bundle = getIntent().getExtras();

        blackStonesScore = bundle.getInt("blackStonesScore");
        whiteStonesScore = bundle.getInt("whiteStonesScore");

        whiteStoneScoreView = findViewById(R.id.whiteStoneScore);
        blackStoneScoreView = findViewById(R.id.blackStoneScore);
        winnerView = findViewById(R.id.winner);

        updateScores();
    }

    /**
     * To update the view with all the pertinent information.
     */

    public void updateScores()
    {
        blackStoneScoreView.setText("Ili Ele (Black Stones) Score: " + blackStonesScore);
        whiteStoneScoreView.setText("Ili Kea (White Stones) Score: " + whiteStonesScore);
        winnerView.setText("Winner: " + determineWinner());
    }

    /**
     * Button handler for the main menu button.
     *
     * @param view The view which we are currently in.
     */

    public void mainMenu(View view)
    {
        // Set the intent to the MainActivity class.
        Intent intent = new Intent(this, MainActivity.class);

        // Start the activity.
        startActivity(intent);
    }

    /**
     * To determine the winner of the game.
     *
     * @return String indicating the winner of the game.
     */

    private String determineWinner()
    {
        if(blackStonesScore > whiteStonesScore)
        {
            return "Ili Ele (Black Stones)";
        }
        else if(blackStonesScore < whiteStonesScore)
        {
            return "Ili Kea (White Stones)";
        }
        else
        {
            return "Draw";
        }
    }
}
