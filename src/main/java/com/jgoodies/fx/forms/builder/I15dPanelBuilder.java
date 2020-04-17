//******************************************************************
//
//  I15dPanelBuilder.java
//  Copyright 2016 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.builder;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.jgoodies.fx.forms.layout.FormLayoutPane;

/**
 * A general purpose panel builder that uses the {@link FormLayout} to lay out <code>JPanel</code>s. In
 * addition to its superclass {@link PanelBuilder} this class provides convenience behavior to map resource
 * keys to their associated internationalized (i15d) strings when adding labels, titles and titled separators.
 * <p>
 * The localized texts used in methods <code>#addI15dLabel</code> and <code>#addI15dTitle</code> can contain
 * an optional mnemonic marker. The mnemonic and mnemonic index are indicated by a single ampersand (
 * <tt>&amp;</tt>). For example <tt>&quot;&amp;Save&quot</tt>, or <tt>&quot;Save&nbsp;&amp;as&quot</tt>. To
 * use the ampersand itself, duplicate it, for example <tt>&quot;Look&amp;&amp;Feel&quot</tt>.
 * <p>
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.7 $
 * @since 1.0.3
 * @see ResourceBundle
 */
public class I15dPanelBuilder extends AbstractI15dPanelBuilder
{

    /**
     * Holds the <code>ResourceBundle</code> used to lookup internationalized (i15d) String resources.
     */
    private final ResourceBundle bundle;

    // Instance Creation ****************************************************

    /**
     * Constructs an <code>I15dPanelBuilder</code> for the given layout and resource bundle. Uses an instance
     * of <code>JPanel</code> as layout container.
     *
     * @param layout
     *            the <code>FormLayout</code> used to layout the container
     * @param bundle
     *            the <code>ResourceBundle</code> used to lookup i15d strings
     */
    public I15dPanelBuilder( FormLayoutPane layout, ResourceBundle bundle )
    {
        super( layout );
        this.bundle = bundle;
    }

    // Implementing Abstract Behavior *****************************************

    /**
     * Looks up and returns the internationalized (i15d) string for the given resource key from the
     * <code>ResourceBundle</code>.
     *
     * @param resourceKey
     *            the key to look for in the resource bundle
     * @return the associated internationalized string, or the resource key itself in case of a missing
     *         resource
     * @throws IllegalStateException
     *             if no <code>ResourceBundle</code> has been set
     */
    @Override
    protected String getI15dString( String resourceKey )
    {
        if( bundle == null )
        {
            throw new IllegalStateException( "You must specify a ResourceBundle" +
                " before using the internationalization support." );
        }
        try
        {
            return bundle.getString( resourceKey );
        }
        catch( MissingResourceException mre )
        {
            return resourceKey;
        }
    }

}
