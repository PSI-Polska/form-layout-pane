//******************************************************************
//
//  LineOrientationIf.java
//  Copyright 2015 PSI AG. All rights reserved.
//  PSI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms
//
// ******************************************************************

package com.jgoodies.fx.forms.grid;

import com.jgoodies.fx.forms.grid.FormLayoutGrid.GridType;


/**
 * Interface for line orientation dependant operations.
 * @see LineOrientation
 *
 * @author created: gpasieka on 11 sie 2015 09:14:28
 * @author last change: $Author: $ on $Date: $
 * @version $Revision: $
 */
interface LineOrientationIf
{
    /**
     * Get GridDecorator.
     * @param aGridType
     * @param aGrid
     * @return
     */
    GridDecoratorIf getGridDecorator( GridType aGridType, FormLayoutGrid aGrid );
}


