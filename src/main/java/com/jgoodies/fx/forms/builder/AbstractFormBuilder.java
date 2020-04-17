//******************************************************************
//
//  AbstractFormBuilder.java
//  Copyright 2016 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.builder;

import com.jgoodies.fx.forms.factories.FormFactory;
import com.jgoodies.fx.forms.layout.CellConstraints;
import com.jgoodies.fx.forms.layout.ColumnSpec;
import com.jgoodies.fx.forms.layout.FormLayoutPane;
import com.jgoodies.fx.forms.layout.RowSpec;

import javafx.scene.Node;

/**
 * An abstract class that minimizes the effort required to implement non-visual builders that use the
 * {@link FormLayout}.
 * <p>
 * Builders hide details of the FormLayout and provide convenience behavior that assists you in constructing a
 * form. This class provides a cell cursor that helps you traverse a form while you add components. Also, it
 * offers several methods to append custom and logical columns and rows.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.10 $
 * @see ButtonBarBuilder
 * @see ButtonStackBuilder
 * @see PanelBuilder
 * @see I15dPanelBuilder
 * @see DefaultFormBuilder
 */
public abstract class AbstractFormBuilder
{
    /**
     * Holds the instance of <code>FormLayoutPane</code>.
     */
    private final FormLayoutPane layoutPane;

    /**
     * Holds an instance of <code>CellConstraints</code> that will be used to specify the location, extent and
     * alignments of the component to be added next.
     */
    private final CellConstraints currentCellConstraints;

    /**
     * Specifies if we fill the grid from left to right or right to left. This value is initialized during the
     * construction from the layout container's component orientation.
     *
     * @see #isLeftToRight()
     * @see #setLeftToRight(boolean)
     * @see ComponentOrientation
     */
    private boolean leftToRight;

    // Instance Creation ****************************************************

    /**
     * Constructs a <code>AbstractFormBuilder</code> for the given FormLayout and layout container.
     *
     * @param layout
     *            the {@link FormLayout} to use
     * @param container
     *            the layout container
     * @throws NullPointerException
     *             if the layout or container is null
     */
    public AbstractFormBuilder( FormLayoutPane aLayoutPane )
    {
        if( aLayoutPane == null )
        {
            throw new NullPointerException( "The layout must not be null." );
        }

        layoutPane = aLayoutPane;

        currentCellConstraints = new CellConstraints();

        leftToRight = true;

        // ComponentOrientation orientation = container.getComponentOrientation();
        // leftToRight = orientation.isLeftToRight()
        // || !orientation.isHorizontal();
    }

    // Accessors ************************************************************

    /**
     * Returns the instance of {@link FormLayout} used to build this form.
     *
     * @return the FormLayout
     */
    public final FormLayoutPane getLayoutPane()
    {
        return layoutPane;
    }

    /**
     * Returns the number of columns in the form.
     *
     * @return the number of columns
     */
    public final int getColumnCount()
    {
        return getLayoutPane().getColumnCount();
    }

    /**
     * Returns the number of rows in the form.
     *
     * @return the number of rows
     */
    public final int getRowCount()
    {
        return getLayoutPane().getRowCount();
    }

    // Accessing the Cursor Direction ***************************************

    /**
     * Returns whether this builder fills the form left-to-right or right-to-left. The initial value of this
     * property is set during the builder construction from the layout container's
     * <code>componentOrientation</code> property.
     *
     * @return true indicates left-to-right, false indicates right-to-left
     * @see #setLeftToRight(boolean)
     * @see ComponentOrientation
     */
    public final boolean isLeftToRight()
    {
        return leftToRight;
    }

    /**
     * Sets the form fill direction to left-to-right or right-to-left. The initial value of this property is
     * set during the builder construction from the layout container's <code>componentOrientation</code>
     * property.
     *
     * @param b
     *            true indicates left-to-right, false right-to-left
     * @see #isLeftToRight()
     * @see ComponentOrientation
     */
    public final void setLeftToRight( boolean b )
    {
        leftToRight = b;
    }

    // Accessing the Cursor Location and Extent *****************************

    /**
     * Returns the cursor's column.
     *
     * @return the cursor's column
     */
    public final int getColumn()
    {
        return currentCellConstraints.gridX;
    }

    /**
     * Sets the cursor to the given column.
     *
     * @param column
     *            the cursor's new column index
     */
    public final void setColumn( int column )
    {
        currentCellConstraints.gridX = column;
    }

    /**
     * Returns the cursor's row.
     *
     * @return the cursor's row
     */
    public final int getRow()
    {
        return currentCellConstraints.gridY;
    }

    /**
     * Sets the cursor to the given row.
     *
     * @param row
     *            the cursor's new row index
     */
    public final void setRow( int row )
    {
        currentCellConstraints.gridY = row;
    }

    /**
     * Sets the cursor's column span.
     *
     * @param columnSpan
     *            the cursor's new column span (grid width)
     */
    public final void setColumnSpan( int columnSpan )
    {
        currentCellConstraints.gridWidth = columnSpan;
    }

    /**
     * Sets the cursor's row span.
     *
     * @param rowSpan
     *            the cursor's new row span (grid height)
     */
    public final void setRowSpan( int rowSpan )
    {
        currentCellConstraints.gridHeight = rowSpan;
    }

    /**
     * Sets the cursor's origin to the given column and row.
     *
     * @param column
     *            the new column index
     * @param row
     *            the new row index
     */
    public final void setOrigin( int column, int row )
    {
        setColumn( column );
        setRow( row );
    }

    /**
     * Sets the cursor's extent to the given column span and row span.
     *
     * @param columnSpan
     *            the new column span (grid width)
     * @param rowSpan
     *            the new row span (grid height)
     */
    public final void setExtent( int columnSpan, int rowSpan )
    {
        setColumnSpan( columnSpan );
        setRowSpan( rowSpan );
    }

    /**
     * Sets the cell bounds (location and extent) to the given column, row, column span and row span.
     *
     * @param column
     *            the new column index (grid x)
     * @param row
     *            the new row index (grid y)
     * @param columnSpan
     *            the new column span (grid width)
     * @param rowSpan
     *            the new row span (grid height)
     */
    public final void setBounds( int column, int row, int columnSpan, int rowSpan )
    {
        setColumn( column );
        setRow( row );
        setColumnSpan( columnSpan );
        setRowSpan( rowSpan );
    }

    /**
     * Moves to the next column, does the same as #nextColumn(1).
     */
    public final void nextColumn()
    {
        nextColumn( 1 );
    }

    /**
     * Moves to the next column.
     *
     * @param columns
     *            number of columns to move
     */
    public final void nextColumn( int columns )
    {
        currentCellConstraints.gridX += columns * getColumnIncrementSign();
    }

    /**
     * Increases the row by one; does the same as #nextRow(1).
     */
    public final void nextRow()
    {
        nextRow( 1 );
    }

    /**
     * Increases the row by the specified rows.
     *
     * @param rows
     *            number of rows to move
     */
    public final void nextRow( int rows )
    {
        currentCellConstraints.gridY += rows;
    }

    /**
     * Moves to the next line: increases the row and resets the column; does the same as #nextLine(1).
     */
    public final void nextLine()
    {
        nextLine( 1 );
    }

    /**
     * Moves the cursor down several lines: increases the row by the specified number of lines and sets the
     * cursor to the leading column.
     *
     * @param lines
     *            number of rows to move
     */
    public final void nextLine( int lines )
    {
        nextRow( lines );
        setColumn( getLeadingColumn() );
    }

    // Form Constraints Alignment *******************************************

    /**
     * Sets the horizontal alignment.
     *
     * @param alignment
     *            the new horizontal alignment
     */
    public final void setHAlignment( CellConstraints.Alignment alignment )
    {
        currentCellConstraints.hAlign = alignment;
    }

    /**
     * Sets the vertical alignment.
     *
     * @param alignment
     *            the new vertical alignment
     */
    public final void setVAlignment( CellConstraints.Alignment alignment )
    {
        currentCellConstraints.vAlign = alignment;
    }

    /**
     * Sets the horizontal and vertical alignment.
     *
     * @param hAlign
     *            the new horizontal alignment
     * @param vAlign
     *            the new vertical alignment
     */
    public final void setAlignment( CellConstraints.Alignment hAlign,
        CellConstraints.Alignment vAlign )
    {
        setHAlignment( hAlign );
        setVAlignment( vAlign );
    }

    // Appending Columns ******************************************************

    /**
     * Appends the given column specification to the builder's layout.
     *
     * @param columnSpec
     *            the column specification object to append
     * @see #appendColumn(String)
     */
    public final void appendColumn( ColumnSpec columnSpec )
    {
        getLayoutPane().appendColumn( columnSpec );
    }

    /**
     * Appends a column specification to the builder's layout that represents the given string encoding.
     *
     * @param encodedColumnSpec
     *            the column specification to append in encoded form
     * @see #appendColumn(ColumnSpec)
     */
    public final void appendColumn( String encodedColumnSpec )
    {
        appendColumn( ColumnSpec.decode( encodedColumnSpec ) );
    }

    /**
     * Appends a glue column.
     *
     * @see #appendLabelComponentsGapColumn()
     * @see #appendRelatedComponentsGapColumn()
     * @see #appendUnrelatedComponentsGapColumn()
     */
    public final void appendGlueColumn()
    {
        appendColumn( FormFactory.GLUE_COLSPEC );
    }

    /**
     * Appends a column that is the default gap between a label and its associated component.
     *
     * @since 1.0.3
     * @see #appendGlueColumn()
     * @see #appendRelatedComponentsGapColumn()
     * @see #appendUnrelatedComponentsGapColumn()
     */
    public final void appendLabelComponentsGapColumn()
    {
        appendColumn( FormFactory.LABEL_COMPONENT_GAP_COLSPEC );
    }

    /**
     * Appends a column that is the default gap for related components.
     *
     * @see #appendGlueColumn()
     * @see #appendLabelComponentsGapColumn()
     * @see #appendUnrelatedComponentsGapColumn()
     */
    public final void appendRelatedComponentsGapColumn()
    {
        appendColumn( FormFactory.RELATED_GAP_COLSPEC );
    }

    /**
     * Appends a column that is the default gap for unrelated components.
     *
     * @see #appendGlueColumn()
     * @see #appendLabelComponentsGapColumn()
     * @see #appendRelatedComponentsGapColumn()
     */
    public final void appendUnrelatedComponentsGapColumn()
    {
        appendColumn( FormFactory.UNRELATED_GAP_COLSPEC );
    }

    // Appending Rows ********************************************************

    /**
     * Appends the given row specification to the builder's layout.
     *
     * @param rowSpec
     *            the row specification object to append
     * @see #appendRow(String)
     */
    public final void appendRow( RowSpec rowSpec )
    {
        getLayoutPane().appendRow( rowSpec );
    }

    /**
     * Appends a row specification to the builder's layout that represents the given string encoding.
     *
     * @param encodedRowSpec
     *            the row specification to append in encoded form
     * @see #appendRow(RowSpec)
     */
    public final void appendRow( String encodedRowSpec )
    {
        appendRow( RowSpec.decode( encodedRowSpec ) );
    }

    /**
     * Appends a glue row.
     *
     * @see #appendRelatedComponentsGapRow()
     * @see #appendUnrelatedComponentsGapRow()
     * @see #appendParagraphGapRow()
     */
    public final void appendGlueRow()
    {
        appendRow( FormFactory.GLUE_ROWSPEC );
    }

    /**
     * Appends a row that is the default gap for related components.
     *
     * @see #appendGlueRow()
     * @see #appendUnrelatedComponentsGapRow()
     * @see #appendParagraphGapRow()
     */
    public final void appendRelatedComponentsGapRow()
    {
        appendRow( FormFactory.RELATED_GAP_ROWSPEC );
    }

    /**
     * Appends a row that is the default gap for unrelated components.
     *
     * @see #appendGlueRow()
     * @see #appendRelatedComponentsGapRow()
     * @see #appendParagraphGapRow()
     */
    public final void appendUnrelatedComponentsGapRow()
    {
        appendRow( FormFactory.UNRELATED_GAP_ROWSPEC );
    }

    /**
     * Appends a row that is the default gap for paragraphs.
     *
     * @since 1.0.3
     * @see #appendGlueRow()
     * @see #appendRelatedComponentsGapRow()
     * @see #appendUnrelatedComponentsGapRow()
     */
    public final void appendParagraphGapRow()
    {
        appendRow( FormFactory.PARAGRAPH_GAP_ROWSPEC );
    }

    // Adding Components ****************************************************

    /**
     * Adds a component to the panel using the given cell constraints.
     *
     * @param component
     *            the component to add
     * @param cellConstraints
     *            the component's cell constraints
     * @return the added component
     */
    public Node add( Node component, CellConstraints cellConstraints )
    {
        layoutPane.add( component, cellConstraints );
        return component;
    }

    /**
     * Adds a component to the panel using the given encoded cell constraints.
     *
     * @param component
     *            the component to add
     * @param encodedCellConstraints
     *            the component's encoded cell constraints
     * @return the added component
     */
    public final Node add( Node component, String encodedCellConstraints )
    {
        layoutPane.add( component, new CellConstraints( encodedCellConstraints ) );
        return component;
    }

    /**
     * Adds a component to the container using the default cell constraints. Note that when building from left
     * to right, this method won't adjust the cell constraints if the column span is larger than 1. In this
     * case you should use {@link #add(Component, CellConstraints)} with a cell constraints object created by
     * {@link #createLeftAdjustedConstraints(int)}.
     *
     * @param component
     *            the component to add
     * @return the added component
     * @see #add(Component, CellConstraints)
     * @see #createLeftAdjustedConstraints(int)
     */
    public final Node add( Node component )
    {
        add( component, currentCellConstraints );
        return component;
    }

    // Misc *****************************************************************

    /**
     * Returns the CellConstraints object that is used as a cursor and holds the current column span and row
     * span.
     *
     * @return the builder's current {@link CellConstraints} object
     */
    protected final CellConstraints cellConstraints()
    {
        return currentCellConstraints;
    }

    /**
     * Returns the index of the leading column.
     * <p>
     * Subclasses may override this method, for example, if the form has a leading gap column that should not
     * be filled with components.
     *
     * @return the leading column
     */
    protected int getLeadingColumn()
    {
        return isLeftToRight() ? 1 : getColumnCount();
    }

    /**
     * Returns the sign (-1 or 1) used to increment the cursor's column when moving to the next column.
     *
     * @return -1 for right-to-left, 1 for left-to-right
     */
    protected final int getColumnIncrementSign()
    {
        return isLeftToRight() ? 1 : -1;
    }

    /**
     * Creates and returns a <code>CellConstraints</code> object at the current cursor position that uses the
     * given column span and is adjusted to the left. Useful when building from right to left.
     *
     * @param columnSpan
     *            the column span to be used in the constraints
     * @return CellConstraints adjusted to the left hand side
     */
    protected final CellConstraints createLeftAdjustedConstraints( int columnSpan )
    {
        int firstColumn = isLeftToRight()
            ? getColumn()
            : getColumn() + 1 - columnSpan;
        return new CellConstraints( firstColumn, getRow(),
            columnSpan,
            cellConstraints().gridHeight );
    }

}
