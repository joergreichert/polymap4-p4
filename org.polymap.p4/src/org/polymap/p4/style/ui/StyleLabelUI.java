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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.polymap.core.runtime.event.EventManager;
import org.polymap.core.ui.ColumnLayoutFactory;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.style.font.FontInfo;
import org.polymap.p4.style.font.FontPanel;
import org.polymap.p4.style.font.IFontInfo;
import org.polymap.p4.util.PropertyAdapter;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.FontFormField;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.IFormField;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.field.PicklistFormField;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.IFormPageSite;

import com.google.common.collect.Sets;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelUI
        extends AbstractStylerFragmentUI
        implements IFormFieldListener {

    private final IAppContext        context;

    private final IPanelSite         panelSite;

    private PicklistFormField        labelTextField;

    private FontFormField            fontFormField;

    // GeoServer extension
    // http://docs.geoserver.org/stable/en/user/styling/sld-reference/labeling.html#autowrap
    private SpinnerFormField         autoWrap;

    private final Context<IFontInfo> fontInfoInContext;

    private StyleLabel               styleLabel = null;

    private final List<IFormField>   formFields;


    public StyleLabelUI( IAppContext context, IPanelSite panelSite, Context<IFontInfo> fontInfoInContext ) {
        this.context = context;
        this.panelSite = panelSite;
        this.fontInfoInContext = fontInfoInContext;

        FontInfo fontInfo = new FontInfo();
        fontInfoInContext.set( fontInfo );

        formFields = new ArrayList<IFormField>();
        formFields.add(fontFormField);
        formFields.add(autoWrap);

        EventManager.instance().subscribe( fontInfo, ev -> ev.getSource() instanceof IFontInfo );
    }


    public void setModel( StyleLabel styleLabel ) {
        this.styleLabel = styleLabel;
    }


    @Override
    public Composite createContents( IFormPageSite site ) {
        Composite parent = site.getPageBody();
        parent.setLayout( ColumnLayoutFactory.defaults().spacing( 5 )
                .margins( panelSite.getLayoutPreference().getSpacing() / 2 ).create() );
        HashSet<String> labels = Sets.newHashSet( "", "<Placeholder>" );
        labelTextField = new PicklistFormField( labels );
        site.newFormField( new PropertyAdapter( styleLabel.labelText ) ).label.put( "Label text" ).field
                .put( labelTextField ).tooltip.put(
                "The actual label value can be assigned later when "
                        + "combining this style with a layer resp. a feature type of that layer. "
                        + "If you don't want to use labels, just leave this field blank." ).create();
        fontFormField = new FontFormField();
        fontFormField.setEnabled( false );
        site.newFormField( new PropertyAdapter( styleLabel.labelFont ) ).label.put( "Label font" ).field
                .put( fontFormField ).tooltip.put( "" ).create();

        // TODO
        // line placement
        // point placement
        // halo

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
            if (ev.getSource() == labelTextField) {
                boolean newValueNotEmpty = !StringUtils.isEmpty( ev.getNewFieldValue() );
                fontFormField.setEnabled( newValueNotEmpty );
                // labelOffsetFormField.setEnabled( newValueNotEmpty );
            }
            else if (ev.getSource() == fontFormField) {
                fontInfoInContext.get().setFormField( fontFormField );
                Object[] array = (Object[])ev.getNewFieldValue();
                if (array != null && array.length == 2) {
                    fontInfoInContext.get().setFontData( new FontData[] { (FontData)array[0] } );
                    fontInfoInContext.get().setColor( (RGB)array[1] );
                }
                context.openPanel( panelSite.getPath(), FontPanel.ID );
            }
        }
    }

}
