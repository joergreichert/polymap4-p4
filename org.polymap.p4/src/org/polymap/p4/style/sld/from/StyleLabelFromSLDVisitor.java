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

import java.util.function.Function;

import org.geotools.styling.AnchorPoint;
import org.geotools.styling.Displacement;
import org.geotools.styling.Fill;
import org.geotools.styling.Halo;
import org.geotools.styling.LinePlacement;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.TextSymbolizer;
import org.opengis.style.Font;
import org.polymap.model2.Property;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.style.sld.from.helper.StyleColorFromSLDHelper;
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
    public void visit( TextSymbolizer ts ) {
        if (ts.getLabel() != null) {
            styleLabel.labelText.set( (String)ts.getLabel().accept( getLabelExpressionVisitor(), null ) );
        }
        if(ts.getHalo() != null) {
            ts.getHalo().accept( this );
        }
        handleGeoServerVendorExtensions( ts, styleLabel );
        super.visit( ts );
    }
    
    @Override
    public void visit( Halo halo ) {
        if(halo.getRadius() != null) {
            styleLabel.haloRadius.set( (double)halo.getRadius().accept( getNumberExpressionVisitor(), null ) );
        }
        if(halo.getFill() != null) {
            new StyleColorFromSLDHelper().fromSLD( styleLabel.haloFill, halo.getFill().getColor() );
        }
    }

    private void handleGeoServerVendorExtensions( TextSymbolizer ts, StyleLabel styleLabel ) {
        handleDoubleVendorOption( ts, styleLabel.autoWrap );
        handleDoubleVendorOption( ts, styleLabel.maxDisplacement );
        handleBooleanVendorOption( ts, styleLabel.followLine );
        handleDoubleVendorOption( ts, styleLabel.maxAngleDelta );
        handleDoubleVendorOption( ts, styleLabel.repeat );
    }


    private void handleDoubleVendorOption( TextSymbolizer ts, Property<Double> property ) {
        handleVendorOption( ts, property, value -> {
            try {
                return Double.valueOf( value );
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return null;
        } );
    }


    private void handleBooleanVendorOption( TextSymbolizer ts, Property<Boolean> property ) {
        handleVendorOption( ts, property, value -> Boolean.valueOf( value ) );
    }


    private <T> void handleVendorOption( TextSymbolizer ts, Property<T> property, Function<String,T> converter ) {
        String label = property.info().getName();
        if (ts.hasOption( label )) {
            try {
                property.set( converter.apply( ts.getOptions().get( label ) ) );
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void visit( Fill fill ) {
        if (fill.getColor() != null) {
            new StyleColorFromSLDHelper().fromSLD( styleLabel.labelFontColor, fill.getColor() );
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
            styleLabel.labelAnchor.createValue( anchor -> {
                anchor.x.set( (double)ap.getAnchorPointX().accept( getNumberExpressionVisitor(), null ) );
                anchor.y.set( (double)ap.getAnchorPointY().accept( getNumberExpressionVisitor(), null ) );
                return anchor;
            } );
        }
    }


    @Override
    public void visit( Displacement dis ) {
        if (dis.getDisplacementX() != null && dis.getDisplacementY() != null) {
            styleLabel.labelOffset.createValue( offset -> {
                offset.x.set( (double)dis.getDisplacementX().accept( getNumberExpressionVisitor(), null ) );
                offset.y.set( (double)dis.getDisplacementY().accept( getNumberExpressionVisitor(), null ) );
                return offset;
            } );
        }
    }


    @Override
    public void visit( LinePlacement lp ) {
        if (lp.getPerpendicularOffset() != null) {
            styleLabel.perpendicularOffset.set( (double)lp.getPerpendicularOffset().accept( getNumberExpressionVisitor(), null ) );
        }
        super.visit( lp );
    }
}
