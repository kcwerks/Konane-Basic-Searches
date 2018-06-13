//     ************************************************************
//     * Name:  Kyle Calabro                                      *
//     * Project:  Konane AI Search - Project 2                   *
//     * Class:  CMPS 331 - Artificial Intelligence               *
//     * Date:  3/6/18                                            *
//     ************************************************************

package edu.ramapo.kcalabro.konane.model;

/**
 * Created by KyleCalabro on 2/13/18.
 */

public class Position
{
    //------------------------Data Members------------------------

    private int row;

    private int col;

    //------------------------Member Functions------------------------

    /**
     * Default constructor for the Position class.
     *
     * @param row the row position of the position.
     * @param col the column position of the position.
     */
    public Position(int row, int col)
    {
        setRowPosition(row);
        setColPosition(col);
    }

    /**
     * Getter function for the row position.
     *
     * @return The row position.
     */

    public int getRowPosition()
    {
        return row;
    }

    /**
     * Getter function for the column position.
     *
     * @return The column position.
     */

    public int getColPosition()
    {
        return col;
    }

    /**
     * Setter function for the row position.
     *
     * @param row The row position.
     */

    public void setRowPosition(int row)
    {
        this.row = row;
    }

    /**
     * Setter function for the column position.
     *
     * @param col The column position.
     */

    public void setColPosition(int col)
    {
        this.col = col;
    }
}
