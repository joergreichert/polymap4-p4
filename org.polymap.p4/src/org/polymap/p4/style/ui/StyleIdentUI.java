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
package org.polymap.p4.style.ui;

import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.polymap.p4.style.entities.FeatureType;
import org.polymap.p4.style.entities.StyleIdent;
import org.polymap.p4.util.PropertyAdapter;
import org.polymap.rhei.field.NotEmptyValidator;
import org.polymap.rhei.field.PicklistFormField;
import org.polymap.rhei.field.StringFormField;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleIdentUI {

    /**
     * 
     */
    public StyleIdentUI( StyleIdent styleIdent, IFormPageSite site ) {
        site.newFormField( new PropertyAdapter( styleIdent.name ) ).label.put( "Style name" ).field
                .put( new StringFormField() ).tooltip.put( "" ).validator.put( new NotEmptyValidator<String,String>() )
                .create();
        site.newFormField( new PropertyAdapter( styleIdent.title ) ).label.put( "Style title" ).field
                .put( new StringFormField() ).tooltip.put( "" ).create();

        List<String> orderedLabel = FeatureType.getOrdered().stream().map( value -> value.getLabel() )
                .collect( Collectors.toList() );
        Comparator<String> comparator = ( String ft1Label, String ft2Label ) -> Integer.valueOf(
                orderedLabel.indexOf( ft1Label ) ).compareTo( orderedLabel.indexOf( ft2Label ) );
        SortedMap<String,Object> orderFeatureTypes = new TreeMap<String,Object>( comparator );
        FeatureType.getOrdered().stream().forEach( value -> orderFeatureTypes.put( value.getLabel(), value ) );
        PicklistFormField picklistFormField = new PicklistFormField( ( ) -> orderFeatureTypes );
        if (styleIdent.featureType.get() == null) {
            styleIdent.featureType.set( FeatureType.POINT );
        }
        site.newFormField( new PropertyAdapter( styleIdent.featureType ) ).label.put( "Feature type" ).field
                .put( picklistFormField ).tooltip.put( "" ).create();
    }
}
