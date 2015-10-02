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
package org.polymap.p4.style;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.StyledLayerDescriptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerDAOTest {

    @Test
    public void testStyleDAO() throws Exception {
        StylerDAO dao = new StylerDAO();
        dao.setUserStyleName( "MeinStyle" );
        dao.setFeatureType( "point" );
        dao.setMarkerWellKnownName( "Circle" );
        dao.setMarkerSize( 12 );
        dao.setMarkerFill( new RGB( 255, 255, 255 ) );
        dao.setMarkerStrokeColor( new RGB( 0, 0, 0 ) );
        dao.setMarkerStrokeSize( 3 );
        StyledLayerDescriptor sld = dao.toSLD();
        String actual = writeSLDToString( sld );
        String expected = FileUtils.readFileToString( new File( getClass().getResource( "simple_sld.xml" ).toURI() ) );
        Assert.assertEquals( expected, actual );
    }


    private String writeSLDToString( StyledLayerDescriptor sld ) throws TransformerException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        SLDTransformer tx = new SLDTransformer();
        tx.setIndentation( 4 );
        tx.transform( sld, byteOut );
        String output = new String( byteOut.toByteArray() );
        return output;
    }
}
