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

import org.geotools.styling.builder.PolygonSymbolizerBuilder;
import org.geotools.styling.builder.RuleBuilder;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePolygonToSLDVisitor
        extends AbstractStyleToSLDVisitor {

    private final StylePolygonDao stylePolygonDao;


    public StylePolygonToSLDVisitor( StylePolygonDao stylePolygonDao ) {
        this.stylePolygonDao = stylePolygonDao;
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
        if(false) {
            RuleBuilder ruleBuilder = getRuleBuilder( builder );
            PolygonSymbolizerBuilder polygonBuilder = ruleBuilder.polygon();
            // TODO
        }
    }
}
