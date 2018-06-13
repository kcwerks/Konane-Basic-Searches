//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project:  Konane AI Search - Project 2                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/6/18                                            *
//     ************************************************************

package edu.ramapo.kcalabro.konane.view;

/**
 * Created by KyleCalabro on 1/17/18.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.util.Pair;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Vector;

import edu.ramapo.kcalabro.konane.R;
import edu.ramapo.kcalabro.konane.model.Game;
import edu.ramapo.kcalabro.konane.model.Board;
import edu.ramapo.kcalabro.konane.model.Position;

public class GameActivity extends AppCompatActivity
{
    //------------------------Data Members------------------------

    public final static String WHITE_PLAYER = "White";
    public final static String BLACK_PLAYER = "Black";
    public final static String DEPTH_FIRST = "Depth-First";
    public final static String BEST_FIRST = "Best-First";
    public final static String BREADTH_FIRST = "Breadth-First";

    private Boolean stonePositionClicked;
    private Boolean vacantPositionClicked;
    private Boolean stonesBlinking;

    private int stoneRowPosition, stoneColPosition;
    private int vacantRowPosition, vacantColPosition;

    private BoardView boardView;

    private TextView blackScoreView;
    private TextView whiteScoreView;
    private TextView playerPassedView;
    private TextView currentPlayerView;
    private TextView stoneToMove;
    private TextView vacantPosition;
    private TextView algorithmScore;

    private Button makeMoveButton;
    private Button unselectAllButton;
    private Button passPlayButton;
    private Button saveGameButton;
    private Button nextMoveButton;
    private Button branchAndBoundButton;

    private Spinner algoSpinner;

    private Game game;
    private Board board;

    private String currentPlayer;
    private String selectedAlgo;

    private Vector<Position> selectedOpenPositions = new Vector<Position>();

    //------------------------Member Functions------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Removes the tile bar, for aesthetic purposes.
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Determine if the user wants to start a new game.
        Intent mainIntent = getIntent();
        boolean newGame = mainIntent.getExtras().getBoolean(MainActivity.EXTRA_NEWGAME);
        String fileName = mainIntent.getExtras().getString("selectedFile");

        boardView = new BoardView(this);

        algoSpinner = (Spinner) findViewById(R.id.algoSpinner);

        blackScoreView = findViewById(R.id.blackStoneScore);
        whiteScoreView = findViewById(R.id.whiteStoneScore);
        playerPassedView = findViewById(R.id.playerPassed);
        currentPlayerView = findViewById(R.id.currentPlayer);
        algorithmScore = findViewById(R.id.algoScore);

        makeMoveButton = findViewById(R.id.makeMoveButton);
        makeMoveButton.setOnClickListener(makeMoveButtonHandler);

        passPlayButton = findViewById(R.id.passPlayButton);
        passPlayButton.setOnClickListener(passPlayButtonHandler);

        branchAndBoundButton = findViewById(R.id.branchBoundButton);
        branchAndBoundButton.setOnClickListener(branchAndBoundButtonHandler);

        unselectAllButton = findViewById(R.id.unselectButton);
        unselectAllButton.setOnClickListener(unselectAllButtonHandler);

        saveGameButton = findViewById(R.id.saveGameButton);
        saveGameButton.setOnClickListener(saveGameButtonHandler);

        nextMoveButton = findViewById(R.id.nextMoveButton);
        nextMoveButton.setOnClickListener(nextMoveButtonHandler);

        //ArrayAdapter algoPickerAdapter = new ArrayAdapter<>(GameActivity.this, R.layout.spinner_field, R.array.algoArray);
        ArrayAdapter<CharSequence> algoPickerAdapter = ArrayAdapter.createFromResource(this, R.array.algoArray, R.layout.spinner_field);
        algoPickerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        algoSpinner.setAdapter(algoPickerAdapter);
        algoSpinner.setBackgroundResource(R.drawable.buttonborder);

        algoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    selectedAlgo = algoSpinner.getItemAtPosition(position).toString();
                    nextMoveButton.setEnabled(true);
                }
                else
                {
                    nextMoveButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // If the user wishes to start a new game.
        if(newGame)
        {
            game = new Game();
        }

        // Otherwise, the user wishes to load a game from a saved file.
        else
        {
            game = new Game();

            // Attempt to load a game from a saved file.
            if(game.getSerializer().restoreFile(game, fileName))
            {
                updateView(true);

            }
            else
            {
                generateToastMessage("That file does not exist! Try again.");
                Intent mainMenu = new Intent(GameActivity.this, MainActivity.class);
                startActivity(mainMenu);
                finish();
            }
        }

        stonePositionClicked = false;
        vacantPositionClicked = false;
        stonesBlinking = false;

        stoneRowPosition = 0;
        stoneColPosition = 0;

        vacantRowPosition = 0;
        vacantColPosition = 0;

        updateView(true);
    }

    /**
     * To place data in a bundle to be read from by an EndRoundActivity object.
     */

    public void endActivity()
    {
        Intent endGame = new Intent(GameActivity.this, EndGameActivity.class);
        endGame.putExtra("blackStonesScore", game.getPlayers()[0].getRoundScore());
        endGame.putExtra("whiteStonesScore", game.getPlayers()[1].getRoundScore());

        startActivity(endGame);
        finish();
    }

    //------------------------Output/Display Updating Functions------------------------

    /**
     * To display a toast message to the screen.
     *
     * @param message The String to be displayed to the screen via toast.
     */

    public void generateToastMessage(String message)
    {
        Toast toastToDisplay = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toastToDisplay.setGravity(Gravity.CENTER, 0, 0);
        toastToDisplay.show();
    }

    /**
     * To update the view with all the proper information.
     *
     * @param isActive Boolean value indicating if the board is to be displayed.
     */

    public void updateView(boolean isActive)
    {
        algorithmScore.setText(" ");

        // Determine if it is the black stone player's turn.
        if(game.getCurrentPlayer().equals(BLACK_PLAYER))
        {
            currentPlayerView.setText("Current Player: Ili Ele (Black Stones)");

            // Determine if the white player passed their previous play.
            if(game.getPlayers()[1].isPlayerPassed())
            {
                playerPassedView.setText("Previous Player Passed: Yes");
            }
            else
            {
                playerPassedView.setText("Previous Player Passed: No");
            }

            if(!game.getPlayers()[0].canMakePlay(game, true))
            {
                generateToastMessage("Sorry, you cannot make a move and must pass your play!");
                makeMoveButton.setEnabled(false);
                unselectAllButton.setEnabled(false);
                passPlayButton.setEnabled(true);
            }
            else
            {
                makeMoveButton.setEnabled(true);
                unselectAllButton.setEnabled(true);
            }
        }

        // Otherwise, it is the white stone player's turn.
        else
        {
            currentPlayerView.setText("Current Player: Ili Kea (White Stones)");

            // Determine if the human player passed their previous play.
            if(game.getPlayers()[0].isPlayerPassed())
            {
                playerPassedView.setText("Previous Player Passed: Yes");
            }
            else
            {
                playerPassedView.setText("Previous Player Passed: No");
            }

            if(!game.getPlayers()[1].canMakePlay(game, false))
            {
                generateToastMessage("Sorry, you cannot make a move and must pass your play!");
                passPlayButton.setEnabled(true);
                makeMoveButton.setEnabled(false);
                unselectAllButton.setEnabled(false);
            }
            else
            {
                makeMoveButton.setEnabled(true);
                unselectAllButton.setEnabled(true);
            }
        }

        // Update all the remaining TextView's to display proper information.
        blackScoreView.setText("Ili Ele (Black Stones) Score: " + game.getPlayers()[0].getRoundScore());
        whiteScoreView.setText("Ili Kea (White Stones) Score: " + game.getPlayers()[1].getRoundScore());

        // Update the board view.
        boardView.updateBoardView(game.getBoard());

        stonePositionClicked = false;
        vacantPositionClicked = false;
    }

    /**
     * To handle a human player clicking on a position of the grid.
     */

    public void makeMove(View view)
    {
        board = game.getBoard();
        currentPlayer = game.getCurrentPlayer();

        // Get the row and column positions of the clicked slot of the grid.
        int[] coordinates = getPositionCoordinates(view);

        int rowPosition = coordinates[0];
        int colPosition = coordinates[1];

        // If a stone's position has already been clicked, the next position must be a vacant one.
        if(stonePositionClicked)
        {
            if(board.isPositionOpen(rowPosition, colPosition))
            {
                vacantRowPosition = rowPosition;
                vacantColPosition = colPosition;

                vacantPositionClicked = true;

                highlightPosition(rowPosition, colPosition, false, true);

                Position newOpenPosition = new Position(rowPosition, colPosition);
                selectedOpenPositions.add(newOpenPosition);
            }

            else
            {
                String message = "Sorry, you must select a vacant puka for movement!";

                generateToastMessage(message);
            }
        }

        // Otherwise, the user is selecting a stone that they will use to make a jump.
        else
        {
            if (currentPlayer.equals(BLACK_PLAYER) && board.isPositionBlack(rowPosition, colPosition))
            {
                stonePositionClicked = true;

                stoneRowPosition= rowPosition;
                stoneColPosition = colPosition;

                highlightPosition(rowPosition, colPosition, true, false);
            }

            else if (currentPlayer.equals(WHITE_PLAYER) && board.isPositionWhite(rowPosition, colPosition))
            {
                stonePositionClicked = true;

                stoneRowPosition= rowPosition;
                stoneColPosition = colPosition;

                highlightPosition(rowPosition, colPosition, false, false);
            }

            else
            {
                String message = "Sorry, you are not able to select that stone for movement!";

                generateToastMessage(message);
            }
        }
    }

    /**
     * To unhighlight a given row and column position on the grid.
     *
     * @param row The row position.
     * @param col The column position.
     */

    private void unhighlightPosition(int row, int col)
    {
        // Find the id of the given slot in the grid representing the board.
        int slotId = getResources().getIdentifier("position_" +
                Integer.toString(row) + "_" + Integer.toString(col), "id", getPackageName());

        TextView slotView = findViewById(slotId);

        // Unhighlight that given position.
        slotView.setBackgroundResource(R.drawable.buttonborder);

        if(game.getBoard().isPositionBlack(row, col))
        {
            slotView.setBackgroundResource(R.drawable.black_stone_border);
        }
        else if(game.getBoard().isPositionWhite(row, col))
        {
            slotView.setBackgroundResource(R.drawable.white_stone_border);
        }
        else
        {
            slotView.setBackgroundResource(R.drawable.buttonborder);
        }
    }

    /**
     * To highlight a given row and column position on the grid.
     *
     * @param row The row position.
     * @param col The column position.
     * @param isBlack Boolean value indicating if the current player is using black stones.
     * @param isVacant Boolean value indicating if the given row/col position is open.
     */

    private void highlightPosition(int row, int col, boolean isBlack, boolean isVacant)
    {
        // Find the id of the given slot in the grid representing the board.
        int slotId = getResources().getIdentifier("position_" +
                Integer.toString(row) + "_" + Integer.toString(col), "id", getPackageName());

        TextView slotView = findViewById(slotId);

        if(isBlack)
        {
            slotView.setBackgroundResource(R.drawable.black_stone_border_hl);
        }

        else if(isVacant)
        {
            slotView.setBackgroundResource(R.drawable.buttonborder_hl);
        }

        // Otherwise, the position is a white stone.
        else
        {
            slotView.setBackgroundResource(R.drawable.white_stone_border_hl);
        }

    }

    /**
     * To get the coordinates of the grid position clicked on by the user.
     *
     * @param view The view from which the user clicked.
     * @return Array of two integers, [0] holds the row, [1] holds the column.
     */

    private int[] getPositionCoordinates(View view)
    {
        // Get the id string from the view.
        String positionCoordinates = getResources().getResourceEntryName(view.getId());

        // The pattern to be matched for position coordinates of the grid.
        String pattern = "(position_([0-6])_([0-6]))";

        // The regular expression to use in conjunction with the pattern.
        Pattern regex = Pattern.compile(pattern);

        // Parse the coordinates from the grid.
        Matcher matcher = regex.matcher(positionCoordinates);

        matcher.find();

        int rowPosition = Integer.parseInt(matcher.group(2));
        int colPosition = Integer.parseInt(matcher.group(3));

        int[] coordinates = new int[2];

        coordinates[0] = rowPosition;
        coordinates[1] = colPosition;

        return coordinates;
    }

    /**
     * To blink a given pair of positions representing stones when using an AI algorithm.
     *
     * @param pair The pair of positions to blink.
     */

    private void blinkStones(Pair<Position, Position> pair)
    {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(150);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        // Find the id of the given slot in the grid representing the board.
        int stoneId = getResources().getIdentifier("position_" +
                Integer.toString(pair.first.getRowPosition()) + "_" + Integer.toString(pair.first.getColPosition()), "id", getPackageName());

        stoneToMove = findViewById(stoneId);

        // Find the id of the given slot in the grid representing the board.
        int vacantId = getResources().getIdentifier("position_" +
                Integer.toString(pair.second.getRowPosition()) + "_" + Integer.toString(pair.second.getColPosition()), "id", getPackageName());

        vacantPosition = findViewById(vacantId);

        stoneToMove.startAnimation(animation);
        vacantPosition.startAnimation(animation);

        stonesBlinking = true;
    }

    /**
     * To stop the stones from blinking when using a search algorithm.
     */

    private void stopBlinkingStones()
    {
        stoneToMove.clearAnimation();
        vacantPosition.clearAnimation();
    }

    /**
     * To handle when the user clicks the Make Move button.
     */

    View.OnClickListener makeMoveButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if(stonePositionClicked && vacantPositionClicked)
            {
                int[] stonePosition = new int[]{stoneRowPosition, stoneColPosition};
                int[] vacantPosition = new int[]{vacantRowPosition, vacantColPosition};

                if(game.getCurrentPlayer() == Game.BLACK_PLAYER)
                {
                    if(game.getPlayers()[0].isMoveValid(game, true, stonePosition, selectedOpenPositions))
                    {
                        updateView(true);
                        selectedOpenPositions.clear();
                    }
                    else
                    {
                        generateToastMessage("Sorry, but that move is not valid! Try again.");
                    }
                }
                // Otherwise, the player is using white stones.
                else
                {
                    if(game.getPlayers()[1].isMoveValid(game, false, stonePosition, selectedOpenPositions))
                    {
                        updateView(true);
                        selectedOpenPositions.clear();
                    }
                    else
                    {
                        generateToastMessage("Sorry, but that move is not valid! Try again.");
                    }
                }

                if(stonesBlinking)
                {
                    stopBlinkingStones();
                }

                unhighlightPosition(stoneRowPosition, stoneColPosition);

                for(int pos = 0; pos < selectedOpenPositions.size(); pos++)
                {
                    int row = selectedOpenPositions.elementAt(pos).getRowPosition();
                    int col = selectedOpenPositions.elementAt(pos).getColPosition();

                    unhighlightPosition(row, col);
                }

                stonePositionClicked = false;
                vacantPositionClicked = false;
                selectedOpenPositions.clear();

            }

            else
            {
                String message = "Sorry, you must select a vacant position to move to!";

                generateToastMessage(message);
            }
        }
    });

    /**
     * To handle when the user clicks the Unselect All button.
     */

    View.OnClickListener unselectAllButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            stonePositionClicked = false;
            vacantPositionClicked = false;

            unhighlightPosition(stoneRowPosition, stoneColPosition);

            for(int pos = 0; pos < selectedOpenPositions.size(); pos++)
            {
                int row = selectedOpenPositions.elementAt(pos).getRowPosition();
                int col = selectedOpenPositions.elementAt(pos).getColPosition();

                unhighlightPosition(row, col);
            }

            selectedOpenPositions.clear();
        }
    });

    /**
     * To handle when the user clicks the Pass Play button.
     */

    View.OnClickListener passPlayButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if(game.hasGameEnded())
            {
                endActivity();
            }
            else
            {
                passPlayButton.setEnabled(false);
                game.swapCurrentPlayer();
                updateView(true);
            }
        }
    });

    /**
     * To handle when the user clicks the Save Game button.
     */

    View.OnClickListener saveGameButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            try
            {
                // Attempt to save the file.
                game.getSerializer().serializeFile(game);
            }
            catch(IOException ioException)
            {
                ioException.printStackTrace();
            }
            finish();
        }
    });

    /**
     * To handle when the user clicks the Save Game button.
     */

    View.OnClickListener branchAndBoundButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            game.branchAndBound();
            if(!game.getBranchAndBoundMoves().isEmpty())
            {
                Position oldPosition = game.branchAndBoundMoves.get(0).first.first;
                Position newPosition = game.branchAndBoundMoves.get(0).first.second;
                int scorePossible = game.branchAndBoundMoves.get(0).second;

                Pair<Position, Position> newPair = new Pair<>(oldPosition, newPosition);

                blinkStones(newPair);
                algorithmScore.setText(Integer.toString(scorePossible));
                game.branchAndBoundMoves.remove(0);
            }
            else
            {
                generateToastMessage("No available moves to display!");
            }
        }
    });

    /**
     * To handle when the user clicks the Next Move button.
     */

    View.OnClickListener nextMoveButtonHandler = (new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {

            if(stonesBlinking)
            {
                stopBlinkingStones();
            }

            Pair<Position, Position> newPair;

            if(selectedAlgo.equals(DEPTH_FIRST))
            {
                game.depthFirstSearch();
                if(!game.getDfsMoves().isEmpty())
                {
                    Position oldPosition = game.depthFirstMoves.get(0).first.first;
                    Position newPosition = game.depthFirstMoves.get(0).first.second;
                    int scorePossible = game.depthFirstMoves.get(0).second;

                    System.out.println(game.depthFirstMoves.get(0).second);

                    newPair = new Pair<>(oldPosition, newPosition);

                    blinkStones(newPair);
                    algorithmScore.setText(Integer.toString(scorePossible));
                    game.depthFirstMoves.remove(0);
                }
                else
                {
                    generateToastMessage("No available moves to display!");
                }
            }
            else if(selectedAlgo.equals(BREADTH_FIRST))
            {
                game.breadthFirstSearch();
                if(!game.getBfsMoves().isEmpty())
                {
                    Position oldPosition = game.breadthFirstMoves.get(0).first.first;
                    Position newPosition = game.breadthFirstMoves.get(0).first.second;
                    int scorePossible = game.breadthFirstMoves.get(0).second;

                    newPair = new Pair<>(oldPosition, newPosition);

                    blinkStones(newPair);
                    algorithmScore.setText(Integer.toString(scorePossible));
                    game.breadthFirstMoves.remove(0);

                }
                else
                {
                    generateToastMessage("No available moves to display!");
                }
            }
            else if(selectedAlgo.equals(BEST_FIRST))
            {
                game.bestFirstSearch();
                if(!game.getBestFirstSearchMoves().isEmpty())
                {
                    Position oldPosition = game.bestFirstSearchMoves.get(0).first.first;
                    Position newPosition = game.bestFirstSearchMoves.get(0).first.second;
                    int scorePossible = game.bestFirstSearchMoves.get(0).second;

                    newPair = new Pair<>(oldPosition, newPosition);

                    blinkStones(newPair);
                    algorithmScore.setText(Integer.toString(scorePossible));
                    game.bestFirstSearchMoves.remove(0);

                }
                else
                {
                    generateToastMessage("No available moves to display!");
                }
            }
            else
            {
                generateToastMessage("Sorry, you must select an algorithm to proceed!");
            }
        }
    });
}
