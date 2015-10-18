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

import org.geotools.filter.AttributeExpressionImpl;
import org.geotools.styling.builder.AnchorPointBuilder;
import org.geotools.styling.builder.DisplacementBuilder;
import org.geotools.styling.builder.FontBuilder;
import org.geotools.styling.builder.PointPlacementBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.TextSymbolizerBuilder;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelToSLDVisitor
        extends AbstractStyleToSLDVisitor {

    private static String       FONT_FAMILY_DEFAULT = "Serif";

    private static Integer      FONT_SIZE_DEFAULT   = 10;

    private static String       FONT_STYLE_DEFAULT  = "normal";

    private static String       FONT_WEIGHT_DEFAULT = "normal";

    private static Double       ANCHOR_X_DEFAULT    = 0.0d;

    private static Double       ANCHOR_Y_DEFAULT    = 0.5d;

    private static Double       OFFSET_X_DEFAULT    = 0.0d;

    private static Double       OFFSET_Y_DEFAULT    = 0.0d;

    private static Double       ROTATION_DEFAULT    = 0.0d;

    private final StyleLabelDao styleLabelDao;


    public StyleLabelToSLDVisitor( StyleLabelDao styleLabelDao ) {
        this.styleLabelDao = styleLabelDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.p4.style.daos.AbstractStyleToSLDVisitor#fillSLD(org.geotools.styling
     * .builder.StyledLayerDescriptorBuilder)
     */
    @Override
    public void fillSLD( SLDBuilder builder ) {
        if (styleLabelDao.getLabelText() != null) {
            RuleBuilder ruleBuilder = getRuleBuilder( builder );
            TextSymbolizerBuilder textBuilder = builder.text( ruleBuilder );
            textBuilder.label( new AttributeExpressionImpl( new org.geotools.feature.NameImpl( styleLabelDao
                    .getLabelText() ) ) );
            if (styleLabelDao.getLabelFontColor() != null) {
                textBuilder.fill().color( toAwtColor( styleLabelDao.getLabelFontColor() ) );
            }
            if (styleLabelDao.getLabelFont() != null && (!FONT_FAMILY_DEFAULT.equals( styleLabelDao.getLabelFont() )
                    || styleLabelDao.getLabelFontSize().intValue() != FONT_SIZE_DEFAULT
                    || !FONT_STYLE_DEFAULT.equals( styleLabelDao.getLabelFontStyle() )
                    || !FONT_WEIGHT_DEFAULT.equals( styleLabelDao.getLabelFontWeight() ))) {
                FontBuilder fontBuilder = textBuilder.newFont();
                fontBuilder.familyName( styleLabelDao.getLabelFont() );
                if (styleLabelDao.getLabelFontSize() != null) {
                    fontBuilder.size( styleLabelDao.getLabelFontSize() );
                }
                if (styleLabelDao.getLabelFontStyle() != null) {
                    fontBuilder.styleName( styleLabelDao.getLabelFontStyle() );
                }
                if (styleLabelDao.getLabelFontWeight() != null) {
                    fontBuilder.weightName( styleLabelDao.getLabelFontWeight() );
                }
            }
            if ((styleLabelDao.getLabelAnchor() != null && !(styleLabelDao.getLabelAnchor().x.compareTo( ANCHOR_X_DEFAULT) == 0 && styleLabelDao
                    .getLabelAnchor().y.compareTo( ANCHOR_Y_DEFAULT) == 0))
                    || (styleLabelDao.getLabelOffset() != null && !(styleLabelDao.getLabelOffset().x.compareTo( OFFSET_X_DEFAULT) == 0 && styleLabelDao
                            .getLabelOffset().y.compareTo( OFFSET_Y_DEFAULT) == 0))
                    || (styleLabelDao.getLabelRotation() != null && styleLabelDao.getLabelRotation().compareTo(ROTATION_DEFAULT) != 0)) {
                PointPlacementBuilder placementBuilder = textBuilder.pointPlacement();
                if (styleLabelDao.getLabelAnchor() != null) {
                    AnchorPointBuilder anchorBuilder = placementBuilder.anchor();
                    anchorBuilder.x( styleLabelDao.getLabelAnchor().x );
                    anchorBuilder.y( styleLabelDao.getLabelAnchor().y );
                }
                if (styleLabelDao.getLabelOffset() != null) {
                    DisplacementBuilder offsetBuilder = placementBuilder.displacement();
                    offsetBuilder.x( styleLabelDao.getLabelOffset().x );
                    offsetBuilder.y( styleLabelDao.getLabelOffset().y );
                }
                if (styleLabelDao.getLabelRotation() != null) {
                    placementBuilder.rotation( styleLabelDao.getLabelRotation() );
                }
            }

        }
    }
}
