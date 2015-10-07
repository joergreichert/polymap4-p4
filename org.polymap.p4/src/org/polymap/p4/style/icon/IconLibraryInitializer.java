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
package org.polymap.p4.style.icon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.polymap.rhei.field.ImageDescription;

import com.google.common.collect.Lists;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class IconLibraryInitializer {

    private final ImageHelper                                           imageHelper;

    private final SortedMap<Pair<String,String>,List<ImageDescription>> imageLibrary;


    public IconLibraryInitializer() {
        imageHelper = new ImageHelper();
        List<String> names = Lists.newArrayList( "Predefined", "Simple icon places", "Pin of maps", "Maki icons" );
        Comparator<Pair<String,String>> comparator = ( Pair<String,String> pair1, Pair<String,String> pair2 ) -> Integer
                .valueOf( names.indexOf( pair1.getLeft() ) ).compareTo( names.indexOf( pair2.getLeft() ) );
        imageLibrary = new TreeMap<Pair<String,String>,List<ImageDescription>>( comparator );

        addToImageLibrary( names.get( 0 ), "well_known.lst", "Predefined shapes by SLD" );
        addToImageLibrary(
                names.get( 1 ),
                "simple-icon-places.lst",
                "<a href=\"http://www.flaticon.com/packs/simpleicon-places/\">SimpleIcons Places</a>, designed by <a href=\"http://freepik.com\">freepik</a>" );
        addToImageLibrary(
                names.get( 2 ),
                "pin-of-maps.lst",
                "<a href=\"http://www.flaticon.com/packs/pins-of-maps/\">Pin of maps</a>, designed by <a href=\"http://freepik.com\">freepik</a>" );
        addToImageLibrary( names.get( 3 ), "mapzone_maki.lst",
                "<a href=\"https://github.com/mapbox/maki\">Maki icons</a>, designed by <a href=\"http://mapbox.com/\">Mapbox</a>" );

    }


    private void addToImageLibrary( String name, String listFile, String markUpLicenceText ) {
        List<ImageDescription> imageDescriptors = new ArrayList<ImageDescription>();
        try (BufferedReader br = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( listFile ) ) )) {
            String line;
            while ((line = br.readLine()) != null) {
                final String imagePath = line;
                ImageDescription imageDescription = getImageHelper().createImageDescription( imagePath );
                imageDescriptors.add( imageDescription );
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        imageLibrary.put( Pair.of( name, markUpLicenceText ), imageDescriptors );
    }


    private ImageHelper getImageHelper() {
        return imageHelper;
    }


    public SortedMap<Pair<String,String>,List<ImageDescription>> getImageLibrary() {
        return imageLibrary;
    }
}
