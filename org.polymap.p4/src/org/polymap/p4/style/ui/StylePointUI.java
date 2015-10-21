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

import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.entities.StyleFigure;
import org.polymap.p4.style.entities.StyleImage;
import org.polymap.p4.style.entities.StylePoint;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.util.PropertyAdapter;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePointUI {

    private final IAppContext         context;

    private final IPanelSite          panelSite;

    private SpinnerFormField          markerSizeField;

    private SpinnerFormField          markerRotationFormField;

    private final Context<IImageInfo> imageInfoInContext;

    private final Context<IColorInfo> colorInfoInContext;


    public StylePointUI( StylePoint stylePoint, IFormPageSite site, IAppContext context, IPanelSite panelSite,
            Context<IImageInfo> imageInfoInContext, Context<IColorInfo> colorInfoInContext ) {
        this.context = context;
        this.panelSite = panelSite;
        this.imageInfoInContext = imageInfoInContext;
        this.colorInfoInContext = colorInfoInContext;

        createStylePointContent( stylePoint, site );
    }


    private void createStylePointContent( StylePoint stylePoint, IFormPageSite site ) {
        markerSizeField = new SpinnerFormField( 1, 128, 12 );
        site.newFormField( new PropertyAdapter( stylePoint.markerSize ) ).label.put( "Marker size" ).field
                .put( markerSizeField ).tooltip.put( "" ).create();
        if (stylePoint.markerGraphic.get() instanceof StyleFigure) {
            new StyleFigureUI( (StyleFigure)stylePoint.markerGraphic.get(), site, context, panelSite,
                    imageInfoInContext, colorInfoInContext );
        }
        else if (stylePoint.markerGraphic.get() instanceof StyleImage) {
            new StyleImageUI( (StyleImage)stylePoint.markerGraphic.get(), site, context, panelSite, imageInfoInContext );
        }
        markerRotationFormField = new SpinnerFormField( -360, 360, 0 );
        markerRotationFormField.setEnabled( false );
        site.newFormField( new PropertyAdapter( stylePoint.markerRotation ) ).label.put( "Marker rotation" ).field
                .put( markerRotationFormField ).tooltip.put( "" ).create();
    }
}
