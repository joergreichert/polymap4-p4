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
import org.polymap.p4.style.entities.StyleLabelLinePlacement;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.CheckboxFormField;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelLinePlacementUI
        extends AbstractStylerFragmentUI
        implements IFormFieldListener {

    private final IAppContext       context;

    private final IPanelSite        panelSite;

    // http://docs.geoserver.org/stable/en/user/styling/sld-reference/labeling.html#perpendicularoffset
    private SpinnerFormField        labelPerpendicularOffsetFormField;

    // GeoServer extension
    // http://docs.geoserver.org/stable/en/user/styling/sld-reference/labeling.html#followline
    private CheckboxFormField       followLine;

    // GeoServer extension
    // requires followLine set to true
    private SpinnerFormField        maxAngleDelta;

    // GeoServer extension
    // http://docs.geoserver.org/stable/en/user/styling/sld-reference/labeling.html#repeat
    private SpinnerFormField        repeat;

    // http://docs.geoserver.org/stable/en/user/styling/sld-reference/labeling.html#maxdisplacement
    private SpinnerFormField         maxDisplacement;

    private StyleLabelLinePlacement styleLabelLinePlacement = null;


    public StyleLabelLinePlacementUI( IAppContext context, IPanelSite panelSite ) {
        this.context = context;
        this.panelSite = panelSite;
    }


    public void setModel( StyleLabelLinePlacement styleLabelLinePlacement ) {
        this.styleLabelLinePlacement = styleLabelLinePlacement;
    }


    @Override
    public Composite createContents( IFormPageSite site ) {
        Composite parent = site.getPageBody();
        parent.setLayout( ColumnLayoutFactory.defaults().spacing( 5 )
                .margins( panelSite.getLayoutPreference().getSpacing() / 2 ).create() );
        // TODO
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


    @Override
    public void fieldChange( FormFieldEvent ev ) {
        if (ev.getEventCode() == VALUE_CHANGE) {
        }
    }
}
