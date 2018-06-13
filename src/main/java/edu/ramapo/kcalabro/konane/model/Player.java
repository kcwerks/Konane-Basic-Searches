//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project:  Konane AI Search - Project 2                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/6/18                                            *
//     ************************************************************

package edu.ramapo.kcalabro.konane.model;

import java.util.Vector;

/**
 * Created by KyleCalabro on 1/17/18.
 */

public class Player
{
    //------------------------Data Members------------------------

    // The round score for the current round of a tournament.
    private int roundScore;

    // Whether or not the player has passed their play.
    private boolean playerPassed;

    // The color stones being used by a player for the game.
    private String stoneColor;

    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Player class.
     */

    public Player()
    {
        roundScore = 0;
        playerPassed = false;
    }

    /**
     * Getter function for a player's round score.
     *
     * @return Integer representing a player's round score.
     */

    public int getRoundScore()
    {
        return roundScore;
    }

    /**
     * Setter function for a player's round score.
     *
     * @param roundScore The value to set the player's round score to.
     */

    public void setRoundScore(int roundScore)
    {
        this.roundScore = roundScore;
    }

    /**
     * Determines if the player has passed their turn.
     *
     * @return Boolean value indicating if the player has passed their turn.
     */

    public boolean isPlayerPassed()
    {
        return playerPassed;
    }

    /**
     * Setter function for a player passing their turn.
     *
     * @param playerPassed Boolean value indicating if a player has passed their turn.
     */

    public void setPlayerPassed(boolean playerPassed)
    {
        this.playerPassed = playerPassed;
    }

    /**
     * Getter function for the color of stone's a player is using.
     *
     * @return String indicating the color of stones being used by a player.
     */

    public String getStoneColor()
    {
        return stoneColor;
    }

    /**
     * Setter function for the color of stone's a player is using.
     *
     * @param stoneColor String to assign the value of a player's stone color to.
     */

    public void setStoneColor(String stoneColor)
    {
        this.stoneColor = stoneColor;
    }

    /**
     * To determine if a given move is valid. (Virtual method).
     *
     * @param game Object of the Game class representing the current game.
     * @param stonePosition Array of integers containing the coordinates of the stone position.
     * @param vacantPositions Vector of positions containing the coordinates of the vacant position.
     * @param isBlack Boolean value indicating if the current player is the one using black stones.
     * @return Boolean value indicating if the move was valid.
     */

    public boolean isMoveValid(Game game, Boolean isBlack, int stonePosition[], Vector<Position> vacantPositions)
    {
        return false;
    }

    /**
     * To determine if a player can make a play.
     * @param game Object of the Game class representing the current game.
     * @param isBlack Boolean value indicating if the current player is the one using black stones.
     * @return Boolean value indicating if a player can make a move.
     */

    public boolean canMakePlay(Game game, boolean isBlack)
    {
        return false;
    }

}
