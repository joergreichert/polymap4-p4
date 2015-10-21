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

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.store.ContentDataStore;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.parameter.GeneralParameterValue;
import org.polymap.core.data.feature.FeatureRenderProcessor2;
import org.polymap.core.data.image.GetMapRequest;
import org.polymap.core.data.image.ImageResponse;
import org.polymap.core.data.pipeline.DataSourceDescription;
import org.polymap.core.data.pipeline.DepthFirstStackExecutor;
import org.polymap.core.data.pipeline.Pipeline;
import org.polymap.core.data.pipeline.PipelineProcessorConfiguration;
import org.polymap.core.data.pipeline.ProcessorResponse;
import org.polymap.core.data.pipeline.ResponseHandler;
import org.polymap.p4.data.P4PipelineIncubator;
import org.polymap.p4.style.entities.StyleIdent;
import org.polymap.rap.openlayers.style.FillStyle;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.Style;
import org.polymap.rap.openlayers.types.Color;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePreview {

    public void createPreviewMap( Composite parent, SimpleStyler simpleStyler ) throws Exception {
        Label comp = new Label( parent, SWT.NONE );
        // comp.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_CYAN )
        // );
        comp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        String typeName = "stylerExample";
        ContentDataStore dataStore = null;
        switch (simpleStyler.sldFragments.stream().filter( fragment -> fragment instanceof StyleIdent )
                .map( fragment -> (StyleIdent)fragment ).findFirst().get().featureType.get()) {
            case LINE_STRING:
                dataStore = createSimpleFeature( typeName, "lineProperty", LineString.class );
                break;
            case POINT:
                dataStore = createSimpleFeature( typeName, "pointProperty", Point.class );
                break;
            case POLYGON:
                dataStore = createSimpleFeature( typeName, "polygonProperty", Polygon.class );
                break;
            case RASTER:
                dataStore = null;
                break;
        }
        ReferencedEnvelope bounds = dataStore.getFeatureSource( typeName ).getBounds()
                .transform( org.polymap.core.data.util.Geometries.crs( "EPSG:3857" ), false );
        P4PipelineIncubator inc = P4PipelineIncubator.forLayer( null );
        DataSourceDescription dsd = new DataSourceDescription();
        dsd.service.set( dataStore );
        dsd.resourceName.set( typeName );
        Pipeline pipeline = inc.newPipeline( FeatureRenderProcessor2.class, dsd, new PipelineProcessorConfiguration[0] );
        List<String> layers = new ArrayList<String>();
        String layerName = "stylerLayer";
        layers.add( layerName );

        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory( null );
        StyleBuilder styleBuilder = new StyleBuilder();
        PointSymbolizer pointSymbolizer = styleFactory.createPointSymbolizer();
        styleBuilder.createFeatureTypeStyle( pointSymbolizer );

        // NamedLayer layer = styleFactory.createNamedLayer();
        // layer.setName(layerName);
        // sld.addStyledLayer(layer);

        org.geotools.styling.Style style = null;// new org.geotools.styling.Style();
        GetMapRequest getMapRequest = new GetMapRequest( layers, "EPSG:3857", bounds, "image/png", 100, 100, -1 );
        DepthFirstStackExecutor executer = new DepthFirstStackExecutor() {

            protected DepthFirstContext createDepthFirstContext(
                    org.polymap.core.data.pipeline.ProcessorDescription desc, int i ) {
                DepthFirstContext depthFirstContext = super.createDepthFirstContext( desc, i );
                depthFirstContext.put( FeatureRenderProcessor2.PROVIDED_STYLE, style );
                return depthFirstContext;
            }
        };
        executer.execute( pipeline, getMapRequest, new ResponseHandler() {

            @Override
            public void handle( ProcessorResponse pipeResponse ) throws Exception {
                java.awt.Image awtImage = ((ImageResponse)pipeResponse).getImage();
                BufferedImage bi = new BufferedImage( awtImage.getWidth( null ), awtImage.getHeight( null ),
                        BufferedImage.TYPE_INT_ARGB );
                java.awt.Graphics g = bi.getGraphics();
                g.drawImage( awtImage, 0, 0, null );
                g.dispose();

                final PaletteData palette = new PaletteData( 0x0000FF, 0x00FF00, 0xFF0000 );
                DataBuffer dataBuffer = bi.getData().getDataBuffer();
                ImageData imageData = null;
                if (dataBuffer.getDataType() == DataBuffer.TYPE_BYTE) {
                    byte[] bytes = ((DataBufferByte)dataBuffer).getData();
                    imageData = new ImageData( bi.getWidth(), bi.getHeight(), 24, palette, 4, bytes );
                }
                else if (dataBuffer.getDataType() == DataBuffer.TYPE_INT) {
                    int[] data = ((DataBufferInt)dataBuffer).getData();
                    imageData = new ImageData( bi.getWidth(), bi.getHeight(), 24, palette );
                    imageData.setPixels( 0, 0, data.length, data, 0 );
                }
                final Image swtImage = new Image( Display.getDefault(), imageData );
                comp.setBackgroundImage( swtImage );
                parent.layout();
                BufferedImage bi2 = new BufferedImage( awtImage.getWidth( null ), awtImage.getHeight( null ),
                        BufferedImage.TYPE_INT_ARGB );
                java.awt.Graphics g2 = bi2.getGraphics();
                g2.drawImage( awtImage, 0, 0, null );
                g2.dispose();
                ImageIO.write( bi2, "png", new File( "out.png" ) );
            }
        } );
    }


    private Style getStyle() {
        return new Style().fill.put( new FillStyle().color.put( new Color( 0, 0, 255, 0.1f ) ) ).stroke
                .put( new StrokeStyle().color.put( new Color( "red" ) ).width.put( 1f ) );
    }


    private ContentDataStore createSimpleFeature( String typeName, String geometryPropertyKey, Class<?> geometryClass ) {
        SimpleFeatureTypeBuilder ftb = new SimpleFeatureTypeBuilder();
        ftb.setName( typeName );
        String namespace = "http://www.polymap.org/";
        ftb.setNamespaceURI( namespace );
        ftb.setCRS( DefaultGeographicCRS.WGS84 );
        ftb.add( geometryPropertyKey, geometryClass );
        ftb.add( "params", GeneralParameterValue[].class );
        final SimpleFeatureType simpleFeatureType = ftb.buildFeatureType();
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder( simpleFeatureType );
        fb.add( null );
        final SimpleFeature feature = fb.buildFeature( null );
        return new ContentDataStore() {

            @Override
            protected List<Name> createTypeNames() throws IOException {
                return Lists.newArrayList( simpleFeatureType.getName() );
            }


            @Override
            protected ContentFeatureSource createFeatureSource( ContentEntry entry ) throws IOException {
                return new ContentFeatureSource( entry, null ) {

                    @Override
                    protected SimpleFeatureType buildFeatureType() throws IOException {
                        entries.put( simpleFeatureType.getName(), entry );
                        return simpleFeatureType;
                    }


                    @Override
                    protected ReferencedEnvelope getBoundsInternal( Query arg0 ) throws IOException {
                        ReferencedEnvelope env = new ReferencedEnvelope( new DefaultGeographicCRS(
                                DefaultGeographicCRS.WGS84 ) );
                        return env;
                    }


                    @Override
                    protected int getCountInternal( Query arg0 ) throws IOException {
                        return 1;
                    }


                    @Override
                    protected FeatureReader<SimpleFeatureType,SimpleFeature> getReaderInternal( Query arg0 )
                            throws IOException {
                        return new FeatureReader<SimpleFeatureType,SimpleFeature>() {

                            private boolean first = true;


                            @Override
                            public void close() throws IOException {
                            }


                            @Override
                            public SimpleFeatureType getFeatureType() {
                                return simpleFeatureType;
                            }


                            @Override
                            public boolean hasNext() throws IOException {
                                return first;
                            }


                            @Override
                            public SimpleFeature next() throws IOException, IllegalArgumentException,
                                    NoSuchElementException {
                                first = false;
                                return feature;
                            }
                        };
                    }
                };
            }
        };
    }
}
