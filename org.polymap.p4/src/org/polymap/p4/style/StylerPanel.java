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

import java.io.IOException;
import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.polymap.core.runtime.Callback;
import org.polymap.core.ui.FormDataFactory;
import org.polymap.core.ui.FormLayoutFactory;
import org.polymap.model2.runtime.EntityRepository;
import org.polymap.model2.runtime.UnitOfWork;
import org.polymap.model2.runtime.ValueInitializer;
import org.polymap.model2.store.recordstore.RecordStoreAdapter;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.map.ProjectMapPanel;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.entities.FeatureType;
import org.polymap.p4.style.font.IFontInfo;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.ui.SimpleStylerUI;
import org.polymap.recordstore.lucene.LuceneRecordStore;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.DefaultPanel;
import org.polymap.rhei.batik.PanelIdentifier;
import org.polymap.rhei.batik.Scope;
import org.polymap.rhei.batik.toolkit.md.AbstractFeedbackComponent;
import org.polymap.rhei.batik.toolkit.md.MdToast;
import org.polymap.rhei.batik.toolkit.md.MdToolkit;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerPanel
        extends DefaultPanel {

    public static final PanelIdentifier ID = PanelIdentifier.parse( "styler" );

    @Scope(P4Plugin.Scope)
    private Context<IImageInfo>         imageInfo;

    @Scope(P4Plugin.Scope)
    private Context<IColorInfo>         colorInfo;

    @Scope(P4Plugin.Scope)
    private Context<IFontInfo>          fontInfo;

    private MdToast                     mdToast;

    private SimpleStyler                simpleStyler;


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

        try {
            simpleStyler = createEmptySimpleStyler();
            internalCreateContents( parent );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    protected SimpleStyler createEmptySimpleStyler() throws IOException {
        LuceneRecordStore store = new LuceneRecordStore();
        EntityRepository entityRepository = EntityRepository.newConfiguration().store.set( new RecordStoreAdapter(
                store ) ).entities.set( new Class[] { SimpleStyler.class } ).create();
        UnitOfWork unitOfWork = entityRepository.newUnitOfWork();
        ValueInitializer<SimpleStyler> init = ( styler ) -> styler;
        SimpleStyler simpleStyler = unitOfWork.createEntity( SimpleStyler.class, null, init );
        simpleStyler.styleIdent.createValue( styleIdent -> {
            styleIdent.featureType.set( FeatureType.POINT );
            return styleIdent;
        } );
        simpleStyler.styleLabel.createValue( null );
        simpleStyler.stylePoint.createValue( null );
        simpleStyler.styleLine.createValue( null );
        simpleStyler.stylePolygon.createValue( null );
        return simpleStyler;
    }


    private void internalCreateContents( Composite parent ) {
        MdToolkit tk = (MdToolkit)getSite().toolkit();
        SimpleStylerUI simpleStylerUI = new SimpleStylerUI( getContext(), getSite(), imageInfo, colorInfo, fontInfo );
        simpleStylerUI.setModel( simpleStyler );
        Composite stylerComposite = simpleStylerUI.createContents( parent );
        FormDataFactory.on( stylerComposite ).left( 0 ).right( 100 );

        Supplier<Boolean> newCallback = ( ) -> {
            simpleStylerUI.resetUI();
            return true;
        };
        Supplier<SimpleStyler> saveSupplier = ( ) -> {
            try {
                simpleStylerUI.submitUI();
                return simpleStyler;
            }
            catch (Exception exc) {
                mdToast.showIssue( AbstractFeedbackComponent.MessageType.ERROR, exc.getMessage() );
            }
            return null;
        };
        Callback<SimpleStyler> loadCallback = ( SimpleStyler newStyler ) -> {
            simpleStyler = newStyler;
            simpleStylerUI.setModel( simpleStyler );
        };
        Supplier<Boolean> deleteCallback = ( ) -> {
            simpleStylerUI.resetUI();
            return true;
        };
        Supplier<SimpleStyler> createNewSimpleStylerCallback = ( ) -> {
            simpleStylerUI.resetUI();
            return simpleStyler;
        };
        StylerList stylerList = new StylerList( parent, tk, SWT.NONE, newCallback, saveSupplier, loadCallback,
                deleteCallback, createNewSimpleStylerCallback );
        FormDataFactory.on( stylerList ).fill().top( stylerComposite, dp( 30 ).pix() );

        // try {
        // new StylePreview().createPreviewMap( parent, getStylerDao() );
        // }
        // catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }
}
