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

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.geotools.filter.expression.AbstractExpressionVisitor;
import org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Mark;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleVisitor;
import org.geotools.styling.StyledLayer;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.UserLayer;
import org.geotools.styling.builder.MarkBuilder;
import org.geotools.styling.builder.NamedLayerBuilder;
import org.geotools.styling.builder.PointSymbolizerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.StrokeBuilder;
import org.geotools.styling.builder.StyleBuilder;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;
import org.opengis.style.GraphicalSymbol;
import org.polymap.rhei.field.ImageDescription;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerDAO {

    public enum FeatureType {
        POINT("Point"), LINE_STRING("Line string"), POLYGON("Polygon"), RASTER("Raster");

        private String label;


        FeatureType( String label ) {
            this.label = label;

        }


        public static List<FeatureType> getOrdered() {
            return Arrays.asList( POINT, LINE_STRING, POLYGON, RASTER );
        }


        public String getLabel() {
            return label;
        }


        public FeatureType getTypeForLabel( String label ) {
            return Arrays.asList( values() ).stream().filter( value -> value.getLabel().equals( label ) ).findFirst()
                    .get();
        }
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


    /**
     * @param style
     */
    public StylerDAO( StyledLayerDescriptor style ) {
        ExpressionVisitor expressionVisitor = new AbstractExpressionVisitor() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.filter.expression.AbstractExpressionVisitor#visit(org
             * .opengis.filter.expression.Literal, java.lang.Object)
             */
            @Override
            public Object visit( Literal expr, Object extraData ) {
                if (expr.getValue() != null) {
                    if (expr.getValue().toString().startsWith( "#" )) {
                        String hexValue = expr.getValue().toString();
                        Color color = Color.decode( hexValue );
                        return new RGB( color.getRed(), color.getGreen(), color.getBlue() );
                    }
                    else {
                        try {
                            return new Double( Double.parseDouble( expr.getValue().toString() ) ).intValue();
                        }
                        catch (NumberFormatException nfe) {
                            //
                        }
                    }
                }
                return expr.getValue();
            }
        };
        StyleVisitor styleVisitor = new StyleVisitorAdapter() {

            public void visit( StyledLayerDescriptor sld ) {
                for (StyledLayer layer : sld.getStyledLayers()) {
                    if (layer instanceof NamedLayer) {
                        ((NamedLayer)layer).accept( this );
                    }
                    else if (layer instanceof UserLayer) {
                        ((UserLayer)layer).accept( this );
                    }
                }
            }


            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter#visit
             * (org.geotools.styling.NamedLayer)
             */
            @Override
            public void visit( NamedLayer layer ) {
                setLayerName( layer.getName() );
                for (Style style : layer.getStyles()) {
                    style.accept( this );
                }
            }


            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter#visit
             * (org.geotools.styling.Style)
             */
            @Override
            public void visit( Style style ) {
                setUserStyleName( style.getName() );
                if (style.getDescription() != null && style.getDescription().getTitle() != null) {
                    setUserStyleTitle( style.getDescription().getTitle().toString() );
                }
                for (FeatureTypeStyle featureTypeStyle : style.featureTypeStyles()) {
                    featureTypeStyle.accept( this );
                }
            }


            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter#visit
             * (org.geotools.styling.FeatureTypeStyle)
             */
            @Override
            public void visit( FeatureTypeStyle fts ) {
                for (Rule rule : fts.rules()) {
                    rule.accept( this );
                }
            }


            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter#visit
             * (org.geotools.styling.Rule)
             */
            @Override
            public void visit( Rule rule ) {
                for (Symbolizer symbolizer : rule.getSymbolizers()) {
                    symbolizer.accept( this );
                }
            }


            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter#visit
             * (org.geotools.styling.PointSymbolizer)
             */
            @Override
            public void visit( PointSymbolizer ps ) {
                for (GraphicalSymbol graphicalSymbol : ps.getGraphic().graphicalSymbols()) {
                    if (graphicalSymbol instanceof Mark) {
                        ((Mark)graphicalSymbol).accept( this );
                    }
                }
            }


            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter#visit
             * (org.geotools.styling.Mark)
             */
            @Override
            public void visit( Mark mark ) {
                setMarkerWellKnownName( (String)mark.getWellKnownName().accept( expressionVisitor, null ) );
                setMarkerFill( (RGB)mark.getFill().getColor().accept( expressionVisitor, null ) );
                setMarkerTransparency( (int)mark.getFill().getOpacity().accept( expressionVisitor, null ) );
                setMarkerSize( (int)mark.getFill().getGraphicFill().getSize().accept( expressionVisitor, null ) );
                mark.getStroke().accept( this );
            }


            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter#visit
             * (org.geotools.styling.ExternalGraphic)
             */
            @Override
            public void visit( ExternalGraphic exgr ) {
                setMarkerIcon( new ImageDescription().localURL.put( exgr.getURI() ) );
            }


            /*
             * (non-Javadoc)
             * 
             * @see
             * org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter#visit
             * (org.geotools.styling.Stroke)
             */
            @Override
            public void visit( Stroke stroke ) {
                setMarkerStrokeColor( (RGB)stroke.getColor().accept( expressionVisitor, null ) );
                setMarkerStrokeSize( (int)stroke.getWidth().accept( expressionVisitor, null ) );
            }
        };
        style.accept( styleVisitor );
    }


    public StylerDAO() {
    }


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
                if (getMarkerSize() != null) {
                    markBuilder.fill().graphicFill().size( getMarkerSize() );
                }
                if (getMarkerFill() != null) {
                    markBuilder.fill().color( toAwtColor( getMarkerFill() ) );
                }
                if (getMarkerIcon() != null) {
                    markBuilder.fill().graphicFill().externalGraphic( getMarkerIcon().localURL.get(), "svg" );
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
