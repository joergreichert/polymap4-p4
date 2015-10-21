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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geotools.styling.builder.LineSymbolizerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.StrokeBuilder;
import org.polymap.p4.style.SLDBuilder;
import org.polymap.p4.style.entities.StyleLine;
import org.polymap.p4.style.sld.to.helper.StyleColorToSLDHelper;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLineToSLDVisitor
        extends AbstractStyleToSLDVisitor {

    private final StyleLine styleLine;


    public StyleLineToSLDVisitor( StyleLine styleLine ) {
        this.styleLine = styleLine;
    }


    @Override
    public void fillSLD( SLDBuilder builder ) {
        internalFillSLD( styleLine, false, builder );
    }


    private void internalFillSLD( StyleLine styleLine, boolean newRule, SLDBuilder builder ) {
        if (styleLine.lineWidth.get() != null && styleLine.lineWidth.get() > 0) {
            RuleBuilder ruleBuilder = newRule ? builder.style( builder.namedLayer() ).featureTypeStyle().rule()
                    : singletonRule( builder );
            LineSymbolizerBuilder polylineBuilder = ruleBuilder.line();
            StrokeBuilder strokeBuilder = polylineBuilder.stroke();
            strokeBuilder.width( styleLine.lineWidth.get() );
            if (styleLine.lineColor.get() != null) {
                strokeBuilder.color( new StyleColorToSLDHelper().getSLDColor( styleLine.lineColor.get() ) );
            }
            if (styleLine.lineCap.get() != null) {
                strokeBuilder.lineCapName( styleLine.lineCap.get().getLabel() );
            }
            if (styleLine.lineDashPattern.get() != null) {
                List<Float> floats = Arrays.asList( styleLine.lineDashPattern.get().split( " " ) ).stream()
                        .map( item -> new Float( item ) ).collect( Collectors.<Float>toList() );
                float[] dashArray = new float[floats.size()];
                for (int i = 0; i < floats.size(); i++) {
                    dashArray[i] = floats.get( i ).floatValue();
                }
                strokeBuilder.dashArray( dashArray );
            }
            if (styleLine.border.get() != null) {
                internalFillSLD( styleLine.border.get(), true, builder );
            }
            if(styleLine.lineDashPattern.get() != null) {
                List<String> parts = Arrays.asList(styleLine.lineDashPattern.get().split( " " )).stream().filter( part -> part.trim().length() > 0 ).collect( Collectors.toList() );
                float [] dashArray = new float [parts.size()];
                for(int i=0; i<parts.size(); i++) {
                    try {
                        dashArray[i] = Float.parseFloat( parts.get(i) );
                    } catch(NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
                if(styleLine.lineDashOffset.get() != null) {
                    strokeBuilder.dashOffset( styleLine.lineDashOffset.get() );
                }
            }
        }
    }
}
