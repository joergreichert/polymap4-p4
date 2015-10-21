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

import org.eclipse.swt.widgets.Composite;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.entities.StylePolygon;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePolygonUI
        extends AbstractStylerFragmentUI {

    private final IAppContext         context;

    private final IPanelSite          panelSite;

    private final Context<IImageInfo> imageInfoInContext;

    private final Context<IColorInfo> colorInfoInContext;

    private StylePolygon              stylePolygon = null;


    public StylePolygonUI( IAppContext context, IPanelSite panelSite, Context<IImageInfo> imageInfoInContext,
            Context<IColorInfo> colorInfoInContext ) {
        this.context = context;
        this.panelSite = panelSite;
        this.imageInfoInContext = imageInfoInContext;
        this.colorInfoInContext = colorInfoInContext;
    }


    public void setModel( StylePolygon stylePolygon ) {
        this.stylePolygon = stylePolygon;
    }


    @Override
    public Composite createContents( IFormPageSite site ) {
        // TODO Auto-generated method stub
        return null;
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
