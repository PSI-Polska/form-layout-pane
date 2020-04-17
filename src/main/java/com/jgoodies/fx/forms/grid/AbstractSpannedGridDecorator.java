//******************************************************************
//
//  AbstractSpannedGridDecorator.java
//  Copyright 2015 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.shape.Line;

import com.jgoodies.fx.forms.grid.FormLayoutGrid.GridType;

/**
 * Abstract decorator for spanned grid. Decorator is making gaps in lines when col/row is spanned.
 * @see HorizontalSpannedGridDecorator
 * @see VerticalSpannedGridDecorator
 *
 * @author created: gpasieka on 11 sie 2015 09:07:09
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public abstract class AbstractSpannedGridDecorator implements GridDecoratorIf
{
    protected static double SHARPEN_LINE_OFFSET = 0.5;
    protected static final String CELL_CONSTRAINTS_KEY = "CellConstraintsKey";

    private final GridType gridType;
    private final List< Line > spannedLines;
    private final FormLayoutGrid grid;
    private final List< Line > originalLines;
    private final List< LineGap > lineGaps;
    private final Map< Integer, List< IndexedLine > > sublinesMap;
    private ListChangeListener< Node > formPaneChildrenListener;
    private MapChangeListener< Object, Object > formPaneChildrenSpansListener;
    private ChangeListener< Number > gridLanesListener;

    AbstractSpannedGridDecorator( GridType aGridType, FormLayoutGrid aGrid )
    {
        gridType = aGridType;
        grid = aGrid;
        spannedLines = new ArrayList< Line >();
        originalLines = getOriginalLinesFromGrid( grid );
        lineGaps = new ArrayList< LineGap >();
        sublinesMap = new TreeMap< Integer, List<IndexedLine> >();
        formPaneChildrenListener = new FormPaneChildrenListener();
        formPaneChildrenSpansListener = new FormPaneChildrenContraintsListener();
        gridLanesListener = new GridLanesListener();

    }

    protected GridType getGridType()
    {
        return gridType;
    }

    protected FormLayoutGrid getGrid()
    {
        return grid;
    }

    protected List< Line > getOriginalLines()
    {
        return originalLines;
    }

    protected List< LineGap > getLineGaps()
    {
        return lineGaps;
    }

    protected Map< Integer, List< IndexedLine > > getSublinesMap()
    {
        return sublinesMap;
    }

    /**
     * Get original lines from grid: horizontal or vertical.
     * @param aGrid
     * @return
     */
    abstract protected List< Line > getOriginalLinesFromGrid( FormLayoutGrid aGrid );

    /**
     * Initialize spanned lines.
     */
    abstract protected void initializeLines();

    @Override
    public List< Line > getLines()
    {
        return spannedLines;
    }

    @Override
    public ListChangeListener< Node > getFormPaneChildrenListener()
    {
        return formPaneChildrenListener;
    }

    @Override
    public MapChangeListener< Object, Object > getFormPaneChildrenSpansListener()
    {
        return formPaneChildrenSpansListener;
    }

    @Override
    public ChangeListener< Number > getGridLanesListener()
    {
        return gridLanesListener;
    }

    private class FormPaneChildrenListener implements ListChangeListener< Node >
    {
        @Override
        public void onChanged( javafx.collections.ListChangeListener.Change< ? extends Node > aChange )
        {
            createLines();
        }
    }

    private class FormPaneChildrenContraintsListener implements MapChangeListener< Object, Object >
    {
        @Override
        public void onChanged(
            javafx.collections.MapChangeListener.Change< ? extends Object, ? extends Object > aChange )
        {
            if( aChange.getKey() instanceof String )
            {
                String key = (String)aChange.getKey();

                if( key.equals( CELL_CONSTRAINTS_KEY ) )
                {
                    createLines();
                }
            }
        }
    }

    private class GridLanesListener implements ChangeListener< Number >
    {
        @Override
        public void changed( ObservableValue< ? extends Number > observable, Number oldValue,
            Number newValue )
        {
            createLines();
        }
    }

    /**
     * Class representing a gap in line.
     * @see add further links
     *
     * @author created: gpasieka on 11 sie 2015 09:10:08
     * @author last change: $Author: $ on $Date: $
     * @version $Revision: $
     */
    protected class LineGap
    {
        private int colIndex;
        private int colSpan;
        private int rowIndex;
        private int rowSpan;

        /**
         * Constructor.
         * @param aColIndex
         * @param aColSpan
         * @param aRowIndex
         * @param aRowSpan
         */
        public LineGap( int aColIndex , int aColSpan , int aRowIndex , int aRowSpan  )
        {
            colIndex = aColIndex;
            colSpan = aColSpan;
            rowIndex = aRowIndex;
            rowSpan = aRowSpan;
        }

        public int getColIndex()
        {
            return colIndex;
        }
        public int getColSpan()
        {
            return colSpan;
        }

        public int getRowIndex()
        {
            return rowIndex;
        }

        public int getRowSpan()
        {
            return rowSpan;
        }
    }

    /**
     * Line from start to end index.
     *
     * @author created: gpasieka on 11 sie 2015 09:10:55
     * @author last change: $Author: $ on $Date: $
     * @version $Revision: $
     */
    protected class IndexedLine
    {
        private final Line line;
        private int startIndex;
        private int endIndex;

        /**
         * Constructor.
         * @param aLine
         * @param aStartIndex
         * @param aEndIndex
         */
        public IndexedLine( final Line aLine, final int aStartIndex, final int aEndIndex )
        {
            line = aLine;
            startIndex = aStartIndex;
            endIndex = aEndIndex;
        }

        public int getStartIndex()
        {
            return startIndex;
        }

        public void setStartIndex( int aStartIndex )
        {
            startIndex = aStartIndex;
        }

        public int getEndIndex()
        {
            return endIndex;
        }

        public void setEndIndex( int aEndIndex )
        {
            endIndex = aEndIndex;
        }

        public Line getLine()
        {
            return line;
        }

    }
}


