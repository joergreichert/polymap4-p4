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
package org.polymap.p4.style.sld.from;

import org.geotools.styling.AnchorPoint;
import org.geotools.styling.Displacement;
import org.geotools.styling.Fill;
import org.geotools.styling.LinePlacement;
import org.geotools.styling.PointPlacement;
import org.opengis.style.Font;
import org.polymap.p4.style.entities.StyleColor;
import org.polymap.p4.style.entities.StyleCoord;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.style.sld.from.helper.StyleFontFromSLDHelper;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelFromSLDVisitor
        extends AbstractStyleFromSLDVisitor {

    private final StyleLabel styleLabel;


    public StyleLabelFromSLDVisitor( StyleLabel styleLabel ) {
        this.styleLabel = styleLabel;
    }


    @Override
    public void visit( org.geotools.styling.TextSymbolizer ts ) {
        if (ts.getLabel() != null) {
            styleLabel.labelText.set( (String)ts.getLabel().accept( getLabelExpressionVisitor(), null ) );
        }
        super.visit( ts );
    }


    @Override
    public void visit( Fill fill ) {
        if (fill.getColor() != null) {
            styleLabel.labelFontColor.set( (StyleColor)fill.getColor().accept( getColorExpressionVisitor(), null ) );
        }
        super.visit( fill );
    }


    public void visit( Font font ) {
        if (font.getFamily().size() > 0) {
            styleLabel.labelFont.createValue( newValue -> new StyleFontFromSLDHelper().fromSLD( newValue, font ) );
        }
        super.visit( font );
    }


    @Override
    public void visit( PointPlacement pp ) {
        if (pp.getRotation() != null) {
            styleLabel.labelRotation.set( (double)pp.getRotation().accept( getNumberExpressionVisitor(), null ) );
        }
        super.visit( pp );
    }


    @Override
    public void visit( AnchorPoint ap ) {
        if (ap.getAnchorPointX() != null && ap.getAnchorPointY() != null) {
            StyleCoord styleCoord = new StyleCoord();
            styleCoord.x.set( (double)ap.getAnchorPointX().accept( getNumberExpressionVisitor(), null ) );
            styleCoord.y.set( (double)ap.getAnchorPointY().accept( getNumberExpressionVisitor(), null ) );
            styleLabel.labelAnchor.set( styleCoord );
        }
    }


    @Override
    public void visit( Displacement dis ) {
        if (dis.getDisplacementX() != null && dis.getDisplacementY() != null) {
            StyleCoord styleCoord = new StyleCoord();
            styleCoord.x.set( (double)dis.getDisplacementX().accept( getNumberExpressionVisitor(), null ) );
            styleCoord.y.set( (double)dis.getDisplacementY().accept( getNumberExpressionVisitor(), null ) );
            styleLabel.labelOffset.set( styleCoord );
        }
    }


    @Override
    public void visit( LinePlacement lp ) {
    }
}
