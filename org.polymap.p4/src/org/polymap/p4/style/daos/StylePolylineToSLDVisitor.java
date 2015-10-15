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

import org.geotools.styling.builder.MarkBuilder;
import org.geotools.styling.builder.NamedLayerBuilder;
import org.geotools.styling.builder.PointSymbolizerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.StrokeBuilder;
import org.geotools.styling.builder.StyleBuilder;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;

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
    public void fillSLD( StyledLayerDescriptorBuilder builder ) {
        NamedLayerBuilder namedLayer = builder.namedLayer();
        StyleBuilder userStyle = namedLayer.style();
        RuleBuilder ruleBuilder = userStyle.featureTypeStyle().rule();
        PointSymbolizerBuilder polylineBuilder = ruleBuilder.point();
        if (stylePolylineDao.getMarkerSize() != null) {
            polylineBuilder.graphic().size( stylePolylineDao.getMarkerSize() );
        }
        MarkBuilder markBuilder = polylineBuilder.graphic().mark();
        if (stylePolylineDao.getMarkerWellKnownName() != null) {
            markBuilder.name( stylePolylineDao.getMarkerWellKnownName() );
        }
        if (stylePolylineDao.getMarkerSize() != null) {
            markBuilder.fill().graphicFill().size( stylePolylineDao.getMarkerSize() );
        }
        if (stylePolylineDao.getMarkerFill() != null) {
            markBuilder.fill().color( toAwtColor( stylePolylineDao.getMarkerFill() ) );
        }
        if (stylePolylineDao.getMarkerIcon() != null) {
            markBuilder.fill().graphicFill().externalGraphic( stylePolylineDao.getMarkerIcon().localURL.get(), "svg" );
        }
        if (stylePolylineDao.getMarkerTransparency() != null) {
            markBuilder.fill().opacity( stylePolylineDao.getMarkerTransparency() );
        }
        StrokeBuilder strokeBuilder = markBuilder.stroke();
        if (stylePolylineDao.getMarkerStrokeSize() != null && stylePolylineDao.getMarkerStrokeSize() > 0) {
            strokeBuilder.width( stylePolylineDao.getMarkerStrokeSize() );
            if (stylePolylineDao.getMarkerStrokeColor() != null) {
                strokeBuilder.color( toAwtColor( stylePolylineDao.getMarkerStrokeColor() ) );
            }
            if (stylePolylineDao.getMarkerStrokeTransparency() != null) {
                strokeBuilder.opacity( stylePolylineDao.getMarkerStrokeTransparency() );
            }
        }
    }
}
