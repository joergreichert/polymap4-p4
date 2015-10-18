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

import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.polymap.p4.style.daos.SLDBuilder;
import org.polymap.p4.style.daos.StyleIdentDao;
import org.polymap.p4.style.daos.StyleIdentDao.FeatureType;
import org.polymap.p4.style.daos.StylePointDao;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerDAOTest extends AbstractSLDTest {

    @Test
    public void testStyleDAO() throws Exception {
        StyleIdentDao identDao = new StyleIdentDao();
        identDao.setName( "MeinStyle" );
        identDao.setFeatureType( FeatureType.POINT );

        StylePointDao pointDao = new StylePointDao();
        pointDao.setMarkerWellKnownName( "Circle" );
        pointDao.setMarkerSize( 12d );
        pointDao.setMarkerFill( new RGB( 255, 255, 255 ) );
        pointDao.setMarkerStrokeColor( new RGB( 0, 0, 0 ) );
        pointDao.setMarkerStrokeSize( 3d );

        StyledLayerDescriptorBuilder wrappedBuilder = new StyledLayerDescriptorBuilder();
        SLDBuilder builder = new SLDBuilder( wrappedBuilder );
        identDao.fillSLD( builder );
        pointDao.fillSLD( builder );

        assertWrittenSLD( builder, getClass().getResource( "simple_sld.xml" ) );
    }


    @Test
    public void testFromStyleDAO() throws Exception {
        StyledLayerDescriptor sld = getSLD( getClass().getResourceAsStream( "simple_sld.xml" ) );

        StyleIdentDao identDao = new StyleIdentDao( sld );
        Assert.assertEquals( "MeinStyle", identDao.getName() );
        Assert.assertEquals( FeatureType.POINT, identDao.getFeatureType() );

        StylePointDao pointDao = new StylePointDao( sld );
        Assert.assertEquals( "Circle", pointDao.getMarkerWellKnownName() );
        Assert.assertSame( 12, pointDao.getMarkerSize() );
        Assert.assertEquals( new RGB( 255, 255, 255 ).toString(), pointDao.getMarkerFill().toString() );
        Assert.assertEquals( new RGB( 0, 0, 0 ).toString(), pointDao.getMarkerStrokeColor().toString() );
        Assert.assertSame( 3, pointDao.getMarkerStrokeSize() );
    }
}
