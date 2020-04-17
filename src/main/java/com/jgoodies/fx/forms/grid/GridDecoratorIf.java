//******************************************************************
//
//  GridDecoratorIf.java
//  Copyright 2015 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.grid;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.shape.Line;

/**
 * Decorating FormPaneGrid with nonstandard behavior.
 * @see add further links
 *
 * @author created: gpasieka on 11 sie 2015 09:03:57
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
interface GridDecoratorIf
{
    /**
     * Get additional lines added by decorator.
     * @return
     */
    List< Line > getLines();

    /**
     * Create additional lines.
     */
    void createLines();

    /**
     * Layout lines (apply FormPane's size).
     * @param aOrigins
     */
    void layoutLines( double[] aOrigins );

    /**
     * Get listener of FormPane's children list.
     * @return
     */
    ListChangeListener< Node > getFormPaneChildrenListener();

    /**
     * Get listener of Node properties: rowspan & colspan.
     * @return
     */
    MapChangeListener< Object, Object > getFormPaneChildrenSpansListener();

    /**
     * Get listener of columns/rows number.
     * @return
     */
    ChangeListener< Number > getGridLanesListener();

}


