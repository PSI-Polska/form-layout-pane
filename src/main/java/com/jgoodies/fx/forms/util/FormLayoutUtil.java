//******************************************************************
//
//  FormLayoutUtil.java
//  Copyright 2015 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jgoodies.fx.forms.layout.CellConstraints;
import com.jgoodies.fx.forms.layout.FormLayoutPane;
import javafx.scene.Node;
import org.apache.commons.lang.StringUtils;



public class FormLayoutUtil
{
    public static final String ROW = "Row";
    public static final String COLUMN = "Column";

    /**
     * Checks if given row and column indexes are positive and less than upper bounds.
     *
     * @param aRow
     *            row index
     * @param aRowUpperBound
     *            row upper bound
     * @param aCol
     *            column index
     * @param aColUpperBound
     *            column upper bound
     */
    public static void checkIndexes( final int aRow, final int aRowUpperBound, final int aCol,
        final int aColUpperBound )
    {
        checkIndex( aRow, aRowUpperBound, ROW );
        checkIndex( aCol, aColUpperBound, COLUMN );
    }

    /**
     * Checks if given column index is positive and less than upper bound.
     *
     * @param aCol
     *            column index
     * @param aUpperBound
     *            upper bound
     */
    public static void checkIndex( final int aCol, final int aUpperBound, final String aWhat )
    {
        final String addition = Optional.ofNullable( aWhat ).map( s -> s + ' ' ).orElse( StringUtils.EMPTY );
        checkLowerBound( aCol, addition + aCol + " < 1!" );
        checkUpperBound( aCol, aUpperBound, addition + aCol + " > " + aUpperBound );
    }

    public static void checkColIndex( final int aIndex, final int aColCount )
    {
        checkIndex( aIndex, aColCount, FormLayoutUtil.COLUMN );
    }

    public static void checkRowIndex( final int aIndex, final int aRowCount )
    {
        checkIndex( aIndex, aRowCount, FormLayoutUtil.ROW );
    }

    /**
     * Checks if lower bound is violated. For rows and columns lower bound is 0.
     *
     * @param aIndex
     *            index to check
     * @param aMessage
     *            message to be displayed in case of lower bound violation
     */
    private static void checkLowerBound( final int aIndex, final String aMessage )
    {
        if( aIndex < 1 )
        {
            throw new IndexOutOfBoundsException( aMessage );
        }
    }

    /**
     * Checks if upper bound is violated.
     *
     * @param aIndex
     *            index to check
     * @param aUpperBound
     *            upper bound value
     * @param aMessage
     *            message to be displayed in case of lower bound violation
     */
    private static void checkUpperBound( final int aIndex, final int aUpperBound, final String aMessage )
    {
        if( aIndex > aUpperBound )
        {
            throw new IndexOutOfBoundsException( aMessage );
        }
    }

    /**
     * Converts current sizes of {@link FormLayoutPane} to origins.
     *
     * @param aCurrentSize
     * @return Array of origins.
     */
    public static Double[] currentSizesToOrigins( final double[] aCurrentSize )
    {
        final List< Double > origins = new ArrayList<>();

        double current = 0;
        origins.add( current );
        for( final double size : aCurrentSize )
        {
            origins.add( current + size );
            current += size;
        }

        return origins.toArray( new Double[ origins.size() ] );
    }

    /**
     * Get Node from FormLayoutPane by column, row index
     *
     * @param aFormPane
     * @param aColumnIndex
     * @param aRowIndex
     * @return
     */
    public static Node getNodeByIndex( final FormLayoutPane aFormPane, final int aColumnIndex, final int aRowIndex )
    {
        Node retNode = null;

        for( final Node child : aFormPane.getChildren() )
        {
            CellConstraints cc = null;
            if( child.isManaged() )
            {
                cc = aFormPane.getConstraints( child );
            }

            if( cc != null && cc.gridX == aColumnIndex
                && cc.gridY == aRowIndex )
            {
                retNode = child;
                break;
            }
        }

        return retNode;
    }

    /**
     * Reposition nodes in FormPane after adding a row
     * @param aFormPane
     * @param aAddedRowIndex
     */
    public static void recalcLayoutAfterRowAdded( final FormLayoutPane aFormPane, final int aAddedRowIndex )
    {
        for( final Node child : aFormPane.getChildren() )
        {
            CellConstraints cc = null;
            if( child.isManaged() )
            {
                cc = aFormPane.getConstraints( child );
            }
            if( cc != null && cc.gridY >= aAddedRowIndex)
            {
                cc.gridY++;
                FormLayoutPane.setConstraints( child, cc );
            }
        }
    }

    /**
     * Reposition nodes in FormPane after adding a column
     * @param aFormPane
     * @param aAddedColumnIndex
     */
    public static void recalcLayoutAfterColumnAdded( final FormLayoutPane aFormPane, final int aAddedColumnIndex )
    {
        for( final Node child : aFormPane.getChildren() )
        {
            CellConstraints cc = null;
            if( child.isManaged() )
            {
                cc = aFormPane.getConstraints( child );
            }
            if( cc != null && cc.gridX >= aAddedColumnIndex )
            {
                cc.gridX++;
                FormLayoutPane.setConstraints( child, cc );
            }
        }
    }

    /**
     * Remove row with all nodes it contains
     * @param aFormPane
     * @param aRowIndex
     */
    public static void removeRowWithNodes( final FormLayoutPane aFormPane, final int aRowIndex )
    {
        final List< Node > toRemove = new ArrayList<>();
        for( final Node child : aFormPane.getChildren() )
        {
            CellConstraints cc = null;
            if( child.isManaged() )
            {
                cc = aFormPane.getConstraints( child );
            }
            if( cc != null && cc.gridY == aRowIndex )
            {
                toRemove.add( child );
            }

        }

        aFormPane.getChildren().removeAll( toRemove );
        aFormPane.removeRow( aRowIndex );
//        aFormPane.layout();
    }

    /**
     * Remove column with all nodes it contains
     * @param aFormPane
     * @param aColumnIndex
     */
    public static void removeColumnWithNodes( final FormLayoutPane aFormPane, final int aColumnIndex )
    {
        final List< Node > toRemove = new ArrayList<>();
        for( final Node child : aFormPane.getChildren() )
        {
            CellConstraints cc = null;
            if( child.isManaged() )
            {
                cc = aFormPane.getConstraints( child );
            }
            if( cc != null && cc.gridX == aColumnIndex )
            {
                toRemove.add( child );
            }
        }

        aFormPane.getChildren().removeAll( toRemove );
        aFormPane.removeColumn( aColumnIndex );
//        aFormPane.layout();
    }

    /**
     * Get row index from coordinate
     * @param aFormPane
     * @param aCoordinate
     * @return
     */
    public static Integer getRowIndexFromCoordinate( final FormLayoutPane aFormPane, final double aCoordinate )
    {
        return getIndexFromCoordinate( aCoordinate, aFormPane.getLayoutInfo().rowOrigins );
    }

    /**
     * Get column index from coordinate
     * @param aFormPane
     * @param aCoordinate
     * @return
     */
    public static Integer getColumnIndexFromCoordinate( final FormLayoutPane aFormPane, final double aCoordinate )
    {
        return getIndexFromCoordinate( aCoordinate, aFormPane.getLayoutInfo().columnOrigins );
    }

    /**
     * Get index from coordiante
     * @see getColumnIndexFromCoordinate
     * @see getRowIndexFromCoordinate
     *
     * @param aCoordinate
     * @param aBounds
     * @return
     */
    protected static Integer getIndexFromCoordinate( final double aCoordinate, final double[] aBounds )
    {
        for( int i = 1; i < aBounds.length; i++ )
        {
            if( aCoordinate > aBounds[ i - 1 ] && aCoordinate < aBounds[ i ] )
            {
                return i;
            }
        }
        return null;
    }

    /**
     * Constructor.
     */
    private FormLayoutUtil()
    {
        // Utility class - no instantiation allowed.
    }
}


