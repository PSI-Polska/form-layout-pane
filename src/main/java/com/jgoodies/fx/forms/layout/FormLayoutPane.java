/*
 * Copyright (c) 2002-2008 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.fx.forms.layout;

import java.awt.Container;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * FormLayout is a powerful, flexible and precise general purpose layout manager. It aligns components
 * vertically and horizontally in a dynamic rectangular grid of cells, with each component occupying one or
 * more cells. A <a href="../../../../../whitepaper.pdf" target="secondary">whitepaper</a> about the
 * FormLayout ships with the product documentation and is available
 * <a href="http://www.jgoodies.com/articles/forms.pdf">online</a>.
 * <p>
 * To use FormLayout you first define the grid by specifying the columns and rows. In a second step you add
 * components to the grid. You can specify columns and rows via human-readable String descriptions or via
 * arrays of {@link ColumnSpec} and {@link RowSpec} instances.
 * <p>
 * Each component managed by a FormLayout is associated with an instance of {@link CellConstraints}. The
 * constraints object specifies where a component should be located on the form's grid and how the component
 * should be positioned. In addition to its constraints object the <code>FormLayout</code> also considers each
 * component's minimum and preferred sizes in order to determine a component's size.
 * <p>
 * FormLayout has been designed to work with non-visual builders that help you specify the layout and fill the
 * grid. For example, the {@link com.jgoodies.forms.builder.ButtonBarBuilder2} assists you in building button
 * bars; it creates a standardized FormLayout and provides a minimal API that specializes in adding buttons
 * and Actions. Other builders can create frequently used panel design, for example a form that consists of
 * rows of label-component pairs.
 * <p>
 * FormLayout has been prepared to work with different types of sizes as defined by the {@link Size}
 * interface.
 * <p>
 * <strong>Example 1</strong> (Plain FormLayout):<br>
 * The following example creates a panel with 3 data columns and 3 data rows; the columns and rows are
 * specified before components are added to the form.
 *
 * <pre>
 * FormLayout layout = new FormLayout( &quot;right:pref, 6dlu, 50dlu, 4dlu, default&quot;, // columns
 *     &quot;pref, 3dlu, pref, 3dlu, pref&quot; ); // rows
 *
 * CellConstraints cc = new CellConstraints();
 * JPanel panel = new JPanel( layout );
 * panel.add( new JLabel( &quot;Label1&quot; ), cc.xy( 1, 1 ) );
 * panel.add( new JTextField(), cc.xywh( 3, 1, 3, 1 ) );
 * panel.add( new JLabel( &quot;Label2&quot; ), cc.xy( 1, 3 ) );
 * panel.add( new JTextField(), cc.xy( 3, 3 ) );
 * panel.add( new JLabel( &quot;Label3&quot; ), cc.xy( 1, 5 ) );
 * panel.add( new JTextField(), cc.xy( 3, 5 ) );
 * panel.add( new JButton( &quot;/u2026&quot; ), cc.xy( 5, 5 ) );
 * return panel;
 * </pre>
 * <p>
 * <strong>Example 2</strong> (Using PanelBuilder):<br>
 * This example creates the same panel as above using the {@link com.jgoodies.forms.builder.PanelBuilder} to
 * add components to the form.
 *
 * <pre>
 * FormLayout layout = new FormLayout( &quot;right:pref, 6dlu, 50dlu, 4dlu, default&quot;, // columns
 *     &quot;pref, 3dlu, pref, 3dlu, pref&quot; ); // rows
 *
 * PanelBuilder builder = new PanelBuilder( layout );
 * CellConstraints cc = new CellConstraints();
 * builder.addLabel( &quot;Label1&quot;, cc.xy( 1, 1 ) );
 * builder.add( new JTextField(), cc.xywh( 3, 1, 3, 1 ) );
 * builder.addLabel( &quot;Label2&quot;, cc.xy( 1, 3 ) );
 * builder.add( new JTextField(), cc.xy( 3, 3 ) );
 * builder.addLabel( &quot;Label3&quot;, cc.xy( 1, 5 ) );
 * builder.add( new JTextField(), cc.xy( 3, 5 ) );
 * builder.add( new JButton( &quot;/u2026&quot; ), cc.xy( 5, 5 ) );
 * return builder.getPanel();
 * </pre>
 * <p>
 * <strong>Example 3</strong> (Using DefaultFormBuilder):<br>
 * This example utilizes the {@link com.jgoodies.forms.builder.DefaultFormBuilder} that ships with the source
 * distribution.
 *
 * <pre>
 * FormLayout layout = new FormLayout( &quot;right:pref, 6dlu, 50dlu, 4dlu, default&quot; ); // 5 columns; add rows
 *                                                                                 // later
 *
 * DefaultFormBuilder builder = new DefaultFormBuilder( layout );
 * builder.append( &quot;Label1&quot;, new JTextField(), 3 );
 * builder.append( &quot;Label2&quot;, new JTextField() );
 * builder.append( &quot;Label3&quot;, new JTextField() );
 * builder.append( new JButton( &quot;/u2026&quot; ) );
 * return builder.getPanel();
 * </pre>
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.19 $
 * @see ColumnSpec
 * @see RowSpec
 * @see CellConstraints
 * @see com.jgoodies.forms.builder.AbstractFormBuilder
 * @see com.jgoodies.forms.builder.ButtonBarBuilder
 * @see com.jgoodies.forms.builder.DefaultFormBuilder
 * @see com.jgoodies.fx.forms.factories.FormFactory
 * @see Size
 * @see Sizes
 */
public class FormLayoutPane extends Pane
{

    private Consumer< FormLayoutPane > preLayoutCallback;

    public static final String CELL_CONSTRAINTS_KEY = "CellConstraintsKey";

    // Instance Fields ********************************************************

    /**
     * Holds the column specifications.
     *
     * @see ColumnSpec
     * @see #getColumnCount()
     * @see #getColumnSpec(int)
     * @see #appendColumn(ColumnSpec)
     * @see #insertColumn(int, ColumnSpec)
     * @see #removeColumn(int)
     */
    private final ObservableList< ColumnSpec > colSpecs;

    private final ReadOnlyListProperty< ColumnSpec > colSpecsProperty;

    /**
     * Holds column number.
     */
    private final ReadOnlyIntegerWrapper columnCount = new ReadOnlyIntegerWrapper( 0 );

    /**
     * Holds the row specifications.
     *
     * @see RowSpec
     * @see #getRowCount()
     * @see #getRowSpec(int)
     * @see #appendRow(RowSpec)
     * @see #insertRow(int, RowSpec)
     * @see #removeRow(int)
     */
    private final ObservableList< RowSpec > rowSpecs;

    private final ReadOnlyListProperty< RowSpec > rowSpecsProperty;

    /**
     * Holds row number.
     */
    private final ReadOnlyIntegerWrapper rowCount = new ReadOnlyIntegerWrapper( 0 );

    /**
     * Holds the column groups as an array of arrays of column indices.
     *
     * @see #getColumnGroups()
     * @see #setColumnGroups(int[][])
     * @see #addGroupedColumn(int)
     */
    private int[][] colGroupIndices;

    /**
     * Holds the row groups as an array of arrays of row indices.
     *
     * @see #getRowGroups()
     * @see #setRowGroups(int[][])
     * @see #addGroupedRow(int)
     */
    private int[][] rowGroupIndices;

    // private boolean honorsVisibility = true;

    // Fields used by the Layout Algorithm ************************************

    /**
     * Holds the components that occupy exactly one column. For each column we keep a list of these
     * components.
     */
    private transient List< Node >[] colComponents;

    /**
     * Holds the components that occupy exactly one row. For each row we keep a list of these components.
     */
    private transient List< Node >[] rowComponents;

    /**
     * Caches component minimum and preferred sizes. All requests for component sizes shall be directed to the
     * cache.
     */
    private final ComponentSizeCache componentSizeCache;

    /**
     * These functional objects are used to measure component sizes. They abstract from horizontal and
     * vertical orientation and so, allow to implement the layout algorithm for both orientations with a
     * single set of methods.
     */
    private final Measure minimumWidthMeasure;
    private final Measure minimumHeightMeasure;
    private final Measure preferredWidthMeasure;
    private final Measure preferredHeightMeasure;

    private boolean debugLinesVisible = false;

    private ReadOnlyObjectWrapper< LayoutInfo > currentLayoutInfo = new ReadOnlyObjectWrapper<>();

    private ListChangeListener< Node > debugLinesListener = new ListChangeListener<>()
    {
        @Override
        public void onChanged( final javafx.collections.ListChangeListener.Change< ? extends Node > aChange )
        {
            final String borderString =
                "-fx-border-color: #FF0000; -fx-border-style: solid; -fx-border-width: 1;";
            while( aChange.next() )
            {
                if( aChange.wasAdded() )
                {
                    for( final Node addItem : aChange.getAddedSubList() )
                    {
                        String currentStyle = getStyle();
                        currentStyle += " " + borderString;
                        addItem.setStyle( currentStyle );
                    }
                }
            }
        }

    };

    /**
     * FormLayoutPane's focus traversal policy.
     */
    private FocusTraversalPolicy traversalPolicy;

    private static final Function< CellConstraints, Integer > rowPositionProvider = c -> c.gridY;
    private static final Function< CellConstraints, Integer > rowSpanSizeProvider = c -> c.gridHeight;
    private static final Function< CellConstraints, Integer > columnPositionProvider = c -> c.gridX;
    private static final Function< CellConstraints, Integer > columnSpanSizeProvider = c -> c.gridWidth;

    private static final Function< Node, int[] > nodeToSpannedCollsMapping =
        nodeToSpannedSpecsMapping( columnPositionProvider, columnSpanSizeProvider );
    private static final Function< Node, int[] > nodeToSpannedRowsMapping =
        nodeToSpannedSpecsMapping( rowPositionProvider, rowSpanSizeProvider );

    private final BooleanProperty layoutOverflowOutside = new SimpleBooleanProperty( true );

    // Instance Creation ****************************************************

    /**
     * Constructs an empty FormLayout. Columns and rows must be added before components can be added to the
     * layout container.
     * <p>
     * This constructor is intended to be used in environments that add columns and rows dynamically.
     */
    public FormLayoutPane()
    {
        this( new ColumnSpec[ 0 ], new RowSpec[ 0 ] );
    }

    /**
     * Constructs a FormLayout using the given encoded column specifications. The constructed layout has no
     * rows; these must be added before components can be added to the layout container. The string decoding
     * uses the default LayoutMap.
     * <p>
     * This constructor is intended to be used with builder classes that add rows dynamically, such as the
     * <code>DefaultFormBuilder</code>.
     * <p>
     * <strong>Examples:</strong>
     *
     * <pre>
     * // Label, gap, component
     * FormLayout layout = new FormLayout( &quot;pref, 4dlu, pref&quot; );
     *
     * // Right-aligned label, gap, component, gap, component
     * FormLayout layout = new FormLayout( &quot;right:pref, 4dlu, 50dlu, 4dlu, 50dlu&quot; );
     *
     * // Left-aligned labels, gap, components, gap, components
     * FormLayout layout = new FormLayout( &quot;left:pref, 4dlu, pref, 4dlu, pref&quot; );
     * </pre>
     *
     * See the class comment for more examples.
     *
     * @param encodedColumnSpecs
     *            comma separated encoded column specifications
     * @throws NullPointerException
     *             if encodedColumnSpecs is {@code null}
     * @see LayoutMap#getRoot()
     */
    public FormLayoutPane( final String encodedColumnSpecs )
    {
        this( encodedColumnSpecs, LayoutMap.getRoot() );
    }

    /**
     * Constructs a FormLayout using the given encoded column specifications and LayoutMap. The constructed
     * layout has no rows; these must be added before components can be added to the layout container.
     * <p>
     * This constructor is intended to be used with builder classes that add rows dynamically, such as the
     * <code>DefaultFormBuilder</code>.
     * <p>
     * <strong>Examples:</strong>
     *
     * <pre>
     * // Label, gap, component
     * FormLayout layout = new FormLayout( &quot;pref, 4dlu, pref&quot;, myLayoutMap );
     *
     * // Right-aligned label, gap, component, gap, component
     * FormLayout layout = new FormLayout( &quot;right:pref, @lcgap, 50dlu, 4dlu, 50dlu&quot;, myLayoutMap );
     *
     * // Left-aligned labels, gap, components, gap, components
     * FormLayout layout = new FormLayout( &quot;left:pref, @lcgap, pref, @myGap, pref&quot;, myLayoutMap );
     * </pre>
     *
     * See the class comment for more examples.
     *
     * @param encodedColumnSpecs
     *            comma separated encoded column specifications
     * @param layoutMap
     *            expands layout column and row variables
     * @throws NullPointerException
     *             if {@code encodedColumnSpecs} or {@code layoutMap} is {@code null}
     * @see LayoutMap#getRoot()
     * @since 1.2
     */
    public FormLayoutPane( final String encodedColumnSpecs, final LayoutMap layoutMap )
    {
        this( ColumnSpec.decodeSpecs( encodedColumnSpecs, layoutMap ), new RowSpec[ 0 ] );
    }

    /**
     * Constructs a FormLayout using the given encoded column and row specifications and the default
     * LayoutMap.
     * <p>
     * This constructor is recommended for most hand-coded layouts.
     * <p>
     * <strong>Examples:</strong>
     *
     * <pre>
     * FormLayout layout = new FormLayout( &quot;pref, 4dlu, pref&quot;, // columns
     *     &quot;p, 3dlu, p&quot; ); // rows
     *
     * FormLayout layout = new FormLayout( &quot;right:pref, 4dlu, pref&quot;, // columns
     *     &quot;p, 3dlu, p, 3dlu, fill:p:grow&quot; ); // rows
     *
     * FormLayout layout = new FormLayout( &quot;left:pref, 4dlu, 50dlu&quot;, // columns
     *     &quot;p, 2px, p, 3dlu, p, 9dlu, p&quot; ); // rows
     *
     * FormLayout layout = new FormLayout( &quot;max(75dlu;pref), 4dlu, default&quot;, // columns
     *     &quot;p, 3dlu, p, 3dlu, p, 3dlu, p&quot; ); // rows
     * </pre>
     *
     * See the class comment for more examples.
     *
     * @param encodedColumnSpecs
     *            comma separated encoded column specifications
     * @param encodedRowSpecs
     *            comma separated encoded row specifications
     * @throws NullPointerException
     *             if encodedColumnSpecs or encodedRowSpecs is {@code null}
     * @see LayoutMap#getRoot()
     */
    public FormLayoutPane( final String encodedColumnSpecs, final String encodedRowSpecs )
    {
        this( encodedColumnSpecs, encodedRowSpecs, LayoutMap.getRoot() );
    }

    /**
     * Constructs a FormLayout using the given encoded column and row specifications and the given LayoutMap.
     * <p>
     * <strong>Examples:</strong>
     *
     * <pre>
     * FormLayout layout = new FormLayout( &quot;pref, 4dlu, pref&quot;, // columns
     *     &quot;p, 3dlu, p&quot;, // rows
     *     myLayoutMap ); // custom LayoutMap
     *
     * FormLayout layout = new FormLayout( &quot;right:pref, 4dlu, pref&quot;, // columns
     *     &quot;p, @lgap, p, @lgap, fill:p:grow&quot;, // rows
     *     myLayoutMap ); // custom LayoutMap
     *
     * FormLayout layout = new FormLayout( &quot;left:pref, 4dlu, 50dlu&quot;, // columns
     *     &quot;p, 2px, p, 3dlu, p, 9dlu, p&quot;, // rows
     *     myLayoutMap ); // custom LayoutMap
     *
     * FormLayout layout = new FormLayout( &quot;max(75dlu;pref), 4dlu, default&quot;, // columns
     *     &quot;p, 3dlu, p, 3dlu, p, 3dlu, p&quot;, // rows
     *     myLayoutMap ); // custom LayoutMap
     * </pre>
     *
     * See the class comment for more examples.
     *
     * @param encodedColumnSpecs
     *            comma separated encoded column specifications
     * @param encodedRowSpecs
     *            comma separated encoded row specifications
     * @param layoutMap
     *            expands layout column and row variables
     * @throws NullPointerException
     *             if {@code encodedColumnSpecs}, {@code encodedRowSpecs}, or {@code layoutMap} is
     *             {@code null}
     * @since 1.2
     */
    public FormLayoutPane( final String encodedColumnSpecs, final String encodedRowSpecs,
        final LayoutMap layoutMap )
    {
        this( ColumnSpec.decodeSpecs( encodedColumnSpecs, layoutMap ),
            RowSpec.decodeSpecs( encodedRowSpecs, layoutMap ) );
    }

    /**
     * Constructs a FormLayout using the given column specifications. The constructed layout has no rows;
     * these must be added before components can be added to the layout container.
     *
     * @param colSpecs
     *            an array of column specifications.
     * @throws NullPointerException
     *             if {@code colSpecs} is {@code null}
     * @since 1.1
     */
    public FormLayoutPane( final ColumnSpec[] colSpecs )
    {
        this( colSpecs, new RowSpec[]
        {} );
    }

    /**
     * Constructs a FormLayout using the given column and row specifications.
     *
     * @param colSpecs
     *            an array of column specifications.
     * @param rowSpecs
     *            an array of row specifications.
     * @throws NullPointerException
     *             if colSpecs or rowSpecs is {@code null}
     */
    public FormLayoutPane( final ColumnSpec[] colSpecs, final RowSpec[] rowSpecs )
    {
        if( colSpecs == null )
        {
            throw new NullPointerException( "The column specifications must not be null." );
        }
        if( rowSpecs == null )
        {
            throw new NullPointerException( "The row specifications must not be null." );
        }

        this.colSpecs = FXCollections.observableArrayList( colSpecs );
        this.colSpecsProperty = new SimpleListProperty<>( this.colSpecs );
        this.rowSpecs = FXCollections.observableArrayList( Arrays.asList( rowSpecs ) );
        this.rowSpecsProperty = new SimpleListProperty<>( this.rowSpecs );
        colGroupIndices = new int[][]
        {};
        rowGroupIndices = new int[][]
        {};
        final int initialCapacity = colSpecs.length * rowSpecs.length / 4;
        componentSizeCache = new ComponentSizeCache( initialCapacity );
        minimumWidthMeasure = new MinimumWidthMeasure( componentSizeCache );
        minimumHeightMeasure = new MinimumHeightMeasure( componentSizeCache );
        preferredWidthMeasure = new PreferredWidthMeasure( componentSizeCache );
        preferredHeightMeasure = new PreferredHeightMeasure( componentSizeCache );
        getChildren().addListener( this::childrenChanged );

        columnCount.set( this.colSpecs.size() );
        rowCount.set( this.rowSpecs.size() );

        traversalPolicy = FocusTraversalPolicy.HORIZONTAL;

    }

    // Accessing the Column and Row Specifications **************************

    /**
     * Returns the number of columns in this layout.
     *
     * @return the number of columns
     */
    public int getColumnCount()
    {
        return columnCount.intValue();
    }

    /**
     * Returns the number of columns in this layout as read only property.
     *
     * @return
     */
    public ReadOnlyIntegerProperty columnCountProperty()
    {
        return columnCount.getReadOnlyProperty();
    }

    /**
     * Returns the number of rows in this layout.
     *
     * @return the number of rows
     */
    public int getRowCount()
    {
        return rowCount.intValue();
    }

    /**
     * Returns the number of rows in this layout as read only property.
     *
     * @return
     */
    public ReadOnlyIntegerProperty rowCountProperty()
    {
        return rowCount.getReadOnlyProperty();
    }

    /**
     * Returns the <code>ColumnSpec</code> at the specified column index.
     *
     * @param columnIndex
     *            the column index of the requested <code>ColumnSpec</code>
     * @return the <code>ColumnSpec</code> at the specified column
     * @throws IndexOutOfBoundsException
     *             if the column index is out of range
     */
    public ColumnSpec getColumnSpec( final int columnIndex )
    {
        return colSpecs.get( columnIndex - 1 );
    }

    /**
     * Sets the <code>ColumnSpec</code> at the specified column index.
     *
     * @param columnIndex
     *            the index of the column to be changed
     * @param columnSpec
     *            the <code>ColumnSpec</code> to be set
     * @throws NullPointerException
     *             if the column specification is null
     * @throws IndexOutOfBoundsException
     *             if the column index is out of range
     */
    public void setColumnSpec( final int columnIndex, final ColumnSpec columnSpec )
    {
        if( columnSpec == null )
        {
            throw new NullPointerException( "The column spec must not be null." );
        }
        colSpecs.set( columnIndex - 1, columnSpec );
    }

    /**
     * Returns the <code>RowSpec</code> at the specified row index.
     *
     * @param rowIndex
     *            the row index of the requested <code>RowSpec</code>
     * @return the <code>RowSpec</code> at the specified row
     * @throws IndexOutOfBoundsException
     *             if the row index is out of range
     */
    public RowSpec getRowSpec( final int rowIndex )
    {
        return rowSpecs.get( rowIndex - 1 );
    }

    /**
     * Sets the <code>RowSpec</code> at the specified row index.
     *
     * @param rowIndex
     *            the index of the row to be changed
     * @param rowSpec
     *            the <code>RowSpec</code> to be set
     * @throws NullPointerException
     *             if the row specification is null
     * @throws IndexOutOfBoundsException
     *             if the row index is out of range
     */
    public void setRowSpec( final int rowIndex, final RowSpec rowSpec )
    {
        if( rowSpec == null )
        {
            throw new NullPointerException( "The row spec must not be null." );
        }
        rowSpecs.set( rowIndex - 1, rowSpec );
    }

    /**
     * Appends the given column specification to the right hand side of all columns.
     *
     * @param columnSpec
     *            the column specification to be added
     * @throws NullPointerException
     *             if the column specification is null
     */
    public void appendColumn( final ColumnSpec columnSpec )
    {
        if( columnSpec == null )
        {
            throw new NullPointerException( "The column spec must not be null." );
        }
        colSpecs.add( columnSpec );

        columnCount.set( columnCount.get() + 1 );
    }

    /**
     * Inserts the specified column at the specified position. Shifts components that intersect the new column
     * to the right hand side and readjusts column groups.
     * <p>
     * The component shift works as follows: components that were located on the right hand side of the
     * inserted column are shifted one column to the right; component column span is increased by one if it
     * intersects the new column.
     * <p>
     * Column group indices that are greater or equal than the given column index will be increased by one.
     *
     * @param columnIndex
     *            index of the column to be inserted
     * @param columnSpec
     *            specification of the column to be inserted
     * @throws IndexOutOfBoundsException
     *             if the column index is out of range
     */
    public void insertColumn( final int columnIndex, final ColumnSpec columnSpec )
    {
        if( columnIndex < 1 || columnIndex > getColumnCount() )
        {
            throw new IndexOutOfBoundsException(
                "The column index " + columnIndex + "must be in the range [1, " + getColumnCount() + "]." );
        }
        colSpecs.add( columnIndex - 1, columnSpec );
        shiftComponentsHorizontally( columnIndex, false );
        adjustGroupIndices( colGroupIndices, columnIndex, false );

        columnCount.set( columnCount.get() + 1 );
    }

    /**
     * Removes the column with the given column index from the layout. Components will be rearranged and
     * column groups will be readjusted. Therefore, the column must not contain components and must not be
     * part of a column group.
     * <p>
     * The component shift works as follows: components that were located on the right hand side of the
     * removed column are moved one column to the left; component column span is decreased by one if it
     * intersects the removed column.
     * <p>
     * Column group indices that are greater than the column index will be decreased by one.
     * <p>
     * <strong>Note:</strong> If one of the constraints mentioned above is violated, this layout's state
     * becomes illegal and it is unsafe to work with this layout. A typical layout implementation can ensure
     * that these constraints are not violated. However, in some cases you may need to check these conditions
     * before you invoke this method. The Forms extras contain source code for class
     * <code>FormLayoutUtils</code> that provides the required test methods:<br>
     * <code>#columnContainsComponents(Parent, int)</code> and<br>
     * <code>#isGroupedColumn(FormLayout, int)</code>.
     *
     * @param columnIndex
     *            index of the column to remove
     * @throws IndexOutOfBoundsException
     *             if the column index is out of range
     * @throws IllegalStateException
     *             if the column contains components or if the column is already grouped
     * @see com.jgoodies.forms.extras.FormLayoutUtils#columnContainsComponent(Parent, int)
     * @see com.jgoodies.forms.extras.FormLayoutUtils#isGroupedColumn(FormLayoutPane, int)
     */
    public void removeColumn( final int columnIndex )
    {
        if( columnIndex < 1 || columnIndex > getColumnCount() )
        {
            throw new IndexOutOfBoundsException(
                "The column index " + columnIndex + " must be in the range [1, " + getColumnCount() + "]." );
        }
        colSpecs.remove( columnIndex - 1 );
        shiftComponentsHorizontally( columnIndex, true );
        adjustGroupIndices( colGroupIndices, columnIndex, true );

        columnCount.set( columnCount.get() - 1 );
    }

    /**
     * Appends the given row specification to the bottom of all rows.
     *
     * @param rowSpec
     *            the row specification to be added to the form layout
     * @throws NullPointerException
     *             if the rowSpec is null
     */
    public void appendRow( final RowSpec rowSpec )
    {
        if( rowSpec == null )
        {
            throw new NullPointerException( "The row spec must not be null." );
        }
        rowSpecs.add( rowSpec );

        rowCount.set( rowCount.get() + 1 );
    }

    /**
     * Inserts the specified column at the specified position. Shifts components that intersect the new column
     * to the right and readjusts column groups.
     * <p>
     * The component shift works as follows: components that were located on the right hand side of the
     * inserted column are shifted one column to the right; component column span is increased by one if it
     * intersects the new column.
     * <p>
     * Column group indices that are greater or equal than the given column index will be increased by one.
     *
     * @param rowIndex
     *            index of the row to be inserted
     * @param rowSpec
     *            specification of the row to be inserted
     * @throws IndexOutOfBoundsException
     *             if the row index is out of range
     */
    public void insertRow( final int rowIndex, final RowSpec rowSpec )
    {
        if( rowIndex < 1 || rowIndex > getRowCount() )
        {
            throw new IndexOutOfBoundsException(
                "The row index " + rowIndex + " must be in the range [1, " + getRowCount() + "]." );
        }
        rowSpecs.add( rowIndex - 1, rowSpec );
        shiftComponentsVertically( rowIndex, false );
        adjustGroupIndices( rowGroupIndices, rowIndex, false );

        rowCount.set( rowCount.get() + 1 );
    }

    /**
     * Removes the row with the given row index from the layout. Components will be rearranged and row groups
     * will be readjusted. Therefore, the row must not contain components and must not be part of a row group.
     * <p>
     * The component shift works as follows: components that were located below the removed row are moved up
     * one row; component row span is decreased by one if it intersects the removed row.
     * <p>
     * Row group indices that are greater than the row index will be decreased by one.
     * <p>
     * <strong>Note:</strong> If one of the constraints mentioned above is violated, this layout's state
     * becomes illegal and it is unsafe to work with this layout. A typical layout implementation can ensure
     * that these constraints are not violated. However, in some cases you may need to check these conditions
     * before you invoke this method. The Forms extras contain source code for class
     * <code>FormLayoutUtils</code> that provides the required test methods:<br>
     * <code>#rowContainsComponents(Parent, int)</code> and<br>
     * <code>#isGroupedRow(FormLayout, int)</code>.
     *
     * @param rowIndex
     *            index of the row to remove
     * @throws IndexOutOfBoundsException
     *             if the row index is out of range
     * @throws IllegalStateException
     *             if the row contains components or if the row is already grouped
     * @see com.jgoodies.forms.extras.FormLayoutUtils#rowContainsComponent(Container, int)
     * @see com.jgoodies.forms.extras.FormLayoutUtils#isGroupedRow(FormLayoutPane, int)
     */
    public void removeRow( final int rowIndex )
    {
        if( rowIndex < 1 || rowIndex > getRowCount() )
        {
            throw new IndexOutOfBoundsException(
                "The row index " + rowIndex + " must be in the range [1, " + getRowCount() + "]." );
        }
        rowSpecs.remove( rowIndex - 1 );
        shiftComponentsVertically( rowIndex, true );
        adjustGroupIndices( rowGroupIndices, rowIndex, true );

        rowCount.set( rowCount.get() - 1 );
    }

    /**
     * Shifts components horizontally, either to the right if a column has been inserted or to the left if a
     * column has been removed.
     *
     * @param columnIndex
     *            index of the column to remove
     * @param remove
     *            true for remove, false for insert
     * @throws IllegalStateException
     *             if a removed column contains components
     */
    private void shiftComponentsHorizontally( final int columnIndex, final boolean remove )
    {
        final int offset = remove ? -1 : 1;
        for( final Node child : getChildren() )
        {
            final CellConstraints constraints = getConstraints0( child );
            final int x1 = constraints.gridX;
            final int w = constraints.gridWidth;
            final int x2 = x1 + w - 1;
            if( x1 == columnIndex && remove )
            {
                throw new IllegalStateException( "The removed column " + columnIndex
                    + " must not contain component origins.\n" + "Illegal component=" + child );
            }
            else if( x1 >= columnIndex )
            {
                constraints.gridX += offset;
            }
            else if( x2 >= columnIndex )
            {
                constraints.gridWidth += offset;
            }
        }
    }

    /**
     * Shifts components vertically, either to the bottom if a row has been inserted or to the top if a row
     * has been removed.
     *
     * @param rowIndex
     *            index of the row to remove
     * @param remove
     *            true for remove, false for insert
     * @throws IllegalStateException
     *             if a removed column contains components
     */
    private void shiftComponentsVertically( final int rowIndex, final boolean remove )
    {
        final int offset = remove ? -1 : 1;
        for( final Node child : getChildren() )
        {
            final CellConstraints constraints = getConstraints0( child );
            final int y1 = constraints.gridY;
            final int h = constraints.gridHeight;
            final int y2 = y1 + h - 1;
            if( y1 == rowIndex && remove )
            {
                throw new IllegalStateException( "The removed row " + rowIndex
                    + " must not contain component origins.\n" + "Illegal component=" + child );
            }
            else if( y1 >= rowIndex )
            {
                constraints.gridY += offset;
            }
            else if( y2 >= rowIndex )
            {
                constraints.gridHeight += offset;
            }
        }
    }

    /**
     * Adjusts group indices. Shifts the given groups to left, right, up, down according to the specified
     * remove or add flag.
     *
     * @param allGroupIndices
     *            the groups to be adjusted
     * @param modifiedIndex
     *            the modified column or row index
     * @param remove
     *            true for remove, false for add
     * @throws IllegalStateException
     *             if we remove and the index is grouped
     */
    private void adjustGroupIndices( final int[][] allGroupIndices, final int modifiedIndex,
        final boolean remove )
    {
        final int offset = remove ? -1 : +1;
        for( int group = 0; group < allGroupIndices.length; group++ )
        {
            final int[] groupIndices = allGroupIndices[ group ];
            for( int i = 0; i < groupIndices.length; i++ )
            {
                final int index = groupIndices[ i ];
                if( index == modifiedIndex && remove )
                {
                    throw new IllegalStateException(
                        "The removed index " + modifiedIndex + " must not be grouped." );
                }
                else if( index >= modifiedIndex )
                {
                    groupIndices[ i ] += offset;
                }
            }
        }
    }

    // Accessing Constraints ************************************************

    /**
     * Looks up and returns the constraints for the specified component. A copy of the actual
     * <code>CellConstraints</code> object is returned.
     *
     * @param component
     *            the component to be queried
     * @return the <code>CellConstraints</code> for the specified component, or null if not set
     */
    public CellConstraints getConstraints( final Node component )
    {
        final CellConstraints constraints = getConstraints0( component );
        if( constraints != null )
        {
            return constraints.clone();
        }
        return constraints;
    }

    /**
     * Removes the constraints for the specified component in this layout.
     *
     * @param component
     *            the component to be modified
     */
    private void removeConstraints( final Node component )
    {
        componentSizeCache.removeEntry( component );
    }

    /**
     * Sets the column groups, where each column in a group gets the same group wide width. Each group is
     * described by an array of integers that are interpreted as column indices. The parameter is an array of
     * such group descriptions.
     * <p>
     * <strong>Examples:</strong>
     *
     * <pre>
     * // Group columns 1, 3 and 4.
     * setColumnGroups( new int[][]
     * {
     *     { 1, 3, 4 } } );
     *
     * // Group columns 1, 3, 4, and group columns 7 and 9
     * setColumnGroups( new int[][]
     * {
     *     { 1, 3, 4 },
     *     { 7, 9 } } );
     * </pre>
     *
     * @param colGroupIndices
     *            a two-dimensional array of column groups indices
     * @throws IndexOutOfBoundsException
     *             if an index is outside the grid
     * @throws IllegalArgumentException
     *             if a column index is used twice
     */
    public void setColumnGroups( final int[][] colGroupIndices )
    {
        final int maxColumn = getColumnCount();
        final boolean[] usedIndices = new boolean[ maxColumn + 1 ];
        for( int group = 0; group < colGroupIndices.length; group++ )
        {
            for( int j = 0; j < colGroupIndices[ group ].length; j++ )
            {
                final int colIndex = colGroupIndices[ group ][ j ];
                if( colIndex < 1 || colIndex > maxColumn )
                {
                    throw new IndexOutOfBoundsException(
                        "Invalid column group index " + colIndex + " in group " + (group + 1) );
                }
                if( usedIndices[ colIndex ] )
                {
                    throw new IllegalArgumentException(
                        "Column index " + colIndex + " must not be used in multiple column groups." );
                }
                usedIndices[ colIndex ] = true;
            }
        }
        this.colGroupIndices = deepClone( colGroupIndices );
    }

    /**
     * Adds the specified column index to the last column group. In case there are no groups, a new group will
     * be created.
     *
     * @param columnIndex
     *            the column index to be set grouped
     */
    public void addGroupedColumn( final int columnIndex )
    {
        int[][] newColGroups = getColumnGroups();
        // Create a group if none exists.
        if( newColGroups.length == 0 )
        {
            newColGroups = new int[][]
            {
                { columnIndex } };
        }
        else
        {
            final int lastGroupIndex = newColGroups.length - 1;
            final int[] lastGroup = newColGroups[ lastGroupIndex ];
            final int groupSize = lastGroup.length;
            final int[] newLastGroup = new int[ groupSize + 1 ];
            System.arraycopy( lastGroup, 0, newLastGroup, 0, groupSize );
            newLastGroup[ groupSize ] = columnIndex;
            newColGroups[ lastGroupIndex ] = newLastGroup;
        }
        setColumnGroups( newColGroups );
    }

    // Accessing Column and Row Groups **************************************

    /**
     * Returns a deep copy of the column groups.
     *
     * @return the column groups as two-dimensional int array
     */
    public int[][] getColumnGroups()
    {
        return deepClone( colGroupIndices );
    }

    /**
     * Sets the row groups, where each row in such a group gets the same group wide height. Each group is
     * described by an array of integers that are interpreted as row indices. The parameter is an array of
     * such group descriptions.
     * <p>
     * <strong>Examples:</strong>
     *
     * <pre>
     * // Group rows 1 and 2.
     * setRowGroups( new int[][]
     * {
     *     { 1, 2 } } );
     *
     * // Group rows 1 and 2, and group rows 5, 7, and 9.
     * setRowGroups( new int[][]
     * {
     *     { 1, 2 },
     *     { 5, 7, 9 } } );
     * </pre>
     *
     * @param rowGroupIndices
     *            a two-dimensional array of row group indices.
     * @throws IndexOutOfBoundsException
     *             if an index is outside the grid
     */
    public void setRowGroups( final int[][] rowGroupIndices )
    {
        final int rowCount = getRowCount();
        final boolean[] usedIndices = new boolean[ rowCount + 1 ];
        for( int i = 0; i < rowGroupIndices.length; i++ )
        {
            for( int j = 0; j < rowGroupIndices[ i ].length; j++ )
            {
                final int rowIndex = rowGroupIndices[ i ][ j ];
                if( rowIndex < 1 || rowIndex > rowCount )
                {
                    throw new IndexOutOfBoundsException(
                        "Invalid row group index " + rowIndex + " in group " + (i + 1) );
                }
                if( usedIndices[ rowIndex ] )
                {
                    throw new IllegalArgumentException(
                        "Row index " + rowIndex + " must not be used in multiple row groups." );
                }
                usedIndices[ rowIndex ] = true;
            }
        }
        this.rowGroupIndices = deepClone( rowGroupIndices );
    }

    /**
     * Adds the specified row index to the last row group. In case there are no groups, a new group will be
     * created.
     *
     * @param rowIndex
     *            the index of the row that should be grouped
     */
    public void addGroupedRow( final int rowIndex )
    {
        int[][] newRowGroups = getRowGroups();
        // Create a group if none exists.
        if( newRowGroups.length == 0 )
        {
            newRowGroups = new int[][]
            {
                { rowIndex } };
        }
        else
        {
            final int lastGroupIndex = newRowGroups.length - 1;
            final int[] lastGroup = newRowGroups[ lastGroupIndex ];
            final int groupSize = lastGroup.length;
            final int[] newLastGroup = new int[ groupSize + 1 ];
            System.arraycopy( lastGroup, 0, newLastGroup, 0, groupSize );
            newLastGroup[ groupSize ] = rowIndex;
            newRowGroups[ lastGroupIndex ] = newLastGroup;
        }
        setRowGroups( newRowGroups );
    }

    /**
     * Returns a deep copy of the row groups.
     *
     * @return the row groups as two-dimensional int array
     */
    public int[][] getRowGroups()
    {
        return deepClone( rowGroupIndices );
    }

    /**
     * Adds given node to children and sets contastraints.
     *
     * @param aChild
     * @param aConstraints
     */
    public void add( final Node aChild, final CellConstraints aConstraints )
    {
        setConstraints( aChild, aConstraints );
        getChildren().add( aChild );
    }

    /**
     * Adds the specified component to the layout, using the specified <code>constraints</code> object. Note
     * that constraints are mutable and are, therefore, cloned when cached.
     *
     * @param comp
     *            the component to be added
     * @param constraints
     *            the component's cell constraints
     * @throws NullPointerException
     *             if <code>constraints</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if <code>constraints</code> is not a <code>CellConstraints</code> or a String that cannot
     *             be used to construct a <code>CellConstraints</code>
     */
    public void addLayoutComponent( final Node comp, final Object constraints )
    {
        if( constraints instanceof String )
        {
            setConstraints( comp, new CellConstraints( (String)constraints ) );
        }
        else if( constraints instanceof CellConstraints )
        {
            setConstraints( comp, (CellConstraints)constraints );
        }
        else if( constraints == null )
        {
            throw new NullPointerException( "The constraints must not be null." );
        }
        else
        {
            throw new IllegalArgumentException( "Illegal constraint type " + constraints.getClass() );
        }
        getChildren().add( comp );

    }

    // Implementing the LayoutManager and LayoutManager2 Interfaces *********

    /**
     * Removes the specified component from this layout.
     * <p>
     * Most applications do not call this method directly.
     *
     * @param comp
     *            the component to be removed.
     * @see Container#remove(java.awt.Component)
     * @see Container#removeAll()
     */
    public void removeLayoutComponent( final Node comp )
    {
        getChildren().remove( comp );
    }

    private void childrenChanged( final Change< ? extends Node > aChange )
    {
        while( aChange.next() )
        {
            final List< ? extends Node > removed = aChange.getRemoved();
            if( removed != null && removed.size() > 0 )
            {
                removed.stream().forEach( node -> removeConstraints( node ) );
            }
        }

        setNeedsLayout( true );
    }

    /**
     * Returns the alignment along the x axis. This specifies how the component would like to be aligned
     * relative to other components. The value should be a number between 0 and 1 where 0 represents alignment
     * along the origin, 1 is aligned the farthest away from the origin, 0.5 is centered, etc.
     *
     * @param parent
     *            the parent container
     * @return the value <code>0.5f</code> to indicate center alignment
     */
    public float getLayoutAlignmentX( final Parent parent )
    {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis. This specifies how the component would like to be aligned
     * relative to other components. The value should be a number between 0 and 1 where 0 represents alignment
     * along the origin, 1 is aligned the farthest away from the origin, 0.5 is centered, etc.
     *
     * @param parent
     *            the parent container
     * @return the value <code>0.5f</code> to indicate center alignment
     */
    public float getLayoutAlignmentY( final Parent parent )
    {
        return 0.5f;
    }

    // Layout Requests ******************************************************

    /**
     * Invalidates the layout, indicating that if the layout manager has cached information it should be
     * discarded.
     *
     * @param target
     *            the container that holds the layout to be invalidated
     */
    public void invalidateLayout( final Parent target )
    {
        invalidateCaches();
    }

    /**
     * Is called oncs before layout. After call callback is removed.
     *
     * @param aCallback
     */
    public void setBeforeFirstLayout( final Consumer< FormLayoutPane > aCallback )
    {
        preLayoutCallback = aCallback;
    }

    private void callPreLayoutCallback()
    {
        if( preLayoutCallback != null )
        {
            final Consumer< FormLayoutPane > temp = preLayoutCallback;
            preLayoutCallback = null;
            temp.accept( this );
        }
    }

    @Override
    public void requestLayout()
    {
        invalidateCaches();
        super.requestLayout();
    }

    @Override
    protected double computeMinWidth( final double aHeight )
    {
        callPreLayoutCallback();
        initializeColComponentLists();
        double minWidth =
            snapSpaceX( computeOrigins( this, colSpecs, colComponents, colGroupIndices, spannedNodesInfoForColumns(),
                nodeToSpannedCollsMapping, minimumWidthMeasure, preferredWidthMeasure, false ,  columnPositionProvider, columnSpanSizeProvider ) );
        final Insets insets = getInsets();
        if( insets != null )
        {
            minWidth += snapSpaceX( insets.getLeft() ) + snapSpaceX( insets.getRight() );
        }
        return minWidth;
    }

    @Override
    protected void layoutChildren()
    {
        callPreLayoutCallback();
        //https://jira-bld-ppl.psi.de/browse/PJFDEV-5236
        //        invalidateCaches();
        layoutContainer( this );

    }

    @Override
    protected double computeMaxWidth( final double aHeight )
    {
        return Double.MAX_VALUE;
    }

    @Override
    protected double computeMinHeight( final double aWidth )
    {
        callPreLayoutCallback();
        initializeRowComponentLists();
        double minHeight =
            snapSizeY( computeOrigins( this, rowSpecs, rowComponents, rowGroupIndices, spanedNodesInfoForRows(),
                nodeToSpannedRowsMapping, minimumHeightMeasure, preferredHeightMeasure, false, rowPositionProvider, rowSpanSizeProvider ) );
        final Insets insets = getInsets();
        if( insets != null )
        {
            minHeight += snapSpaceY( insets.getTop() ) + snapSpaceY( insets.getBottom() );
        }
        return minHeight;
    }

    @Override
    protected double computeMaxHeight( final double aWidth )
    {
        return Double.MAX_VALUE;
    }

    @Override
    protected double computePrefHeight( final double aWidth )
    {
        callPreLayoutCallback();
        initializeRowComponentLists();
        double prefHeight =
            snapSizeY( computeOrigins( this, rowSpecs, rowComponents, rowGroupIndices, spanedNodesInfoForRows(),
                nodeToSpannedRowsMapping, minimumHeightMeasure, preferredHeightMeasure, true , rowPositionProvider, rowSpanSizeProvider ) );
        final Insets insets = getInsets();
        if( insets != null )
        {
            prefHeight += snapSpaceY( insets.getTop() ) + snapSpaceY( insets.getBottom() );
        }
        return prefHeight;
    }

    @Override
    protected double computePrefWidth( final double aHeight )
    {
        callPreLayoutCallback();
        initializeColComponentLists();
        double prefWidth =
            snapSizeX( computeOrigins( this, colSpecs, colComponents, colGroupIndices, spannedNodesInfoForColumns(),
                nodeToSpannedCollsMapping, minimumWidthMeasure, preferredWidthMeasure, true, columnPositionProvider, columnSpanSizeProvider ) );
        final Insets insets = getInsets();
        if( insets != null )
        {
            prefWidth += snapSpaceX( insets.getLeft() ) + snapSpaceX( insets.getRight() );
        }
        return prefWidth;
    }

    /**
     * Lays out the specified container using this form layout. This method reshapes components in the
     * specified container in order to satisfy the constraints of this <code>FormLayout</code> object.
     * <p>
     * Most applications do not call this method directly.
     * <p>
     * The form layout performs the following steps:
     * <ol>
     * <li>find components that occupy exactly one column or row
     * <li>compute minimum widths and heights
     * <li>compute preferred widths and heights
     * <li>give cols and row equal size if they share a group
     * <li>compress default columns and rows if total is less than pref size
     * <li>give cols and row equal size if they share a group
     * <li>distribute free space
     * <li>set components bounds
     * </ol>
     *
     * @param parent
     *            the container in which to do the layout
     * @see Container
     * @see Container#doLayout()
     */
    private void layoutContainer( final FormLayoutPane parent )
    {
        final double snappedWidth = snapSizeX( parent.getWidth() );
        final double snappedHeight = snapSizeY( parent.getHeight() );
        final LayoutInfo newLayoutInfo = getLayoutInfo( parent, snappedWidth, snappedHeight );
        layoutComponents( newLayoutInfo.columnOrigins, newLayoutInfo.rowOrigins, snappedWidth,
            snappedHeight );

        currentLayoutInfo.set( newLayoutInfo );
    }

    /**
     * Prepares array of formSpecs' size. Each element is a list of nodes that span through given form spec.
     * For example if node A spans through rows 2 to 4, and node B spans through rows 3 to 5, and there are 7
     * rows in this pane, then resulting array will looks like this:
     *
     * <pre>
     * {null, [A], [A, B], [A, B], [B], null, null}
     * </pre>
     *
     * This arrays contains only nodes that spans multiple form specs (rows/columns). Nodes, that occupy
     * exactly one cell will not be contained in this array.
     *
     * @param formSpecs
     * @param positionProvider
     *            Function mapping cell constraint to position in grid
     * @param spanSizeProvider
     *            Function mapping cell contraint to size of span
     * @return
     */
    private List< Node >[] prepareSpannedNodesInfo( final List< ? extends FormSpec > formSpecs,
        final Function< CellConstraints, Integer > positionProvider,
        final Function< CellConstraints, Integer > spanSizeProvider )
    {
        @SuppressWarnings( "unchecked" )
        final List< Node >[] spanningComponents = new List[ formSpecs.size() ];

        for( final Node node : getChildren() )
        {
            final CellConstraints constraints = getConstraints0( node );
            if( constraints == null )
            {
                continue;
            }

            final int spanSize = spanSizeProvider.apply( constraints );
            if( spanSize <= 1 )
            {
                continue;
            }
            for( int i = 0; i < spanSize; i++ )
            {
                final int position = positionProvider.apply( constraints );
                final int arrayIndex = position + i - 1;
                List< Node > constraintsList = spanningComponents[ arrayIndex ];
                if( constraintsList == null )
                {
                    constraintsList = new ArrayList<>();
                    spanningComponents[ arrayIndex ] = constraintsList;
                }
                constraintsList.add( node );
            }
        }
        return spanningComponents;
    }

    /**
     * Initializes two lists for columns that hold a column's components that span only this column .
     * <p>
     * Iterates over all components and their associated constraints; every component that has a column span
     * of 1 is put into the column's component list.
     */
    @SuppressWarnings( "unchecked" )
    private void initializeColComponentLists()
    {
        colComponents = new List[ getColumnCount() ];
        for( int i = 0; i < getColumnCount(); i++ )
        {
            colComponents[ i ] = new ArrayList<>();
        }

        for( final Node component : getChildren() )
        {
            if( takeIntoAccount( component ) )
            {
                final CellConstraints constraints = getConstraints0( component );
                if( constraints.gridWidth == 1 )
                {
                    colComponents[ constraints.gridX - 1 ].add( component );
                }

            }
        }
    }

    /**
     * Initializes lists for rows that hold a row's components that span only this row.
     * <p>
     * Iterates over all components and their associated constraints; every component that has a row span of 1
     * is put into the row's component list.
     */
    @SuppressWarnings( "unchecked" )
    private void initializeRowComponentLists()
    {
        rowComponents = new List[ getRowCount() ];
        for( int i = 0; i < getRowCount(); i++ )
        {
            rowComponents[ i ] = new ArrayList<>();
        }

        for( final Node component : getChildren() )
        {
            if( takeIntoAccount( component ) )
            {
                final CellConstraints constraints = getConstraints0( component );

                if( constraints.gridHeight == 1 )
                {
                    rowComponents[ constraints.gridY - 1 ].add( component );
                }
            }
        }
    }

    // Layout Algorithm *****************************************************

    /**
     * Computes and returns the grid's origins.
     *
     * @param container
     *            the layout container
     * @param totalSize
     *            the total size to assign
     * @param offset
     *            the offset from left or top margin
     * @param formSpecs
     *            the column or row specs, resp.
     * @param componentLists
     *            the components list for each col/row
     * @param groupIndices
     *            the group specification
     * @param minMeasure
     *            the measure used to determine min sizes
     * @param prefMeasure
     *            the measure used to determine pre sizes
     * @return an int array with the origins
     */
    private double[] computeGridOrigins( final FormLayoutPane container, final double totalSize, final double offset,
        final List< ? extends FormSpec > formSpecs, final List< Node >[] componentLists,
        final int[][] groupIndices, final List< Node >[] spannedNodesInfo,
        final Function< Node, int[] > nodeToSpanedSpecMapping, final Measure minMeasure,
        final Measure prefMeasure, final Function< CellConstraints, Integer > aNodeStartIndex,
        final Function< CellConstraints, Integer > aNodeSpan )
    {
        /*
         * For each spec compute the minimum and preferred size that is the maximum of all component minimum
         * and preferred sizes resp.
         */
        final double[] minSizes =
            maximumSizes( container, formSpecs, componentLists, minMeasure, prefMeasure, minMeasure );
        final double[] prefSizes =
            maximumSizes( container, formSpecs, componentLists, minMeasure, prefMeasure, prefMeasure );

        final double[] groupedMinSizes = groupedSizes( groupIndices, minSizes );
        final double[] groupedPrefSizes = groupedSizes( groupIndices, prefSizes );

        final double[] spannedMinSizes =
            spannedSizes( formSpecs, groupedMinSizes, spannedNodesInfo, nodeToSpanedSpecMapping, minMeasure ,aNodeStartIndex, aNodeSpan );
        final double[] spannedPrefSizes = spannedSizes( formSpecs, groupedPrefSizes, spannedNodesInfo,
            nodeToSpanedSpecMapping, prefMeasure,aNodeStartIndex, aNodeSpan  );

        final double totalMinSize = sum( spannedMinSizes );
        final double totalPrefSize = sum( spannedPrefSizes );

        final double[] compressedSizes = compressedSizes( formSpecs, totalSize, totalMinSize, totalPrefSize,
            spannedMinSizes, spannedPrefSizes );
        final double[] groupedSizes = groupedSizes( groupIndices, compressedSizes );

        final double totalGroupedSize = sum( groupedSizes );
        final double[] sizes =
            distributedSizes( container, componentLists, formSpecs, totalSize, totalGroupedSize,
            groupedSizes, spannedNodesInfo, nodeToSpanedSpecMapping, minMeasure, prefMeasure );
        return computeOrigins( sizes, offset );
    }

    private double computeOrigins( final FormLayoutPane container, final List< ? extends FormSpec > formSpecs,
        final List< Node >[] componentLists, final int[][] groupIndices,
        final List< Node >[] spannedNodesInfo, final Function< Node, int[] > nodeToSpanedSpecMapping,
        final Measure minMeasure, final Measure prefMeasure, final boolean aPref,
        final Function< CellConstraints, Integer > aNodeStartIndex,
        final Function< CellConstraints, Integer > aNodeSpan )
    {
        /*
         * For each spec compute the minimum and preferred size that is the maximum of all component minimum
         * and preferred sizes resp.
         */
        if( aPref )
        {
            final double[] prefSizes =
                maximumSizes( container, formSpecs, componentLists, minMeasure, prefMeasure, prefMeasure );

            final double[] groupedPrefSizes = groupedSizes( groupIndices, prefSizes );

            final double[] spannedPrefSizes = spannedSizes( formSpecs, groupedPrefSizes, spannedNodesInfo,
                nodeToSpanedSpecMapping, prefMeasure, aNodeStartIndex, aNodeSpan );
            return sum( spannedPrefSizes );
        }
        else
        {
            final double[] minSizes =
                maximumSizes( container, formSpecs, componentLists, minMeasure, prefMeasure, minMeasure );
            final double[] groupedMinSizes = groupedSizes( groupIndices, minSizes );
            final double[] spannedMinSizes = spannedSizes( formSpecs, groupedMinSizes, spannedNodesInfo,
                nodeToSpanedSpecMapping, minMeasure, aNodeStartIndex, aNodeSpan );
            return sum( spannedMinSizes );
        }
    }

    /**
     * Computes origins from sizes taking the specified offset into account.
     *
     * @param sizes
     *            the array of sizes
     * @param offset
     *            an offset for the first origin
     * @return an array of origins
     */
    private double[] computeOrigins( final double[] sizes, final double offset )
    {
        final int count = sizes.length;
        final double[] origins = new double[ count + 1 ];
        origins[ 0 ] = offset;
        for( int i = 1; i <= count; i++ )
        {
            origins[ i ] = origins[ i - 1 ] + sizes[ i - 1 ];
        }
        return origins;
    }

    /**
     * Lays out the components using the given x and y origins, the column and row specifications, and the
     * component constraints.
     * <p>
     * The actual computation is done by each component's form constraint object. We just compute the cell,
     * the cell bounds and then hand over the component, cell bounds, and measure to the form constraints.
     * This will allow potential subclasses of <code>CellConstraints</code> to do special micro-layout
     * corrections. For example, such a subclass could map JComponent classes to visual layout bounds that may
     * lead to a slightly different bounds.
     *
     * @param x
     *            an int array of the horizontal origins
     * @param y
     *            an int array of the vertical origins
     */
    private void layoutComponents( final double[] x, final double[] y, final double aMaxX,
        final double aMaxY )
    {
        for( final Node component : getChildren() )
        {
            if( takeIntoAccount( component ) )
            {
                final CellConstraints constraints = getConstraints0( component );
                final int gridX = constraints.gridX - 1;
                final int gridY = constraints.gridY - 1;
                final int gridWidth = constraints.gridWidth;
                final int gridHeight = constraints.gridHeight;
                final double xBounds = calculateXBounds( x, aMaxX, gridX, gridWidth );
                final double yBounds = calculateYBounds( y, aMaxY, gridY, gridHeight );
                double widthBounds = x[ gridX + gridWidth ] - xBounds;
                widthBounds = Math.max( Math.min( widthBounds, aMaxX - xBounds - snappedRightInset() ), 0.0 );
                double height = y[ gridY + gridHeight ] - yBounds;
                height = Math.max( Math.min( height, aMaxY - yBounds - snappedBottomInset() ), 0.0 );
                final Bounds cellBounds = new BoundingBox( xBounds, yBounds, widthBounds, height );
                constraints.setBounds( component, this, cellBounds, minimumWidthMeasure, minimumHeightMeasure,
                    preferredWidthMeasure, preferredHeightMeasure );
            }
        }
    }

    private double calculateYBounds( final double[] aY, final double aMaxY, final int aGridY,
        final int aGridHeight )
    {
        if( isLayoutOverflowInside() && aY[ aGridY ] > aMaxY )
        {
            final double originalHeight = aY[ aGridY + aGridHeight ] - aY[ aGridY ];
            return Math.min( aY[ aGridY ], Math.max( aMaxY - snappedBottomInset() - originalHeight, 0.0 ) );
        }
        else
        {
            return Math.min( aY[ aGridY ], aMaxY - snappedBottomInset() );
        }
    }

    private double calculateXBounds( final double[] aX, final double aMaxX, final int aGridX,
        final int aGridWidth )
    {
        if( isLayoutOverflowInside() && aX[ aGridX ] > aMaxX )
        {
            final double originalWidth = aX[ aGridX + aGridWidth ] - aX[ aGridX ];
            return Math.min( aX[ aGridX ], Math.max( aMaxX - snappedRightInset() - originalWidth, 0.0 ) );
        }
        else
        {
            return Math.min( aX[ aGridX ], aMaxX - snappedRightInset() );
        }
    }

    /**
     * Computes and returns the sizes for the given form specs, component lists and measures for minimum,
     * preferred, and default size.
     *
     * @param container
     *            the layout container
     * @param formSpecs
     *            the column or row specs, resp.
     * @param componentLists
     *            the components list for each col/row
     * @param minMeasure
     *            the measure used to determine min sizes
     * @param prefMeasure
     *            the measure used to determine pre sizes
     * @param defaultMeasure
     *            the measure used to determine default sizes
     * @return the column or row sizes
     */
    private double[] maximumSizes( final FormLayoutPane container, final List< ? extends FormSpec > formSpecs,
        final List< Node >[] componentLists, final Measure minMeasure, final Measure prefMeasure,
        final Measure defaultMeasure )
    {
        FormSpec formSpec;
        final int size = formSpecs.size();
        final double[] result = new double[ size ];
        for( int i = 0; i < size; i++ )
        {
            formSpec = formSpecs.get( i );
            result[ i ] = formSpec.maximumSize( container, componentLists[ i ], minMeasure, prefMeasure,
                defaultMeasure );
        }
        return result;
    }

    /**
     * Computes and returns the compressed sizes. Compresses space for columns and rows iff the available
     * space is less than the total preferred size but more than the total minimum size.
     * <p>
     * Only columns and rows that are specified to be compressible will be affected. You can specify a column
     * and row as compressible by giving it the component size <tt>default</tt>.
     *
     * @param formSpecs
     *            the column or row specs to use
     * @param totalSize
     *            the total available size
     * @param totalMinSize
     *            the sum of all minimum sizes
     * @param totalPrefSize
     *            the sum of all preferred sizes
     * @param minSizes
     *            an int array of column/row minimum sizes
     * @param prefSizes
     *            an int array of column/row preferred sizes
     * @return an int array of compressed column/row sizes
     */
    private double[] compressedSizes( final List< ? extends FormSpec > formSpecs, final double totalSize,
        final double totalMinSize, final double totalPrefSize, final double[] minSizes,
        final double[] prefSizes )
    {

        // If we have less space than the total min size, answer the min sizes.
        if( totalSize < totalMinSize )
        {
            return minSizes;
        }
        // If we have more space than the total pref size, answer the pref sizes.
        if( totalSize >= totalPrefSize )
        {
            return prefSizes;
        }

        final int count = formSpecs.size();
        final double[] sizes = new double[ count ];

        final double totalCompressionSpace = totalPrefSize - totalSize;
        final double maxCompressionSpace = totalPrefSize - totalMinSize;
        final double compressionFactor = totalCompressionSpace / maxCompressionSpace;

        // System.out.println("Total compression space=" + totalCompressionSpace);
        // System.out.println("Max compression space =" + maxCompressionSpace);
        // System.out.println("Compression factor =" + compressionFactor);

        for( int i = 0; i < count; i++ )
        {
            final FormSpec formSpec = formSpecs.get( i );
            sizes[ i ] = prefSizes[ i ];
            if( formSpec.getSize().compressible() )
            {
                sizes[ i ] -= Math.round( (prefSizes[ i ] - minSizes[ i ]) * compressionFactor );
            }
        }
        return sizes;
    }

    /**
     * Computes and returns the grouped sizes. Gives grouped columns and rows the same size.
     *
     * @param groups
     *            the group specification
     * @param rawSizes
     *            the raw sizes before the grouping
     * @return the grouped sizes
     */
    private double[] groupedSizes( final int[][] groups, final double[] rawSizes )
    {
        // Return the compressed sizes if there are no groups.
        if( groups == null || groups.length == 0 )
        {
            return rawSizes;
        }

        // Initialize the result with the given compressed sizes.
        final double[] sizes = new double[ rawSizes.length ];
        for( int i = 0; i < sizes.length; i++ )
        {
            sizes[ i ] = rawSizes[ i ];
        }

        // For each group equalize the sizes.
        for( int group = 0; group < groups.length; group++ )
        {
            final int[] groupIndices = groups[ group ];
            double groupMaxSize = 0;
            // Compute the group's maximum size.
            for( int i = 0; i < groupIndices.length; i++ )
            {
                final int index = groupIndices[ i ] - 1;
                groupMaxSize = Math.max( groupMaxSize, sizes[ index ] );
            }
            // Set all sizes of this group to the group's maximum size.
            for( int i = 0; i < groupIndices.length; i++ )
            {
                final int index = groupIndices[ i ] - 1;
                sizes[ index ] = groupMaxSize;
            }
        }
        return sizes;
    }

    /**
     * Invalidates the component size caches.
     */
    private void invalidateCaches()
    {
        componentSizeCache.invalidate();
    }

    /**
     * Distributes free space over columns and rows and returns the sizes after this distribution process.
     *
     * @param formSpecs
     *            the column/row specifications to work with
     * @param totalSize
     *            the total available size
     * @param totalPrefSize
     *            the sum of all preferred sizes
     * @param inputSizes
     *            the input sizes
     * @param spanningComponents
     * @return the distributed sizes
     */
    private double[] distributedSizes( final Parent parent, final List< Node >[] componentLists,
        final List< ? extends FormSpec > formSpecs, final double totalSize, final double totalPrefSize,
        final double[] inputSizes,
        final List< Node >[] aSpanningComponents, final Function< Node, int[] > nodeToSpanedSpecMapping,
        final Measure minMeasure, final Measure prefMeasure )
    {
        final double totalFreeSpace = totalSize - totalPrefSize;
        // Do nothing if there's no free space.
        if( totalFreeSpace < 0 )
        {
            return inputSizes;
        }

        // Compute the total weight.
        final int count = formSpecs.size();
        double totalWeight = 0.0;
        for( int i = 0; i < count; i++ )
        {
            final FormSpec formSpec = formSpecs.get( i );
            totalWeight += formSpec.getResizeWeight();
        }

        // Do nothing if there's no resizing column.
        if( totalWeight == 0.0 )
        {
            return inputSizes;
        }

        final double[] sizes = new double[ count ];

        double restSpace = totalFreeSpace;
        int roundedRestSpace = (int)totalFreeSpace;
        for( int i = 0; i < count; i++ )
        {
            final FormSpec formSpec = formSpecs.get( i );
            final double weight = formSpec.getResizeWeight();
            if( weight == FormSpec.NO_GROW )
            {
                sizes[ i ] = inputSizes[ i ];
            }
            else
            {
                final double roundingCorrection = restSpace - roundedRestSpace;
                final double extraSpace = totalFreeSpace * weight / totalWeight;
                final double correctedExtraSpace = extraSpace - roundingCorrection;
                int roundedExtraSpace = (int)Math.round( correctedExtraSpace );

                // don't allow size to exceed max size
                final Size size = formSpec.getSize();
                if( size instanceof ConstantSize
                    || (size instanceof BoundedSize && ((BoundedSize)size).getUpperBound() != null) )
                {
                    final double maxSize =
                        size.maximumSize( parent, componentLists[ i ], minMeasure, prefMeasure, prefMeasure );
                    roundedExtraSpace =
                        Math.min( roundedExtraSpace, (int)Math.round( maxSize - inputSizes[ i ] ) );
                }

                // / Line below was causing blur of whole pane in some cases
                // double roundedExtraSpace = correctedExtraSpace;
                sizes[ i ] = inputSizes[ i ] + roundedExtraSpace;
                restSpace -= extraSpace;
                roundedRestSpace -= roundedExtraSpace;
            }
        }
        return sizes;
    }

    private boolean endsAtLine( final Node aNode, final int aLine,
        final Function< CellConstraints, Integer > aNodeStartIndex,
        final Function< CellConstraints, Integer > aNodeSpan )
    {
        final CellConstraints constraints = getConstraints( aNode );
        if( constraints == null )
        {
            return false;
        }
        return aLine == (aNodeStartIndex.apply( constraints ) + aNodeSpan.apply( constraints ) - 1);//cell constraints starts with 1
    }

    private double[] spannedSizes( final List< ? extends FormSpec > formSpecs, final double[] inputSizes,
        final List< Node >[] aSpanningComponents, final Function< Node, int[] > nodeToSpanedSpecMapping,
        final Measure measure, final Function< CellConstraints, Integer > aNodeStartIndex,
        final Function< CellConstraints, Integer > aNodeSpan )
    {
        final List< List< Node > > spanningComponents =
            new ArrayList<>( Arrays.asList( aSpanningComponents ) );
        // long spannedSpecs = countNotEmptyItems( spanningComponents );
        final double[] sizes = Arrays.copyOf( inputSizes, inputSizes.length );

        for( int line = 0; line < sizes.length; line++ )
        {
            final List< Node > spanningComponentsInLine = spanningComponents.get( line );
            if( spanningComponentsInLine == null || spanningComponentsInLine.size() == 0 )
            {
                continue;
            }
            final int currentLine = line;
            final List< Node > spanEndAtLine = spanningComponentsInLine.stream()
                .filter( n -> n != null).filter( n -> endsAtLine( n, currentLine + 1, aNodeStartIndex, aNodeSpan ) )
                .collect( Collectors.toList() );
            for( final Node spannedNode : spanEndAtLine )
            {
                final CellConstraints constraints = getConstraints( spannedNode );
                final int startIndex =
                    aNodeStartIndex.apply( constraints ) - 1;//cell constraints starts with 1
                final int endIndex =
                    startIndex + aNodeSpan.apply( constraints ) - 1;//cell constraints starts with 1
                for( int spannedLine = endIndex; spannedLine >= startIndex ; spannedLine-- )
                {
                    final FormSpec formSpec = formSpecs.get( spannedLine );
                    double currentSpannedLineSize = sizes[ spannedLine ];
                    final double spaceLeft = hasSomeSpace( spanningComponents.get( spannedLine ), formSpec,
                        currentSpannedLineSize, measure );
                    if( spaceLeft > 0 )
                    {
                        final double sizeOfComponent = measure.sizeOf( spannedNode );
                        final int[] spannedSpecForNode = nodeToSpanedSpecMapping.apply( spannedNode );
                        final double currentSpace = Arrays.stream( sizes ).skip( spannedSpecForNode[ 0 ] - 1 )
                            .limit( spannedSpecForNode.length ).sum();
                        if( currentSpace < sizeOfComponent )
                        {
                            currentSpannedLineSize += sizeOfComponent - currentSpace;
                        }

                        final Optional< Double > upperBoundsFromSpec = getUpperBoundsFromSpec( formSpec );
                        if( upperBoundsFromSpec.isPresent() )
                        {
                            final double upperBoundValue = upperBoundsFromSpec.get();
                            currentSpannedLineSize = Math.min( currentSpannedLineSize, upperBoundValue );
                        }

                        sizes[ spannedLine ] = currentSpannedLineSize;
                    }
                }
            }
        }

        // for( int i = 0; i < spannedSpecs; i++ )
        // {
        // int rowWithMaxComponents = findSpecWithMaxSpannedNodes( spanningComponents );
        // List< Node > nodes = spanningComponents.get( rowWithMaxComponents );
        // final FormSpec formSpec = formSpecs.get( rowWithMaxComponents );
        // try
        // {
        // double hasSomeSpace = hasSomeSpace( nodes, formSpec,
        // sizes[ rowWithMaxComponents ] , measure );
        // Optional< Double > upperBoundsFromSpec2 = getUpperBoundsFromSpec( formSpec );
        // if( hasSomeSpace > 0 )
        // {
        // for( Node node : nodes )
        // {
        // double specSize = sizes[ rowWithMaxComponents ];
        // double sizeOfComponent = measure.sizeOf( node );
        // int[] spannedSpecForNode = nodeToSpanedSpecMapping.apply( node );
        // double currentSpace = Arrays.stream( sizes ).skip( spannedSpecForNode[ 0 ] - 1 )
        // .limit( spannedSpecForNode.length ).sum();
        // if( currentSpace < sizeOfComponent )
        // {
        // specSize += sizeOfComponent - currentSpace;
        // }
        //
        // final Optional< Double > upperBoundsFromSpec = getUpperBoundsFromSpec( formSpec );
        // if( upperBoundsFromSpec.isPresent() )
        // {
        // final double upperBoundValue = upperBoundsFromSpec.get();
        // specSize = Math.min( specSize, upperBoundValue );
        // }
        //
        // sizes[ rowWithMaxComponents ] = specSize;
        // }
        // }
        // }
        // catch( Exception a )
        // {
        // a.printStackTrace();
        // }
        //
        // // after processing remove info for this row so that it will not be processed in next iteration
        // spanningComponents.set( rowWithMaxComponents, null );
        // }
        return sizes;
    }

    private double hasSomeSpace( final List< Node > aNodes, final FormSpec aSpec, final double aSize,
        final Measure aMeasure )
    {
        final double maximumSize = aSpec.maximumSize( this, aNodes, aMeasure, aMeasure, aMeasure );
        return maximumSize - aSize;
    }

    private long countNotEmptyItems( final List< List< Node > > listOfLists )
    {
        return listOfLists.stream().filter( list -> list != null && !list.isEmpty() ).count();
    }

    /**
     * Finds an index of a spec (row or column) with largest number of nodes spanning it.
     *
     * @param aSpanningComponents
     * @return
     */
    private int findSpecWithMaxSpannedNodes( final List< List< Node > > aSpanningComponents )
    {
        int maxComponents = 0;
        int index = -1;
        for( int i = 0; i < aSpanningComponents.size(); i++ )
        {
            final List< Node > nodesInRow = aSpanningComponents.get( i );
            if( nodesInRow != null && nodesInRow.size() > maxComponents )
            {
                maxComponents = nodesInRow.size();
                index = i;
            }
        }
        return index;
    }

    /**
     * Components are taken into account, if a) they are visible, or b) they have no individual setting and
     * the container-wide settings ignores the visibility, or c) the individual component ignores the
     * visibility.
     *
     * @param component
     * @return <code>true</code> if the component shall be taken into account, <code>false</code> otherwise
     */
    private boolean takeIntoAccount( final Node component )
    {
        return component.isManaged();
    }

    private Optional< Double > getUpperBoundsFromSpec( final FormSpec aFormSpec )
    {
        final Size formSpecSize = aFormSpec.getSize();
        if( formSpecSize instanceof BoundedSize == false )
        {
            return Optional.empty();
        }

        Double upperBound = null;
        final BoundedSize boundedSize = (BoundedSize)formSpecSize;
        final Size upperBoundSize = boundedSize.getUpperBound();
        if( upperBoundSize instanceof ConstantSize )
        {
            final ConstantSize upperBoundConstantSize = (ConstantSize)upperBoundSize;
            if( upperBoundConstantSize.getUnit() == ConstantSize.PIXEL )
            {
                upperBound = ( upperBoundConstantSize ).getPixelSize( null );
            }
        }
        return Optional.ofNullable( upperBound );
    }

    /**
     * Computes and returns the horizontal and vertical grid origins. Performs the same layout process as
     * <code>#layoutContainer</code> but does not layout the components.
     * <p>
     * This method has been added only to make it easier to debug the form layout. <strong>You must not call
     * this method directly; It may be removed in a future release or the visibility may be reduced.</strong>
     *
     * @param parent
     *            the <code>Container</code> to inspect
     * @param aSnappedWidth
     * @param aSnappedHeight
     * @return an object that comprises the grid x and y origins
     */
    public LayoutInfo getLayoutInfo( final FormLayoutPane parent, final double aSnappedWidth,
        final double aSnappedHeight )
    {
        initializeColComponentLists();
        initializeRowComponentLists();

        // TODO
        // Insets insets = parent.getInsets();
        // int totalWidth = size.width - insets.left - insets.right;
        // int totalHeight = size.height - insets.top - insets.bottom;

        final Insets insets = parent != null ? parent.getInsets() : Insets.EMPTY;
        final double totalWidth = aSnappedWidth - snapSpaceX( insets.getLeft() ) - snapSpaceX( insets.getRight() );
        final double totalHeight = aSnappedHeight - snapSpaceY( insets.getTop() ) - snapSpaceY( insets.getBottom() );

        final double[] x = computeGridOrigins( parent, totalWidth, insets.getLeft(), colSpecs, colComponents,
            colGroupIndices, spannedNodesInfoForColumns(), nodeToSpannedCollsMapping, minimumWidthMeasure,
            preferredWidthMeasure, columnPositionProvider, columnSpanSizeProvider );

        final double[] y = computeGridOrigins( parent, totalHeight, insets.getTop(), rowSpecs, rowComponents,
            rowGroupIndices, spanedNodesInfoForRows(), nodeToSpannedRowsMapping, minimumHeightMeasure,
            preferredHeightMeasure, rowPositionProvider, rowSpanSizeProvider );

        final LayoutInfo layoutInfo = new LayoutInfo( x, y );
        return layoutInfo;
    }

    /**
     * Creates and returns a deep copy of the given array. Unlike <code>#clone</code> that performs a shallow
     * copy, this method copies both array levels.
     *
     * @param array
     *            the array to clone
     * @return a deep copy of the given array
     * @see Object#clone()
     */
    private int[][] deepClone( final int[][] array )
    {
        final int[][] result = new int[ array.length ][];
        for( int i = 0; i < result.length; i++ )
        {
            result[ i ] = array[ i ].clone();
        }
        return result;
    }

    // /**
    // * Computes and returns a table that maps a column/row index to the maximum number of columns/rows that
    // a
    // * component can span without spanning a growing column.
    // * <p>
    // * Iterates over the specs from right to left/bottom to top, sets the table value to zero if a spec can
    // * grow, otherwise increases the span by one.
    // * <p>
    // * <strong>Examples:</strong>
    // *
    // * <pre>
    // * "pref, 4dlu, pref, 2dlu, p:grow, 2dlu, pref" ->
    // * [4, 3, 2, 1, 0, MAX_VALUE, MAX_VALUE]
    // *
    // * "p:grow, 4dlu, p:grow, 9dlu, pref" ->
    // * [0, 1, 0, MAX_VALUE, MAX_VALUE]
    // *
    // * "p, 4dlu, p, 2dlu, 0:grow" ->
    // * [4, 3, 2, 1, 0]
    // * </pre>
    // *
    // * @param formSpecs
    // * the column specs or row specs
    // * @return a table that maps a spec index to the maximum span for fixed size specs
    // */
    // private int[] computeMaximumFixedSpanTable( List< ? extends FormSpec > formSpecs )
    // {
    // int size = formSpecs.size();
    // int[] table = new int[ size ];
    // int maximumFixedSpan = Integer.MAX_VALUE; // Could be 1
    // for( int i = size - 1; i >= 0; i-- )
    // {
    // FormSpec spec = formSpecs.get( i ); // ArrayList access
    // if( spec.canGrow() )
    // {
    // maximumFixedSpan = 0;
    // }
    // table[ i ] = maximumFixedSpan;
    // if( maximumFixedSpan < Integer.MAX_VALUE )
    // {
    // maximumFixedSpan++;
    // }
    // }
    // return table;
    // }

    // Helper Code ************************************************************

    /**
     * In addition to the default serialization mechanism this class invalidates the component size cache. The
     * cache will be populated again after the deserialization. Also, the fields <code>colComponents</code>
     * and <code>rowComponents</code> have been marked as transient to exclude them from the serialization.
     */
    private void writeObject( final ObjectOutputStream out ) throws IOException
    {
        invalidateCaches();
        out.defaultWriteObject();
    }

    // Measuring Component Sizes ********************************************

    /**
     * An interface that describes how to measure a <code>Component</code>. Used to abstract from horizontal
     * and vertical dimensions as well as minimum and preferred sizes.
     *
     * @since 1.1
     */
    public static interface Measure
    {

        /**
         * Computes and returns the size of the given <code>Component</code>.
         *
         * @param component
         *     the component to measure
         *
         * @return the component's size
         */
        double sizeOf( Node component );
    }

    private Dimension2D getMinSize( final Node aNode )
    {
        if( aNode instanceof Region )
        {
            return getMinSize( (Region)aNode );
        }
        return new Dimension2D( snapSizeX( aNode.minWidth( USE_COMPUTED_SIZE ) ),
            snapSizeY( aNode.minHeight( USE_COMPUTED_SIZE ) ) );
    }

    private Dimension2D getMinSize( final Region aNode )
    {
        double width = aNode.getMinWidth();
        if( width == USE_COMPUTED_SIZE )
        {
            width = aNode.minWidth( USE_COMPUTED_SIZE );
        }
        double height = aNode.getMinHeight();
        if( height == USE_COMPUTED_SIZE )
        {
            height = aNode.minHeight( USE_COMPUTED_SIZE );
        }
        return new Dimension2D( snapSizeX( width ), snapSizeY( height ) );
    }

    private Dimension2D getPrefSize( final Node aNode )
    {
        if( aNode instanceof Region )
        {
            return getPrefSize( (Region)aNode );
        }
        return new Dimension2D( snapSizeX( aNode.prefWidth( USE_COMPUTED_SIZE ) ),
            snapSizeY( aNode.prefHeight( USE_COMPUTED_SIZE ) ) );
    }

    private Dimension2D getPrefSize( final Region aNode )
    {
        double prefWidth = aNode.getPrefWidth();
        if( prefWidth == USE_COMPUTED_SIZE )
        {
            prefWidth = aNode.prefWidth( USE_COMPUTED_SIZE );
            if( prefWidth == 0.0 )
            {
                aNode.applyCss();
                prefWidth = aNode.prefWidth( USE_COMPUTED_SIZE );
            }
        }
        double prefHeight = aNode.getPrefHeight();
        if( prefHeight == USE_COMPUTED_SIZE )
        {
            prefHeight = aNode.prefHeight( USE_COMPUTED_SIZE );
            if( prefHeight == 0.0 )
            {
                aNode.applyCss();
                prefHeight = aNode.prefHeight( USE_COMPUTED_SIZE );
            }
        }
        return new Dimension2D( snapSizeX( prefWidth ), snapSizeY( prefHeight ) );
    }

    public void setDebugLinesVisible( final boolean aVisible )
    {
        // TODO 21 lip 2015 gpasieka: Make a class in css file instead raw setStyle
        final String borderString =
            "-fx-border-color: #FF0000; -fx-border-style: solid; -fx-border-width: 1;";

        if( !debugLinesVisible && aVisible )
        {
            getChildren().forEach( node -> {
                String currentStyle = getStyle();
                currentStyle += " " + borderString;
                node.setStyle( currentStyle );
            } );

            getChildren().addListener( debugLinesListener );
        }
        else
        {
            if( debugLinesVisible )
            {
                getChildren().forEach( node -> {
                    final String currentStyle = getStyle();
                    currentStyle.replace( borderString, "" );
                    setStyle( currentStyle );
                } );

                getChildren().removeListener( debugLinesListener );
            }
        }
        debugLinesVisible = aVisible;
    }

    // Caching Component Sizes **********************************************

    public void setFocusTraversalPolicy( final FocusTraversalPolicy traversalPolicy )
    {
        this.traversalPolicy = traversalPolicy;
    }

    // Exposing the Layout Information **************************************

    /**
     * @return current layout info
     */
    public LayoutInfo getLayoutInfo()
    {
        return currentLayoutInfo.getValue();
    }

    /**
     * @return current layout info read only property
     */
    public ReadOnlyObjectProperty< LayoutInfo > layoutInfoProperty()
    {
        return currentLayoutInfo.getReadOnlyProperty();
    }

    public void sortByTraversalPolicy()
    {
        final Node[] toSortArr = getChildren().toArray( new Node[ 0 ] );
        final List< Node > toSort = new ArrayList<>( Arrays.asList( toSortArr ) );
        final AbstractFocusOrderComparator comparator =
            traversalPolicy == FocusTraversalPolicy.VERTICAL ? new VerticalOrderComparator() : new HorizontalOrderComparator();
        toSort.sort( comparator );

        getChildren().clear();
        getChildren().setAll( toSort );
    }

    private List< Node >[] spanedNodesInfoForRows()
    {
        return prepareSpannedNodesInfo( rowSpecs, rowPositionProvider, rowSpanSizeProvider );
    }

    private List< Node >[] spannedNodesInfoForColumns()
    {
        return prepareSpannedNodesInfo( colSpecs, columnPositionProvider, columnSpanSizeProvider );
    }

    @SuppressWarnings( "unchecked" )
    public static < T extends Parent > Optional< T > getAncestorOfClass( final Class< T > aClass,
        final Node aComponent )
    {
        if( aClass == null || aComponent == null )
        {
            return Optional.empty();
        }

        Parent parent = aComponent.getParent();
        while( parent != null && !( aClass.isInstance( parent ) ) )
        {
            parent = parent.getParent();
        }
        return (Optional< T >)Optional.ofNullable( parent );
    }

    // Helper Code **********************************************************

    /**
     * Sets the constraints for the specified component in this layout.
     *
     * @param component
     *     the component to be modified
     * @param constraints
     *     the constraints to be applied
     *
     * @throws NullPointerException
     *     if the component or constraints object is <code>null</code>
     */
    public static void setConstraints( final Node component, final CellConstraints constraints )
    {
        if( component == null )
        {
            throw new NullPointerException( "The component must not be null." );
        }
        if( constraints == null )
        {
            throw new NullPointerException( "The constraints must not be null." );
        }

        // constraints.ensureValidGridBounds( getColumnCount(), getRowCount() );
        // constraintMap.put( component, constraints.clone() );
        component.getProperties().put( CELL_CONSTRAINTS_KEY, constraints.clone() );
    }

    // Serialization ********************************************************

    private static CellConstraints getConstraints0( final Node component )
    {
        return (CellConstraints)component.getProperties().get( CELL_CONSTRAINTS_KEY );
    }

    // Debug Helper Code ****************************************************

    /*
     * // Prints the given column widths and row heights. private void printSizes(String title, int[]
     * colWidths, int[] rowHeights) { System.out.println(); System.out.println(title); int totalWidth = 0;
     * System.out.print("Column widths: "); for (int i=0; i < getColumnCount(); i++) { int width =
     * colWidths[i]; totalWidth += width; System.out.print(width + ", "); } System.out.println(" Total=" +
     * totalWidth);
     *
     * int totalHeight = 0; System.out.print("Row heights:   "); for (int i=0; i < getRowCount(); i++) { int
     * height = rowHeights[i]; totalHeight += height; System.out.print(height + ", "); } System.out.println(
     * " Total=" + totalHeight); System.out.println(); }
     */

    //    private Dimension2D getSize( final Node aNode )
    //    {
    //        if( aNode instanceof Region )
    //        {
    //            return new Dimension2D( ((Region)aNode).getWidth(), ((Region)aNode).getHeight() );
    //        }
    //        else
    //        {
    //            final Bounds bounds = aNode.getLayoutBounds();
    //            return new Dimension2D( Math.ceil( bounds.getWidth() ), Math.ceil( bounds.getHeight() ) );
    //        }
    //
    //    }

    /**
     * Computes and returns the sum of integers in the given array of ints.
     *
     * @param sizes
     *     an array of ints to sum up
     *
     * @return the sum of ints in the array
     */
    private static double sum( final double[] sizes )
    {
        double sum = 0;
        for( int i = sizes.length - 1; i >= 0; i-- )
        {
            sum += sizes[ i ];
        }
        return sum;
    }

    private static Function< Node, int[] > nodeToSpannedSpecsMapping(
        final Function< CellConstraints, Integer > positionProvider,
        final Function< CellConstraints, Integer > spanSizeProvider )
    {
        return n -> {
            final CellConstraints cellConstraints = getConstraints0( n );
            final int position = positionProvider.apply( cellConstraints );
            return IntStream.range( position, position + spanSizeProvider.apply( cellConstraints ) )
                .toArray();

        };
    }

    /**
     * An abstract implementation of the <code>Measure</code> interface that caches component sizes.
     */
    private abstract static class CachingMeasure implements Measure
    {

        /**
         * Holds previously requested component sizes. Used to minimize size requests to subcomponents.
         */
        protected final ComponentSizeCache cache;

        private CachingMeasure( final ComponentSizeCache cache )
        {
            this.cache = cache;
        }

    }

    /**
     * Measures a component by computing its minimum width.
     */
    private static final class MinimumWidthMeasure extends CachingMeasure
    {
        private MinimumWidthMeasure( final ComponentSizeCache cache )
        {
            super( cache );
        }

        @Override
        public double sizeOf( final Node c )
        {
            return cache.getMinimumSize( c ).getWidth();
        }
    }

    /**
     * Measures a component by computing its minimum height.
     */
    private static final class MinimumHeightMeasure extends CachingMeasure
    {
        private MinimumHeightMeasure( final ComponentSizeCache cache )
        {
            super( cache );
        }

        @Override
        public double sizeOf( final Node c )
        {
            return cache.getMinimumSize( c ).getHeight();
        }
    }

    /**
     * Measures a component by computing its preferred width.
     */
    private static final class PreferredWidthMeasure extends CachingMeasure
    {
        private PreferredWidthMeasure( final ComponentSizeCache cache )
        {
            super( cache );
        }

        @Override
        public double sizeOf( final Node c )
        {
            return cache.getPreferredSize( c ).getWidth();
        }
    }

    public boolean isDebugLinesVisible()
    {
        return debugLinesVisible;
    }

    public FocusTraversalPolicy getFocusTraversalPolicy()
    {
        return traversalPolicy;
    }

    /**
     * Measures a component by computing its preferred height.
     */
    private static final class PreferredHeightMeasure extends CachingMeasure
    {
        private PreferredHeightMeasure( final ComponentSizeCache cache )
        {
            super( cache );
        }

        @Override
        public double sizeOf( final Node c )
        {
            return cache.getPreferredSize( c ).getHeight();
        }
    }

    /**
     * Stores column and row origins.
     */
    public static final class LayoutInfo
    {

        /**
         * Holds the origins of the columns.
         */
        public final double[] columnOrigins;

        /**
         * Holds the origins of the rows.
         */
        public final double[] rowOrigins;

        private LayoutInfo( final double[] xOrigins, final double[] yOrigins )
        {
            columnOrigins = xOrigins;
            rowOrigins = yOrigins;
        }

        /**
         * Returns the layout's horizontal origin, the origin of the first column.
         *
         * @return the layout's horizontal origin, the origin of the first column.
         */
        public double getX()
        {
            return columnOrigins[ 0 ];
        }

        /**
         * Returns the layout's vertical origin, the origin of the first row.
         *
         * @return the layout's vertical origin, the origin of the first row.
         */
        public double getY()
        {
            return rowOrigins[ 0 ];
        }

        /**
         * Returns the layout's width, the size between the first and the last column origin.
         *
         * @return the layout's width.
         */
        public double getWidth()
        {
            return columnOrigins[ columnOrigins.length - 1 ] - columnOrigins[ 0 ];
        }

        /**
         * Returns the layout's height, the size between the first and last row.
         *
         * @return the layout's height.
         */
        public double getHeight()
        {
            return rowOrigins[ rowOrigins.length - 1 ] - rowOrigins[ 0 ];
        }

    }

    public enum FocusTraversalPolicy
    {
        HORIZONTAL, VERTICAL
    }

    /**
     * A cache for component minimum and preferred sizes. Used to reduce the requests to determine a
     * component's size.
     */
    private final class ComponentSizeCache
    {

        /**
         * Maps components to their minimum sizes.
         */
        private final Map< Node, Dimension2D > minimumSizes;

        /**
         * Maps components to their preferred sizes.
         */
        private final Map< Node, Dimension2D > preferredSizes;

        /**
         * Constructs a <code>ComponentSizeCache</code>.
         *
         * @param initialCapacity
         *     the initial cache capacity
         */
        private ComponentSizeCache( final int initialCapacity )
        {
            minimumSizes = new HashMap<>( initialCapacity );
            preferredSizes = new HashMap<>( initialCapacity );
        }

        /**
         * Invalidates the cache. Clears all stored size information.
         */
        void invalidate()
        {
            minimumSizes.clear();
            preferredSizes.clear();
        }

        /**
         * Returns the minimum size for the given component. Tries to look up the value from the cache; lazily
         * creates the value if it has not been requested before.
         *
         * @param component
         *     the component to compute the minimum size
         *
         * @return the component's minimum size
         */
        Dimension2D getMinimumSize( final Node component )
        {
            Dimension2D size = minimumSizes.get( component );
            if( size == null )
            {
                size = getMinSize( component );
                minimumSizes.put( component, size );
            }
            return size;
        }

        /**
         * Returns the preferred size for the given component. Tries to look up the value from the cache;
         * lazily creates the value if it has not been requested before.
         *
         * @param component
         *     the component to compute the preferred size
         *
         * @return the component's preferred size
         */
        Dimension2D getPreferredSize( final Node component )
        {
            Dimension2D size = preferredSizes.get( component );
            if( size == null || size.getWidth() <= 0 || size.getWidth() <= 0 )
            {
                size = getPrefSize( component );
                // preferredSizes.put( component, size );
            }
            return size;
        }

        void removeEntry( final Node component )
        {
            minimumSizes.remove( component );
            preferredSizes.remove( component );
        }
    }

    private abstract class AbstractFocusOrderComparator implements Comparator< Node >
    {
        @Override
        public int compare( final Node aLhs, final Node aRhs )
        {
            int retCompare = 0;
            final CellConstraints lConstraints =
                (CellConstraints)aLhs.getProperties().get( CELL_CONSTRAINTS_KEY );
            final CellConstraints rConstraints = (CellConstraints)aRhs.getProperties().get( CELL_CONSTRAINTS_KEY );

            retCompare = compareByOrder( lConstraints, rConstraints );
            if( retCompare == 0 )
            {
                retCompare = compareByCC( lConstraints, rConstraints );
            }
            return retCompare;
        }

        private int compareByOrder( final CellConstraints aLConstraints, final CellConstraints aRConstraints )
        {
            int retCompare = 0;

            if( aLConstraints == null && aRConstraints != null )
            {
                retCompare = 1;
            }
            else if( aLConstraints != null && aRConstraints == null )
            {
                retCompare = -1;
            }
            else if( aLConstraints != null && aRConstraints != null )
            {
                retCompare = aLConstraints.focusIndex == aRConstraints.focusIndex ? 0
                    : aLConstraints.focusIndex > aRConstraints.focusIndex ? 1 : -1;
            }

            return retCompare;
        }

        protected int compareByCC( final CellConstraints aLConstraints, final CellConstraints aRConstraints )
        {
            int retCompare = 0;

            if( aLConstraints == null && aRConstraints != null )
            {
                retCompare = 1;
            }
            else if( aLConstraints != null && aRConstraints == null )
            {
                retCompare = -1;
            }
            else if( aLConstraints != null && aRConstraints != null )
            {
                retCompare = compareCC( aLConstraints, aRConstraints );
            }

            return retCompare;
        }

        abstract protected int compareCC( CellConstraints aLConstraints, CellConstraints aRConstraints );
    }

    private class HorizontalOrderComparator extends AbstractFocusOrderComparator
    {
        @Override
        protected int compareCC( final CellConstraints aLConstraints, final CellConstraints aRConstraints )
        {
            int retCompare = 0;

            if( aLConstraints.gridY == aRConstraints.gridY )
            {
                if( aLConstraints.gridX != aRConstraints.gridX )
                {
                    retCompare = aLConstraints.gridX > aRConstraints.gridX ? 1 : -1;
                }
            }
            else
            {
                retCompare = aLConstraints.gridY > aRConstraints.gridY ? 1 : -1;
            }

            return retCompare;
        }
    }

    private class VerticalOrderComparator extends AbstractFocusOrderComparator
    {
        @Override
        protected int compareCC( final CellConstraints aLConstraints, final CellConstraints aRConstraints )
        {
            int retCompare = 0;

            if( aLConstraints.gridX == aRConstraints.gridX )
            {
                if( aLConstraints.gridY != aRConstraints.gridY )
                {
                    retCompare = aLConstraints.gridY > aRConstraints.gridY ? 1 : -1;
                }
            }
            else
            {
                retCompare = aLConstraints.gridX > aRConstraints.gridX ? 1 : -1;
            }

            return retCompare;
        }
    }

    public ReadOnlyListProperty< ColumnSpec > colSpecsProperty()
    {
        return colSpecsProperty;
    }

    public ReadOnlyListProperty< RowSpec > rowSpecsProperty()
    {
        return rowSpecsProperty;
    }

    /**
     * Components which position id greater than whole pane size will be layouted outside this pane.
     */
    public void setLayoutOverflowOutside()
    {
        layoutOverflowOutside.set( true );
    }

    /**
     * @return <code>true</code> if overflow content will be layouted outside pane; <code>false</code>
     *         otherwise.
     */
    public boolean isLayoutOverflowOutside()
    {
        return layoutOverflowOutside.get();
    }

    /**
     * @return <code>true</code> if overflow content will be layouted inside pane (sticked to bottom/right
     *         border); <code>false</code> otherwise.
     */
    public boolean isLayoutOverflowInside()
    {
        return !isLayoutOverflowOutside();
    }

    /**
     * Components which position id greater than whole pane size will be layouted inside this pane, sticked to
     * bottom/right border
     */
    public void setLayoutOverflowInside()
    {
        layoutOverflowOutside.set( false );
    }

    public BooleanProperty layoutOverflowOutsideProperty()
    {
        return layoutOverflowOutside;
    }
}
