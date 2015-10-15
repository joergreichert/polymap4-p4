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
public class StylePointToSLDVisitor
        extends AbstractStyleToSLDVisitor {

    private final StylePointDao stylePointDao;


    public StylePointToSLDVisitor( StylePointDao stylePointDao ) {
        this.stylePointDao = stylePointDao;
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
        PointSymbolizerBuilder pointBuilder = ruleBuilder.point();
        if (stylePointDao.getMarkerSize() != null) {
            pointBuilder.graphic().size( stylePointDao.getMarkerSize() );
        }
        MarkBuilder markBuilder = pointBuilder.graphic().mark();
        if (stylePointDao.getMarkerWellKnownName() != null) {
            markBuilder.name( stylePointDao.getMarkerWellKnownName() );
        }
        if (stylePointDao.getMarkerSize() != null) {
            markBuilder.fill().graphicFill().size( stylePointDao.getMarkerSize() );
        }
        if (stylePointDao.getMarkerFill() != null) {
            markBuilder.fill().color( toAwtColor( stylePointDao.getMarkerFill() ) );
        }
        if (stylePointDao.getMarkerIcon() != null) {
            markBuilder.fill().graphicFill().externalGraphic( stylePointDao.getMarkerIcon().localURL.get(), "svg" );
        }
        if (stylePointDao.getMarkerTransparency() != null) {
            markBuilder.fill().opacity( stylePointDao.getMarkerTransparency() );
        }
        StrokeBuilder strokeBuilder = markBuilder.stroke();
        if (stylePointDao.getMarkerStrokeSize() != null && stylePointDao.getMarkerStrokeSize() > 0) {
            strokeBuilder.width( stylePointDao.getMarkerStrokeSize() );
            if (stylePointDao.getMarkerStrokeColor() != null) {
                strokeBuilder.color( toAwtColor( stylePointDao.getMarkerStrokeColor() ) );
            }
            if (stylePointDao.getMarkerStrokeTransparency() != null) {
                strokeBuilder.opacity( stylePointDao.getMarkerStrokeTransparency() );
            }
        }
    }
}
