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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.polymap.core.runtime.Callback;
import org.polymap.core.ui.FormDataFactory;
import org.polymap.p4.style.SimpleStyler;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.entities.AbstractSLDModel;
import org.polymap.p4.style.entities.AbstractStyleSymbolizer;
import org.polymap.p4.style.entities.FeatureType;
import org.polymap.p4.style.entities.StyleFeature;
import org.polymap.p4.style.entities.StyleIdent;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.style.entities.StyleLine;
import org.polymap.p4.style.entities.StylePoint;
import org.polymap.p4.style.entities.StylePolygon;
import org.polymap.p4.style.font.IFontInfo;
import org.polymap.p4.style.icon.FigureLibraryInitializer;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.label.IStyleLabelInfo;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.batik.toolkit.md.MdTabFolder;
import org.polymap.rhei.batik.toolkit.md.MdToolkit;
import org.polymap.rhei.form.DefaultFormPage;
import org.polymap.rhei.form.IFormPageSite;
import org.polymap.rhei.form.batik.BatikFormContainer;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class SimpleStylerUI
        extends AbstractStylerUI {

    private final String                                   styleIdentStr      = "Identification", labelStr = "Label",
            styleStr = "Geometry Style";

    private IPanelSite                                     site;

    private String                                         lastOpenTab        = null;

    private Map<String,Function<Composite,Composite>>      styleContainers    = new HashMap<String,Function<Composite,Composite>>();

    private Map<FeatureType,Function<Composite,Composite>> geometryContainers = new HashMap<FeatureType,Function<Composite,Composite>>();

    private SimpleStyler                                   simpleStyler       = null;

    private final StyleIdentUI                             identUI;

    private final StyleLabelUI                             labelUI;

    private final StylePointUI                             pointUI;

    private final StyleLineUI                              lineUI;

    private final StylePolygonUI                           polygonUI;

    private MdTabFolder                                    tabFolder;


    public SimpleStylerUI( IAppContext context, IPanelSite panelSite, Context<IImageInfo> imageInfoInContext,
            Context<IColorInfo> colorInfoInContext, Context<IFontInfo> fontInfoInContext,
            Context<IStyleLabelInfo> styleLabelInfo ) {
        this.site = panelSite;

        identUI = new StyleIdentUI( context, site );
        labelUI = new StyleLabelUI( context, site, fontInfoInContext, styleLabelInfo );
        FigureLibraryInitializer figureLibraryInitializer = new FigureLibraryInitializer();
        pointUI = new StylePointUI( context, site, imageInfoInContext, colorInfoInContext, figureLibraryInitializer );
        lineUI = new StyleLineUI( context, site, imageInfoInContext, colorInfoInContext );
        polygonUI = new StylePolygonUI( context, site, imageInfoInContext, colorInfoInContext );
    }


    public void setModel( SimpleStyler simpleStyler ) {
        this.simpleStyler = simpleStyler;
    }


    public Composite createContents( Composite parent ) {
        simpleStyler.getSldFragments().forEach( sldFragment -> createContent( sldFragment ) );
        return internalCreateContents( parent );
    }


    private void createContent( AbstractSLDModel fragment ) {
        DefaultFormPage page = null;
        String label = null;
        FeatureType featureType = null;
        if (fragment instanceof StyleFeature) {
            StyleFeature styleFeature = (StyleFeature)fragment;
            styleFeature.styleComposite.get().stylePoints.forEach( styleLine -> createContent( styleLine ) );
            styleFeature.styleComposite.get().styleLines.forEach( styleLine -> createContent( styleLine ) );
            styleFeature.styleComposite.get().stylePolygons.forEach( styleLine -> createContent( styleLine ) );
        }
        else if (fragment instanceof StyleIdent) {
            label = styleIdentStr;
            page = new DefaultFormPage() {

                @Override
                public void createFormContents( IFormPageSite formSite ) {
                    identUI.setModel( (StyleIdent)fragment );
                    identUI.createContents( formSite );
                    Callback<FeatureType> callback = ( ft ) -> {
                        tabFolder.replaceTabContent( styleStr, geometryContainers.get( ft ) );
                    };
                    identUI.addCallback( callback );
                }
            };
        }
        else if (fragment instanceof AbstractStyleSymbolizer) {
            label = styleStr;
            if (fragment instanceof StylePoint) {
                featureType = FeatureType.POINT;
                StylePoint point = (StylePoint)fragment;
                page = new DefaultFormPage() {

                    @Override
                    public void createFormContents( IFormPageSite formSite ) {
                        pointUI.setModel( point );
                        pointUI.createContents( formSite );
                    }
                };
                createLabelUI( point.markerLabel.get() != null ? point.markerLabel.get() : point.markerLabel
                        .createValue( null ) );
            }
            else if (fragment instanceof StyleLine) {
                featureType = FeatureType.LINE_STRING;
                StyleLine styleLine = (StyleLine)fragment;
                page = new DefaultFormPage() {

                    @Override
                    public void createFormContents( IFormPageSite formSite ) {
                        lineUI.setModel( styleLine );
                        lineUI.createContents( formSite );
                    }
                };
                createLabelUI( styleLine.lineLabel.get() != null ? styleLine.lineLabel.get() : styleLine.lineLabel
                        .createValue( null ) );
            }
            else if (fragment instanceof StylePolygon) {
                featureType = FeatureType.POLYGON;
                StylePolygon stylePolygon = (StylePolygon)fragment;
                page = new DefaultFormPage() {

                    @Override
                    public void createFormContents( IFormPageSite formSite ) {
                        polygonUI.setModel( stylePolygon );
                        polygonUI.createContents( formSite );
                    }
                };
                createLabelUI( stylePolygon.polygonLabel.get() != null ? stylePolygon.polygonLabel.get()
                        : stylePolygon.polygonLabel.createValue( null ) );
            }
        }
        if (label != null && page != null) {
            BatikFormContainer pageContainer = new BatikFormContainer( page );
            Function<Composite,Composite> contentFunction = createContentFunction( (MdToolkit)site.toolkit(), label,
                    pageContainer );
            if (featureType != null) {
                geometryContainers.put( featureType, contentFunction );
            }
            styleContainers.put( label, contentFunction );
        }
    }


    private void createLabelUI( StyleLabel styleLabel ) {
        if (styleContainers.get( labelStr ) == null) {
            DefaultFormPage page = new DefaultFormPage() {

                @Override
                public void createFormContents( IFormPageSite formSite ) {
                    labelUI.setModel( styleLabel );
                    labelUI.createContents( formSite );
                }
            };
            BatikFormContainer pageContainer = new BatikFormContainer( page );
            Function<Composite,Composite> contentFunction = createContentFunction( (MdToolkit)site.toolkit(), labelStr,
                    pageContainer );
            styleContainers.put( labelStr, contentFunction );
        }
    }


    private Function<Composite,Composite> createContentFunction( MdToolkit tk, String label,
            BatikFormContainer pageContainer ) {
        return new Function<Composite,Composite>() {

            @Override
            public Composite apply( Composite parent ) {
                Composite composite = tk.createComposite( parent, SWT.NONE );
                pageContainer.createContents( composite );
                return composite;
            }
        };
    }


    private Composite internalCreateContents( Composite parent ) {
        MdToolkit tk = (MdToolkit)site.toolkit();
        List<String> tabItems = new ArrayList<String>();
        tabItems.add( styleIdentStr );
        tabItems.add( labelStr );
        tabItems.add( styleStr );
        Map<String,Function<Composite,Composite>> tabContents = new HashMap<String,Function<Composite,Composite>>();
        for (String tabItem : tabItems) {
            tabContents.put( tabItem, styleContainers.get( tabItem ) );
        }
        tabFolder = tk.createTabFolder( parent, tabItems, tabContents );
        tabFolder.openTab( getLastOpenTab() );
        tabFolder.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                setLastOpenTab( ((Button)e.widget).getText() );
            }
        } );
        tabFolder.replaceTabContent( styleStr, geometryContainers.get( FeatureType.POINT ) );
        FormDataFactory.on( tabFolder ).left( 0 ).right( 100 );
        return tabFolder;
    }


    public String getLastOpenTab() {
        return this.lastOpenTab;
    }


    public void setLastOpenTab( String tabname ) {
        this.lastOpenTab = tabname;
    }


    @Override
    public void resetUI() {
        // TODO Auto-generated method stub

    }


    @Override
    public void submitUI() {
        // TODO Auto-generated method stub

    }
}
