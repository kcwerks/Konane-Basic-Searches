//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project:  Konane AI Search - Project 2                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/6/18                                            *
//     ************************************************************

package edu.ramapo.kcalabro.konane.model;

import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Queue;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.util.Pair;

/**
 * Created by KyleCalabro on 1/17/18.
 */

public class Game
{
    //------------------------Data Members------------------------

    public final static String WHITE_PLAYER = "White";

    public final static String BLACK_PLAYER = "Black";

    // The board dimensions, as per the project description.
    public final static int BOARD_SIZE = 6;

    public ArrayList<Pair<Pair<Position, Position>, Integer>> depthFirstMoves;
    public ArrayList<Pair<Pair<Position, Position>, Integer>> breadthFirstMoves;
    public ArrayList<Pair<Pair<Position, Position>, Integer>> bestFirstSearchMoves;
    public ArrayList<Pair<Pair<Position, Position>, Integer>> branchAndBoundMoves;

    private ArrayList<Position> beginningDfsPositions;
    private ArrayList<Position> beginningBfsPositions;
    private ArrayList<Position> beginningBestFirstPositions;
    private ArrayList<Position> beginningBranchBoundPositions;

    // The board used throughout the game.
    private Board board;

    // The current player of the game.
    private String currentPlayer;

    // Array of players current playing the game.
    private Player[] players;

    private Serializer serializer;

    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Game class.
     */

    public Game()
    {
        // The first player is the one using black stones.
        currentPlayer = BLACK_PLAYER;

        board = new Board(BOARD_SIZE);

        serializer = new Serializer();

        players = new Player[2];
        players[0] = new Human(BLACK_PLAYER);
        players[1] = new Human(WHITE_PLAYER);

        board.removeInitialStones();

        depthFirstMoves = new ArrayList<>();
        beginningDfsPositions = new ArrayList<>();

        bestFirstSearchMoves = new ArrayList<>();
        beginningBestFirstPositions = new ArrayList<>();

        branchAndBoundMoves = new ArrayList<>();
        beginningBranchBoundPositions = new ArrayList<>();

        breadthFirstMoves = new ArrayList<>();
        beginningBfsPositions = new ArrayList<>();
    }

    /**
     * Getter function for the board being used by the game class.
     *
     * @return Object of the board class representing the current board of the game.
     */

    public Board getBoard()
    {
        return board;
    }

    /**
     * Getter function for the array of players used by the game class.
     *
     * @return Array of objects of the player class. 0 = Black Stones, 1 = White Stones.
     */

    public Player[] getPlayers()
    {
        return players;
    }

    /**
     * To swap the current player.
     */

    public void swapCurrentPlayer()
    {
        // If the current player is using black stones, swap the current player to the one
        // using white stones.
        if (currentPlayer.equals(BLACK_PLAYER))
        {
            this.currentPlayer = WHITE_PLAYER;
        }

        // And vice-versa.
        else
        {
            this.currentPlayer = BLACK_PLAYER;
        }

        depthFirstMoves.clear();
        breadthFirstMoves.clear();

        beginningDfsPositions.clear();
        beginningBfsPositions.clear();

        bestFirstSearchMoves.clear();
        beginningBestFirstPositions.clear();

        branchAndBoundMoves.clear();
        beginningBranchBoundPositions.clear();
    }

    /**
     * To determine if a game has ended.
     *
     * @return Boolean value indicating if a game has ended.
     */

    public Boolean hasGameEnded()
    {
        if(players[0].isPlayerPassed() && players[1].isPlayerPassed())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Getter function for the current player of the game.
     *
     * @return String indicating the current player of the game, "Black", "White".
     */

    public String getCurrentPlayer()
    {
        return currentPlayer;
    }

    /**
     * To set the current player.
     *
     * @param currentPlayer the current player of the game.
     */

    public void setCurrentPlayer(String currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }

    /**
     * To get the serializer object for use in restoring or saving a game.
     *
     * @return Object of the serializer class.
     */

    public Serializer getSerializer()
    {
        return serializer;
    }

    /**
     * To get the array list containing the moves of depth first search moves.
     *
     * @return Array list containing depth first search moves.
     */

    public ArrayList<Pair<Pair<Position, Position>, Integer>> getDfsMoves()
    {
        return depthFirstMoves;
    }

    /**
     * To get the array list containing the moves of breadth first search moves.
     *
     * @return Array list containing breadth first search moves.
     */

    public ArrayList<Pair<Pair<Position, Position>, Integer>> getBfsMoves()
    {
        return breadthFirstMoves;
    }

    /**
     * To get the array list containing the moves of best first search moves.
     *
     * @return Array list containing best first search moves.
     */

    public ArrayList<Pair<Pair<Position, Position>, Integer>> getBestFirstSearchMoves()
    {
        return bestFirstSearchMoves;
    }

    /**
     * To get the array list containing the moves of branch and bound moves.
     *
     * @return Array list containing branch and bound moves.
     */

    public ArrayList<Pair<Pair<Position, Position>, Integer>> getBranchAndBoundMoves()
    {
        return branchAndBoundMoves;
    }

    /**
     * To determine if any given position can be played.
     *
     * @param position The position from which to make a move.
     * @return Boolean value indicating if the position can be played.
     */

    public Boolean playablePosition(Position position)
    {
        int rowPosition = position.getRowPosition();
        int colPosition = position.getColPosition();

        Position newNorth = new Position(rowPosition - 2, colPosition);
        Position newSouth = new Position(rowPosition + 2, colPosition);
        Position newEast = new Position(rowPosition, colPosition + 2);
        Position newWest = new Position(rowPosition, colPosition - 2);

        boolean isBlack = false;

        if(currentPlayer.equals(BLACK_PLAYER))
        {
            isBlack = true;
        }

        if(board.isNorthAvailable(isBlack, position, newNorth))
        {
            return true;
        }

        if(board.isEastAvailable(isBlack, position, newEast))
        {
            return true;
        }

        if(board.isSouthAvailable(isBlack, position, newSouth))
        {
            return true;
        }

        if(board.isWestAvailable(isBlack, position, newWest))
        {
            return true;
        }

        return false;
    }

    /**
     * To build a Depth-First Search tree and execute the algorithm. Stops upon finding the
     * first available move.
     */

    public void depthFirstSearch()
    {
        Stack<Position> depthFirstStack = new Stack<>();
        HashSet<Position> visitedPositions = new HashSet<>();

        char turnColor = Board.WHITE_STONE;

        Boolean isBlack = false;

        if(currentPlayer.equals(BLACK_PLAYER))
        {
            isBlack = true;
            turnColor = Board.BLACK_STONE;
        }

        // Generate a list of available positions from which you can move from.
        for(int row = 0; row < BOARD_SIZE; row++)
        {
            for(int col = 0; col < BOARD_SIZE; col++)
            {
                Position possiblePosition = new Position(row, col);

                if(board.getStoneColorAtPosition(row, col) == turnColor && playablePosition(possiblePosition))
                {
                    beginningDfsPositions.add(possiblePosition);
                }
            }
        }

        if(beginningDfsPositions.isEmpty())
        {
            return;
        }

        depthFirstStack.push(beginningDfsPositions.get(0));

        HashMap<Pair<Position, Position>, Integer> scores = new HashMap<>();
        scores.put(new Pair<>(beginningDfsPositions.get(0), beginningDfsPositions.get(0)), 0);

        Position previousVisited = null;

        while(!depthFirstStack.empty())
        {
            Position visitedCoordinates = depthFirstStack.pop();

            if(visitedPositions.contains(visitedCoordinates))
            {
                continue;
            }

            visitedPositions.add(visitedCoordinates);

            int visitedRow = visitedCoordinates.getRowPosition();
            int visitedCol = visitedCoordinates.getColPosition();

            Position northPosition = new Position(visitedRow - 2, visitedCol);
            Position southPosition = new Position(visitedRow + 2, visitedCol);
            Position eastPosition = new Position(visitedRow, visitedCol + 2);
            Position westPosition = new Position(visitedRow, visitedCol - 2);

            // Check if there are any moves available in the order: North, East, South, West.

            if (board.isNorthAvailable(isBlack, visitedCoordinates, northPosition))
            {
                depthFirstStack.push(northPosition);

                Pair currentPair = new Pair<>(beginningDfsPositions.get(0), northPosition);

                int score = 1;

                if (previousVisited != null)
                {
                    Pair<Position, Position> previousPair = new Pair<>(beginningDfsPositions.get(0), visitedCoordinates);

                    if (scores.containsKey(previousPair))
                    {
                        score = scores.get(previousPair) + 1;
                    }
                }

                if (!scores.containsKey(currentPair))
                {
                    scores.put(currentPair, score);
                }

                if (!depthFirstMoves.contains(new Pair<>(new Pair<>(beginningDfsPositions.get(0), northPosition), scores.get(currentPair))))
                {
                    depthFirstMoves.add(new Pair<>(new Pair<>(beginningDfsPositions.get(0), northPosition), scores.get(currentPair)));
                }
            }

            if (board.isEastAvailable(isBlack, visitedCoordinates, eastPosition))
            {
                depthFirstStack.push(eastPosition);

                Pair currentPair = new Pair<>(beginningDfsPositions.get(0), eastPosition);

                int score = 1;

                if (previousVisited != null)
                {
                    Pair<Position, Position> previousPair = new Pair<>(beginningDfsPositions.get(0), visitedCoordinates);

                    if (scores.containsKey(previousPair))
                    {
                        score = scores.get(previousPair) + 1;
                    }
                }

                if (!scores.containsKey(currentPair))
                {
                    scores.put(currentPair, score);
                }

                if (!depthFirstMoves.contains(new Pair<>(new Pair<>(beginningDfsPositions.get(0), eastPosition), scores.get(currentPair))))
                {
                    depthFirstMoves.add(new Pair<>(new Pair<>(beginningDfsPositions.get(0), eastPosition), scores.get(currentPair)));
                }
            }

            if (board.isSouthAvailable(isBlack, visitedCoordinates, southPosition))
            {
                depthFirstStack.push(southPosition);

                Pair currentPair = new Pair<>(beginningDfsPositions.get(0), southPosition);

                int score = 1;

                if (previousVisited != null)
                {
                    Pair<Position, Position> previousPair = new Pair<>(beginningDfsPositions.get(0), visitedCoordinates);

                    if (scores.containsKey(previousPair))
                    {
                        score = scores.get(previousPair) + 1;
                    }
                }

                if (!scores.containsKey(currentPair))
                {
                    scores.put(currentPair, score);
                }

                if (!depthFirstMoves.contains(new Pair<>(new Pair<>(beginningDfsPositions.get(0), southPosition), scores.get(currentPair))))
                {
                    depthFirstMoves.add(new Pair<>(new Pair<>(beginningDfsPositions.get(0), southPosition), scores.get(currentPair)));
                }
            }

            if (board.isWestAvailable(isBlack, visitedCoordinates, westPosition))
            {
                depthFirstStack.push(westPosition);

                Pair currentPair = new Pair<>(beginningDfsPositions.get(0), westPosition);

                int score = 1;

                if (previousVisited != null)
                {
                    Pair<Position, Position> previousPair = new Pair<>(beginningDfsPositions.get(0), visitedCoordinates);

                    if (scores.containsKey(previousPair))
                    {
                        score = scores.get(previousPair) + 1;
                    }
                }

                if (!scores.containsKey(currentPair))
                {
                    scores.put(currentPair, score);
                }

                if (!depthFirstMoves.contains(new Pair<>(new Pair<>(beginningDfsPositions.get(0), westPosition), scores.get(currentPair))))
                {
                    depthFirstMoves.add(new Pair<>(new Pair<>(beginningDfsPositions.get(0), westPosition), scores.get(currentPair)));
                }
            }
            previousVisited = visitedCoordinates;
        }
        beginningDfsPositions.remove(0);
    }

    /**
     * To build a Breadth-First Search tree and execute the algorithm. Stops upon finding the
     * first available move.
     */

    public void breadthFirstSearch()
    {
        Queue<Position> breadthFirstQueue = new LinkedList<>();
        HashSet<Position> visitedPositions = new HashSet<>();

        char turnColor = Board.WHITE_STONE;

        Boolean isBlack = false;

        if(currentPlayer.equals(BLACK_PLAYER))
        {
            isBlack = true;
            turnColor = Board.BLACK_STONE;
        }

        // Generate a list of available positions from which you can move from.
        for(int row = 0; row < BOARD_SIZE; row++)
        {
            for(int col = 0; col < BOARD_SIZE; col++)
            {
                Position possiblePosition = new Position(row, col);

                if(board.getStoneColorAtPosition(row, col) == turnColor && playablePosition(possiblePosition))
                {
                    beginningBfsPositions.add(possiblePosition);
                }
            }
        }

        if(beginningBfsPositions.isEmpty())
        {
            return;
        }

        breadthFirstQueue.add(beginningBfsPositions.get(0));

        HashMap<Pair<Position, Position>, Integer> scores = new HashMap<>();
        scores.put(new Pair<>(beginningBfsPositions.get(0), beginningBfsPositions.get(0)), 0);

        Position previousVisited = null;

        while(!breadthFirstQueue.isEmpty())
        {
            Position visitedCoordinates = breadthFirstQueue.poll();

            if(visitedPositions.contains(visitedCoordinates))
            {
                continue;
            }

            visitedPositions.add(visitedCoordinates);

            int visitedRow = visitedCoordinates.getRowPosition();
            int visitedCol = visitedCoordinates.getColPosition();

            Position northPosition = new Position(visitedRow - 2, visitedCol);
            Position southPosition = new Position(visitedRow + 2, visitedCol);
            Position eastPosition = new Position(visitedRow, visitedCol + 2);
            Position westPosition = new Position(visitedRow, visitedCol - 2);

            // Check if there are any moves available in the order: North, East, South, West.

            if (board.isNorthAvailable(isBlack, visitedCoordinates, northPosition))
            {
                breadthFirstQueue.add(northPosition);

                Pair currentPair = new Pair<>(beginningBfsPositions.get(0), northPosition);

                int score = 1;

                if (previousVisited != null)
                {
                    Pair<Position, Position> previousPair = new Pair<>(beginningBfsPositions.get(0), visitedCoordinates);

                    if (scores.containsKey(previousPair))
                    {
                        score = scores.get(previousPair) + 1;
                    }
                }

                if (!scores.containsKey(currentPair))
                {
                    scores.put(currentPair, score);
                }

                if (!breadthFirstMoves.contains(new Pair<>(new Pair<>(beginningBfsPositions.get(0), northPosition), scores.get(currentPair))))
                {
                    breadthFirstMoves.add(new Pair<>(new Pair<>(beginningBfsPositions.get(0), northPosition), scores.get(currentPair)));
                }
            }

            if (board.isEastAvailable(isBlack, visitedCoordinates, eastPosition))
            {
                breadthFirstQueue.add(eastPosition);

                Pair currentPair = new Pair<>(beginningBfsPositions.get(0), eastPosition);

                int score = 1;

                if (previousVisited != null)
                {
                    Pair<Position, Position> previousPair = new Pair<>(beginningBfsPositions.get(0), visitedCoordinates);

                    if (scores.containsKey(previousPair))
                    {
                        score = scores.get(previousPair) + 1;
                    }
                }

                if (!scores.containsKey(currentPair))
                {
                    scores.put(currentPair, score);
                }

                if (!breadthFirstMoves.contains(new Pair<>(new Pair<>(beginningBfsPositions.get(0), eastPosition), scores.get(currentPair))))
                {
                    breadthFirstMoves.add(new Pair<>(new Pair<>(beginningBfsPositions.get(0), eastPosition), scores.get(currentPair)));
                }
            }

            if (board.isSouthAvailable(isBlack, visitedCoordinates, southPosition))
            {
                breadthFirstQueue.add(southPosition);

                Pair currentPair = new Pair<>(beginningBfsPositions.get(0), southPosition);

                int score = 1;

                if (previousVisited != null)
                {
                    Pair<Position, Position> previousPair = new Pair<>(beginningBfsPositions.get(0), visitedCoordinates);

                    if (scores.containsKey(previousPair))
                    {
                        score = scores.get(previousPair) + 1;
                    }
                }

                if (!scores.containsKey(currentPair))
                {
                    scores.put(currentPair, score);
                }

                if (!breadthFirstMoves.contains(new Pair<>(new Pair<>(beginningBfsPositions.get(0), southPosition), scores.get(currentPair))))
                {
                    breadthFirstMoves.add(new Pair<>(new Pair<>(beginningBfsPositions.get(0), southPosition), scores.get(currentPair)));
                }
            }

            if (board.isWestAvailable(isBlack, visitedCoordinates, westPosition))
            {
                breadthFirstQueue.add(westPosition);

                Pair currentPair = new Pair<>(beginningBfsPositions.get(0), westPosition);

                int score = 1;

                if (previousVisited != null)
                {
                    Pair<Position, Position> previousPair = new Pair<>(beginningBfsPositions.get(0), visitedCoordinates);

                    if (scores.containsKey(previousPair))
                    {
                        score = scores.get(previousPair) + 1;
                    }
                }

                if (!scores.containsKey(currentPair))
                {
                    scores.put(currentPair, score);
                }

                if (!breadthFirstMoves.contains(new Pair<>(new Pair<>(beginningBfsPositions.get(0), westPosition), scores.get(currentPair))))
                {
                    breadthFirstMoves.add(new Pair<>(new Pair<>(beginningBfsPositions.get(0), westPosition), scores.get(currentPair)));
                }
            }
            previousVisited = visitedCoordinates;
        }
        beginningBfsPositions.remove(0);
    }

    /**
     * Method to build best first search tree and execute algorithm.
     */

    public void bestFirstSearch()
    {
        HashMap<Pair<Position, Position>, Integer> heuristic = new HashMap<>();

        char turnColor = Board.WHITE_STONE;

        Boolean isBlack = false;

        if(currentPlayer.equals(BLACK_PLAYER))
        {
            isBlack = true;
            turnColor = Board.BLACK_STONE;
        }

        // Generate a list of available positions from which you can move from.
        for(int row = 0; row < BOARD_SIZE; row++)
        {
            for(int col = 0; col < BOARD_SIZE; col++)
            {
                Position possiblePosition = new Position(row, col);

                if(board.getStoneColorAtPosition(row, col) == turnColor && playablePosition(possiblePosition))
                {
                    beginningBestFirstPositions.add(possiblePosition);
                }
            }
        }

        if(beginningBestFirstPositions.isEmpty())
        {
            return;
        }

        while(!beginningBestFirstPositions.isEmpty())
        {
            Stack<Position> dfsStack = new Stack<>();
            HashSet<Position> visitedPositions = new HashSet<>();

            Position startingPosition = new Position(beginningBestFirstPositions.get(0).getRowPosition(), beginningBestFirstPositions.get(0).getColPosition());
            dfsStack.add(startingPosition);

            heuristic.put(new Pair<>(startingPosition, startingPosition), 0);

            Position previousVisited = null;

            while (!dfsStack.empty()) {
                Position visitedPosition = dfsStack.pop();

                if (visitedPositions.contains(visitedPosition)) {
                    continue;
                }

                visitedPositions.add(visitedPosition);

                int visitedRow = visitedPosition.getRowPosition();
                int visitedCol = visitedPosition.getColPosition();

                Position northPosition = new Position(visitedRow - 2, visitedCol);
                Position southPosition = new Position(visitedRow + 2, visitedCol);
                Position eastPosition = new Position(visitedRow, visitedCol + 2);
                Position westPosition = new Position(visitedRow, visitedCol - 2);

                // Check if there are any moves available in the order: North, East, South, West.

                if (board.isNorthAvailable(isBlack, visitedPosition, northPosition))
                {
                    dfsStack.push(northPosition);

                    Pair currentPair = new Pair<>(startingPosition, northPosition);

                    int heuristicValue = 1;

                    if (previousVisited != null) {
                        Pair<Position, Position> previousPair = new Pair<>(startingPosition, visitedPosition);

                        if (heuristic.containsKey(previousPair)) {
                            heuristicValue = heuristic.get(previousPair) + 1;
                        }
                    }

                    if (!heuristic.containsKey(currentPair)) {
                        heuristic.put(currentPair, heuristicValue);
                    }
                }

                if (board.isEastAvailable(isBlack, visitedPosition, eastPosition)) {
                    dfsStack.push(eastPosition);

                    Pair currentPair = new Pair<>(startingPosition, eastPosition);

                    int heuristicValue = 1;

                    if (previousVisited != null) {
                        Pair<Position, Position> previousPair = new Pair<>(startingPosition, visitedPosition);

                        if (heuristic.containsKey(previousPair)) {
                            heuristicValue = heuristic.get(previousPair) + 1;
                        }
                    }

                    if (!heuristic.containsKey(currentPair)) {
                        heuristic.put(currentPair, heuristicValue);
                    }
                }

                if (board.isSouthAvailable(isBlack, visitedPosition, southPosition)) {
                    dfsStack.push(southPosition);

                    Pair currentPair = new Pair<>(startingPosition, southPosition);
                    
                    int heuristicValue = 1;

                    if (previousVisited != null) {
                        Pair<Position, Position> previousPair = new Pair<>(startingPosition, visitedPosition);

                        if (heuristic.containsKey(previousPair)) {
                            heuristicValue = heuristic.get(previousPair) + 1;
                        }
                    }

                    if (!heuristic.containsKey(currentPair)) {
                        heuristic.put(currentPair, heuristicValue);
                    }
                }

                if (board.isWestAvailable(isBlack, visitedPosition, westPosition)) {
                    dfsStack.push(westPosition);

                    Pair currentPair = new Pair<>(startingPosition, westPosition);

                    int heuristicValue = 1;

                    if (previousVisited != null) {
                        Pair<Position, Position> previousPair = new Pair<>(startingPosition, visitedPosition);

                        if (heuristic.containsKey(previousPair)) {
                            heuristicValue = heuristic.get(previousPair) + 1;
                        }
                    }

                    if (!heuristic.containsKey(currentPair)) {
                        heuristic.put(currentPair, heuristicValue);
                    }
                }
                previousVisited = visitedPosition;
            }
            beginningBestFirstPositions.remove(0);
        }

        for(Pair<Position, Position> position : heuristic.keySet())
        {
            if(heuristic.get(position) > 0)
            {
                bestFirstSearchMoves.add(new Pair<>(position, heuristic.get(position)));
            }
        }

        // Sort the available moves based on the highest score.

        for(int i = 0; i < bestFirstSearchMoves.size() - 1; i++)
        {
            for(int y = 0; y < bestFirstSearchMoves.size() - i - 1; y++)
            {
                if(heuristic.get(bestFirstSearchMoves.get(y).first) != null)
                {
                    // Swap the available moves accordingly
                    if (heuristic.get(bestFirstSearchMoves.get(y).first) < heuristic.get(bestFirstSearchMoves.get(y + 1).first))
                    {
                        Collections.swap(bestFirstSearchMoves, y, y + 1);
                    }
                    if (heuristic.get(bestFirstSearchMoves.get(y).first) == heuristic.get(bestFirstSearchMoves.get(y + 1).first))
                    {
                        if (bestFirstSearchMoves.get(y).first.first.getRowPosition() > bestFirstSearchMoves.get(y + 1).first.first.getRowPosition())
                        {
                            Collections.swap(bestFirstSearchMoves, y, y + 1);
                        }
                        else if (bestFirstSearchMoves.get(y).first.first.getRowPosition() == bestFirstSearchMoves.get(y + 1).first.first.getRowPosition())
                        {
                            if (bestFirstSearchMoves.get(y).first.first.getColPosition() > bestFirstSearchMoves.get(y + 1).first.first.getColPosition())
                            {
                                Collections.swap(bestFirstSearchMoves, y, y + 1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * To execute the branch and bound algorithm on a tree.
     */

    public void branchAndBound() {
        char turnColor = Board.WHITE_STONE;

        Boolean isBlack = false;

        if (currentPlayer.equals(BLACK_PLAYER)) {
            isBlack = true;
            turnColor = Board.BLACK_STONE;
        }

        // Generate a list of available positions from which you can move from.
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position possiblePosition = new Position(row, col);

                if (board.getStoneColorAtPosition(row, col) == turnColor && playablePosition(possiblePosition)) {
                    beginningBranchBoundPositions.add(possiblePosition);
                }
            }
        }

        if (beginningBranchBoundPositions.isEmpty())
        {
            return;
        }

        HashMap<Pair<Position, Position>, Integer> depths = new HashMap<>();
        Pair<Position, Position> maxDepthPair = new Pair<>(beginningBranchBoundPositions.get(0), beginningBranchBoundPositions.get(0));

        while (!beginningBranchBoundPositions.isEmpty())
        {
            Stack<Position> dfsStack = new Stack<>();
            HashSet<Position> visitedPositions = new HashSet<>();

            Position startingPosition = new Position(beginningBranchBoundPositions.get(0).getRowPosition(), beginningBranchBoundPositions.get(0).getColPosition());
            dfsStack.push(startingPosition);

            depths.put(new Pair<>(startingPosition, startingPosition), 0);
            Position previousVisited = null;

            while (!dfsStack.empty())
            {
                Position visitedPosition = dfsStack.pop();

                if (visitedPositions.contains(visitedPosition))
                {
                    continue;
                }

                visitedPositions.add(visitedPosition);

                int visitedRow = visitedPosition.getRowPosition();
                int visitedCol = visitedPosition.getColPosition();

                Position northPosition = new Position(visitedRow - 2, visitedCol);
                Position southPosition = new Position(visitedRow + 2, visitedCol);
                Position eastPosition = new Position(visitedRow, visitedCol + 2);
                Position westPosition = new Position(visitedRow, visitedCol - 2);

                int[] stonePosition = new int[]{startingPosition.getRowPosition(), startingPosition.getColPosition()};

                Vector<Position> northPositions = new Vector<>();
                northPositions.add(northPosition);

                Vector<Position> southPositions = new Vector<>();
                northPositions.add(southPosition);

                Vector<Position> eastPositions = new Vector<>();
                northPositions.add(eastPosition);

                Vector<Position> westPositions = new Vector<>();
                northPositions.add(westPosition);

                if (board.isMoveValid(isBlack, stonePosition, northPositions))
                {
                    dfsStack.push(northPosition);
                    Pair currentPair = new Pair<>(startingPosition, northPosition);

                    int depth = 1;

                    if (previousVisited != null)
                    {
                        Pair<Position, Position> previousPair = new Pair<>(startingPosition, visitedPosition);
                        if (depths.containsKey(previousPair))
                        {
                            depth = depths.get(previousPair) + 1;
                        }
                    }
                    if (!depths.containsKey(currentPair)) {
                        depths.put(currentPair, depth);
                    }

                    if(depths.get(currentPair) != null && depths.get(maxDepthPair) != null) {
                        if (depths.get(currentPair) > depths.get(maxDepthPair)) {
                            maxDepthPair = currentPair;
                        }
                    }
                }

                if (board.isMoveValid(isBlack, stonePosition, eastPositions))
                {
                    dfsStack.push(eastPosition);
                    Pair currentPair = new Pair<>(startingPosition, eastPosition);

                    int depth = 1;

                    if (previousVisited != null)
                    {
                        Pair<Position, Position> previousPair = new Pair<>(startingPosition, visitedPosition);
                        if (depths.containsKey(previousPair))
                        {
                            depth = depths.get(previousPair) + 1;
                        }
                    }
                    if (!depths.containsKey(currentPair)) {
                        depths.put(currentPair, depth);
                    }

                    if(depths.get(currentPair) != null && depths.get(maxDepthPair) != null) {
                        if (depths.get(currentPair) > depths.get(maxDepthPair)) {
                            maxDepthPair = currentPair;
                        }
                    }
                }

                if (board.isMoveValid(isBlack, stonePosition, southPositions))
                {
                    dfsStack.push(southPosition);
                    Pair currentPair = new Pair<>(startingPosition, southPosition);

                    int depth = 1;

                    if (previousVisited != null)
                    {
                        Pair<Position, Position> previousPair = new Pair<>(startingPosition, visitedPosition);
                        if (depths.containsKey(previousPair))
                        {
                            depth = depths.get(previousPair) + 1;
                        }
                    }
                    if (!depths.containsKey(currentPair)) {
                        depths.put(currentPair, depth);
                    }

                    if(depths.get(currentPair) != null && depths.get(maxDepthPair) != null) {
                        if (depths.get(currentPair) > depths.get(maxDepthPair)) {
                            maxDepthPair = currentPair;
                        }
                    }
                }

                if (board.isMoveValid(isBlack, stonePosition, westPositions))
                {
                    dfsStack.push(westPosition);
                    Pair currentPair = new Pair<>(startingPosition, westPosition);

                    int depth = 1;

                    if (previousVisited != null)
                    {
                        Pair<Position, Position> previousPair = new Pair<>(startingPosition, visitedPosition);
                        if (depths.containsKey(previousPair))
                        {
                            depth = depths.get(previousPair) + 1;
                        }
                    }
                    if (!depths.containsKey(currentPair)) {
                        depths.put(currentPair, depth);
                    }
                    if(depths.get(currentPair) != null && depths.get(maxDepthPair) != null) {
                        if (depths.get(currentPair) > depths.get(maxDepthPair)) {
                            maxDepthPair = currentPair;
                        }
                    }
                }
                previousVisited = visitedPosition;
            }
            beginningBranchBoundPositions.remove(0);
        }


        Stack<Position> secondaryDfsStack = new Stack<>();
        HashSet<Position> secondaryVisitedPositions = new HashSet<>();

        secondaryDfsStack.push(maxDepthPair.first);

        while(!secondaryDfsStack.empty())
        {
            Position secondaryVisitedPosition = secondaryDfsStack.pop();

            if (secondaryVisitedPositions.contains(secondaryVisitedPosition))
            {
                continue;
            }

            secondaryVisitedPositions.add(secondaryVisitedPosition);

            int secondaryVisitedRow = secondaryVisitedPosition.getRowPosition();
            int secondaryVisitedCol = secondaryVisitedPosition.getRowPosition();

            Position secondaryNorthPosition = new Position(secondaryVisitedRow - 2, secondaryVisitedCol);
            Position secondarySouthPosition = new Position(secondaryVisitedRow + 2, secondaryVisitedCol);
            Position secondaryEastPosition = new Position(secondaryVisitedRow, secondaryVisitedCol + 2);
            Position secondaryWestPosition = new Position(secondaryVisitedRow, secondaryVisitedCol - 2);

            int[] secondaryStonePosition = new int[]{secondaryVisitedRow, secondaryVisitedCol};

            Vector<Position> northPositions = new Vector<>();
            northPositions.add(secondaryNorthPosition);

            Vector<Position> southPositions = new Vector<>();
            southPositions.add(secondarySouthPosition);

            Vector<Position> eastPositions = new Vector<>();
            eastPositions.add(secondaryEastPosition);

            Vector<Position> westPositions = new Vector<>();
            westPositions.add(secondaryWestPosition);

            if (board.isMoveValid(isBlack, secondaryStonePosition, northPositions))
            {
                secondaryDfsStack.push(secondaryNorthPosition);
                Pair currentPair = new Pair<>(maxDepthPair.first, secondaryNorthPosition);

                if (!branchAndBoundMoves.contains(new Pair<>(new Pair<>(maxDepthPair.first, secondaryNorthPosition), depths.get(currentPair)))) {
                    branchAndBoundMoves.add(new Pair<>(new Pair<>(maxDepthPair.first, secondaryNorthPosition), depths.get(currentPair)));
                }
            }
            if (board.isMoveValid(isBlack, secondaryStonePosition, eastPositions))
            {
                secondaryDfsStack.push(secondaryEastPosition);
                Pair currentPair = new Pair<>(maxDepthPair.first, secondaryEastPosition);

                if (!branchAndBoundMoves.contains(new Pair<>(new Pair<>(maxDepthPair.first, secondaryEastPosition), depths.get(currentPair)))) {
                    branchAndBoundMoves.add(new Pair<>(new Pair<>(maxDepthPair.first, secondaryEastPosition), depths.get(currentPair)));
                }
            }
            if (board.isMoveValid(isBlack, secondaryStonePosition, southPositions))
            {
                secondaryDfsStack.push(secondarySouthPosition);
                Pair currentPair = new Pair<>(maxDepthPair.first, secondarySouthPosition);

                if (!branchAndBoundMoves.contains(new Pair<>(new Pair<>(maxDepthPair.first, secondarySouthPosition), depths.get(currentPair)))) {
                    branchAndBoundMoves.add(new Pair<>(new Pair<>(maxDepthPair.first, secondarySouthPosition), depths.get(currentPair)));
                }
            }
            if (board.isMoveValid(isBlack, secondaryStonePosition, westPositions))
            {
                secondaryDfsStack.push(secondaryWestPosition);
                Pair currentPair = new Pair<>(maxDepthPair.first, secondaryWestPosition);

                if (!branchAndBoundMoves.contains(new Pair<>(new Pair<>(maxDepthPair.first, secondaryWestPosition), depths.get(currentPair)))) {
                    branchAndBoundMoves.add(new Pair<>(new Pair<>(maxDepthPair.first, secondaryWestPosition), depths.get(currentPair)));
                }
            }
        }
    }
}
