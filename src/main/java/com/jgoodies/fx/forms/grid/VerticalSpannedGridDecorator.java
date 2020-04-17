//******************************************************************
//
//  VerticalSpannedGridDecorator.java
//  Copyright 2015 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jgoodies.fx.forms.grid.FormLayoutGrid.GridType;
import com.jgoodies.fx.forms.layout.CellConstraints;
import com.jgoodies.fx.forms.layout.FormLayoutPane;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.shape.Line;

/**
 * Spanned grid decorator for vertical lines.
 *
 * @author created: gpasieka on 11 sie 2015 09:13:24
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class VerticalSpannedGridDecorator extends AbstractSpannedGridDecorator
{
    VerticalSpannedGridDecorator( final GridType aGridType, final FormLayoutGrid aGrid )
    {
        super( aGridType, aGrid );
    }

    @Override
    protected List< Line > getOriginalLinesFromGrid( final FormLayoutGrid aGrid )
    {
        return aGrid.getVerticalLines();
    }

    @Override
    public void createLines()
    {
        getLineGaps().clear();
        //first find all gaps in lines
        for( final Node child : getGrid().getFormPane().getChildren() )
        {
            if( child.isManaged() )
            {
                final CellConstraints cc = (CellConstraints)child.getProperties().get( CELL_CONSTRAINTS_KEY );

                if( cc.gridWidth > 1 )
                {
                    getLineGaps().add( new LineGap( cc.gridX-1, cc.gridWidth, cc.gridY-1, cc.gridHeight ) );
                }
            }
        }

        initializeLines( );
    }

    @Override
    protected void initializeLines()
    {
        final List< Line > originalLines = getOriginalLines();
        final List< Line > spannedLines = getLines();
        final List< LineGap > lineGaps = getLineGaps();
        final Map< Integer, List< IndexedLine > > sublinesMap = getSublinesMap();

        final int colOffset;
        if( getGridType().hasOuterLines() )
        {
            colOffset = 1;
        }
        else
        {
            colOffset = 0;
        }

        getGrid().getAllLines().getChildren().removeAll( spannedLines );
        spannedLines.clear();
        sublinesMap.clear();
        originalLines.forEach( line -> line.setVisible( true ) );

        for( int ind=colOffset; ind<originalLines.size()-colOffset; ind++ )
        {
            //get array of gaps for current row sorted by colIndex
            final int index = ind;
            final LineGap[] currentGaps = lineGaps.stream()
                .filter(
                    gap ->
                    {
                       int comp = gap.getColSpan() - index + gap.getColIndex();
                       return comp > 0 && comp < gap.getColSpan();
                    } )
                .sorted( (aLhs, aRhs) -> Integer.compare( aLhs.getRowIndex(), aRhs.getRowIndex() ) )
                .toArray( LineGap[]::new );

            int previousIndex = -1;
            for( int gapIndex = 0; gapIndex < currentGaps.length; gapIndex++ )
            {
                if( currentGaps[gapIndex].getRowIndex() - previousIndex > 1 )
                {
                    if( !sublinesMap.containsKey( ind ) )
                    {
                        sublinesMap.put( ind, new ArrayList<>() );
                    }

                    final List< IndexedLine > linesInCol = sublinesMap.get( ind );
                    final Line line = new Line();
                    final Line originalLine = originalLines.get( ind );
                    line.layoutXProperty().bind( originalLine.layoutXProperty() );
                    Bindings.bindContent( line.getStyleClass(), getGrid().getLineStyleClasses() );
                    line.styleProperty().bind( originalLine.styleProperty() );
                    linesInCol.add( new IndexedLine( line, previousIndex + 1,
                        currentGaps[gapIndex].getRowIndex() ) );
                    spannedLines.add( line );
                    originalLines.get( ind ).setVisible( false );
                }

                previousIndex = currentGaps[gapIndex].getRowIndex() + currentGaps[gapIndex].getRowSpan() - 1;
            }

            // last line
            final int lastIndex = getGrid().getFormPane().getRowCount();
            if( currentGaps.length > 0 && previousIndex != lastIndex )
            {
                if( !sublinesMap.containsKey( ind ) )
                {
                    sublinesMap.put( ind, new ArrayList<>() );
                }

                final List< IndexedLine > linesInCol = sublinesMap.get( ind );
                final Line line = new Line();
                final Line originalLine = originalLines.get( ind );
                line.layoutXProperty().bind( originalLine.layoutXProperty() );
                Bindings.bindContent( line.getStyleClass(), getGrid().getLineStyleClasses() );
                line.styleProperty().bind( originalLine.styleProperty() );
                linesInCol.add( new IndexedLine( line, previousIndex + 1, lastIndex ) );
                spannedLines.add( line );

                originalLines.get( ind ).setVisible( false );
            }
        }

        getGrid().getAllLines().getChildren().addAll( spannedLines );
    }

    @Override
    public void layoutLines( final double[] aOrigins )
    {
        final Map< Integer, List< IndexedLine > > sublinesMap = getSublinesMap();

        final Iterator< Integer > keyIterator = sublinesMap.keySet().iterator();

        final FormLayoutPane formPane = getGrid().formPane;

        while( keyIterator.hasNext() )
        {
            final Integer key = keyIterator.next();

            final List< IndexedLine > lines = sublinesMap.get( key );

            for( final IndexedLine spannedLine : lines )
            {
                final Line currentLine = spannedLine.getLine();
                final double startY = aOrigins[ spannedLine.getStartIndex() ];
                final double endY = aOrigins.length > spannedLine.getEndIndex()
                    ? aOrigins[ spannedLine.getEndIndex() ] : aOrigins[ aOrigins.length - 1 ];
                final double currentHeight = endY - startY;
                currentLine.setLayoutY( formPane.snapPositionY( startY ) + SHARPEN_LINE_OFFSET );
                currentLine.setEndY( formPane.snapSizeY( currentHeight ) - SHARPEN_LINE_OFFSET );
            }
        }
    }
}

