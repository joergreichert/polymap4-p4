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

import org.geotools.renderer.lite.gridcoverage2d.StyleVisitorAdapter;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayer;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.UserLayer;
import org.opengis.filter.expression.ExpressionVisitor;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public abstract class AbstractStyleFromSLDVisitor
        extends StyleVisitorAdapter {

    private StylerExpressionVisitor stylerExpressionVisitor = null;


    protected ExpressionVisitor getExpressionVisitor() {
        if (stylerExpressionVisitor == null) {
            stylerExpressionVisitor = new StylerExpressionVisitor();
        }
        return stylerExpressionVisitor;
    }


    @Override
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


    @Override
    public void visit( NamedLayer layer ) {
        for (Style style : layer.getStyles()) {
            style.accept( this );
        }
    }


    @Override
    public void visit( Style style ) {
        for (FeatureTypeStyle featureTypeStyle : style.featureTypeStyles()) {
            featureTypeStyle.accept( this );
        }
    }


    @Override
    public void visit( FeatureTypeStyle fts ) {
        for (Rule rule : fts.rules()) {
            rule.accept( this );
        }
    }


    @Override
    public void visit( Rule rule ) {
        for (Symbolizer symbolizer : rule.getSymbolizers()) {
            symbolizer.accept( this );
        }
    }
}
