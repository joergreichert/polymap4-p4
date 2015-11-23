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
package org.polymap.p4.style.sld.from;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.polymap.p4.style.entities.StyleFeature;
import org.polymap.p4.style.entities.StyleFilterConfiguration;
import org.polymap.p4.style.entities.StyleZoomConfiguration;
import org.polymap.p4.style.sld.from.helper.StyleCompositeFromSLDHelper;
import org.polymap.p4.style.sld.from.helper.StyleFilterFromSLDHelper;
import org.polymap.p4.style.sld.from.helper.StyleZoomFromSLDHelper;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleFeatureFromSLDVisitor
        extends AbstractStyleFromSLDVisitor {

    private final StyleFeature styleFeature;


    public StyleFeatureFromSLDVisitor( StyleFeature styleFeature ) {
        this.styleFeature = styleFeature;
    }


    @Override
    public void visit( Style style ) {
        long nonZoomedOrFilteredFeatureCount = getNonZoomedOrFilteredFeatureCount( style );
        if (nonZoomedOrFilteredFeatureCount > 0) {
            new StyleCompositeFromSLDHelper( styleFeature.styleComposite.get() ).visit( style );
        }
        super.visit( style );
    }


    private long getNonZoomedOrFilteredFeatureCount( Style style ) {
        return style.featureTypeStyles().stream()
                .flatMap( featureTypeStyle -> featureTypeStyle.rules().stream() )
                .filter( rule -> isNonZoomedOrFilteredRule(rule) ).count();
    }

    public long getNonZoomedOrFilteredRulesCount( FeatureTypeStyle featureTypeStyle ) {
        return featureTypeStyle.rules().stream().filter( rule -> isNonZoomedOrFilteredRule(rule) ).count();
    }

    boolean isNonZoomedOrFilteredRule(Rule rule) {
        return !hasZoomAttributes( rule ) && !hasFilterAttribute( rule );
    }

    public void visit( FeatureTypeStyle featureTypeStyle ) {
        handleZoomFeatures( featureTypeStyle );
        handleFilterFeatures( featureTypeStyle );
    }


    private void handleZoomFeatures( FeatureTypeStyle featureTypeStyle ) {
        List<Rule> zoomedFeatures = featureTypeStyle.rules().stream().filter( rule -> hasZoomAttributes( rule ) )
                .collect( Collectors.toList() );
        Function<String,StyleZoomConfiguration> fun = ( String label ) -> styleFeature.zoomConfigurations
                .createElement( zoomConfiguration -> {
                    zoomConfiguration.zoomLevelName.set( label );
                    zoomConfiguration.styleComposite.createValue( null );
                    return zoomConfiguration;
                } );
        zoomedFeatures.stream().forEach( rule -> new StyleZoomFromSLDHelper( fun ).visit( rule ) );
    }


    private boolean hasZoomAttributes( Rule rule ) {
        return rule.getMinScaleDenominator() != 0 || rule.getMaxScaleDenominator() != Double.POSITIVE_INFINITY;
    }
    
    private void handleFilterFeatures( FeatureTypeStyle featureTypeStyle ) {
        List<Rule> filterFeatures = featureTypeStyle.rules().stream().filter( rule -> hasFilterAttribute( rule ) )
                .collect( Collectors.toList() );
        Function<String,StyleFilterConfiguration> fun = ( String label ) -> styleFeature.filterConfigurations
                .createElement( filterConfiguration -> {
                    filterConfiguration.ruleName.set( label );
                    filterConfiguration.styleComposites.createElement( null );
                    return filterConfiguration;
                } );
        filterFeatures.stream().forEach( rule -> new StyleFilterFromSLDHelper( fun ).visit( rule ) );
    }


    private boolean hasFilterAttribute( Rule rule ) {
        return rule.getFilter() != null;
    }
}
