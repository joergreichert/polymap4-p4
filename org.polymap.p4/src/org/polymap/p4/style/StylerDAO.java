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
import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.MarkBuilder;
import org.geotools.styling.builder.NamedLayerBuilder;
import org.geotools.styling.builder.PointSymbolizerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.StrokeBuilder;
import org.geotools.styling.builder.StyleBuilder;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.polymap.rhei.field.ImageDescription;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerDAO {

    public enum FeatureType {
        POINT, LINE_STRING, POLYGON, RASTER
    }



    public static final String          LAYER_NAME                 = "layerName";

    public static final String          USER_STYLE_NAME            = "userStyleName";

    public static final String          USER_STYLE_TITLE           = "userStyleTitle";

    public static final String          FEATURE_TYPE               = "featureType";

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

    private String                      layerName;                                              // optional

    private String                      userStyleName;

    private String                      userStyleTitle;

    private FeatureType                 featureType                = FeatureType.POINT;

    private String                      labelText;

    private FontData                    labelFontData;

    private String                      labelFont;

    private Integer                     labelFontSize;

    private RGB                         labelFontColor;

    private Integer                     labelOffset;

    private String                      markerWellKnownName;                                    // optional

    private Integer                     markerSize;

    private RGB                         markerFill;

    private ImageDescription            markerIcon;

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


    public String getLayerName() {
        return layerName;
    }


    public void setLayerName( String layerName ) {
        this.layerName = layerName;
    }


    public String getUserStyleName() {
        return userStyleName;
    }


    public void setUserStyleName( String userStyleName ) {
        this.userStyleName = userStyleName;
    }


    public String getUserStyleTitle() {
        return userStyleTitle;
    }


    public void setUserStyleTitle( String userStyleTitle ) {
        this.userStyleTitle = userStyleTitle;
    }


    public FeatureType getFeatureType() {
        return featureType;
    }


    public void setFeatureType( FeatureType featureType ) {
        this.featureType = featureType;
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
        NamedLayerBuilder namedLayer = builder.namedLayer();
        namedLayer.name( getLayerName() );
        StyleBuilder userStyle = namedLayer.style();
        userStyle.name( getUserStyleName() ).title( getUserStyleTitle() );
        RuleBuilder ruleBuilder = userStyle.featureTypeStyle().rule();
        switch (getFeatureType()) {
            case POINT: {
                PointSymbolizerBuilder pointBuilder = ruleBuilder.point();
                pointBuilder.graphic().size( getMarkerSize() );
                MarkBuilder markBuilder = pointBuilder.graphic().mark();
                if (getMarkerWellKnownName() != null) {
                    markBuilder.name( getMarkerWellKnownName() );
                }
                if (getMarkerFill() != null) {
                    markBuilder.fill().color( toAwtColor( getMarkerFill() ) );
                }
                if (getMarkerTransparency() != null) {
                    markBuilder.fill().opacity( getMarkerTransparency() );
                }
                StrokeBuilder strokeBuilder = markBuilder.stroke();
                if (getMarkerStrokeSize() != null && getMarkerStrokeSize() > 0) {
                    strokeBuilder.width( getMarkerStrokeSize() );
                    if (getMarkerStrokeColor() != null) {
                        strokeBuilder.color( toAwtColor( getMarkerStrokeColor() ) );
                    }
                    if (getMarkerStrokeTransparency() != null) {
                        strokeBuilder.opacity( getMarkerStrokeTransparency() );
                    }
                }
                break;
            }
            case LINE_STRING: {
                ruleBuilder.line();
                break;
            }
            case POLYGON: {
                ruleBuilder.polygon();
                break;
            }
            case RASTER: {
                ruleBuilder.raster();
                break;
            }
        }
        return builder.build();
    }


    private java.awt.Color toAwtColor( RGB rgb ) {
        return new java.awt.Color( rgb.red, rgb.green, rgb.blue );
    }
}
