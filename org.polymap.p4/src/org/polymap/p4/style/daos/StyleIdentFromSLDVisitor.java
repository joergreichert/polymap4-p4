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

import org.geotools.styling.NamedLayer;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;


/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleIdentFromSLDVisitor extends AbstractStyleFromSLDVisitor {
    private final StyleIdentDao styleIdentDao;
    
    public StyleIdentFromSLDVisitor(StyleIdentDao styleIdentDao) {
        this.styleIdentDao = styleIdentDao;
    }

    public void visit( StyledLayerDescriptor sld ) {
        styleIdentDao.setName( sld.getName() );
        styleIdentDao.setTitle( sld.getTitle() );
        super.visit( sld );
    }


    @Override
    public void visit( NamedLayer layer ) {
        styleIdentDao.setName( layer.getName() );
        for(Style style : layer.getStyles()) {
            if(style.getDescription() != null) {
                styleIdentDao.setTitle(style.getDescription().getTitle().toString());
            }
        }
    }    
}
