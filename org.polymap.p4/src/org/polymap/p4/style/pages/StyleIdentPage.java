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

import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;
import org.polymap.core.ui.ColumnLayoutFactory;
import org.polymap.p4.style.daos.StyleIdentDao;
import org.polymap.p4.style.daos.StyleIdentDao.FeatureType;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.BeanPropertyAdapter;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.NotEmptyValidator;
import org.polymap.rhei.field.PicklistFormField;
import org.polymap.rhei.field.StringFormField;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleIdentPage
        extends AbstractStylePage<StyleIdentDao> {

    public StyleIdentPage( IAppContext context, IPanelSite panelSite) {
        super( context, panelSite);
    }

    /* (non-Javadoc)
     * @see org.polymap.p4.style.pages.AbstractStylePage#createEmptyDao()
     */
    @Override
    public StyleIdentDao createEmptyDao() {
        return new StyleIdentDao();
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
        site.newFormField( new BeanPropertyAdapter( getDao(), StyleIdentDao.NAME ) ).label.put( "Style name" ).field
                .put( new StringFormField() ).tooltip.put( "" ).validator.put( new NotEmptyValidator<String,String>() )
                .create();
        site.newFormField( new BeanPropertyAdapter( getDao(), StyleIdentDao.TITLE ) ).label
                .put( "Style title" ).field.put( new StringFormField() ).tooltip.put( "" ).create();

        List<String> orderedLabel = FeatureType.getOrdered().stream().map( value -> value.getLabel() )
                .collect( Collectors.toList() );
        Comparator<String> comparator = ( String ft1Label, String ft2Label ) -> Integer.valueOf(
                orderedLabel.indexOf( ft1Label ) ).compareTo( orderedLabel.indexOf( ft2Label ) );
        SortedMap<String,Object> orderFeatureTypes = new TreeMap<String,Object>( comparator );
        FeatureType.getOrdered().stream().forEach( value -> orderFeatureTypes.put( value.getLabel(), value ) );
        PicklistFormField picklistFormField = new PicklistFormField( ( ) -> orderFeatureTypes );
        if (getDao().getFeatureType() == null) {
            getDao().setFeatureType( FeatureType.POINT );
        }
        site.newFormField( new BeanPropertyAdapter( getDao(), StyleIdentDao.FEATURE_TYPE ) ).label
                .put( "Feature type" ).field.put( picklistFormField ).tooltip.put( "" ).create();
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
    }
}
