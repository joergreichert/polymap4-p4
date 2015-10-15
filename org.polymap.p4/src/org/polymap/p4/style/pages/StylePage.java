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
package org.polymap.p4.style.pages;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.polymap.core.runtime.event.EventManager;
import org.polymap.core.ui.ColumnLayoutFactory;
import org.polymap.p4.style.StylerDAO;
import org.polymap.p4.style.color.ColorInfo;
import org.polymap.p4.style.color.ColorPanel;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.icon.IconLibraryInitializer;
import org.polymap.p4.style.icon.ImageHelper;
import org.polymap.p4.style.icon.ImageInfo;
import org.polymap.p4.style.icon.ImagePanel;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.BeanPropertyAdapter;
import org.polymap.rhei.field.CheckboxFormField;
import org.polymap.rhei.field.ColorFormField;
import org.polymap.rhei.field.EnablableFormField;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.field.IconFormField;
import org.polymap.rhei.field.ImageDescription;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.DefaultFormPage;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePage
        extends DefaultFormPage
        implements IFormFieldListener {

    private String                    DEFAULT_ICON_PATH = "sld_wellknown/circle.svg";

    private final IAppContext         context;

    private final IPanelSite          panelSite;

    private final StylerDAO           styleDao;

    private SpinnerFormField          markerSizeField;
    
    private IconFormField             iconFormField;

    private EnablableFormField        backgroundFormEnabledField;

    private ColorFormField            backgroundFormField;

    private SpinnerFormField          markerTransparencyField;

    private SpinnerFormField          markerStrokeSizeField;

    private ColorFormField            markerStrokeColorField;

    private SpinnerFormField          markerStrokeTransparencyField;

    private final Context<IImageInfo> imageInfoInContext;

    private final Context<IColorInfo> colorInfoInContext;


    public StylePage( IAppContext context, IPanelSite panelSite, StylerDAO styleDao,
            Context<IImageInfo> imageInfoInContext, Context<IColorInfo> colorInfoInContext ) {
        this.context = context;
        this.panelSite = panelSite;
        this.styleDao = styleDao;
        this.imageInfoInContext = imageInfoInContext;
        this.colorInfoInContext = colorInfoInContext;

        IconLibraryInitializer iconLibraryInitializer = new IconLibraryInitializer();

        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setImageLibrary( iconLibraryInitializer.getImageLibrary() );
        imageInfo.setPathToImageDescription(iconLibraryInitializer.getPathToImageDescription());
        imageInfoInContext.set( imageInfo );

        ColorInfo colorInfo = new ColorInfo();
        colorInfoInContext.set( colorInfo );

        EventManager.instance().subscribe( imageInfo, ev -> ev.getSource() instanceof IImageInfo );
        EventManager.instance().subscribe( colorInfo, ev -> ev.getSource() instanceof IColorInfo );
    }


    private IPanelSite getPanelSite() {
        return panelSite;
    }


    public StylerDAO getStyleDao() {
        return styleDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.rhei.form.DefaultFormPage#createFormContents(org.polymap.rhei.
     * form.IFormPageSite)
     */
    @Override
    public void createFormContents( IFormPageSite site ) {
        super.createFormContents( site );
        Composite parent = site.getPageBody();
        parent.setLayout( ColumnLayoutFactory.defaults().spacing( 5 )
                .margins( getPanelSite().getLayoutPreference().getSpacing() / 2 ).create() );
        createStyleTabItemForPoint( site );
        site.addFieldListener( this );
    }


    private void createStyleTabItemForPoint( IFormPageSite site ) {
        markerSizeField = new SpinnerFormField( 1, 128, 12 );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_SIZE ) ).label.put( "Marker size" ).field
                .put( markerSizeField ).tooltip.put( "" ).create();
        backgroundFormField = new ColorFormField();
        if (colorInfoInContext.get().getColor() != null) {
            backgroundFormField.setValue( colorInfoInContext.get().getColor() );
        }
        backgroundFormEnabledField = new EnablableFormField( new CheckboxFormField(),
                backgroundFormField );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_FILL ) ).label.put( "Marker fill" ).field
                .put( backgroundFormEnabledField ).tooltip.put( "" ).create();
        iconFormField = new IconFormField();
        if (imageInfoInContext.get().getImageDescription() != null) {
            iconFormField.setValue( imageInfoInContext.get().getImageDescription() );
        }
        else {
            ImageDescription imageDescription = new ImageHelper().createImageDescription( DEFAULT_ICON_PATH );
            iconFormField.setValue( imageDescription );
        }
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_ICON ) ).label.put( "Marker icon" ).field
                .put( iconFormField ).tooltip.put( "" ).create();
        markerTransparencyField = new SpinnerFormField( 0, 1, 0.1, 1, 1 );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_TRANSPARENCY ) ).label
                .put( "Marker transparency" ).field.put( markerTransparencyField ).tooltip.put( "" )
                .create();
        markerStrokeSizeField = new SpinnerFormField( 0, 32, 1 );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_STROKE_SIZE ) ).label
                .put( "Marker border size" ).field.put( markerStrokeSizeField ).tooltip.put( "" ).create();
        markerStrokeColorField = new ColorFormField();
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_STROKE_COLOR ) ).label
                .put( "Marker border color" ).field.put( markerStrokeColorField ).tooltip.put( "" ).create();
        markerStrokeTransparencyField = new SpinnerFormField( 0, 1, 0.1, 1, 1 );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_STROKE_TRANSPARENCY ) ).label
                .put( "Marker border transparency" ).field.put( markerStrokeTransparencyField ).tooltip.put( "" )
                .create();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.rhei.field.IFormFieldListener#fieldChange(org.polymap.rhei.field
     * .FormFieldEvent)
     */
    @Override
    public void fieldChange( FormFieldEvent ev ) {
        if (ev.getEventCode() == VALUE_CHANGE) {
            if (ev.getSource() == backgroundFormEnabledField) {
                colorInfoInContext.get().setFormField( backgroundFormField );
                Object [] array = (Object []) ev.getNewFieldValue();
                if(array != null) {
                    if(array[0] == Boolean.TRUE) {
                        colorInfoInContext.get().setColor( (RGB) array[1] );
                        context.openPanel( panelSite.getPath(), ColorPanel.ID );
                    } else {
                        styleDao.setMarkerFill( null );
                    }
                } else {
                    styleDao.setMarkerFill( null );
                }
            }
            else if (ev.getSource() == iconFormField) {
                imageInfoInContext.get().setFormField( iconFormField );
                imageInfoInContext.get().setImageDescription( ev.getNewFieldValue() );
                final ServerPushSession pushSession = new ServerPushSession();
                pushSession.start();
                context.openPanel( panelSite.getPath(), ImagePanel.ID );
                pushSession.stop();
            }
            else if (ev.getSource() == markerTransparencyField) {
                boolean markerIsVisible = ev.getNewFieldValue() instanceof Integer
                        && ((Integer)ev.getNewFieldValue()).intValue() > 0;
                markerSizeField.setEnabled( markerIsVisible );
                iconFormField.setEnabled( markerIsVisible );
                backgroundFormEnabledField.setEnabled( markerIsVisible );
            }
            else if (ev.getSource() == markerStrokeSizeField) {
                boolean strokeIsVisible = ev.getNewFieldValue() instanceof Integer
                        && ((Integer)ev.getNewFieldValue()).intValue() > 0;
                markerStrokeColorField.setEnabled( strokeIsVisible );
                markerStrokeTransparencyField.setEnabled( strokeIsVisible );
            }
            else if (ev.getSource() == markerStrokeTransparencyField) {
                boolean strokeIsVisible = ev.getNewFieldValue() instanceof Integer
                        && ((Integer)ev.getNewFieldValue()).intValue() > 0;
                markerStrokeSizeField.setEnabled( strokeIsVisible );
                markerStrokeColorField.setEnabled( strokeIsVisible );
            }
            else if (ev.getSource() == markerStrokeColorField) {
                colorInfoInContext.get().setFormField( markerStrokeColorField );
                colorInfoInContext.get().setColor( ev.getNewFieldValue() );
                context.openPanel( panelSite.getPath(), ColorPanel.ID );
            }
        }
    }
}
