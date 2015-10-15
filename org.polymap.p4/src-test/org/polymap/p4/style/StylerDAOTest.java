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
import java.io.InputStreamReader;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.graphics.RGB;
import org.geoserver.catalog.SLDHandler;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.geotools.util.Version;
import org.junit.Assert;
import org.junit.Test;
import org.polymap.p4.style.daos.StyleIdentDao;
import org.polymap.p4.style.daos.StyleIdentDao.FeatureType;
import org.polymap.p4.style.daos.StylePointDao;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerDAOTest {

    @Test
    public void testStyleDAO() throws Exception {
        StyleIdentDao identDao = new StyleIdentDao();
        identDao.setName( "MeinStyle" );
        identDao.setFeatureType( FeatureType.POINT );
        
        StylePointDao pointDao = new StylePointDao();
        pointDao.setMarkerWellKnownName( "Circle" );
        pointDao.setMarkerSize( 12 );
        pointDao.setMarkerFill( new RGB( 255, 255, 255 ) );
        pointDao.setMarkerStrokeColor( new RGB( 0, 0, 0 ) );
        pointDao.setMarkerStrokeSize( 3 );
        
        StyledLayerDescriptorBuilder builder = new StyledLayerDescriptorBuilder();
        identDao.fillSLD( builder );
        pointDao.fillSLD( builder );
        
        StyledLayerDescriptor sld = builder.build();
        String actual = writeSLDToString( sld );
        String expected = FileUtils.readFileToString( new File( getClass().getResource( "simple_sld.xml" ).toURI() ) );
        Assert.assertEquals( expected, actual );
    }


    @Test
    public void testFromStyleDAO() throws Exception {
        InputStreamReader reader = new InputStreamReader( getClass().getResourceAsStream( "simple_sld.xml" ) );
        String styleFormat = SLDHandler.FORMAT;
        Version styleVersion = new Version("1.0.0");
        StyledLayerDescriptor sld = /*Styles.handler( styleFormat )*/new SLDHandler().parse( reader, styleVersion, null, null );

        StyleIdentDao identDao = new StyleIdentDao(sld);
        Assert.assertEquals( "MeinStyle", identDao.getName() );
        Assert.assertEquals( FeatureType.POINT, identDao.getFeatureType() );

        StylePointDao pointDao = new StylePointDao(sld);
        Assert.assertEquals( "Circle", pointDao.getMarkerWellKnownName() );
        Assert.assertSame( 12, pointDao.getMarkerSize() );
        Assert.assertEquals( new RGB( 255, 255, 255 ).toString(), pointDao.getMarkerFill().toString() );
        Assert.assertEquals( new RGB( 0, 0, 0 ).toString(), pointDao.getMarkerStrokeColor().toString() );
        Assert.assertSame( 3, pointDao.getMarkerStrokeSize() );
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
