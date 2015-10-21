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

import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.polymap.model2.Entity;
import org.polymap.model2.runtime.EntityRepository;
import org.polymap.model2.runtime.EntityRepository.Configuration;
import org.polymap.model2.runtime.UnitOfWork;
import org.polymap.model2.runtime.ValueInitializer;
import org.polymap.model2.store.recordstore.RecordStoreAdapter;
import org.polymap.model2.test.Company;
import org.polymap.model2.test.Employee;
import org.polymap.model2.test.Female;
import org.polymap.model2.test.Male;
import org.polymap.p4.style.entities.FeatureType;
import org.polymap.p4.style.entities.StyleColor;
import org.polymap.p4.style.entities.StyleFigure;
import org.polymap.p4.style.entities.StyleIdent;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.style.entities.StylePoint;
import org.polymap.recordstore.lucene.LuceneRecordStore;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class SimpleStylerTest
        extends AbstractSLDTest {

    @Test
    public void testStyleDAO() throws Exception {
        LuceneRecordStore store = new LuceneRecordStore();
        EntityRepository entityRepository = EntityRepository.newConfiguration()
                .store.set( new RecordStoreAdapter( store ) )
                .entities.set( new Class[] {SimpleStyler.class} )
                .create();
        UnitOfWork unitOfWork = entityRepository.newUnitOfWork();
        ValueInitializer<SimpleStyler> init = ( styler ) -> styler;
        SimpleStyler simpleStyler = unitOfWork.createEntity( SimpleStyler.class, init );
        ValueInitializer<StyleIdent> identInit = (ident) -> ident;
        StyleIdent ident = simpleStyler.sldFragments.createElement( StyleIdent.class, identInit );
        ident.featureType.set( FeatureType.POINT );

        ValueInitializer<StylePoint> pointInit = (point) -> point;
        StylePoint point = simpleStyler.sldFragments.createElement( StylePoint.class, pointInit );

        ValueInitializer<StyleFigure> styleFigureInit = (figure) -> figure;
        StyleFigure styleFigure = (StyleFigure) point.markerGraphic.createValue( StyleFigure.class, styleFigureInit );
        styleFigure.markerWellKnownName.set( "Circle" );
        point.markerSize.set( 12d );
        ValueInitializer<StyleColor> styleColorInit = (color) -> color;
        StyleColor fillColor = (StyleColor) styleFigure.markerFill.createValue( styleColorInit );
        fillColor.red.set( 255 );
        fillColor.green.set( 255 );
        fillColor.blue.set( 255 );
        StyleColor strokeColor = (StyleColor) styleFigure.markerStrokeColor.createValue( styleColorInit );
        strokeColor.red.set( 0 );
        strokeColor.green.set( 0 );
        strokeColor.blue.set( 0 );
        styleFigure.markerStrokeSize.set( 3d );

        StyledLayerDescriptorBuilder wrappedBuilder = new StyledLayerDescriptorBuilder();
        SLDBuilder builder = new SLDBuilder( wrappedBuilder );
        ident.fillSLD( builder );
        point.fillSLD( builder );

        assertWrittenSLD( builder, getClass().getResource( "simple_sld.xml" ) );
    }


    @Test
    public void testFromStyleDAO() throws Exception {
        StyledLayerDescriptor sld = getSLD( getClass().getResourceAsStream( "simple_sld.xml" ) );

        StyleIdent ident = new StyleIdent();
        ident.fromSLD( sld );
        Assert.assertEquals( "MeinStyle", ident.name.get() );
        Assert.assertEquals( FeatureType.POINT, ident.featureType.get() );

        StylePoint point = new StylePoint();
        point.fromSLD( sld );
        Assert.assertTrue( point.markerGraphic.get() instanceof StyleFigure );
        StyleFigure styleFigure = (StyleFigure)point.markerGraphic.get();
        Assert.assertEquals( "Circle", styleFigure.markerWellKnownName.get() );
        Assert.assertSame( 12, point.markerSize.get() );
        StyleColor fillColor = new StyleColor();
        fillColor.red.set( 255 );
        fillColor.green.set( 255 );
        fillColor.blue.set( 255 );
        Assert.assertEquals( fillColor.toString(), styleFigure.markerFill.get().toString() );
        StyleColor strokeColor = new StyleColor();
        strokeColor.red.set( 0 );
        strokeColor.green.set( 0 );
        strokeColor.blue.set( 0 );
        Assert.assertEquals( strokeColor.toString(), styleFigure.markerStrokeColor.get().toString() );
        Assert.assertSame( 3, styleFigure.markerStrokeSize.get() );
    }
}
