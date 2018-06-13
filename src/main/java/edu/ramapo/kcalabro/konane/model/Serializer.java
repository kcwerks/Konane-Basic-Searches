//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project:  Konane AI Search - Project 2                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/6/18                                            *
//     ************************************************************


package edu.ramapo.kcalabro.konane.model;

/**
 * Created by KyleCalabro on 2/7/18.
 */

import android.os.Environment;
import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Serializer {
    //------------------------Data Members------------------------

    // The pattern to be matched for the board.
    private String pattern = "([BWO])";

    // The regular expression to use in conjunction with the pattern.
    private Pattern regex = Pattern.compile(pattern);

    //------------------------Member Functions------------------------

    /**
     * The default constructor for the Serializer class.
     */

    public Serializer()
    {
    }

    /**
     * To restore a game from a serialized file properly.
     *
     * @param game Game object representing the current game.
     * @param fileName The name of the serialized file to load from.
     * @return Boolean value indicating if the given file could be loaded.
     */

    public boolean restoreFile(Game game, String fileName)
    {
        File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File serializedFile = new File(sdcard, fileName);

        try {
            String fileLine;
            int lineNumber = 0;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(serializedFile));

            try {
                while ((fileLine = bufferedReader.readLine()) != null)
                {
                    if (!fileLine.equals(""))
                    {
                        lineNumber++;

                        // First line contains the Black player's score.
                        if (lineNumber == 1)
                        {
                            String blackPlayerScore = fileLine.substring(fileLine.indexOf(':') + 2);
                            int blackScore = Integer.parseInt(blackPlayerScore);

                            game.getBoard().setNumWhiteStonesCaptured(blackScore);
                            game.getPlayers()[0].setRoundScore(blackScore);
                        }

                        // Second line contains the White player's score.
                        else if (lineNumber == 2)
                        {
                            String whitePlayerScore = fileLine.substring(fileLine.indexOf(':') + 2);
                            int whiteScore = Integer.parseInt(whitePlayerScore);

                            game.getBoard().setNumBlackStonesCaptured(whiteScore);
                            game.getPlayers()[1].setRoundScore(whiteScore);
                        }

                        // Board data comes next.
                        else if (lineNumber >= 4 && lineNumber <= 9)
                        {
                            restoreBoard(game.getBoard(), fileLine, lineNumber - 4);
                        }

                        // Next Player data comes next.
                        else if (lineNumber == 10)
                        {
                            String nextPlayer = fileLine.substring(fileLine.indexOf(':') + 2);

                            if (nextPlayer.equals("Black"))
                            {
                               game.setCurrentPlayer(Game.BLACK_PLAYER);
                            }
                            else
                            {
                                game.setCurrentPlayer(Game.WHITE_PLAYER);
                            }
                        }
                    }
                }
                // Clean up the BufferedReader.
                bufferedReader.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                return false;
            }
        } catch (FileNotFoundException fileException) {
            fileException.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * To serialize a given game in the correct format.
     *
     * @param game Object of the game class representing the game to be saved.
     * @throws IOException If file could not be found.
     */

    public void serializeFile(Game game) throws IOException
    {
        // The filename to save the round as.
        String fileName = "SavedGame.txt";

        String filepath = Environment.getExternalStorageDirectory().toString();

        File serializedFile = new File(filepath, fileName);

        // If the file exists delete it so we can write a new file.
        if(serializedFile.exists())
        {
            serializedFile.delete();
        }

        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(serializedFile));

            // Write the black stone player's score to the file.
            bufferedWriter.write("Black: ");
            bufferedWriter.write(game.getPlayers()[0].getRoundScore() + "\n");

            // Write the white stone player's score to the file.
            bufferedWriter.write("White: ");
            bufferedWriter.write(game.getPlayers()[1].getRoundScore() + "\n");

            // Write the Board data to the file.
            bufferedWriter.write("Board: \n");
            serializeBoard(game.getBoard(), bufferedWriter);

            // Write the next player data to the file.
            bufferedWriter.write("Next Player: ");

            if(game.getCurrentPlayer() == Game.BLACK_PLAYER)
            {
                bufferedWriter.write("Black");
            }
            else
            {
                bufferedWriter.write("White");
            }

            // Clean up the buffered writer.
            bufferedWriter.close();
        }
        catch (FileNotFoundException fileException)
        {
            fileException.printStackTrace();
        }
    }

    //------------------------Restoring Functions------------------------

    /**
     * To restore the board properly.
     *
     * @param board Object of the board class to restore.
     * @param boardLine The string containing the pertinent data.
     * @param rowNum the row number for the current line of data.
     * @throws IOException
     */

    private void restoreBoard(Board board, String boardLine, int rowNum) throws IOException
    {
        Matcher matcher = regex.matcher(boardLine);

        int colNum = 0;

        // Traverse all the regular expression matches found in the String.
        while (matcher.find())
        {
            board.setBoardAtPosition(rowNum, colNum, matcher.group(1).charAt(0));
            colNum++;
        }
    }

    //------------------------Serializing Functions------------------------

    /**
     * To save the board to a file.
     *
     * @param board The Board object which to save to a file.
     * @param bufferedWriter The bufferedWriter which writes to the open serialization file.
     * @throws IOException
     */

    private void serializeBoard(Board board, BufferedWriter bufferedWriter) throws IOException {

        // Traverse the board and write it to the file.
        for(int row = 0; row < Game.BOARD_SIZE; row++)
        {
            for(int col = 0; col < Game.BOARD_SIZE; col++)
            {
                // Write the tile at the index to the file.
                bufferedWriter.write(board.getBoard()[row][col]);
                bufferedWriter.write(" ");
                System.out.print(board.getBoard()[row][col] + " ");
            }
            bufferedWriter.write("\n");
            System.out.println("");
        }
    }
}
