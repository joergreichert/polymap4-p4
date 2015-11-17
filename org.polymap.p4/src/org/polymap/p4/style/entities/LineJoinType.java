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
package org.polymap.p4.style.entities;

import java.util.Arrays;
import java.util.List;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public enum LineJoinType {
    MITRE("mitre"), ROUND("round"), BEVEL("bevel");

    private String label;


    LineJoinType( String label ) {
        this.label = label;

    }


    public static List<LineJoinType> getOrdered() {
        return Arrays.asList( MITRE, ROUND, BEVEL );
    }


    public String getLabel() {
        return label;
    }


    public static LineJoinType getTypeForLabel( String label ) {
        return Arrays.asList( values() ).stream().filter( value -> value.getLabel().equals( label ) ).findFirst().get();
    }
}