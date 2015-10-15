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
public class StylePointFromSLDVisitor
        extends AbstractStyleFromSLDVisitor {

    private final StylePointDao stylePointDao;


    public StylePointFromSLDVisitor( StylePointDao stylePointDao ) {
        this.stylePointDao = stylePointDao;
    }

    @Override
    public void visit( Mark mark ) {
        if(mark.getWellKnownName() != null) {
            stylePointDao.setMarkerWellKnownName( (String)mark.getWellKnownName().accept( getExpressionVisitor(), null ) );
        }
        if(mark.getFill() != null) {
            if(mark.getFill().getColor() != null) {
                stylePointDao.setMarkerFill( (RGB)mark.getFill().getColor().accept( getExpressionVisitor(), null ) );
            }
            if(mark.getFill().getOpacity() != null) {
                stylePointDao.setMarkerTransparency( (int)mark.getFill().getOpacity().accept( getExpressionVisitor(), null ) );
            }
            if(mark.getFill().getGraphicFill() != null && mark.getFill().getGraphicFill().getSize() != null) {
                stylePointDao.setMarkerSize( (int)mark.getFill().getGraphicFill().getSize().accept( getExpressionVisitor(), null ) );
            }
        }
        if(mark.getStroke() != null) {
            mark.getStroke().accept( this );
        }
    }


    @Override
    public void visit( ExternalGraphic exgr ) {
        stylePointDao.setMarkerIcon( new ImageDescription().localURL.put( exgr.getURI() ) );
    }


    @Override
    public void visit( Stroke stroke ) {
        if(stroke.getColor() != null) {
            stylePointDao.setMarkerStrokeColor( (RGB)stroke.getColor().accept( getExpressionVisitor(), null ) );
        }
        if(stroke.getWidth() != null) {
            stylePointDao.setMarkerStrokeSize( (int)stroke.getWidth().accept( getExpressionVisitor(), null ) );
        }
    }
}
