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

import java.util.Vector;

public class Human extends Player
{
    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Human class.
     *
     * @param stoneColor The color of stone the player is using for the game.
     */

    public Human(String stoneColor)
    {
        setStoneColor(stoneColor);
    }

    /**
     * To determine if the human player has a tile that can be placed.
     *
     * @param game game object representing the current round.
     * @return Boolean value indicating if the human player has a tile that can be placed in their hand.
     */

    public boolean canMakePlay(Game game, boolean isBlack)
    {
        // If the player can make a move, they must play.
        if(game.getBoard().canMakePlay(isBlack))
        {
            return true;
        }

        // If the player cannot make a move, they must pass their turn.
        else
        {
            setPlayerPassed(true);
            return false;
        }
    }

    /**
     * To determine if a move given by a human player is valid and can be made.
     *
     * @param stonePosition Array of integers containing the coordinates of the stone to be moved.
     * @param vacantPositions Vector of Positions containing the coordinates of the vacant positions.
     * @return Boolean indicating if the given move could be made.
     */

    public boolean isMoveValid(Game game, Boolean isBlack, int stonePosition[], Vector<Position> vacantPositions)
    {
        int[] stonePositionNew = new int[]{stonePosition[0], stonePosition[1]};

        if(game.getBoard().isMoveValid(isBlack, stonePosition, vacantPositions))
        {
            game.getBoard().modifyBoard(isBlack, stonePositionNew, vacantPositions);
            game.swapCurrentPlayer();

            if(isBlack)
            {
                setRoundScore(game.getBoard().getNumWhiteStonesCaptured());
            }
            else
            {
                setRoundScore(game.getBoard().getNumBlackStonesCaptured());
            }

            setPlayerPassed(false);
            return true;
        }

        else
        {
            return false;
        }
    }
}
