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
package org.polymap.p4.style;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.SLD;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerDAO {

    public static final String          LABEL_TEXT                 = "labelText";

    public static final String          LABEL_FONT_DATA            = "labelFontData";

    public static final String          LABEL_OFFSET               = "labelOffset";

    public static final String          MARKER_SIZE                = "markerSize";

    public static final String          MARKER_FILL                = "markerFill";

    public static final String          MARKER_ICON                = "markerIcon";

    public static final String          MARKER_TRANSPARENCY        = "markerTransparency";

    public static final String          MARKER_STROKE_SIZE         = "markerStrokeSize";

    public static final String          MARKER_STROKE_COLOR        = "markerStrokeColor";

    public static final String          MARKER_STROKE_TRANSPARENCY = "markerStrokeTransparency";

    private org.opengis.feature.Feature selectedFeature;

    private String                      labelText;

    private FontData                    labelFontData;

    private String                      labelFont;

    private Integer                     labelFontSize;

    private RGB                         labelFontColor;

    private Integer                     labelOffset;

    private Integer                     markerSize;

    private RGB                         markerFill;

    private Image                       markerIcon;

    private Integer                     markerTransparency;

    private Integer                     markerStrokeSize;

    private RGB                         markerStrokeColor;

    private Integer                     markerStrokeTransparency;


    public org.opengis.feature.Feature getSelectedFeature() {
        return selectedFeature;
    }


    public void setSelectedFeature( org.opengis.feature.Feature selectedFeature ) {
        this.selectedFeature = selectedFeature;
    }


    public String getLabelText() {
        return labelText;
    }


    public void setLabelText( String labelText ) {
        this.labelText = labelText;
    }


    public FontData getLabelFontData() {
        return labelFontData;
    }


    public void setLabelFontData( FontData labelFontData ) {
        this.labelFontData = labelFontData;
    }


    public String getLabelFont() {
        if (getLabelFontData() != null) {
            labelFont = getLabelFontData().getName();
        }
        return labelFont;
    }


    public Integer getLabelFontSize() {
        if (getLabelFontData() != null) {
            labelFontSize = getLabelFontData().getHeight();
        }
        return labelFontSize;
    }


    public RGB getLabelFontColor() {
        if (getLabelFontData() != null) {
            labelFontSize = getLabelFontData().getStyle();
        }
        return labelFontColor;
    }


    public Integer getLabelOffset() {
        return labelOffset;
    }


    public void setLabelOffset( Integer labelOffset ) {
        this.labelOffset = labelOffset;
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


    public Image getMarkerIcon() {
        return markerIcon;
    }


    public void setMarkerIcon( Image markerIcon ) {
        this.markerIcon = markerIcon;
    }


    public Integer getMarkerTransparency() {
        return markerTransparency;
    }


    public void setMarkerTransparency( Integer markerTransparency ) {
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


    public Integer getMarkerStrokeTransparency() {
        return markerStrokeTransparency;
    }


    public void setMarkerStrokeTransparency( Integer markerStrokeTransparency ) {
        this.markerStrokeTransparency = markerStrokeTransparency;
    }
    
    public StyledLayerDescriptor toSLD() {
        StyledLayerDescriptorBuilder builder = new StyledLayerDescriptorBuilder();
        
        return builder.build();
    }
}
