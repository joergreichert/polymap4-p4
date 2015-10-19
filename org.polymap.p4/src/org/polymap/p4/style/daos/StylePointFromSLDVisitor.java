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

import java.util.Arrays;

import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.opengis.metadata.citation.OnLineResource;
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
    public void visit( Rule rule ) {
        Arrays.asList( rule.getSymbolizers() ).stream().filter( symb -> symb instanceof PointSymbolizer )
                .forEach( symb -> symb.accept( this ) );
    }


    @Override
    public void visit( PointSymbolizer ps ) {
        if (ps.getGraphic() != null) {
            ps.getGraphic().accept( this );
            if (ps.getGraphic().getSize() != null) {
                stylePointDao.setMarkerSize( ((Double)ps.getGraphic().getSize()
                        .accept( getNumberExpressionVisitor(), null )) );
            }
            if (ps.getGraphic().getRotation() != null) {
                stylePointDao.setMarkerRotation( ((Double)ps.getGraphic().getRotation()
                        .accept( getNumberExpressionVisitor(), null )) );
            }
        }
    }


    @Override
    public void visit( Mark mark ) {
        if (mark.getWellKnownName() != null) {
            stylePointDao.setMarkerWellKnownName( (String)mark.getWellKnownName().accept( getStringExpressionVisitor(),
                    null ) );
        }
        if (mark.getFill() != null) {
            if (mark.getFill().getColor() != null) {
                stylePointDao
                        .setMarkerFill( (RGB)mark.getFill().getColor().accept( getColorExpressionVisitor(), null ) );
            }
            if (mark.getFill().getOpacity() != null) {
                stylePointDao.setMarkerTransparency( (double)mark.getFill().getOpacity()
                        .accept( getNumberExpressionVisitor(), null ) );
            }
        }
        if (mark.getStroke() != null) {
            mark.getStroke().accept( this );
        }
    }


    @Override
    public void visit( ExternalGraphic exgr ) {
        if (exgr.getURI() != null) {
            stylePointDao.setMarkerIcon( new ImageDescription().localURL.put( exgr.getURI() ) );
        }
        if (exgr.getOnlineResource() != null) {
            OnLineResource onlineResource = exgr.getOnlineResource();
            if (onlineResource.getLinkage() != null) {
                stylePointDao.setMarkerIcon( new ImageDescription().localURL.put( onlineResource.getLinkage()
                        .toString() ) );
            }
        }
    }


    @Override
    public void visit( Stroke stroke ) {
        if (stroke.getColor() != null) {
            stylePointDao.setMarkerStrokeColor( (RGB)stroke.getColor().accept( getColorExpressionVisitor(), null ) );
        }
        if (stroke.getWidth() != null) {
            stylePointDao
                    .setMarkerStrokeSize( ((Double)stroke.getWidth().accept( getNumberExpressionVisitor(), null )) );
        }
    }
}
