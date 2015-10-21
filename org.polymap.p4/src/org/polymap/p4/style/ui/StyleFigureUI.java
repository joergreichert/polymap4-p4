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

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.graphics.RGB;
import org.polymap.core.runtime.event.EventManager;
import org.polymap.p4.style.color.ColorInfo;
import org.polymap.p4.style.color.ColorPanel;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.entities.StyleFigure;
import org.polymap.p4.style.icon.FigureLibraryInitializer;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.icon.ImageHelper;
import org.polymap.p4.style.icon.ImageInfo;
import org.polymap.p4.style.icon.ImagePanel;
import org.polymap.p4.util.PropertyAdapter;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.CheckboxFormField;
import org.polymap.rhei.field.ColorFormField;
import org.polymap.rhei.field.EnablableFormField;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.field.IconFormField;
import org.polymap.rhei.field.ImageDescription;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleFigureUI
        implements IFormFieldListener {

    private final IAppContext         context;

    private final IPanelSite          panelSite;

    private String                    DEFAULT_ICON_PATH = "sld_wellknown/circle.svg";

    private IconFormField             figureFormField;

    private SpinnerFormField          markerSizeField;

    private EnablableFormField        backgroundFormEnabledField;

    private ColorFormField            backgroundFormField;

    private SpinnerFormField          markerTransparencyField;

    private SpinnerFormField          markerStrokeSizeField;

    private ColorFormField            markerStrokeColorField;

    private SpinnerFormField          markerStrokeTransparencyField;

    private final Context<IColorInfo> colorInfoInContext;

    private final Context<IImageInfo> imageInfoInContext;
    
    private final StyleFigure styleFigure;


    public StyleFigureUI( StyleFigure styleFigure, IFormPageSite site, IAppContext context, IPanelSite panelSite,
            Context<IImageInfo> imageInfoInContext, Context<IColorInfo> colorInfoInContext ) {
        this.context = context;
        this.panelSite = panelSite;
        this.colorInfoInContext = colorInfoInContext;
        this.imageInfoInContext = imageInfoInContext;
        this.styleFigure = styleFigure;

        ColorInfo colorInfo = new ColorInfo();
        colorInfoInContext.set( colorInfo );

        FigureLibraryInitializer figureLibraryInitializer = new FigureLibraryInitializer();

        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setImageLibrary( figureLibraryInitializer.getImageLibrary() );
        imageInfo.setPathToImageDescription( figureLibraryInitializer.getPathToImageDescription() );
        imageInfoInContext.set( imageInfo );

        EventManager.instance().subscribe( colorInfo, ev -> ev.getSource() instanceof IColorInfo );
        EventManager.instance().subscribe( imageInfo, ev -> ev.getSource() instanceof IImageInfo );

        createStyleFigureContent( styleFigure, site );
        site.addFieldListener( this );
    }


    private void createStyleFigureContent( StyleFigure styleFigure, IFormPageSite site ) {
        figureFormField = new IconFormField();
        if (imageInfoInContext.get().getImageDescription() != null) {
            figureFormField.setValue( imageInfoInContext.get().getImageDescription() );
        }
        else {
            ImageDescription imageDescription = new ImageHelper().createImageDescription( DEFAULT_ICON_PATH );
            figureFormField.setValue( imageDescription );
        }
        site.newFormField( new PropertyAdapter( styleFigure.markerWellKnownName ) ).label.put( "Marker figure" ).field.put( figureFormField ).tooltip
                .put( "" ).create();
        backgroundFormField = new ColorFormField();
        if (colorInfoInContext.get().getColor() != null) {
            backgroundFormField.setValue( colorInfoInContext.get().getColor() );
        }
        backgroundFormEnabledField = new EnablableFormField( new CheckboxFormField(), backgroundFormField );
        site.newFormField( new PropertyAdapter( styleFigure.markerFill ) ).label.put( "Marker fill" ).field
                .put( backgroundFormEnabledField ).tooltip.put( "" ).create();
        markerTransparencyField = new SpinnerFormField( 0, 1, 0.1, 1, 1 );
        site.newFormField( new PropertyAdapter( styleFigure.markerTransparency ) ).label
                .put( "Marker transparency" ).field.put( markerTransparencyField ).tooltip.put( "" ).create();
        markerStrokeSizeField = new SpinnerFormField( 0, 32, 1 );
        site.newFormField( new PropertyAdapter( styleFigure.markerStrokeSize ) ).label
                .put( "Marker border size" ).field.put( markerStrokeSizeField ).tooltip.put( "" ).create();
        markerStrokeColorField = new ColorFormField();
        site.newFormField( new PropertyAdapter( styleFigure.markerStrokeColor) ).label
                .put( "Marker border color" ).field.put( markerStrokeColorField ).tooltip.put( "" ).create();
        markerStrokeTransparencyField = new SpinnerFormField( 0, 1, 0.1, 1, 1 );
        site.newFormField( new PropertyAdapter( styleFigure.markerStrokeTransparency ) ).label
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
            if (ev.getSource() == figureFormField) {
                imageInfoInContext.get().setFormField( figureFormField );
                imageInfoInContext.get().setImageDescription( ev.getNewFieldValue() );
                final ServerPushSession pushSession = new ServerPushSession();
                pushSession.start();
                context.openPanel( panelSite.getPath(), ImagePanel.ID );
                pushSession.stop();
            }
            else if (ev.getSource() == backgroundFormEnabledField) {
                colorInfoInContext.get().setFormField( backgroundFormField );
                Object[] array = (Object[])ev.getNewFieldValue();
                if (array != null) {
                    if (array[0] == Boolean.TRUE) {
                        colorInfoInContext.get().setColor( (RGB)array[1] );
                        context.openPanel( panelSite.getPath(), ColorPanel.ID );
                    }
                    else {
                        styleFigure.markerFill.set( null );
                    }
                }
                else {
                    styleFigure.markerFill.set( null );
                }
            }
            else if (ev.getSource() == markerTransparencyField) {
                boolean markerIsVisible = ev.getNewFieldValue() instanceof Integer
                        && ((Integer)ev.getNewFieldValue()).intValue() > 0;
                markerSizeField.setEnabled( markerIsVisible );
//                iconFormField.setEnabled( markerIsVisible );
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
