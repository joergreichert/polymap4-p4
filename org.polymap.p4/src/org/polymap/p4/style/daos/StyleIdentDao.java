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

import java.util.Arrays;
import java.util.List;

import org.geotools.styling.StyledLayerDescriptor;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleIdentDao implements IStyleDao {

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

    public static final String NAME             = "name";

    public static final String TITLE            = "title";

    public static final String FEATURE_TYPE     = "featureType";

    private String             name;

    private String             title;

    private FeatureType        featureType      = FeatureType.POINT;


    public StyleIdentDao() {
    }


    /**
     * @param style
     */
    public StyleIdentDao( StyledLayerDescriptor style ) {
        fromSLD( style );
    }


    public String getName() {
        return name;
    }


    public void setName( String name ) {
        this.name = name;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle( String title ) {
        this.title = title;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }


    public void setFeatureType( FeatureType featureType ) {
        this.featureType = featureType;
    }

    public void fromSLD(StyledLayerDescriptor style) {
        style.accept( new StyleIdentFromSLDVisitor(this) );
    }
    
    public void fillSLD(SLDBuilder builder) {
        new StyleIdentToSLDVisitor( this ).fillSLD( builder );
    }   
}
