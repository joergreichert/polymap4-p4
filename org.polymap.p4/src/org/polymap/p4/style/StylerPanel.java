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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
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
import org.polymap.core.mapeditor.MapViewer;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.data.P4PipelineIncubator;
import org.polymap.p4.map.ProjectMapPanel;
import org.polymap.p4.style.StylerDAO.FeatureType;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.pages.LabelPage;
import org.polymap.p4.style.pages.StyleIdentPage;
import org.polymap.p4.style.pages.StylePage;
import org.polymap.rap.openlayers.style.FillStyle;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.Style;
import org.polymap.rap.openlayers.types.Color;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.DefaultPanel;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.batik.PanelIdentifier;
import org.polymap.rhei.batik.Scope;
import org.polymap.rhei.batik.toolkit.md.MdTabFolder;
import org.polymap.rhei.batik.toolkit.md.MdToolkit;
import org.polymap.rhei.form.batik.BatikFormContainer;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerPanel
        extends DefaultPanel {

    private static Log                  log         = LogFactory.getLog( StylerPanel.class );

    public static final PanelIdentifier ID          = PanelIdentifier.parse( "styler" );

    private IPanelSite                  site;

    private IAppContext                 context;

    private BatikFormContainer          styleIdentPageContainer;

    private BatikFormContainer          labelPageContainer;

    private BatikFormContainer          stylePageContainer;

    private Geometry                    geometry;

    private MapViewer                   mapViewer;

    private StylerDAO                   styleDAO;

    @Scope(P4Plugin.Scope)
    private Context<IImageInfo>         imageInfo;

    @Scope(P4Plugin.Scope)
    private Context<IColorInfo>         colorInfo;

    private StylePage                   stylePage;

    private String                      lastOpenTab = null;


    @Override
    public boolean wantsToBeShown() {
        return parentPanel().filter( parent -> parent instanceof ProjectMapPanel ).map( parent -> {
            setTitle();
            getSite().setPreferredWidth( 350 );
            return true;
        } ).orElse( false );
    }


    private void setTitle() {
        getSite().setTitle( "Styler" );
    }


    @Override
    public void createContents( Composite parent ) {
        setTitle();
        parent.setLayout( new GridLayout( 1, false ) );

        styleDAO = new StylerDAO();
        styleIdentPageContainer = new BatikFormContainer( new StyleIdentPage( getSite(), styleDAO ) );
        labelPageContainer = new BatikFormContainer( new LabelPage( getSite(), styleDAO ) );
        stylePage = new StylePage( getContext(), getSite(), styleDAO, imageInfo, colorInfo );
        stylePageContainer = new BatikFormContainer( stylePage );

        internalCreateContents( parent );
    }


    private void internalCreateContents( Composite parent ) {
        MdToolkit tk = (MdToolkit)getSite().toolkit();
        Function<Composite,Composite> styleIdentTabItemContent = createStyleIdentTabItemContent( tk );
        Function<Composite,Composite> labelTabItemContent = createLabelTabItemContent( tk );
        Function<Composite,Composite> styleTabItemContent = createStyleTabItemContent( tk );
        String styleIdentStr = "Identification", labelStr = "Label", styleStr = "Geometry Style";
        List<String> tabItems = new ArrayList<String>();
        tabItems.add( styleIdentStr );
        tabItems.add( labelStr );
        tabItems.add( styleStr );
        Map<String,Function<Composite,Composite>> tabContents = new HashMap<String,Function<Composite,Composite>>();
        tabContents.put( labelStr, styleIdentTabItemContent );
        tabContents.put( labelStr, labelTabItemContent );
        tabContents.put( styleStr, styleTabItemContent );
        MdTabFolder tabFolder = tk.createTabFolder( parent, tabItems, tabContents );
        tabFolder.openTab( getLastOpenTab() );
        tabFolder.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                setLastOpenTab( ((Button)e.widget).getText() );
            }
        } );
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        tabFolder.setLayoutData( gd );
        // try {
        // internalCreateMap( parent );
        // }
        // catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }


    public String getLastOpenTab() {
        return this.lastOpenTab;
    }


    public void setLastOpenTab( String tabname ) {
        this.lastOpenTab = tabname;
    }


    private Function<Composite,Composite> createStyleIdentTabItemContent( MdToolkit tk ) {
        return new Function<Composite,Composite>() {

            @Override
            public Composite apply( Composite parent ) {
                return createStyleIdentTabItemContent( tk, parent );
            }
        };
    }


    private Function<Composite,Composite> createStyleTabItemContent( MdToolkit tk ) {
        return new Function<Composite,Composite>() {

            @Override
            public Composite apply( Composite parent ) {
                return createStyleTabItemContent( tk, parent );
            }
        };
    }


    private Function<Composite,Composite> createLabelTabItemContent( MdToolkit tk ) {
        return new Function<Composite,Composite>() {

            @Override
            public Composite apply( Composite parent ) {
                return createLabelTabItemContent( tk, parent );
            }
        };
    }


    private Composite createStyleIdentTabItemContent( MdToolkit tk, Composite parent ) {
        Composite labelComposite = tk.createComposite( parent, SWT.NONE );
        styleIdentPageContainer.createContents( labelComposite );
        return labelComposite;
    }


    private Composite createLabelTabItemContent( MdToolkit tk, Composite parent ) {
        Composite labelComposite = tk.createComposite( parent, SWT.NONE );
        labelPageContainer.createContents( labelComposite );
        return labelComposite;
    }


    private Composite createStyleTabItemContent( MdToolkit tk, Composite parent ) {
        Composite styleComposite = tk.createComposite( parent, SWT.NONE );
        // FeatureSource fs = null;
        // Style style = new DefaultStyles().findStyle( fs );
        FeatureType featureType = styleDAO.getFeatureType();
        switch (featureType) {
            case POINT: {
                stylePageContainer.createContents( styleComposite );
                break;
            }
            case LINE_STRING: {
                stylePageContainer.createContents( styleComposite );
                break;
            }
            case POLYGON: {
                stylePageContainer.createContents( styleComposite );
                break;
            }
            case RASTER: {
                stylePageContainer.createContents( styleComposite );
                break;
            }
        }
        // createByGeometry( styleComposite );
        return styleComposite;
    }


    private void createByGeometry( Composite styleComposite ) {
        Object geometry = getLayerDefaultGeometry();
        if (geometry instanceof com.vividsolutions.jts.geom.Point) {
            stylePageContainer.createContents( styleComposite );
        }
        else if (geometry instanceof Polygon) {
            // TODO
        }
        else if (geometry instanceof LineString) {
            // TODO
        }
        else {
            stylePageContainer.createContents( styleComposite );
        }
    }


    public Geometry getLayerDefaultGeometry() {
        // TODO should this somehow persisted at ILayer or where to fetch this
        // information from?
        if (geometry == null) {
            geometry = new Point( com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory.instance()
                    .create(
                            new com.vividsolutions.jts.geom.Coordinate[] { new com.vividsolutions.jts.geom.Coordinate(
                                    12, 12 ) } ), new GeometryFactory() );
        }
        return geometry;
    }


    protected void internalCreateMap( Composite parent ) throws Exception {
        Label comp = new Label( parent, SWT.NONE );
        // comp.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_CYAN )
        // );
        comp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        String typeName = "stylerExample";
        ContentDataStore dataStore = null;
        if (geometry instanceof Polygon) {
            dataStore = createSimpleFeature( typeName, "polygonProperty", Polygon.class );
        }
        else if (geometry instanceof Point) {
            dataStore = createSimpleFeature( typeName, "pointProperty", Point.class );
        }
        else if (geometry instanceof LineString) {
            dataStore = createSimpleFeature( typeName, "lineProperty", LineString.class );
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


    private void createGeometrySelectionBar( Composite parent ) {
        Group checkboxGroup = new Group( parent, SWT.NONE );
        checkboxGroup.setLayout( new GridLayout( 3, false ) );
        checkboxGroup.setLayoutData( new GridData( GridData.FILL_VERTICAL ) );
        // have to use SWT.CHECK, as SWT.RADIO doesn't display a button
        Button cb1 = new Button( checkboxGroup, SWT.CHECK );
        cb1.setText( "Point" );
        Button cb2 = new Button( checkboxGroup, SWT.CHECK );
        cb2.setText( "Polyline" );
        Button cb3 = new Button( checkboxGroup, SWT.CHECK );
        cb3.setText( "Polygon" );
        SelectionListener selectionListener = new SelectionAdapter() {

            public void widgetSelected( SelectionEvent event ) {
                if (event.widget == cb1) {
                    geometry = new Point(
                            com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory
                                    .instance()
                                    .create(
                                            new com.vividsolutions.jts.geom.Coordinate[] { new com.vividsolutions.jts.geom.Coordinate(
                                                    12, 12 ) } ), new GeometryFactory() );
                    cb2.setSelection( false );
                    cb3.setSelection( false );
                }
                else if (event.widget == cb2) {
                    geometry = new LineString( com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory
                            .instance().create(
                                    new com.vividsolutions.jts.geom.Coordinate[] {
                                            new com.vividsolutions.jts.geom.Coordinate( 12, 12 ),
                                            new com.vividsolutions.jts.geom.Coordinate( 13, 13 ) } ),
                            new GeometryFactory() );
                    cb1.setSelection( false );
                    cb3.setSelection( false );
                }
                else if (event.widget == cb3) {
                    geometry = new Polygon( new LinearRing(
                            com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory.instance().create(
                                    new com.vividsolutions.jts.geom.Coordinate[0] ), new GeometryFactory() ),
                            new LinearRing[] {
                                    new LinearRing( com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory
                                            .instance().create(
                                                    new com.vividsolutions.jts.geom.Coordinate[] {
                                                            new com.vividsolutions.jts.geom.Coordinate( 12, 12 ),
                                                            new com.vividsolutions.jts.geom.Coordinate( 13, 13 ) } ),
                                            new GeometryFactory() ),
                                    new LinearRing( com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory
                                            .instance().create(
                                                    new com.vividsolutions.jts.geom.Coordinate[] {
                                                            new com.vividsolutions.jts.geom.Coordinate( 13, 13 ),
                                                            new com.vividsolutions.jts.geom.Coordinate( 15, 16 ) } ),
                                            new GeometryFactory() ) }, new GeometryFactory() );
                    cb1.setSelection( false );
                    cb2.setSelection( false );
                }
            };
        };
        cb1.addSelectionListener( selectionListener );
        cb2.addSelectionListener( selectionListener );
        cb3.addSelectionListener( selectionListener );

        Button b = new Button( checkboxGroup, SWT.PUSH );
        b.setText( "Update viewer" );
        b.addSelectionListener( new SelectionAdapter() {

            public void widgetSelected( SelectionEvent e ) {
                if (mapViewer != null) {
                    System.out.println( mapViewer );
                }
            }
        } );
    }
}
