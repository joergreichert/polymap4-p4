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
import org.polymap.core.ui.ColumnLayoutFactory;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.util.PropertyAdapter;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.ColorFormField;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelHaloUI
        extends AbstractStylerFragmentUI {

    private final IAppContext context;

    private final IPanelSite  panelSite;

    private SpinnerFormField  labelHaloRadiusFormField;

    private ColorFormField    labelHaloFillFormField;

    private StyleLabel        styleLabel = null;


    public StyleLabelHaloUI( IAppContext context, IPanelSite panelSite ) {
        this.context = context;
        this.panelSite = panelSite;
    }


    public void setModel( StyleLabel styleLabel ) {
        this.styleLabel = styleLabel;
    }


    @Override
    public Composite createContents( IFormPageSite site ) {
        Composite parent = site.getPageBody();
        parent.setLayout( ColumnLayoutFactory.defaults().spacing( 5 )
                .margins( panelSite.getLayoutPreference().getSpacing() / 2 ).create() );
        labelHaloRadiusFormField = new SpinnerFormField( 0, 128, 10 );
        site.newFormField( new PropertyAdapter( styleLabel.haloRadius ) ).label.put( "Label halo radius" ).field
                .put( labelHaloRadiusFormField ).tooltip.put(
                "The radius around the label to halo to improve readability" ).create();
        labelHaloFillFormField = new ColorFormField();
        site.newFormField( new PropertyAdapter( styleLabel.haloFill ) ).label.put( "Label halo fill" ).field
                .put( labelHaloFillFormField ).tooltip.put( "The halo fill color" ).create();
        return site.getPageBody();
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
