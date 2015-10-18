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

import org.geotools.styling.builder.FillBuilder;
import org.geotools.styling.builder.MarkBuilder;
import org.geotools.styling.builder.PointSymbolizerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.StrokeBuilder;

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
    public void fillSLD( SLDBuilder builder ) {
        if (stylePointDao.getMarkerWellKnownName() != null || stylePointDao.getMarkerIcon() != null) {
            RuleBuilder ruleBuilder = getRuleBuilder( builder );
            PointSymbolizerBuilder pointBuilder = ruleBuilder.point();
            if (stylePointDao.getMarkerSize() != null || stylePointDao.getMarkerRotation() != null || stylePointDao.getMarkerWellKnownName() != null
                    || stylePointDao.getMarkerTransparency() != null) {
                org.geotools.styling.builder.GraphicBuilder pointGraphicBuilder = pointBuilder.graphic();
                if (stylePointDao.getMarkerSize() != null) {
                    pointGraphicBuilder.size( stylePointDao.getMarkerSize() );
                }
                if (stylePointDao.getMarkerRotation() != null) {
                    pointGraphicBuilder.rotation( stylePointDao.getMarkerRotation() );
                }
                if (stylePointDao.getMarkerWellKnownName() != null) {
                    MarkBuilder markBuilder = pointGraphicBuilder.mark();
                    markBuilder.name( stylePointDao.getMarkerWellKnownName() );
                    if (stylePointDao.getMarkerSize() != null || stylePointDao.getMarkerTransparency() != null) {
                        FillBuilder fillBuilder = markBuilder.fill();
                        if (stylePointDao.getMarkerFill() != null) {
                            fillBuilder.color( toAwtColor( stylePointDao.getMarkerFill() ) );
                        }
                        if (stylePointDao.getMarkerTransparency() != null) {
                            fillBuilder.opacity( stylePointDao.getMarkerTransparency() );
                        }
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
                if (stylePointDao.getMarkerIcon() != null) {
                    pointGraphicBuilder.externalGraphic( stylePointDao.getMarkerIcon().localURL.get(), getFormat(stylePointDao.getMarkerIcon().localURL.get()) );
                }
            }
        }
    }


    private String getFormat( String url ) {
        if(url.endsWith( ".png" )) {
            return "image/png";
        }
        return null;
    }
}
