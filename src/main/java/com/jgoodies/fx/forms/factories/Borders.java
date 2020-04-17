//******************************************************************
//
//  Borders.java
//  Copyright 2016 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.factories;

import com.jgoodies.fx.forms.layout.ConstantSize;
import com.jgoodies.fx.forms.layout.Sizes;
import com.jgoodies.fx.forms.util.LayoutStyle;

import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;

/**
 * Provides constants and factory methods for <code>Border</code>s that use instances of {@link ConstantSize}
 * to define the margins.
 * <p>
 * <strong>Examples:</strong><br>
 *
 * <pre>
 * Borders.DLU2_BORDER
 * Borders.createEmptyBorder(Sizes.DLUY4, Sizes.DLUX2, Sizes.DLUY4, Sizes.DLUX2);
 * Borders.createEmptyBorder("4dlu, 2dlu, 4dlu, 2dlu");
 * </pre>
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.10 $
 * @see Border
 * @see Sizes
 */
public final class Borders
{

    private Borders()
    {
        // Overrides default constructor; prevents instantiation.
    }

    // Constant Borders *****************************************************

    /**
     * A prepared and reusable EmptyBorder without gaps.
     */
    public static final Border EMPTY_BORDER = Border.EMPTY;

    /**
     * A prepared and reusable Border with 2dlu on all sides.
     */
    public static final Border DLU2_BORDER =
        createEmptyBorder( Sizes.DLUY2,
            Sizes.DLUX2,
            Sizes.DLUY2,
            Sizes.DLUX2 );

    /**
     * A prepared and reusable Border with 4dlu on all sides.
     */
    public static final Border DLU4_BORDER =
        createEmptyBorder( Sizes.DLUY4,
            Sizes.DLUX4,
            Sizes.DLUY4,
            Sizes.DLUX4 );

    /**
     * A prepared and reusable Border with 7dlu on all sides.
     */
    public static final Border DLU7_BORDER =
        createEmptyBorder( Sizes.DLUY7,
            Sizes.DLUX7,
            Sizes.DLUY7,
            Sizes.DLUX7 );

    /**
     * A prepared Border with 14dlu on all sides.
     */
    public static final Border DLU14_BORDER =
        createEmptyBorder( Sizes.DLUY14,
            Sizes.DLUX14,
            Sizes.DLUY14,
            Sizes.DLUX14 );

    /**
     * A prepared Border with 21dlu on all sides.
     *
     * @since 1.2
     */
    public static final Border DLU21_BORDER =
        createEmptyBorder( Sizes.DLUY21,
            Sizes.DLUX21,
            Sizes.DLUY21,
            Sizes.DLUX21 );

    /**
     * A standardized Border that describes the gap between a component and a button bar in its bottom.
     */
    public static final Border BUTTON_BAR_GAP_BORDER =
        createEmptyBorder(
            LayoutStyle.getCurrent().getButtonBarPad(),
            Sizes.dluX( 0 ),
            Sizes.dluY( 0 ),
            Sizes.dluX( 0 ) );

    /**
     * A standardized Border that describes the border around a dialog content that has no tabs.
     *
     * @see #TABBED_DIALOG_BORDER
     */
    public static final Border DIALOG_BORDER =
        createEmptyBorder(
            LayoutStyle.getCurrent().getDialogMarginY(),
            LayoutStyle.getCurrent().getDialogMarginX(),
            LayoutStyle.getCurrent().getDialogMarginY(),
            LayoutStyle.getCurrent().getDialogMarginX() );

    /**
     * A standardized Border that describes the border around a dialog content that uses tabs.
     *
     * @see #DIALOG_BORDER
     */
    public static final Border TABBED_DIALOG_BORDER =
        createEmptyBorder(
            LayoutStyle.getCurrent().getTabbedDialogMarginY(),
            LayoutStyle.getCurrent().getTabbedDialogMarginX(),
            LayoutStyle.getCurrent().getTabbedDialogMarginY(),
            LayoutStyle.getCurrent().getTabbedDialogMarginX() );

    // Factory Methods ******************************************************

    /**
     * Creates and returns an <code>EmptyBorder</code> with the specified gaps.
     *
     * @param top
     *            the top gap
     * @param left
     *            the left-hand side gap
     * @param bottom
     *            the bottom gap
     * @param right
     *            the right-hand side gap
     * @return an <code>EmptyBorder</code> with the specified gaps
     * @throws NullPointerException
     *             if top, left, bottom, or right is {@code null}
     * @see #createEmptyBorder(String)
     */
    public static Border createEmptyBorder( ConstantSize top, ConstantSize left,
        ConstantSize bottom, ConstantSize right )
    {
        Insets insets = new Insets( top.getPixelSize( null ), right.getPixelSize( null ),
            bottom.getPixelSize( null ), left.getPixelSize( null ) );
        BorderStroke stroke = new BorderStroke( null, null, null, null, insets );
        Border border = new Border( stroke );
        return border;
    }

    /**
     * Creates and returns a <code>Border</code> using sizes as specified by the given string. This string is
     * a comma-separated encoding of 4 <code>ConstantSize</code>s.
     *
     * @param encodedSizes
     *            top, left, bottom, right gap encoded as String
     * @return an <code>EmptyBorder</code> with the specified gaps
     * @see #createEmptyBorder(ConstantSize, ConstantSize, ConstantSize, ConstantSize)
     */
    public static Border createEmptyBorder( String encodedSizes )
    {
        String[] token = encodedSizes.split( "\\s*,\\s*" );
        int tokenCount = token.length;
        if( token.length != 4 )
        {
            throw new IllegalArgumentException(
                "The border requires 4 sizes, but \"" + encodedSizes +
                    "\" has " + tokenCount + "." );
        }
        ConstantSize top = Sizes.constant( token[ 0 ], false );
        ConstantSize left = Sizes.constant( token[ 1 ], true );
        ConstantSize bottom = Sizes.constant( token[ 2 ], false );
        ConstantSize right = Sizes.constant( token[ 3 ], true );
        return createEmptyBorder( top, left, bottom, right );
    }
}
