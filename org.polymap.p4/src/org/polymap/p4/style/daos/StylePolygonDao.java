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

import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.StyledLayerDescriptor;
import org.polymap.rhei.field.ImageDescription;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePolygonDao
        extends AbstractStyleSymbolizerDao {

    public static final String MARKER_SIZE                = "markerSize";

    public static final String MARKER_FILL                = "markerFill";

    public static final String MARKER_ICON                = "markerIcon";

    public static final String MARKER_TRANSPARENCY        = "markerTransparency";

    public static final String MARKER_STROKE_SIZE         = "markerStrokeSize";

    public static final String MARKER_STROKE_COLOR        = "markerStrokeColor";

    public static final String MARKER_STROKE_TRANSPARENCY = "markerStrokeTransparency";

    private String             markerWellKnownName;                                    // optional

    private Integer            markerSize;

    private RGB                markerFill;

    private ImageDescription   markerIcon;

    private Double             markerTransparency;

    private Integer            markerStrokeSize;

    private RGB                markerStrokeColor;

    private Double             markerStrokeTransparency;


    public StylePolygonDao() {
    }


    /**
     * @param style
     */
    public StylePolygonDao( StyledLayerDescriptor style ) {
        fromSLD( style );
    }


    public String getMarkerWellKnownName() {
        return markerWellKnownName;
    }


    public void setMarkerWellKnownName( String markerWellKnownName ) {
        this.markerWellKnownName = markerWellKnownName;
    }


    public Integer getMarkerSize() {
        return markerSize;
    }


    public void setMarkerSize( Integer markerSize ) {
        this.markerSize = markerSize;
    }


    public RGB getMarkerFill() {
        return markerFill;
    }


    public void setMarkerFill( RGB markerFill ) {
        this.markerFill = markerFill;
    }


    public ImageDescription getMarkerIcon() {
        return markerIcon;
    }


    public void setMarkerIcon( ImageDescription markerIcon ) {
        this.markerIcon = markerIcon;
    }


    public Double getMarkerTransparency() {
        return markerTransparency;
    }


    public void setMarkerTransparency( Double markerTransparency ) {
        this.markerTransparency = markerTransparency;
    }


    public Integer getMarkerStrokeSize() {
        return markerStrokeSize;
    }


    public void setMarkerStrokeSize( Integer markerStrokeSize ) {
        this.markerStrokeSize = markerStrokeSize;
    }


    public RGB getMarkerStrokeColor() {
        return markerStrokeColor;
    }


    public void setMarkerStrokeColor( RGB markerStrokeColor ) {
        this.markerStrokeColor = markerStrokeColor;
    }


    public Double getMarkerStrokeTransparency() {
        return markerStrokeTransparency;
    }


    public void setMarkerStrokeTransparency( Double markerStrokeTransparency ) {
        this.markerStrokeTransparency = markerStrokeTransparency;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.style.daos.IStyleDao#fromSLD(org.geotools.styling.
     * StyledLayerDescriptor)
     */
    @Override
    public void fromSLD( StyledLayerDescriptor style ) {
        style.accept( new StylePolygonFromSLDVisitor( this ) );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.style.daos.IStyleDao#fillSLD(org.geotools.styling.builder.
     * StyledLayerDescriptorBuilder)
     */
    @Override
    public void fillSLD( SLDBuilder builder ) {
        new StylePolygonToSLDVisitor( this ).fillSLD( builder );
    }
}
