//******************************************************************
//
//  NullGridDecorator.java
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
 * Null GridDecorator.
 *
 * @author created: gpasieka on 10 sie 2015 11:14:40
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
public class NullGridDecorator implements GridDecoratorIf
{
    public static NullGridDecorator INSTANCE = new NullGridDecorator();

    private NullGridDecorator()
    {
    }

    @Override
    public List< Line > getLines()
    {
        return null;
    }

    @Override
    public void createLines()
    {
    }

    @Override
    public void layoutLines( double[] aOrigins )
    {
    }

    @Override
    public ListChangeListener< Node > getFormPaneChildrenListener()
    {
        return null;
    }

    @Override
    public MapChangeListener< Object, Object > getFormPaneChildrenSpansListener()
    {
        return null;
    }

    @Override
    public ChangeListener< Number > getGridLanesListener()
    {
        return null;
    }
}
