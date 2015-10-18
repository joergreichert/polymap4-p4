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

import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.FeatureTypeStyleBuilder;
import org.geotools.styling.builder.NamedLayerBuilder;
import org.geotools.styling.builder.RuleBuilder;
import org.geotools.styling.builder.StyleBuilder;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.geotools.styling.builder.TextSymbolizerBuilder;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class SLDBuilder {

    private final StyledLayerDescriptorBuilder wrappedBuilder;

    private NamedLayerBuilder                  namedLayerBuilder       = null;

    private StyleBuilder                       namedLayerStyleBuilder  = null;

    private FeatureTypeStyleBuilder            featureTypeStyleBuilder = null;

    private RuleBuilder                        ruleBuilder             = null;

    private TextSymbolizerBuilder              textSymbolizerBuilder   = null;


    public SLDBuilder( StyledLayerDescriptorBuilder wrappedBuilder ) {
        this.wrappedBuilder = wrappedBuilder;
    }


    /**
     * @return
     */
    public NamedLayerBuilder namedLayer() {
        if (namedLayerBuilder == null) {
            namedLayerBuilder = wrappedBuilder.namedLayer();
        }
        return namedLayerBuilder;
    }


    public StyledLayerDescriptor build() {
        return wrappedBuilder.build();
    }


    public StyleBuilder style( NamedLayerBuilder namedLayer ) {
        if (namedLayerStyleBuilder == null) {
            namedLayerStyleBuilder = namedLayer.style();
        }
        return namedLayerStyleBuilder;
    }


    public FeatureTypeStyleBuilder featureTypeStyle( StyleBuilder style ) {
        if (featureTypeStyleBuilder == null) {
            featureTypeStyleBuilder = style.featureTypeStyle();
        }
        return featureTypeStyleBuilder;
    }


    public RuleBuilder rule( FeatureTypeStyleBuilder featureTypeStyle ) {
        if (ruleBuilder == null) {
            ruleBuilder = featureTypeStyle.rule();
        }
        return ruleBuilder;
    }


    public TextSymbolizerBuilder text( RuleBuilder ruleBuilder ) {
        if (textSymbolizerBuilder == null) {
            textSymbolizerBuilder = ruleBuilder.text();
        }
        return textSymbolizerBuilder;
    }
}
