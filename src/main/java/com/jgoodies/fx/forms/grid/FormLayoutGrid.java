//******************************************************************
//
//  FormPaneGrid.java
//  Copyright 2015 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jgoodies.fx.forms.layout.FormLayoutPane;
import com.jgoodies.fx.forms.layout.FormLayoutPane.LayoutInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.shape.Line;

/**
 * Class for drawing formpane's configurable grid
 * @see FormLayoutPane
 *
 * @author created: gpasieka on 24 lip 2015 14:50:41
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class FormLayoutGrid
{
    final FormLayoutPane formPane;
    final private Group allLines;
    private GridType horizontalGridType;
    private GridType verticalGridType;

    final private List< Line > horizontalLines;
    private final GridDecoratorIf horizontalGridDecorator;
    final private List< Line > verticalLines;
    private final GridDecoratorIf verticalGridDecorator;

    private final ObservableList< String > lineStyleClasses;
    private String lineStyle;

    /**
     * When painting a line adding offset of 0.5 is needed in order
     * to paint 1px line (without it it will be not rendered as 1px).
     */
    private static double SHARPEN_LINE_OFFSET = 0.5;

    private ChangeListener< LayoutInfo > layoutInfoColListener = new ChangeListener<>()
    {
        @Override
        public void changed( final ObservableValue< ? extends LayoutInfo > observable,
            final LayoutInfo oldValue, final LayoutInfo newValue )
        {
            if( newValue.columnOrigins != null && newValue.columnOrigins.length > 0 )
            {
                onWidthsChanged( newValue.columnOrigins, newValue.columnOrigins.length );
            }
        }
    };

    private ChangeListener< LayoutInfo > layoutInfoRowListener = new ChangeListener<>()
    {
        @Override
        public void changed( final ObservableValue< ? extends LayoutInfo > observable,
            final LayoutInfo oldValue, final LayoutInfo newValue )
        {
            if( newValue.rowOrigins != null && newValue.rowOrigins.length > 0 )
            {
                onHeightsChanged( newValue.rowOrigins, newValue.rowOrigins.length );
            }
        }
    };

    /**
     * Constructor. By default sets all grid lines visible
     * @param aFormPane
     */
    public FormLayoutGrid( final FormLayoutPane aFormPane )
    {
        this( aFormPane, GridType.ALL);
    }

    /**
     * Constructor.
     * @param aFormPane
     * @param aGridType common grid type for vertical and horizontal lines
     */
    public FormLayoutGrid( final FormLayoutPane aFormPane, final GridType aGridType )
    {
        this( aFormPane, aGridType, aGridType);
    }

    /**
     * Constructor.
     * @param aFormPane
     * @param aHorizontalGridType
     * @param aVerticalGridType
     */
    public FormLayoutGrid( final FormLayoutPane aFormPane, final GridType aHorizontalGridType,
        final GridType aVerticalGridType )
    {
        Objects.requireNonNull( aFormPane );
        formPane = aFormPane;
        allLines = new Group();
        allLines.setManaged( false );
        allLines.setMouseTransparent( true );
        horizontalGridType = aHorizontalGridType;
        verticalGridType = aVerticalGridType;
        horizontalLines = new ArrayList<>();
        horizontalGridDecorator = horizontalGridType.getGridDecorator( this, LineOrientation.HORIZONTAL );
        verticalLines = new ArrayList<>();
        verticalGridDecorator = verticalGridType.getGridDecorator( this, LineOrientation.VERTICAL );
        lineStyleClasses = FXCollections.observableArrayList();

        initializeHorizontalLines();
        initializeVerticalLines();

        allLines.getChildren().addAll( horizontalLines );
        allLines.getChildren().addAll( verticalLines );

        setupListeners();
    }

    public void syncWithFormLayoutPane()
    {
        final int verticalLinesChange =
            verticalGridType.countGridLines( verticalLines, formPane.getColumnCount() );

        if( verticalLinesChange != 0 )
        {
            allLines.getChildren().removeAll( verticalLines );

            if( verticalLinesChange > 0 )
            {
                for( int ind = 0; ind < verticalLinesChange; ind++ )
                {
                    final Line added = verticalGridType.addLine( verticalLines, formPane.getColumnCount() );
                    if( added != null )
                    {
                        added.setEndX( 0.0 );
                        added.endYProperty().bind( formPane.heightProperty() );
                        Bindings.bindContent( added.getStyleClass(), lineStyleClasses );
                        added.setStyle( lineStyle );
                    }
                }
            }
            else
            {
                for( int ind = 0; ind < -verticalLinesChange; ind++ )
                {
                    verticalGridType.removeLine( verticalLines );
                }
            }

            verticalGridDecorator.createLines();
            allLines.getChildren().addAll( verticalLines );
        }

        final int horizontalLinesChange =
            horizontalGridType.countGridLines( horizontalLines, formPane.getRowCount() );

        if( horizontalLinesChange != 0 )
        {
            allLines.getChildren().removeAll( horizontalLines );

            if( horizontalLinesChange > 0 )
            {
                for( int ind = 0; ind < horizontalLinesChange; ind++ )
                {
                    final Line added = horizontalGridType.addLine( horizontalLines, formPane.getRowCount() );
                    if( added != null )
                    {
                        added.endXProperty().bind( formPane.widthProperty() );
                        added.setEndY( 0.0 );
                        Bindings.bindContent( added.getStyleClass(), lineStyleClasses );
                        added.setStyle( lineStyle );
                    }
                }
            }
            else
            {
                for( int ind = 0; ind < -horizontalLinesChange; ind++ )
                {
                    horizontalGridType.removeLine( horizontalLines );
                }
            }

            horizontalGridDecorator.createLines();
            allLines.getChildren().addAll( horizontalLines );
        }
    }

    private void setupListeners()
    {
        if( verticalGridType.hasInnerLines() )
        {
            formPane.layoutInfoProperty().addListener( layoutInfoColListener );
        }
        if( horizontalGridType.hasInnerLines() )
        {
            formPane.layoutInfoProperty().addListener( layoutInfoRowListener );
        }
        if( verticalGridDecorator.getFormPaneChildrenListener() != null )
        {
            formPane.getChildren().addListener( verticalGridDecorator.getFormPaneChildrenListener() );
        }
        if( horizontalGridDecorator.getFormPaneChildrenListener() != null )
        {
            formPane.getChildren().addListener( horizontalGridDecorator.getFormPaneChildrenListener() );
        }
        if( verticalGridDecorator.getFormPaneChildrenSpansListener() != null )
        {
            formPane.getChildren().forEach( child -> {
                child.getProperties().addListener( verticalGridDecorator.getFormPaneChildrenSpansListener() );
            });
        }
        if( horizontalGridDecorator.getFormPaneChildrenSpansListener() != null )
        {
            formPane.getChildren().forEach( child -> {
                child.getProperties().addListener( horizontalGridDecorator.getFormPaneChildrenSpansListener() );
            });
        }
        if( verticalGridDecorator.getGridLanesListener() != null )
        {
            formPane.rowCountProperty().addListener( verticalGridDecorator.getGridLanesListener() );
        }
        if( horizontalGridDecorator.getGridLanesListener() != null )
        {
            formPane.columnCountProperty().addListener( horizontalGridDecorator.getGridLanesListener() );
        }

    }

    private void initializeHorizontalLines()
    {
        horizontalGridType.initializeLines( horizontalLines, formPane.getRowCount() );
        horizontalLines.forEach(
            line ->
            {
                line.setStartX( 0.0 );
                line.setStartY( 0.0 );
                line.endXProperty().bind( formPane.widthProperty() );
                line.setEndY( 0.0 );
                Bindings.bindContent( line.getStyleClass(), lineStyleClasses );
            }
        );
    }

    private void initializeVerticalLines()
    {
        verticalGridType.initializeLines( verticalLines, formPane.getColumnCount() );
        verticalLines.forEach(
            line ->
            {
                line.setStartX( 0.0 );
                line.setStartY( 0.0 );
                line.setEndX( 0.0 );
                line.endYProperty().bind( formPane.heightProperty() );
                Bindings.bindContent( line.getStyleClass(), lineStyleClasses );
            }
        );
    }

    private void onWidthsChanged( final double[] aColumnOrigins, final int aOriginsLen )
    {
        int offset = 1;

        if(verticalGridType.hasOuterLines())
        {
            offset = 0;
        }

        if( aOriginsLen-offset > verticalLines.size() )
        {
            return;
        }

        Line currentLine = null;
        for( int ind=0; ind < aOriginsLen-offset-1; ind++ )
        {
            currentLine = verticalLines.get(ind);
            currentLine
                .setLayoutX( formPane.snapPositionX( aColumnOrigins[ ind + offset ] + SHARPEN_LINE_OFFSET ) );
        }
        currentLine = verticalLines.get(aOriginsLen-offset-1);
        currentLine.setLayoutX(
            formPane.snapPositionX( aColumnOrigins[ aOriginsLen - offset - 1 ] - SHARPEN_LINE_OFFSET ) );

        horizontalGridDecorator.layoutLines( aColumnOrigins );
    }

    private void onHeightsChanged( final double[] aRowOrigins, final int aOriginsLen )
    {
        int offset = 1;

        if(horizontalGridType.hasOuterLines())
        {
            offset = 0;
        }

        if( aOriginsLen-offset > horizontalLines.size() )
        {
            return;
        }

        Line currentLine = null;
        for( int ind=0; ind < aOriginsLen-offset-1; ind++ )
        {
            currentLine = horizontalLines.get(ind);
            currentLine
                .setLayoutY( formPane.snapPositionY( aRowOrigins[ ind + offset ] + SHARPEN_LINE_OFFSET ) );
        }
        currentLine = horizontalLines.get(aOriginsLen-offset-1);
        currentLine.setLayoutY(
            formPane.snapPositionY( aRowOrigins[ aOriginsLen - offset - 1 ] - SHARPEN_LINE_OFFSET ) );

        verticalGridDecorator.layoutLines( aRowOrigins );
    }

    public Group getAllLines()
    {
        return allLines;
    }

    public ObservableList< String > getAllLinesStylesheets()
    {
        return allLines.getStylesheets();
    }

    public ObservableList< String > getLineStyleClasses()
    {
        return lineStyleClasses;
    }

    public String getLineStyle()
    {
        return lineStyle;
    }

    public void setLineStyle( final String aLineStyle )
    {
        lineStyle = aLineStyle;
        allLines.getChildren().forEach( line -> line.setStyle( lineStyle  ) );
    }

    public Line getLine( final LineOrientation aOrient, final int aIndex )
    {
        Line retLine = null;

        switch( aOrient )
        {
            case HORIZONTAL:
                retLine = horizontalLines.get(aIndex);
                break;
            case VERTICAL:
                retLine = verticalLines.get(aIndex);
                break;
            default:
                retLine = null;
        }

        return retLine;
    }

    FormLayoutPane getFormPane()
    {
        return formPane;
    }

    List< Line > getHorizontalLines()
    {
        return horizontalLines;
    }

    List< Line > getVerticalLines()
    {
        return verticalLines;
    }

    public static enum LineOrientation implements LineOrientationIf
    {
        HORIZONTAL
        {
            @Override
            public GridDecoratorIf getGridDecorator( final GridType aGridType, final FormLayoutGrid aGrid )
            {
                return new HorizontalSpannedGridDecorator( aGridType, aGrid );
            }
        },
        VERTICAL
        {
            @Override
            public GridDecoratorIf getGridDecorator( final GridType aGridType, final FormLayoutGrid aGrid )
            {
                return new VerticalSpannedGridDecorator( aGridType, aGrid );
            }
        }
    }

    public static enum GridType implements GridTypeIf
    {
        /**
         * No grid lines
         */
        NONE
        {
            @Override
            public boolean hasOuterLines()
            {
                return false;
            }
            @Override
            public boolean hasInnerLines()
            {
                return false;
            }
            @Override
            public boolean isSpanned()
            {
                return false;
            }
            @Override
            public int countGridLines( final List< Line > aLines, final int aSpecCount )
            {
                return -aLines.size();
            }
            @Override
            public void initializeLines( final List< Line > aLines, final int aSpecCount )
            {
                // nothing to do there
            }
            @Override
            public Line addLine( final List< Line > aLines, final int aConstraintsNumber )
            {
                // nothing to do there
                return null;
            }
            @Override
            public void removeLine( final List< Line > aLines )
            {
                // nothing to do there
            }
            @Override
            public GridDecoratorIf getGridDecorator( final FormLayoutGrid aGrid,
                final LineOrientation aLineOrientation )
            {
                return NullGridDecorator.INSTANCE;
            }
        },
        /**
         * Only outer border (like css border/stroke)
         */
        OUTER
        {
            @Override
            public boolean hasOuterLines()
            {
                return true;
            }
            @Override
            public boolean hasInnerLines()
            {
                return false;
            }
            @Override
            public boolean isSpanned()
            {
                return false;
            }
            @Override
            public int countGridLines( final List< Line > aLines, final int aSpecCount )
            {
                return -(aLines.size() - 2);
            }
            @Override
            public void initializeLines( final List< Line > aLines, final int aSpecCount )
            {
                //top, bottom line
                aLines.add( new Line() );
                aLines.add( new Line() );
            }
            @Override
            public Line addLine( final List< Line > aLines, final int aConstraintsNumber )
            {
                return null;
                // nothing to do there
            }
            @Override
            public void removeLine( final List< Line > aLines )
            {
                // nothing to do there
            }
            @Override
            public GridDecoratorIf getGridDecorator( final FormLayoutGrid aGrid,
                final LineOrientation aLineOrientation )
            {
                return NullGridDecorator.INSTANCE;
            }
        },
        /**
         * Only inner grid lines
         */
        INNER
        {
            @Override
            public boolean hasOuterLines()
            {
                return false;
            }
            @Override
            public boolean hasInnerLines()
            {
                return true;
            }
            @Override
            public boolean isSpanned()
            {
                return false;
            }
            @Override
            public int countGridLines( final List< Line > aLines, final int aSpecCount )
            {
                return (aSpecCount - 1) - aLines.size();
            }
            @Override
            public void initializeLines( final List< Line > aLines, final int aSpecCount )
            {
                if( aSpecCount < 2)
                {
                    return;
                }
                for( int ind = 0; ind < aSpecCount-1; ind++ )
                {
                    aLines.add( new Line() );
                }
            }
            @Override
            public Line addLine( final List< Line > aLines, final int aConstraintsNumber )
            {
                Line newLine =  null;
                if( aConstraintsNumber > 1 )
                {
                    newLine = new Line();
                    aLines.add( newLine );
                }

                return newLine;
            }
            @Override
            public void removeLine( final List< Line > aLines )
            {
                if( aLines.size() > 0 )
                {
                    aLines.remove( aLines.size()-1 );
                }
            }
            @Override
            public GridDecoratorIf getGridDecorator( final FormLayoutGrid aGrid,
                final LineOrientation aLineOrientation )
            {
                return NullGridDecorator.INSTANCE;
            }
        },
        /**
         * Only inner grid lines, but grid line will be not drawn on Node if it occupies more than one cell (row/col spans)
         */
//        INNER_SPANS
//        {
//            @Override
//            public void initializeLines( List< Line > aLines, ObservableList< ? extends ConstraintsBase > aConstraints )
//            {
//                // TODO Auto-generated method stub
//
//            }
//        },
        /**
         * All grid lines are drawn INNER + OUTER
         */
        ALL
        {
            @Override
            public boolean hasOuterLines()
            {
                return true;
            }
            @Override
            public boolean hasInnerLines()
            {
                return true;
            }
            @Override
            public boolean isSpanned()
            {
                return false;
            }
            @Override
            public int countGridLines( final List< Line > aLines, final int aSpecCount )
            {
                return (aSpecCount + 1) - aLines.size();
            }
            @Override
            public void initializeLines( final List< Line > aLines, final int aSpecCount )
            {
                aLines.add( new Line() );
                aLines.add( new Line() );
                for(int ind=1; ind<aSpecCount; ind++)
                {
                    aLines.add( new Line() );
                }
            }
            @Override
            public Line addLine( final List< Line > aLines, final int aConstraintsNumber )
            {
                Line newLine = null;
                if( aConstraintsNumber > 1 )
                {
                    newLine = new Line();
                    newLine.setStartX( 0.0 );
                    newLine.setStartY( 0.0 );
                    aLines.add( newLine );
                }

                return newLine;
            }
            @Override
            public void removeLine( final List< Line > aLines )
            {
                if( aLines.size() > 2 )
                {
                    aLines.remove( aLines.size()-1 );
                }
            }
            @Override
            public GridDecoratorIf getGridDecorator( final FormLayoutGrid aGrid,
                final LineOrientation aLineOrientation )
            {
                return NullGridDecorator.INSTANCE;
            }
        },
        /**
         * All grid lines are drawn INNER + OUTER, but grid line will be not drawn on Node if it occupies more than one cell (row/col spans)
         */
        ALL_SPANS
        {
            @Override
            public boolean hasOuterLines()
            {
                return true;
            }
            @Override
            public boolean hasInnerLines()
            {
                return true;
            }
            @Override
            public boolean isSpanned()
            {
                return true;
            }
            @Override
            public int countGridLines( final List< Line > aLines, final int aSpecCount )
            {
                return (aSpecCount + 1) - aLines.size();
            }
            @Override
            public void initializeLines( final List< Line > aLines, final int aSpecCount )
            {
                aLines.add( new Line() );
                aLines.add( new Line() );
                for(int ind=1; ind<aSpecCount; ind++)
                {
                    aLines.add( new Line() );
                }
            }
            @Override
            public Line addLine( final List< Line > aLines, final int aConstraintsNumber )
            {
                Line newLine = null;
                if( aConstraintsNumber > 1 )
                {
                    newLine = new Line();
                    newLine.setStartX( 0.0 );
                    newLine.setStartY( 0.0 );
                    aLines.add( newLine );
                }

                return newLine;
            }
            @Override
            public void removeLine( final List< Line > aLines )
            {
                if( aLines.size() > 2 )
                {
                    aLines.remove( aLines.size()-1 );
                }
            }
            @Override
            public GridDecoratorIf getGridDecorator( final FormLayoutGrid aGrid,
                final LineOrientation aLineOrientation )
            {
                return aLineOrientation.getGridDecorator( this, aGrid );
            }
        }
    }

}


