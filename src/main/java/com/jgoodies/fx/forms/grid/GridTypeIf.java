//******************************************************************
//
//  GridTypeIf.java
//  Copyright 2015 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.grid;

import java.util.List;

import javafx.scene.shape.Line;

import com.jgoodies.fx.forms.grid.FormLayoutGrid.LineOrientation;


/**
 * Painter interface for FormPaneGrid.
 * @see FormLayoutGrid
 * @see FormLayoutGrid.GridType
 *
 * @author created: gpasieka on 24 lip 2015 15:58:28
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
interface GridTypeIf
{
    /**
     * @return true if has outer lines (borders)
     */
    boolean hasOuterLines();

    /**
     * @return true if has inner lines
     */
    boolean hasInnerLines();

    /**
     * @return true if grid supports colspans/rowspans
     */
    boolean isSpanned();

    /**
     * @return compares FormLayoutPane SpecsNumber with lines List. If number is positive
     * such number of lines should be added to grid. If number is zero line number is correct.
     * If number is negative such number of lines should be removed from grid.
     */
    int countGridLines( List< Line > aLines, int aSpecCount );

    /**
     * Initialize lines of grid.
     * @param aLines
     * @param aSpec
     */
    void initializeLines( List< Line > aLines, int aSpecCount );

    /**
     * Add inner line.
     * @param aLines
     * @param aConstraintsNumber
     * @return new Line
     */
    Line addLine( List< Line > aLines, int aConstraintsNumber  );

    /**
     * Remove inner line.
     * @param aLines
     */
    void removeLine( List< Line > aLines);

    /**
     * @param aGrid.
     * @param aLineOrientation.
     * @return GridDecorator
     */
    GridDecoratorIf getGridDecorator( FormLayoutGrid aGrid, LineOrientation aLineOrientation );
}


