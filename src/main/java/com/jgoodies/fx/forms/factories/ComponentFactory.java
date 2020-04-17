//******************************************************************
//
//  ComponentFactory.java
//  Copyright 2016 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.factories;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * An interface that defines the factory methods as used by the
 * {@link com.jgoodies.forms.builder.PanelBuilder} and its subclasses.
 * <p>
 * The String arguments passed to the methods <code>#createLabel(String)</code>,
 * <code>#createTitle(String)</code>, and <code>#createSeparator(String, int)</code> can contain an optional
 * mnemonic marker. The mnemonic and mnemonic index are indicated by a single ampersand (<tt>&amp;</tt>). For
 * example <tt>&quot;&amp;Save&quot</tt>, or <tt>&quot;Save&nbsp;&amp;as&quot</tt>. To use the ampersand
 * itself duplicate it, for example <tt>&quot;Look&amp;&amp;Feel&quot</tt>.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.6 $
 * @see DefaultComponentFactory
 * @see com.jgoodies.forms.builder.PanelBuilder
 */

public interface ComponentFactory
{

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
    Label createLabel( String textWithMnemonic );

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
    Label createTitle( String textWithMnemonic );

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
     * @return a title label with separator on the side
     */
    Node createSeparator( String textWithMnemonic, Pos alignment );

}