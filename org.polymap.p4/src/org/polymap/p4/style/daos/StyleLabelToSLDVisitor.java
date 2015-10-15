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

import org.geotools.styling.builder.FontBuilder;
import org.geotools.styling.builder.NamedLayerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.StyleBuilder;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.geotools.styling.builder.TextSymbolizerBuilder;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelToSLDVisitor
        extends AbstractStyleToSLDVisitor {

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
    public void fillSLD( StyledLayerDescriptorBuilder builder ) {
        NamedLayerBuilder namedLayer = builder.namedLayer();
        StyleBuilder userStyle = namedLayer.style();
        RuleBuilder ruleBuilder = userStyle.featureTypeStyle().rule();
        TextSymbolizerBuilder textBuilder = ruleBuilder.text();
        if(styleLabelDao.getLabelText() != null) {
            textBuilder.labelText( styleLabelDao.getLabelText() );
            if(styleLabelDao.getLabelFontColor() != null) {
                textBuilder.fill().color( toAwtColor( styleLabelDao.getLabelFontColor()) );
            }
            if(styleLabelDao.getLabelFont() != null) {
                FontBuilder fontBuilder = textBuilder.newFont();
                fontBuilder.familyName( styleLabelDao.getLabelFont() );
                if(styleLabelDao.getLabelFontSize()  != null) {
                    fontBuilder.size( styleLabelDao.getLabelFontSize() );
                }
                fontBuilder.styleName( styleLabelDao.getLabelFontStyle() );
                fontBuilder.weightName( styleLabelDao.getLabelFontWeight() );
            }
        }
    }
}
