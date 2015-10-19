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
public class StylePolylineDao
        extends AbstractStyleSymbolizerDao {

    public static final String LINE_WIDTH               = "lineWidth";

    public static final String LINE_COLOR               = "lineColor";

    public static final String LINE_ICON                = "lineIcon";

    public static final String LINE_TRANSPARENCY        = "lineTransparency";

    public static final String LINE_STROKE_WIDTH        = "lineStrokeWidth";

    public static final String LINE_STROKE_COLOR        = "lineStrokeColor";

    public static final String LINE_STROKE_TRANSPARENCY = "lineStrokeTransparency";

    private String             lineWellKnownName;                                  // optional

    private Integer            lineWidth;

    private RGB                lineColor;

    private ImageDescription   lineIcon;

    private Double             lineTransparency;

    private Integer            lineStrokeWidth;

    private RGB                lineStrokeColor;

    private Double             lineStrokeTransparency;


    public StylePolylineDao() {
    }


    /**
     * @param style
     */
    public StylePolylineDao( StyledLayerDescriptor style ) {
        fromSLD( style );
    }


    public String getLineWellKnownName() {
        return lineWellKnownName;
    }


    public void setLineWellKnownName( String lineWellKnownName ) {
        this.lineWellKnownName = lineWellKnownName;
    }


    public Integer getLineWidth() {
        return lineWidth;
    }


    public void setLineWidth( Integer lineWidth ) {
        this.lineWidth = lineWidth;
    }


    public RGB getLineColor() {
        return lineColor;
    }


    public void setLineColor( RGB lineColor ) {
        this.lineColor = lineColor;
    }


    public ImageDescription getLineIcon() {
        return lineIcon;
    }


    public void setLineIcon( ImageDescription lineIcon ) {
        this.lineIcon = lineIcon;
    }


    public Double getLineTransparency() {
        return lineTransparency;
    }


    public void setLineTransparency( Double lineTransparency ) {
        this.lineTransparency = lineTransparency;
    }


    public Integer getLineStrokeWidth() {
        return lineStrokeWidth;
    }


    public void setLineStrokeWidth( Integer lineStrokeWidth ) {
        this.lineStrokeWidth = lineStrokeWidth;
    }


    public RGB getLineStrokeColor() {
        return lineStrokeColor;
    }


    public void setLineStrokeColor( RGB lineStrokeColor ) {
        this.lineStrokeColor = lineStrokeColor;
    }


    public Double getLineStrokeTransparency() {
        return lineStrokeTransparency;
    }


    public void setLineStrokeTransparency( Double lineStrokeTransparency ) {
        this.lineStrokeTransparency = lineStrokeTransparency;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.style.daos.IStyleDao#fromSLD(org.geotools.styling.
     * StyledLayerDescriptor)
     */
    @Override
    public void fromSLD( StyledLayerDescriptor style ) {
        style.accept( new StylePolylineFromSLDVisitor( this ) );
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.p4.style.daos.IStyleDao#colorSLD(org.geotools.styling.builder.
     * StyledLayerDescriptorBuilder)
     */
    @Override
    public void fillSLD( SLDBuilder builder ) {
        new StylePolylineToSLDVisitor( this ).fillSLD( builder );
    }
}
