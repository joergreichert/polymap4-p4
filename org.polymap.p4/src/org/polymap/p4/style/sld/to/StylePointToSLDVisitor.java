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
package org.polymap.p4.style.sld.to;

import org.geotools.styling.builder.GraphicBuilder;
import org.geotools.styling.builder.PointSymbolizerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.polymap.p4.style.SLDBuilder;
import org.polymap.p4.style.entities.StyleFigure;
import org.polymap.p4.style.entities.StyleImage;
import org.polymap.p4.style.entities.StylePoint;
import org.polymap.p4.style.sld.to.helper.StyleFigureToSLDHelper;
import org.polymap.p4.style.sld.to.helper.StyleImageToSLDHelper;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePointToSLDVisitor
        extends AbstractStyleToSLDVisitor {

    private final StylePoint stylePoint;


    public StylePointToSLDVisitor( StylePoint stylePoint ) {
        this.stylePoint = stylePoint;
    }


    @Override
    public void fillSLD( SLDBuilder builder ) {
        if (stylePoint.markerGraphic.get() != null) {
            if (stylePoint.markerSize.get() != null || stylePoint.markerRotation.get() != null) {
                RuleBuilder ruleBuilder = singletonRule( builder );
                PointSymbolizerBuilder pointBuilder = builder.point( ruleBuilder );
                GraphicBuilder pointGraphicBuilder = builder.pointGraphicBuilder( pointBuilder );
                if (stylePoint.markerSize.get() != null) {
                    pointGraphicBuilder.size( stylePoint.markerSize.get() );
                }
                if (stylePoint.markerRotation.get() != null) {
                    pointGraphicBuilder.rotation( stylePoint.markerRotation.get() );
                }
                if (stylePoint.markerGraphic.get() instanceof StyleFigure) {
                    new StyleFigureToSLDHelper().fillSLD( (StyleFigure)stylePoint.markerGraphic.get(), pointGraphicBuilder.mark() );
                }
                else if (stylePoint.markerGraphic.get() instanceof StyleImage) {
                    new StyleImageToSLDHelper().fillSLD( (StyleImage)stylePoint.markerGraphic.get(), pointGraphicBuilder );
                }
            }
        }
    }
}
