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
package org.polymap.p4.style.entities;

import org.geotools.styling.StyledLayerDescriptor;
import org.polymap.model2.Nullable;
import org.polymap.model2.Property;
import org.polymap.p4.style.SLDBuilder;
import org.polymap.p4.style.sld.from.StyleLineFromSLDVisitor;
import org.polymap.p4.style.sld.to.StylePolylineToSLDVisitor;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLine
        extends AbstractStyleSymbolizer {

    @Nullable
    public Property<Integer>     lineWidth;

    @Nullable
    public Property<StyleColor>  lineColor;

    @Nullable
    public Property<LineCapType> lineCap;

    @Nullable
    public Property<String>      lineDashPattern;

    @Nullable
    public Property<Double>      lineTransparency;

    @Nullable
    public Property<StyleLine>   border;


    @Override
    public void fromSLD( StyledLayerDescriptor style ) {
        style.accept( new StyleLineFromSLDVisitor( this ) );
    }


    @Override
    public void fillSLD( SLDBuilder builder ) {
        new StylePolylineToSLDVisitor( this ).fillSLD( builder );
    }
}
