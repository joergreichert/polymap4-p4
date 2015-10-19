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

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.polymap.core.runtime.event.EventManager;
import org.polymap.core.ui.ColumnLayoutFactory;
import org.polymap.p4.style.color.ColorInfo;
import org.polymap.p4.style.color.ColorPanel;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.daos.StylePolylineDao;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.BeanPropertyAdapter;
import org.polymap.rhei.field.ColorFormField;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.IFormPageSite;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePolylinePage
        extends AbstractStylePage<StylePolylineDao> {

    private SpinnerFormField          lineWidthField;

    private ColorFormField            colorFormField;

    private SpinnerFormField          lineTransparencyField;

    private SpinnerFormField          lineStrokeWidthField;

    private ColorFormField            lineStrokeColorField;

    private SpinnerFormField          lineStrokeTransparencyField;

    private final Context<IColorInfo> colorInfoInContext;


    public StylePolylinePage( IAppContext context, IPanelSite panelSite, Context<IImageInfo> imageInfoInContext,
            Context<IColorInfo> colorInfoInContext ) {
        super( context, panelSite );
        this.colorInfoInContext = colorInfoInContext;

        ColorInfo colorInfo = new ColorInfo();
        colorInfoInContext.set( colorInfo );

        EventManager.instance().subscribe( colorInfo, ev -> ev.getSource() instanceof IColorInfo );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.style.pages.AbstractStylePage#createEmptyDao()
     */
    @Override
    public StylePolylineDao createEmptyDao() {
        return new StylePolylineDao();
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
        createStyleTabItemForPolyline( site );
        site.addFieldListener( this );
    }


    private void createStyleTabItemForPolyline( IFormPageSite site ) {
        lineWidthField = new SpinnerFormField( 1, 128, 12 );
        site.newFormField( new BeanPropertyAdapter( getDao(), StylePolylineDao.LINE_WIDTH ) ).label.put( "Line width" ).field
                .put( lineWidthField ).tooltip.put( "" ).create();
        colorFormField = new ColorFormField();
        if (colorInfoInContext.get().getColor() != null) {
            colorFormField.setValue( colorInfoInContext.get().getColor() );
        }
        lineTransparencyField = new SpinnerFormField( 0, 1, 0.1, 1, 1 );
        site.newFormField( new BeanPropertyAdapter( getDao(), StylePolylineDao.LINE_TRANSPARENCY ) ).label
                .put( "Line transparency" ).field.put( lineTransparencyField ).tooltip.put( "" ).create();
        lineStrokeWidthField = new SpinnerFormField( 0, 32, 1 );
        site.newFormField( new BeanPropertyAdapter( getDao(), StylePolylineDao.LINE_STROKE_WIDTH ) ).label
                .put( "Line border width" ).field.put( lineStrokeWidthField ).tooltip.put( "" ).create();
        lineStrokeColorField = new ColorFormField();
        site.newFormField( new BeanPropertyAdapter( getDao(), StylePolylineDao.LINE_STROKE_COLOR ) ).label
                .put( "Line border color" ).field.put( lineStrokeColorField ).tooltip.put( "" ).create();
        lineStrokeTransparencyField = new SpinnerFormField( 0, 1, 0.1, 1, 1 );
        site.newFormField( new BeanPropertyAdapter( getDao(), StylePolylineDao.LINE_STROKE_TRANSPARENCY ) ).label
                .put( "Line border transparency" ).field.put( lineStrokeTransparencyField ).tooltip.put( "" ).create();
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
            if (ev.getSource() == colorFormField) {
                colorInfoInContext.get().setFormField( colorFormField );
                Object[] array = (Object[])ev.getNewFieldValue();
                if (array != null) {
                    if (array[0] == Boolean.TRUE) {
                        colorInfoInContext.get().setColor( (RGB)array[1] );
                        getContext().openPanel( getPanelSite().getPath(), ColorPanel.ID );
                    }
                    else {
                        getDao().setLineColor( null );
                    }
                }
                else {
                    getDao().setLineColor( null );
                }
            }
            else if (ev.getSource() == lineTransparencyField) {
                boolean lineIsVisible = ev.getNewFieldValue() instanceof Integer
                        && ((Integer)ev.getNewFieldValue()).intValue() > 0;
                lineWidthField.setEnabled( lineIsVisible );
                colorFormField.setEnabled( lineIsVisible );
            }
            else if (ev.getSource() == lineStrokeWidthField) {
                boolean strokeIsVisible = ev.getNewFieldValue() instanceof Integer
                        && ((Integer)ev.getNewFieldValue()).intValue() > 0;
                lineStrokeColorField.setEnabled( strokeIsVisible );
                lineStrokeTransparencyField.setEnabled( strokeIsVisible );
            }
            else if (ev.getSource() == lineStrokeTransparencyField) {
                boolean strokeIsVisible = ev.getNewFieldValue() instanceof Integer
                        && ((Integer)ev.getNewFieldValue()).intValue() > 0;
                lineStrokeWidthField.setEnabled( strokeIsVisible );
                lineStrokeColorField.setEnabled( strokeIsVisible );
            }
            else if (ev.getSource() == lineStrokeColorField) {
                colorInfoInContext.get().setFormField( lineStrokeColorField );
                colorInfoInContext.get().setColor( ev.getNewFieldValue() );
                getContext().openPanel( getPanelSite().getPath(), ColorPanel.ID );
            }
        }
    }
}
