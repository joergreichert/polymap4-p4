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
import org.geotools.styling.AnchorPoint;
import org.geotools.styling.Displacement;
import org.geotools.styling.Fill;
import org.geotools.styling.LinePlacement;
import org.geotools.styling.PointPlacement;
import org.opengis.style.Font;
import org.polymap.p4.style.daos.StyleLabelDao.Coord;

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
    public void visit( org.geotools.styling.TextSymbolizer ts ) {
        if (ts.getLabel() != null) {
            styleLabelDao.setLabelText( (String)ts.getLabel().accept( getLabelExpressionVisitor(), null ) );
        }
        super.visit( ts );
    }


    @Override
    public void visit( Fill fill ) {
        if (fill.getColor() != null) {
            styleLabelDao.setLabelFontColor( (RGB)fill.getColor().accept( getColorExpressionVisitor(), null ) );
        }
        super.visit( fill );
    }


    public void visit( Font font ) {
        if (font.getFamily().size() > 0) {
            styleLabelDao.setLabelFont( (String)font.getFamily().get( 0 ).accept( getStringExpressionVisitor(), null ) );
        }
        if (font.getSize() != null) {
            styleLabelDao.setLabelFontSize( ((Double)font.getSize().accept( getNumberExpressionVisitor(), null )) );
        }
        if (font.getWeight() != null) {
            styleLabelDao
                    .setLabelFontWeight( (String)font.getWeight().accept( getFontWeightExpressionVisitor(), null ) );
        }
        if (font.getStyle() != null) {
            styleLabelDao.setLabelFontStyle( (String)font.getStyle().accept( getFontStyleExpressionVisitor(), null ) );
        }
        super.visit( font );
    }


    @Override
    public void visit( PointPlacement pp ) {
        if (pp.getRotation() != null) {
            styleLabelDao.setLabelRotation( (double)pp.getRotation().accept( getNumberExpressionVisitor(), null ) );
        }
        super.visit( pp );
    }


    @Override
    public void visit( AnchorPoint ap ) {
        if (ap.getAnchorPointX() != null && ap.getAnchorPointY() != null) {
            styleLabelDao.setLabelAnchor( new Coord( (double)ap.getAnchorPointX().accept( getNumberExpressionVisitor(),
                    null ), (double)ap.getAnchorPointY().accept( getNumberExpressionVisitor(), null ) ) );
        }
    }


    @Override
    public void visit( Displacement dis ) {
        if (dis.getDisplacementX() != null && dis.getDisplacementY() != null) {
            styleLabelDao.setLabelOffset( new Coord( (double)dis.getDisplacementX().accept(
                    getNumberExpressionVisitor(), null ), (double)dis.getDisplacementY().accept(
                    getNumberExpressionVisitor(), null ) ) );
        }
    }


    @Override
    public void visit( LinePlacement lp ) {
    }
}
