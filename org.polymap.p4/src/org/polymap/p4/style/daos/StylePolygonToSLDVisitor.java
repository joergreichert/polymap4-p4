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
public class StylePolygonToSLDVisitor
        extends AbstractStyleToSLDVisitor {

    private final StylePolygonDao stylePolygonDao;


    public StylePolygonToSLDVisitor( StylePolygonDao stylePolygonDao ) {
        this.stylePolygonDao = stylePolygonDao;
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
        PointSymbolizerBuilder polygonBuilder = ruleBuilder.point();
        if (stylePolygonDao.getMarkerSize() != null) {
            polygonBuilder.graphic().size( stylePolygonDao.getMarkerSize() );
        }
        MarkBuilder markBuilder = polygonBuilder.graphic().mark();
        if (stylePolygonDao.getMarkerWellKnownName() != null) {
            markBuilder.name( stylePolygonDao.getMarkerWellKnownName() );
        }
        if (stylePolygonDao.getMarkerSize() != null) {
            markBuilder.fill().graphicFill().size( stylePolygonDao.getMarkerSize() );
        }
        if (stylePolygonDao.getMarkerFill() != null) {
            markBuilder.fill().color( toAwtColor( stylePolygonDao.getMarkerFill() ) );
        }
        if (stylePolygonDao.getMarkerIcon() != null) {
            markBuilder.fill().graphicFill().externalGraphic( stylePolygonDao.getMarkerIcon().localURL.get(), "svg" );
        }
        if (stylePolygonDao.getMarkerTransparency() != null) {
            markBuilder.fill().opacity( stylePolygonDao.getMarkerTransparency() );
        }
        StrokeBuilder strokeBuilder = markBuilder.stroke();
        if (stylePolygonDao.getMarkerStrokeSize() != null && stylePolygonDao.getMarkerStrokeSize() > 0) {
            strokeBuilder.width( stylePolygonDao.getMarkerStrokeSize() );
            if (stylePolygonDao.getMarkerStrokeColor() != null) {
                strokeBuilder.color( toAwtColor( stylePolygonDao.getMarkerStrokeColor() ) );
            }
            if (stylePolygonDao.getMarkerStrokeTransparency() != null) {
                strokeBuilder.opacity( stylePolygonDao.getMarkerStrokeTransparency() );
            }
        }
    }
}
