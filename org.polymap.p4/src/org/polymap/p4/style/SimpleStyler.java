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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.geotools.styling.StyledLayerDescriptor;
import org.opengis.feature.type.FeatureType;
import org.polymap.core.ui.FormDataFactory;
import org.polymap.model2.CollectionProperty;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.entities.AbstractSLDModel;
import org.polymap.p4.style.entities.AbstractStyleSymbolizer;
import org.polymap.p4.style.entities.StyleIdent;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.style.entities.StyleLine;
import org.polymap.p4.style.entities.StylePoint;
import org.polymap.p4.style.entities.StylePolygon;
import org.polymap.p4.style.font.IFontInfo;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.ui.StyleIdentUI;
import org.polymap.p4.style.ui.StyleLabelUI;
import org.polymap.p4.style.ui.StyleLineUI;
import org.polymap.p4.style.ui.StylePointUI;
import org.polymap.p4.style.ui.StylePolygonUI;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.batik.toolkit.md.MdTabFolder;
import org.polymap.rhei.batik.toolkit.md.MdToolkit;
import org.polymap.rhei.form.DefaultFormPage;
import org.polymap.rhei.form.IFormPageSite;
import org.polymap.rhei.form.batik.BatikFormContainer;

/**
 * Configuration of simple stylers.
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class SimpleStyler
        extends AbstractStyler {

    private final String                              styleIdentStr   = "Identification", labelStr = "Label",
            styleStr = "Geometry Style";

    private IAppContext                               context;

    private IPanelSite                                site;

    private Context<IImageInfo>                       imageInfoInContext;

    private Context<IColorInfo>                       colorInfoInContext;

    private Context<IFontInfo>                        fontInfoInContext;

    private String                                    lastOpenTab     = null;

    private Map<String,Function<Composite,Composite>> styleContainers = new HashMap<String,Function<Composite,Composite>>();

    protected CollectionProperty<AbstractSLDModel>    sldFragments;

    public SimpleStyler( ) {
        
    }

    /**
     * 
     */
    public SimpleStyler( IAppContext context, IPanelSite panelSite, Context<IImageInfo> imageInfoInContext,
            Context<IColorInfo> colorInfoInContext, Context<IFontInfo> fontInfoInContext ) {
        this.context = context;
        this.site = panelSite;
        this.imageInfoInContext = imageInfoInContext;
        this.colorInfoInContext = colorInfoInContext;
        this.fontInfoInContext = fontInfoInContext;
    }


    @Override
    public Composite createContents( Composite parent ) {
        sldFragments.forEach( sldFragment -> createContent( sldFragment ) );

        return internalCreateContents( parent );
    }


    private void createContent( AbstractSLDModel fragment ) {
        DefaultFormPage page = null;
        String label = null;
        if (fragment instanceof StyleIdent) {
            label = styleIdentStr;
            page = new DefaultFormPage() {

                @Override
                public void createFormContents( IFormPageSite site ) {
                    new StyleIdentUI( (StyleIdent)fragment, site );
                }
            };
        }
        else if (fragment instanceof StyleLabel) {
            label = labelStr;
            page = new DefaultFormPage() {

                @Override
                public void createFormContents( IFormPageSite formSite ) {
                    new StyleLabelUI( (StyleLabel)fragment, formSite, context, site, fontInfoInContext );
                }
            };
        }
        else if (fragment instanceof AbstractStyleSymbolizer) {
            label = styleStr;
            if (fragment instanceof StylePoint) {
                page = new DefaultFormPage() {

                    @Override
                    public void createFormContents( IFormPageSite formSite ) {
                        new StylePointUI( (StylePoint)fragment, formSite, context, site, imageInfoInContext,
                                colorInfoInContext );
                    }
                };
            }
            else if (fragment instanceof StyleLine) {
                page = new DefaultFormPage() {

                    @Override
                    public void createFormContents( IFormPageSite formSite ) {
                        new StyleLineUI( (StyleLine)fragment, formSite, context, site, imageInfoInContext,
                                colorInfoInContext );
                    }
                };
            }
            else if (fragment instanceof StylePolygon) {
                page = new DefaultFormPage() {

                    @Override
                    public void createFormContents( IFormPageSite formSite ) {
                        new StylePolygonUI( (StylePolygon)fragment, formSite, context, site, imageInfoInContext,
                                colorInfoInContext );
                    }
                };
            }
        }
        if (label != null && page != null) {
            BatikFormContainer pageContainer = new BatikFormContainer( page );
            styleContainers.put( label, createContentFunction( (MdToolkit)site.toolkit(), label, pageContainer ) );
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
        MdTabFolder tabFolder = tk.createTabFolder( parent, tabItems, tabContents );
        tabFolder.openTab( getLastOpenTab() );
        tabFolder.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                setLastOpenTab( ((Button)e.widget).getText() );
            }
        } );
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
    public void fillSLD( SLDBuilder builder, List<AbstractStyler> children ) {
        for (AbstractSLDModel sldFragment : sldFragments) {
            sldFragment.fillSLD( builder );

            // XXX mit result vereinen

        }
    }


    @Override
    public void fromSLD( StyledLayerDescriptor sld ) {
        sldFragments.stream().forEach( fragment -> fragment.fromSLD( sld ) );
    }


    @Override
    public void init( FeatureType schema ) {
        // TODO Auto-generated method stub

    }


    @Override
    public void submitUI() {
        // TODO Auto-generated method stub

    }


    @Override
    public void resetUI() {
        // TODO Auto-generated method stub

    }
}
