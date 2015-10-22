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

import org.polymap.model2.Nullable;
import org.polymap.model2.Property;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabel
        extends AbstractSLDModelFragment {

    @Nullable
    public Property<String>     labelText;

    @Nullable
    public Property<StyleFont>  labelFont;

    @Nullable
    public Property<StyleColor> labelFontColor;

    @Nullable
    public Property<StyleCoord> labelOffset;

    @Nullable
    public Property<StyleCoord> labelAnchor;

    @Nullable
    public Property<Double>     perpendicularOffset;

    @Nullable
    public Property<Double>     labelRotation;

    // GeoServer vendor options

    @Nullable
    public Property<Double>     maxDisplacement;

    @Nullable
    public Property<Boolean>    followLine;

    @Nullable
    public Property<Double>     maxAngleDelta;

    @Nullable
    public Property<Double>     repeat;
}
