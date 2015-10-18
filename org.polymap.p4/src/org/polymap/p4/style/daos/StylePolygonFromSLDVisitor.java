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
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Stroke;
import org.polymap.rhei.field.ImageDescription;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePolygonFromSLDVisitor
        extends AbstractStyleFromSLDVisitor {

    private final StylePolygonDao stylePolygonDao;


    public StylePolygonFromSLDVisitor( StylePolygonDao stylePolygonDao ) {
        this.stylePolygonDao = stylePolygonDao;
    }


    @Override
    public void visit( org.geotools.styling.PolygonSymbolizer poly ) {
        if (poly.getFill() != null) {
            poly.getFill().accept( this );
        }
        if (poly.getStroke() != null) {
            poly.getStroke().accept( this );
        }
    }


    @Override
    public void visit( Mark mark ) {
        if (mark.getWellKnownName() != null) {
            stylePolygonDao.setMarkerWellKnownName( (String)mark.getWellKnownName().accept(
                    getStringExpressionVisitor(), null ) );
        }
        if (mark.getFill() != null) {
            if (mark.getFill().getColor() != null) {
                stylePolygonDao.setMarkerFill( (RGB)mark.getFill().getColor()
                        .accept( getColorExpressionVisitor(), null ) );
            }
            if (mark.getFill().getOpacity() != null) {
                stylePolygonDao.setMarkerTransparency( (double)mark.getFill().getOpacity()
                        .accept( getNumberExpressionVisitor(), null ) );
            }
            if (mark.getFill().getGraphicFill() != null && mark.getFill().getGraphicFill().getSize() != null) {
                stylePolygonDao.setMarkerSize( (int)mark.getFill().getGraphicFill().getSize()
                        .accept( getNumberExpressionVisitor(), null ) );
            }
        }
        if (mark.getStroke() != null) {
            mark.getStroke().accept( this );
        }
    }


    @Override
    public void visit( ExternalGraphic exgr ) {
        stylePolygonDao.setMarkerIcon( new ImageDescription().localURL.put( exgr.getURI() ) );
    }


    @Override
    public void visit( Stroke stroke ) {
        if (stroke.getColor() != null) {
            stylePolygonDao.setMarkerStrokeColor( (RGB)stroke.getColor().accept( getColorExpressionVisitor(), null ) );
        }
        if (stroke.getWidth() != null) {
            stylePolygonDao
                    .setMarkerStrokeSize( ((Double)stroke.getWidth().accept( getNumberExpressionVisitor(), null ))
                            .intValue() );
        }
    }
}
