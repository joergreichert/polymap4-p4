/*
 * polymap.org 
 * Copyright (C) 2015 individual contributors as indicated by the @authors tag. 
 * All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.p4.imports.labels;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * @author Joerg Reichert <joerg@mapzone.io>
 */
public abstract class AbstractShapeImportCellLabelProvider
        extends CellLabelProvider {

    private static final String CSS_FIRST_ROW = "firstRow";


    protected void handleBackgroundColor( ViewerCell cell ) {
        String data;
        if (isCellExpanded(cell)) {
            data = CSS_FIRST_ROW;
        }
        else {
            data = null;
        }
        setCellData( cell, RWT.CUSTOM_VARIANT, data );
    }

    protected void setCellData( ViewerCell cell, String key, String data ) {
        ViewerRow row = cell.getViewerRow();
        ((TreeItem)row.getItem()).setData( key, data );
    }
    
    protected boolean isCellExpanded(ViewerCell cell) {
        TreeItem treeItem = getTreeItem( cell );
        return treeItem.getExpanded();
    }

    protected TreeItem getTreeItem( ViewerCell cell ) {
        return (TreeItem)cell.getViewerRow().getItem();
    }
}
