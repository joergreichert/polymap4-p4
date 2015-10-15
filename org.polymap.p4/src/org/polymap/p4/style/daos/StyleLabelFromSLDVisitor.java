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
package org.polymap.p4.style.daos;

import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.TextSymbolizer;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelFromSLDVisitor
        extends AbstractStyleFromSLDVisitor {

    private final StyleLabelDao styleLabelDao;


    public StyleLabelFromSLDVisitor( StyleLabelDao styleLabelDao ) {
        this.styleLabelDao = styleLabelDao;
    }


    @Override
    public void visit( TextSymbolizer ts ) {
        if(ts.getLabel() != null) {
            styleLabelDao.setLabelText((String) ts.getLabel().accept( getExpressionVisitor(), null ));
            if(ts.getFill() != null && ts.getFill().getColor() != null) {
                styleLabelDao.setLabelFontColor((RGB) ts.getFill().getColor().accept( getExpressionVisitor(), null ));
            }
            if(ts.getFont() != null) {
                styleLabelDao.setLabelFont((String) ts.getFont().getFamily().get( 0 ).accept( getExpressionVisitor(), null ));
                styleLabelDao.setLabelFontSize((Integer) ts.getFont().getSize().accept( getExpressionVisitor(), null ));
                styleLabelDao.setLabelFontWeight((String) ts.getFont().getWeight().accept( getExpressionVisitor(), null ));
                styleLabelDao.setLabelFontStyle((String) ts.getFont().getStyle().accept( getExpressionVisitor(), null ));
            }
        }
    }
}
