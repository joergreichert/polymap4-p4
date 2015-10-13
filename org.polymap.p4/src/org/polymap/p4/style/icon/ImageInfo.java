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

import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.tuple.Pair;
import org.polymap.core.runtime.event.EventHandler;
import org.polymap.p4.style.AbstractFormFieldInfo;
import org.polymap.rhei.field.ImageDescription;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class ImageInfo
        extends AbstractFormFieldInfo
        implements IImageInfo {

    private SortedMap<Pair<String,String>,List<ImageDescription>> imageLibrary           = null;

    private Map<String,ImageDescription>                          pathToImageDescription = null;

    private ImageDescription                                      imageDescription       = null;


    /**
     * @param imageLibrary
     */
    public void setImageLibrary( SortedMap<Pair<String,String>,List<ImageDescription>> imageLibrary ) {
        this.imageLibrary = imageLibrary;

    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.style.IImageInfo#getImageLibrary()
     */
    @Override
    public SortedMap<Pair<String,String>,List<ImageDescription>> getImageLibrary() {
        return this.imageLibrary;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.p4.style.IImageInfo#setImageDescription(org.polymap.rhei.field
     * .ImageDescription)
     */
    @Override
    public void setImageDescription( ImageDescription imageDescription ) {
        this.imageDescription = imageDescription;

    }


    public ImageDescription getImageDescription() {
        return imageDescription;
    }


    @EventHandler(delay = 100, display = true)
    protected void onStatusChange( List<EventObject> evs ) {
        evs.forEach( ev -> {
            if (ev.getSource() == this) {
                getFormField().setValue( getImageDescription() );
            }
        } );
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.p4.style.icon.IImageInfo#getImageDescriptionByPath(java.lang.String
     * )
     */
    @Override
    public ImageDescription getImageDescriptionByPath( String path ) {
        ImageDescription imgDesc = pathToImageDescription.get( path );
        if(imgDesc == null) {
            imgDesc = pathToImageDescription.get( path.replace( "resources/icons/", "" ) );
        }
        return imgDesc;
    }


    public void setPathToImageDescription( Map<String,ImageDescription> pathToImageDescription ) {
        this.pathToImageDescription = pathToImageDescription;
    }
}