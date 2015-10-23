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

import org.geotools.filter.AttributeExpressionImpl;
import org.geotools.styling.builder.AnchorPointBuilder;
import org.geotools.styling.builder.DisplacementBuilder;
import org.geotools.styling.builder.FillBuilder;
import org.geotools.styling.builder.HaloBuilder;
import org.geotools.styling.builder.LinePlacementBuilder;
import org.geotools.styling.builder.PointPlacementBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.TextSymbolizerBuilder;
import org.polymap.model2.Property;
import org.polymap.p4.style.SLDBuilder;
import org.polymap.p4.style.entities.FeatureType;
import org.polymap.p4.style.entities.StyleCoord;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.style.sld.to.helper.StyleColorToSLDHelper;
import org.polymap.p4.style.sld.to.helper.StyleFontToSLDHelper;

import com.google.common.collect.Lists;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelToSLDVisitor
        extends AbstractStyleToSLDVisitor {

    private static Double     ANCHOR_X_DEFAULT             = 0.0d;

    private static Double     ANCHOR_Y_DEFAULT             = 0.5d;

    private static Double     OFFSET_X_DEFAULT             = 0.0d;

    private static Double     OFFSET_Y_DEFAULT             = 0.0d;

    private static Double     ROTATION_DEFAULT             = 0.0d;

    private static Double     PERPENDICULAR_OFFSET_DEFAULT = 0.0d;

    private final StyleLabel  styleLabel;

    private final FeatureType host;


    public StyleLabelToSLDVisitor( StyleLabel styleLabel, FeatureType host ) {
        this.styleLabel = styleLabel;
        this.host = host;
    }


    @Override
    public void fillSLD( SLDBuilder builder ) {
        if (styleLabel.labelText.get() != null) {
            RuleBuilder ruleBuilder = singletonRule( builder );
            internalFillSLD( builder, ruleBuilder );
        }
    }


    public void fillSLD( SLDBuilder builder, RuleBuilder ruleBuilder ) {
        if (styleLabel.labelText.get() != null) {
            internalFillSLD( builder, ruleBuilder );
        }
    }


    private void internalFillSLD( SLDBuilder builder, RuleBuilder ruleBuilder ) {
        TextSymbolizerBuilder textBuilder = builder.text( ruleBuilder );
        textBuilder
                .label( new AttributeExpressionImpl( new org.geotools.feature.NameImpl( styleLabel.labelText.get() ) ) );
        if (styleLabel.labelFontColor.get() != null) {
            textBuilder.fill().color( new StyleColorToSLDHelper().getSLDColor( styleLabel.labelFontColor.get() ) );
        }
        if (styleLabel.labelFont.get() != null) {
            new StyleFontToSLDHelper().fillSLD( styleLabel.labelFont.get(), ( ) -> textBuilder.newFont() );
        }
        if (host == FeatureType.LINE_STRING
                && !(styleLabel.perpendicularOffset.get() == null || styleLabel.perpendicularOffset.get().compareTo(
                        PERPENDICULAR_OFFSET_DEFAULT ) == 0)) {
            LinePlacementBuilder placementBuilder = textBuilder.linePlacement();
            placementBuilder.offset( styleLabel.perpendicularOffset.get() );
        }
        else if (styleLabel.labelAnchor.get() != null || styleLabel.labelOffset.get() != null
                || styleLabel.labelRotation.get() != null) {
            if ((!isDefaultPointPlacementAndOffset( styleLabel ) || !isDefaultLabelRotation( styleLabel ))) {
                PointPlacementBuilder placementBuilder = textBuilder.pointPlacement();
                if (styleLabel.labelAnchor.get() != null) {
                    AnchorPointBuilder anchorBuilder = placementBuilder.anchor();
                    anchorBuilder.x( styleLabel.labelAnchor.get().x.get() );
                    anchorBuilder.y( styleLabel.labelAnchor.get().y.get() );
                }
                if (styleLabel.labelOffset.get() != null) {
                    DisplacementBuilder offsetBuilder = placementBuilder.displacement();
                    offsetBuilder.x( styleLabel.labelOffset.get().x.get() );
                    offsetBuilder.y( styleLabel.labelOffset.get().y.get() );
                }
                if (styleLabel.labelRotation.get() != null) {
                    placementBuilder.rotation( styleLabel.labelRotation.get() );
                }
            }
        }
        if (styleLabel.haloRadius.get() != null && styleLabel.haloRadius.get() > 0) {
            HaloBuilder haloBuilder = textBuilder.halo();
            haloBuilder.radius( styleLabel.haloRadius.get() );
            if (styleLabel.haloFill.get() != null) {
                FillBuilder haloFillBuilder = haloBuilder.fill();
                haloFillBuilder.color( new StyleColorToSLDHelper().getSLDColor( styleLabel.haloFill.get() ) );
            }
        }
        handleGeoServerVendorExtensions( textBuilder, styleLabel );
    }


    private boolean isDefaultPointPlacementAndOffset( StyleLabel styleLabel ) {
        return isDefaultPointPlacement( styleLabel.labelAnchor.get() )
                && isDefaultPointOffset( styleLabel.labelOffset.get() );
    }


    private boolean isDefaultPointPlacement( StyleCoord labelAnchor ) {
        return labelAnchor == null
                || labelAnchor.x.get() == null
                || (labelAnchor.x.get().compareTo( ANCHOR_X_DEFAULT ) == 0 && labelAnchor.y.get().compareTo(
                        ANCHOR_Y_DEFAULT ) == 0);
    }


    private boolean isDefaultPointOffset( StyleCoord labelOffset ) {
        return labelOffset == null
                || labelOffset.x.get() == null
                || (labelOffset.x.get().compareTo( OFFSET_X_DEFAULT ) == 0 && labelOffset.y.get().compareTo(
                        OFFSET_Y_DEFAULT ) == 0);
    }


    private boolean isDefaultLabelRotation( StyleLabel styleLabel ) {
        return styleLabel.labelRotation.get().compareTo( ROTATION_DEFAULT ) == 0;
    }


    private void handleGeoServerVendorExtensions( TextSymbolizerBuilder textBuilder, StyleLabel styleLabel ) {
        Lists.<Property<?>>newArrayList( styleLabel.autoWrap, styleLabel.followLine, styleLabel.maxAngleDelta,
                styleLabel.maxDisplacement, styleLabel.repeat ).stream()
                .forEach( property -> handleGeoServerVendorExtension( textBuilder, property ) );
    }


    private void handleGeoServerVendorExtension( TextSymbolizerBuilder textBuilder, Property<?> property ) {
        if (property.get() != null) {
            String label = property.info().getName();
            textBuilder.option( label, property.get() );
        }
    }
}
