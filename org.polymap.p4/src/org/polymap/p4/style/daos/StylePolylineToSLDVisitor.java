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

import org.geotools.styling.builder.LineSymbolizerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.StrokeBuilder;
import org.geotools.styling.builder.StyleBuilder;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePolylineToSLDVisitor
        extends AbstractStyleToSLDVisitor {

    private final StylePolylineDao stylePolylineDao;


    public StylePolylineToSLDVisitor( StylePolylineDao stylePolylineDao ) {
        this.stylePolylineDao = stylePolylineDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.p4.style.daos.AbstractStyleToSLDVisitor#fillSLD(org.geotools.styling
     * .builder.StyledLayerDescriptorBuilder)
     */
    @Override
    public void fillSLD( SLDBuilder builder ) {
        if(stylePolylineDao.getLineWidth() != null && stylePolylineDao.getLineWidth() > 0) {
            RuleBuilder ruleBuilder = getRuleBuilder( builder );
            LineSymbolizerBuilder polylineBuilder = ruleBuilder.line();
            StrokeBuilder strokeBuilder = polylineBuilder.stroke();
            strokeBuilder.width( stylePolylineDao.getLineWidth() );
            if(stylePolylineDao.getLineColor() != null) {
                strokeBuilder.color( toAwtColor(stylePolylineDao.getLineColor()) );
            }            
            if(stylePolylineDao.getLineStrokeWidth() != null && stylePolylineDao.getLineStrokeWidth() > 0) {
                StyleBuilder styleBuilder = getStyleBuilder( builder );
                RuleBuilder ruleBorderBuilder = styleBuilder.featureTypeStyle().rule();
                LineSymbolizerBuilder polylineBorderBuilder = ruleBorderBuilder.line();
                StrokeBuilder strokeBorderBuilder = polylineBorderBuilder.stroke();
                strokeBorderBuilder.width( stylePolylineDao.getLineStrokeWidth() );
                if(stylePolylineDao.getLineStrokeColor() != null) {
                    strokeBorderBuilder.color( toAwtColor(stylePolylineDao.getLineStrokeColor()) );
                }            
            }
        }
    }
}
