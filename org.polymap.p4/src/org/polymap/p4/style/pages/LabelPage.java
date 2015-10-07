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

import java.util.HashSet;

import org.eclipse.swt.widgets.Composite;
import org.polymap.core.ui.ColumnLayoutFactory;
import org.polymap.p4.style.StylerDAO;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.BeanPropertyAdapter;
import org.polymap.rhei.field.FontFormField;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.field.PicklistFormField;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.DefaultFormPage;
import org.polymap.rhei.form.IFormPageSite;

import com.google.common.collect.Sets;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class LabelPage
        extends DefaultFormPage
        implements IFormFieldListener {

    private final IPanelSite  panelSite;

    private final StylerDAO   styleDao;

    private PicklistFormField labelTextField;

    private FontFormField     fontFormField;

    private SpinnerFormField  spinnerFormField;


    public LabelPage( IPanelSite panelSite, StylerDAO styleDao ) {
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
        createLabelTabItemContent( site );
        site.addFieldListener( this );

        // init button state after fields and dialog have been initialized
        // UIUtils.sessionDisplay().asyncExec( new Runnable() {
        // public void run() {
        // getButton( IDialogConstants.OK_ID ).setEnabled( pageContainer.isValid() &&
        // pageContainer.isValid() );
        // }
        // });
    }


    private void createLabelTabItemContent( IFormPageSite site ) {
        HashSet<String> labels = Sets.newHashSet( "", "<Placeholder>" );
        labelTextField = new PicklistFormField( labels );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.LABEL_TEXT ) ).label.put( "Label text" ).field
                .put( labelTextField ).tooltip.put(
                "The actual label value can be assigned later when "
                        + "combining this style with a layer resp. a feature type of that layer. "
                        + "If you don't want to use labels, just leave this field blank." ).create();
        fontFormField = new FontFormField();
        fontFormField.setEnabled( false );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.LABEL_FONT_DATA ) ).label
                .put( "Label font" ).field.put( fontFormField ).tooltip.put( "" ).create();
        spinnerFormField = new SpinnerFormField( -128, 128, -16 );
        spinnerFormField.setEnabled( false );
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.LABEL_OFFSET ) ).label
                .put( "Label offset" ).field.put( spinnerFormField ).tooltip.put( "" ).create();
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
            if (ev.getSource() == labelTextField) {
                boolean newValueNotNull = ev.getNewFieldValue() != null;
                fontFormField.setEnabled( newValueNotNull );
                spinnerFormField.setEnabled( newValueNotNull );
            }
            // Button okBtn = getButton( IDialogConstants.OK_ID );
            // if (okBtn != null) {
            // okBtn.setEnabled( pageContainer.isValid() && pageContainer.isValid()
            // );
            // }
        }
    }
}
