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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.map.ProjectMapPanel;
import org.polymap.p4.style.StylerDAO.FeatureType;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.font.IFontInfo;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.pages.LabelPage;
import org.polymap.p4.style.pages.StyleIdentPage;
import org.polymap.p4.style.pages.StylePage;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.DefaultPanel;
import org.polymap.rhei.batik.PanelIdentifier;
import org.polymap.rhei.batik.Scope;
import org.polymap.rhei.batik.toolkit.md.MdTabFolder;
import org.polymap.rhei.batik.toolkit.md.MdToolkit;
import org.polymap.rhei.form.batik.BatikFormContainer;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerPanel
        extends DefaultPanel {

    public static final PanelIdentifier ID          = PanelIdentifier.parse( "styler" );

    private BatikFormContainer          styleIdentPageContainer;

    private BatikFormContainer          labelPageContainer;

    private BatikFormContainer          stylePageContainer;

    private StylerDAO                   styleDAO;

    @Scope(P4Plugin.Scope)
    private Context<IImageInfo>         imageInfo;

    @Scope(P4Plugin.Scope)
    private Context<IColorInfo>         colorInfo;

    @Scope(P4Plugin.Scope)
    private Context<IFontInfo>          fontInfo;

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
        labelPageContainer = new BatikFormContainer( new LabelPage( getContext(), getSite(), styleDAO, fontInfo ) );
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
        tabContents.put( styleIdentStr, styleIdentTabItemContent );
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
        // new StylePreview().createPreviewMap( parent, getStylerDao() );
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
        Composite styleIdentComposite = tk.createComposite( parent, SWT.NONE );
        styleIdentPageContainer.createContents( styleIdentComposite );
        return styleIdentComposite;
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
        return styleComposite;
    }
}
