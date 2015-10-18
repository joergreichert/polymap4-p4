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

import static org.polymap.rhei.batik.toolkit.md.dp.dp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.polymap.core.runtime.Callback;
import org.polymap.core.ui.FormDataFactory;
import org.polymap.core.ui.FormLayoutFactory;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.map.ProjectMapPanel;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.daos.SLDBuilder;
import org.polymap.p4.style.daos.StyleIdentDao;
import org.polymap.p4.style.daos.StyleLabelDao;
import org.polymap.p4.style.daos.StylePointDao;
import org.polymap.p4.style.daos.StylePolygonDao;
import org.polymap.p4.style.daos.StylePolylineDao;
import org.polymap.p4.style.font.IFontInfo;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.pages.AbstractStylePage;
import org.polymap.p4.style.pages.LabelPage;
import org.polymap.p4.style.pages.StyleIdentPage;
import org.polymap.p4.style.pages.StylePointPage;
import org.polymap.p4.style.pages.StylePolygonPage;
import org.polymap.p4.style.pages.StylePolylinePage;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.DefaultPanel;
import org.polymap.rhei.batik.PanelIdentifier;
import org.polymap.rhei.batik.Scope;
import org.polymap.rhei.batik.toolkit.md.AbstractFeedbackComponent;
import org.polymap.rhei.batik.toolkit.md.MdTabFolder;
import org.polymap.rhei.batik.toolkit.md.MdToast;
import org.polymap.rhei.batik.toolkit.md.MdToolkit;
import org.polymap.rhei.form.batik.BatikFormContainer;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerPanel
        extends DefaultPanel {

    public static final PanelIdentifier ID              = PanelIdentifier.parse( "styler" );

    private BatikFormContainer          styleIdentPageContainer;

    private BatikFormContainer          labelPageContainer;

    private BatikFormContainer          stylePageContainer;

    @Scope(P4Plugin.Scope)
    private Context<IImageInfo>         imageInfo;

    @Scope(P4Plugin.Scope)
    private Context<IColorInfo>         colorInfo;

    @Scope(P4Plugin.Scope)
    private Context<IFontInfo>          fontInfo;

    private String                      lastOpenTab     = null;

    private MdToast                     mdToast;

    private List<BatikFormContainer>    styleContainers = new ArrayList<BatikFormContainer>();

    private List<AbstractStylePage<?>>  stylePages      = new ArrayList<AbstractStylePage<?>>();


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
        parent.setLayout( FormLayoutFactory.defaults().spacing( dp( 16 ).pix() ).create() );

        mdToast = ((MdToolkit)getSite().toolkit()).createToast( 60, SWT.NONE );

        StyleIdentPage identPage = new StyleIdentPage( getContext(), getSite() );
        styleIdentPageContainer = new BatikFormContainer( identPage );
        LabelPage labelPage = new LabelPage( getContext(), getSite(), fontInfo );
        labelPageContainer = new BatikFormContainer( labelPage );
        StylePointPage stylePointPage = new StylePointPage( getContext(), getSite(), imageInfo, colorInfo );
        StylePolylinePage stylePolylinePage = new StylePolylinePage( getContext(), getSite(), imageInfo, colorInfo );
        StylePolygonPage stylePolygonPage = new StylePolygonPage( getContext(), getSite(), imageInfo, colorInfo );
        stylePageContainer = new BatikFormContainer( stylePointPage );

        styleContainers.add( styleIdentPageContainer );
        styleContainers.add( labelPageContainer );
        styleContainers.add( stylePageContainer );

        stylePages.add( identPage );
        stylePages.add( labelPage );
        stylePages.add( stylePointPage );
        stylePages.add( stylePolylinePage );
        stylePages.add( stylePolygonPage );

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
        FormDataFactory.on( tabFolder ).left( 0 ).right( 100 );

        Supplier<Boolean> newCallback = ( ) -> {
            stylePages.stream().forEach( page -> page.createEmptyDao() );
            reloadAllEditors();
            return true;
        };
        Supplier<StyledLayerDescriptor> saveSupplier = ( ) -> {
            try {
                submit();
                StyledLayerDescriptorBuilder wrappedBuilder = new StyledLayerDescriptorBuilder();
                SLDBuilder builder = new SLDBuilder(wrappedBuilder);
                stylePages.stream().forEach( page -> page.getDao().fillSLD( builder ) );
                return builder.build();
            }
            catch (Exception exc) {
                mdToast.showIssue( AbstractFeedbackComponent.MessageType.ERROR, exc.getMessage() );
            }
            return null;
        };
        Callback<StyledLayerDescriptor> loadCallback = ( StyledLayerDescriptor sld ) -> {
            for (AbstractStylePage<?> stylePage : stylePages) {
                if (stylePage.getDao() instanceof StyleIdentDao) {
                    StyleIdentDao newDao = new StyleIdentDao( sld );
                    ((StyleIdentPage)stylePage).setDao( newDao );
                }
                else if (stylePage.getDao() instanceof StyleLabelDao) {
                    StyleLabelDao newDao = new StyleLabelDao( sld );
                    ((LabelPage)stylePage).setDao( newDao );
                }
                else if (stylePage.getDao() instanceof StylePointDao) {
                    StylePointDao newDao = new StylePointDao( sld );
                    ((StylePointPage)stylePage).setDao( newDao );
                }
                else if (stylePage.getDao() instanceof StylePolylineDao) {
                    StylePolylineDao newDao = new StylePolylineDao( sld );
                    ((StylePolylinePage)stylePage).setDao( newDao );
                }
                else if (stylePage.getDao() instanceof StylePolygonDao) {
                    StylePolygonDao newDao = new StylePolygonDao( sld );
                    ((StylePolygonPage)stylePage).setDao( newDao );
                }
            }
        };
        Supplier<Boolean> deleteCallback = ( ) -> {
            stylePages.stream().forEach( page -> page.createEmptyDao() );
            reloadAllEditors();
            return true;
        };
        StylerList stylerList = new StylerList( parent, tk, SWT.NONE, newCallback, saveSupplier, loadCallback,
                deleteCallback );
        FormDataFactory.on( stylerList ).fill().top( tabFolder, dp( 30 ).pix() );

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
        stylePageContainer.createContents( styleComposite );
        return styleComposite;
    }


    private void submit() throws Exception {
        try {
            for (BatikFormContainer styleContainer : styleContainers) {
                styleContainer.submit();
            }
        }
        catch (Exception exc) {
            mdToast.showIssue( AbstractFeedbackComponent.MessageType.ERROR, exc.getMessage() );
        }
    }


    private void reloadAllEditors() {
        try {
            for (BatikFormContainer styleContainer : styleContainers) {
                styleContainer.reloadEditor();
            }
        }
        catch (Exception exc) {
            mdToast.showIssue( AbstractFeedbackComponent.MessageType.ERROR, exc.getMessage() );
        }
    }
}
