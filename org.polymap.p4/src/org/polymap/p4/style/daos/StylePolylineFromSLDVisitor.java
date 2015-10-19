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
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePolylineFromSLDVisitor
        extends AbstractStyleFromSLDVisitor {

    private final StylePolylineDao stylePolylineDao;

    private boolean                borderMode = false;


    public StylePolylineFromSLDVisitor( StylePolylineDao stylePolylineDao ) {
        this.stylePolylineDao = stylePolylineDao;
    }


    @Override
    public void visit( Rule rule ) {
        List<LineSymbolizer> lines = Arrays
                .asList( rule.getSymbolizers() )
                .stream()
                .filter( symb -> symb instanceof LineSymbolizer )
                .map( symb -> (LineSymbolizer)symb )
                .sorted(
                        ( LineSymbolizer line1, LineSymbolizer line2 ) -> (Double.valueOf( (double)line1.getStroke()
                                .getWidth().accept( getNumberExpressionVisitor(), null ) )).compareTo( (double)line2
                                .getStroke().getWidth().accept( getNumberExpressionVisitor(), null ) ) )
                .collect( Collectors.<LineSymbolizer>toList() );

        for (LineSymbolizer line : lines) {
            line.accept( this );
            borderMode = true;
        }
    }


    @Override
    public void visit( org.geotools.styling.LineSymbolizer line ) {
        if (line.getStroke() != null) {
            line.getStroke().accept( this );
        }
    }


    @Override
    public void visit( Stroke stroke ) {
        if (stroke.getColor() != null) {
            if (borderMode) {
                stylePolylineDao
                        .setLineStrokeColor( (RGB)stroke.getColor().accept( getColorExpressionVisitor(), null ) );
            }
            else {
                stylePolylineDao.setLineColor( (RGB)stroke.getColor().accept( getColorExpressionVisitor(), null ) );
            }
        }
        if (stroke.getWidth() != null) {
            if (borderMode) {
                stylePolylineDao.setLineStrokeWidth( ((Double)stroke.getWidth().accept( getNumberExpressionVisitor(),
                        null )).intValue() );
            }
            else {
                stylePolylineDao.setLineWidth( ((Double)stroke.getWidth().accept( getNumberExpressionVisitor(), null ))
                        .intValue() );
            }
        }
    }
}
