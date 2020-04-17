//******************************************************************
//
//  DefaultComponentFactory.java
//  Copyright 2016 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.factories;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;

/**
 * A singleton implementation of the {@link ComponentFactory} interface that creates UI components as required
 * by the {@link com.jgoodies.forms.builder.PanelBuilder}.
 * <p>
 * The texts used in methods <code>#createLabel(String)</code> and <code>#createTitle(String)</code> can
 * contain an optional mnemonic marker. The mnemonic and mnemonic index are indicated by a single ampersand (
 * <tt>&amp;</tt>). For example <tt>&quot;&amp;Save&quot</tt>, or <tt>&quot;Save&nbsp;&amp;as&quot</tt>. To
 * use the ampersand itself duplicate it, for example <tt>&quot;Look&amp;&amp;Feel&quot</tt>.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.12 $
 */
public class DefaultComponentFactory implements ComponentFactory
{

    /**
     * Holds the single instance of this class.
     */
    private static final DefaultComponentFactory INSTANCE =
        new DefaultComponentFactory();

    /**
     * The character used to indicate the mnemonic position for labels.
     */
    private static final char MNEMONIC_MARKER = '_';

    // Instance *************************************************************

    /**
     * Returns the sole instance of this factory class.
     *
     * @return the sole instance of this factory class
     */
    public static DefaultComponentFactory getInstance()
    {
        return INSTANCE;
    }

    // Component Creation ***************************************************

    /**
     * Creates and returns a label with an optional mnemonic.
     * <p>
     *
     * <pre>
     * createLabel( "Name" ); // No mnemonic
     * createLabel( "N&ame" ); // Mnemonic is 'a'
     * createLabel( "Save &as" ); // Mnemonic is the second 'a'
     * createLabel( "Look&&Feel" ); // No mnemonic, text is Look&Feel
     * </pre>
     *
     * @param textWithMnemonic
     *            the label's text - may contain an ampersand (<tt>&amp;</tt>) to mark a mnemonic
     * @return an label with optional mnemonic
     */
    @Override
    public Label createLabel( String textWithMnemonic )
    {
        Label label = new Label();
        setTextAndMnemonic( label, textWithMnemonic );
        return label;
    }

    /**
     * Creates and returns a title label that uses the foreground color and font of a
     * <code>TitledBorder</code>.
     * <p>
     *
     * <pre>
     * createTitle( "Name" ); // No mnemonic
     * createTitle( "N&ame" ); // Mnemonic is 'a'
     * createTitle( "Save &as" ); // Mnemonic is the second 'a'
     * createTitle( "Look&&Feel" ); // No mnemonic, text is Look&Feel
     * </pre>
     *
     * @param textWithMnemonic
     *            the label's text - may contain an ampersand (<tt>&amp;</tt>) to mark a mnemonic
     * @return an emphasized title label
     */
    @Override
    public Label createTitle( String textWithMnemonic )
    {
        Label label = createTitleLabel();
        setTextAndMnemonic( label, textWithMnemonic );
        label.setAlignment( Pos.CENTER );
        return label;
    }

    /**
     * Creates and returns a labeled separator with the label in the left-hand side. Useful to separate
     * paragraphs in a panel; often a better choice than a <code>TitledBorder</code>.
     * <p>
     *
     * <pre>
     * createSeparator( "Name" ); // No mnemonic
     * createSeparator( "N&ame" ); // Mnemonic is 'a'
     * createSeparator( "Save &as" ); // Mnemonic is the second 'a'
     * createSeparator( "Look&&Feel" ); // No mnemonic, text is Look&Feel
     * </pre>
     *
     * @param textWithMnemonic
     *            the label's text - may contain an ampersand (<tt>&amp;</tt>) to mark a mnemonic
     * @return a title label with separator on the side
     */
    public Node createSeparator( String textWithMnemonic )
    {
        return createSeparator( textWithMnemonic, Pos.CENTER_LEFT );
    }

    /**
     * Creates and returns a labeled separator. Useful to separate paragraphs in a panel, which is often a
     * better choice than a <code>TitledBorder</code>.
     * <p>
     *
     * <pre>
     * final int LEFT = SwingConstants.LEFT;
     * createSeparator( "Name", LEFT ); // No mnemonic
     * createSeparator( "N&ame", LEFT ); // Mnemonic is 'a'
     * createSeparator( "Save &as", LEFT ); // Mnemonic is the second 'a'
     * createSeparator( "Look&&Feel", LEFT ); // No mnemonic, text is Look&Feel
     * </pre>
     *
     * @param textWithMnemonic
     *            the label's text - may contain an ampersand (<tt>&amp;</tt>) to mark a mnemonic
     * @param alignment
     *            text alignment, one of <code>SwingConstants.LEFT</code>, <code>SwingConstants.CENTER</code>,
     *            <code>SwingConstants.RIGHT</code>
     * @return a separator with title label
     */
    @Override
    public Node createSeparator( String textWithMnemonic, Pos alignment )
    {
        if( textWithMnemonic == null || textWithMnemonic.length() == 0 )
        {
            return new Separator();
        }
        Label title = createTitle( textWithMnemonic );
        title.setAlignment( alignment );
        return createSeparator( title );
    }

    /**
     * Creates and returns a labeled separator. Useful to separate paragraphs in a panel, which is often a
     * better choice than a <code>TitledBorder</code>.
     * <p>
     * The label's position is determined by the label's horizontal alignment, which must be one of:
     * <code>SwingConstants.LEFT</code>, <code>SwingConstants.CENTER</code>, <code>SwingConstants.RIGHT</code>
     * .
     * <p>
     * TODO: Since this method has been marked public in version 1.0.6, we need to precisely describe the
     * semantic of this method.
     * <p>
     * TODO: Check if we can relax the constraint for the label alignment and also accept LEADING and
     * TRAILING.
     *
     * @param label
     *            the title label component
     * @return a separator with title label
     * @throws NullPointerException
     *             if the label is <code>null</code>
     * @throws IllegalArgumentException
     *             if the label's horizontal alignment is not one of: <code>SwingConstants.LEFT</code>,
     *             <code>SwingConstants.CENTER</code>, <code>SwingConstants.RIGHT</code>.
     * @since 1.0.6
     */
    public Node createSeparator( Label label )
    {
        if( label == null )
        {
            throw new NullPointerException( "The label must not be null." );
        }
        Pos horizontalAlignment = label.getAlignment();
        if( (horizontalAlignment != Pos.CENTER_LEFT)
            && (horizontalAlignment != Pos.CENTER)
            && (horizontalAlignment != Pos.CENTER_RIGHT) )
        {
            throw new IllegalArgumentException( "The label's horizontal alignment"
                + " must be one of: LEFT, CENTER, RIGHT." );
        }

        return createTitledSeparator( label, horizontalAlignment );
    }

    // Helper Code ***********************************************************

    /**
     * Sets the text of the given label and optionally a mnemonic. The given text may contain an ampersand (
     * <tt>&amp;</tt>) to mark a mnemonic and its position. Such a marker indicates that the character that
     * follows the ampersand shall be the mnemonic. If you want to use the ampersand itself duplicate it, for
     * example <tt>&quot;Look&amp;&amp;Feel&quot</tt>.
     *
     * @param label
     *            the label that gets a mnemonic
     * @param textWithMnemonic
     *            the text with optional mnemonic marker
     * @since 1.2
     */
    public static void setTextAndMnemonic(
        Label label,
        String textWithMnemonic )
    {
        int markerIndex = textWithMnemonic.indexOf( MNEMONIC_MARKER );
        // No marker at all
        if( markerIndex == -1 )
        {
            label.setText( textWithMnemonic );
            return;
        }
        int mnemonicIndex = -1;
        int begin = 0;
        int end;
        int length = textWithMnemonic.length();
        int quotedMarkers = 0;
        StringBuffer buffer = new StringBuffer();
        do
        {
            // Check whether the next index has a mnemonic marker, too
            if( (markerIndex + 1 < length)
                && (textWithMnemonic.charAt( markerIndex + 1 ) == MNEMONIC_MARKER) )
            {
                end = markerIndex + 1;
                quotedMarkers++;
            }
            else
            {
                end = markerIndex;
                if( mnemonicIndex == -1 )
                {
                    mnemonicIndex = markerIndex - quotedMarkers;
                }
            }
            buffer.append( textWithMnemonic.substring( begin, end ) );
            begin = end + 1;
            markerIndex = begin < length
                ? textWithMnemonic.indexOf( MNEMONIC_MARKER, begin )
                : -1;
        }
        while( markerIndex != -1 );
        buffer.append( textWithMnemonic.substring( begin ) );

        String text = buffer.toString();
        label.setText( text );
        if( (mnemonicIndex != -1) && (mnemonicIndex < text.length()) )
        {
            label.setMnemonicParsing( true );
        }
    }

    private static Label createTitleLabel()
    {
        Label label = new Label();

        return label;

        // public void updateUI()
        // {
        // super.updateUI();
        // Color foreground = getTitleColor();
        // if( foreground != null )
        // {
        // setForeground( foreground );
        // }
        // setFont( getTitleFont() );
        // }
        //
        // private Color getTitleColor()
        // {
        // return UIManager.getColor( "TitledBorder.titleColor" );
        // }
        //
        // private Font getTitleFont()
        // {
        // return FormUtils.isLafAqua()
        // ? UIManager.getFont( "Label.font" ).deriveFont( Font.BOLD )
        // : UIManager.getFont( "TitledBorder.font" );
        // }
    }

    private static Node createTitledSeparator( Label aLabel, Pos aAlignment )
    {
        BorderPane borderPane = new BorderPane();

        if( aAlignment == Pos.CENTER_LEFT )
        {
            borderPane.setLeft( aLabel );
            borderPane.setCenter( new Separator() );
        }
        else if( aAlignment == Pos.CENTER_RIGHT )
        {
            borderPane.setCenter( new Separator() );
            borderPane.setRight( aLabel );
        }
        else if( aAlignment == Pos.CENTER )
        {
            borderPane.setLeft( new Separator() );
            borderPane.setCenter( aLabel );
            borderPane.setRight( new Separator() );
        }

        return borderPane;
    }
}
