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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;
import org.polymap.core.ui.ColumnLayoutFactory;
import org.polymap.p4.style.StylerDAO;
import org.polymap.p4.style.StylerDAO.FeatureType;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.BeanPropertyAdapter;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.field.PicklistFormField;
import org.polymap.rhei.field.StringFormField;
import org.polymap.rhei.form.DefaultFormPage;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleIdentPage
        extends DefaultFormPage
        implements IFormFieldListener {

    private final IPanelSite panelSite;

    private final StylerDAO  styleDao;


    public StyleIdentPage( IPanelSite panelSite, StylerDAO styleDao ) {
        this.panelSite = panelSite;
        this.styleDao = styleDao;
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
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.USER_STYLE_NAME ) ).label
                .put( "Style name" ).field.put( new StringFormField() ).tooltip.put( "" ).create();
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.USER_STYLE_TITLE ) ).label
                .put( "Style title" ).field.put( new StringFormField() ).tooltip.put( "" ).create();
        List<String> featureTypes = Arrays.asList( FeatureType.values() ).stream().map( value -> value.name() )
                .collect( Collectors.toList() );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.FEATURE_TYPE ) ).label
                .put( "Feature type" ).field.put( new PicklistFormField( featureTypes ) ).tooltip.put( "" ).create();
        site.addFieldListener( this );
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
        // if (ev.getEventCode() == VALUE_CHANGE) {
        // Button okBtn = getButton( IDialogConstants.OK_ID );
        // if (okBtn != null) {
        // okBtn.setEnabled( pageContainer.isValid() && pageContainer.isValid() );
        // }
        // }
    }
}
