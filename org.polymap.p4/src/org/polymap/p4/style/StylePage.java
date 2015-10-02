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
package org.polymap.p4.style;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.polymap.core.ui.ColumnLayoutFactory;
import org.polymap.p4.P4Plugin;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.batik.app.SvgImageRegistryHelper;
import org.polymap.rhei.field.BeanPropertyAdapter;
import org.polymap.rhei.field.ColorFormField;
import org.polymap.rhei.field.FormFieldEvent;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.field.IconFormField;
import org.polymap.rhei.field.SpinnerFormField;
import org.polymap.rhei.form.DefaultFormPage;
import org.polymap.rhei.form.IFormPageSite;

import com.google.common.collect.Lists;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylePage
        extends DefaultFormPage
        implements IFormFieldListener {

    private final IPanelSite                                                     panelSite;

    private final StylerDAO                                                      styleDao;

    private final SortedMap<Pair<String,String>,List<Supplier<ImageDescriptor>>> imageLibrary;


    public StylePage( IPanelSite panelSite, StylerDAO styleDao ) {
        this.panelSite = panelSite;
        this.styleDao = styleDao;

        List<String> names = Lists.newArrayList( "Simple icon places", "Pin of maps", "Maki icons" );

        Comparator<Pair<String,String>> comparator = ( Pair<String,String> pair1, Pair<String,String> pair2 ) -> Integer
                .valueOf( names.indexOf( pair1.getLeft() ) ).compareTo( names.indexOf( pair2.getLeft() ) );
        imageLibrary = new TreeMap<Pair<String,String>,List<Supplier<ImageDescriptor>>>( comparator );

        addToImageLibrary(
                names.get( 0 ),
                "simple-icon-places.lst",
                "<a href=\"http://www.flaticon.com/packs/simpleicon-places/\">SimpleIcons Places</a>, designed by <a href=\"http://freepik.com\">freepik</a>" );
        addToImageLibrary(
                names.get( 1 ),
                "pin-of-maps.lst",
                "<a href=\"http://www.flaticon.com/packs/pins-of-maps/\">Pin of maps</a>, designed by <a href=\"http://freepik.com\">freepik</a>" );
        addToImageLibrary( names.get( 2 ), "mapzone_maki.lst",
                "<a href=\"https://github.com/mapbox/maki\">Maki icons</a>, designed by <a href=\"http://mapbox.com/\">Mapbox</a>" );
    }


    private void addToImageLibrary( String name, String listFile, String markUpLicenceText ) {
        List<Supplier<ImageDescriptor>> imageDescriptors = new ArrayList<Supplier<ImageDescriptor>>();
        try (BufferedReader br = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( listFile ) ) )) {
            String line;
            while ((line = br.readLine()) != null) {
                final String imagePath = line;
                imageDescriptors.add( ( ) -> P4Plugin.images().svgImageDescriptor( imagePath,
                        SvgImageRegistryHelper.NORMAL24 ) );
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        imageLibrary.put( Pair.of( name, markUpLicenceText ), imageDescriptors );
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
        createStyleTabItemForPoint( site );
        site.addFieldListener( this );

        // init button state after fields and dialog have been initialized
        // UIUtils.sessionDisplay().asyncExec( new Runnable() {
        // public void run() {
        // getButton( IDialogConstants.OK_ID ).setEnabled( pageContainer.isValid() &&
        // pageContainer.isValid() );
        // }
        // });
    }


    private void createStyleTabItemForPoint( IFormPageSite site ) {
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_SIZE ) ).label.put( "Marker size" ).field
                .put( new SpinnerFormField( 1, 128, 12 ) ).tooltip.put( "" ).create();
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_FILL ) ).label.put( "Marker fill" ).field
                .put( new ColorFormField() ).tooltip.put( "" ).create();
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_ICON ) ).label.put( "Marker icon" ).field
                .put( new IconFormField( imageLibrary ) ).tooltip.put( "" ).create();
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_TRANSPARENCY ) ).label
                .put( "Marker transparency" ).field.put( new SpinnerFormField( 0, 1, 0.1, 0.1, 1 ) ).tooltip.put( "" )
                .create();
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_STROKE_SIZE ) ).label
                .put( "Marker border size" ).field.put( new SpinnerFormField( 0, 32 ) ).tooltip.put( "" ).create();
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_STROKE_COLOR ) ).label
                .put( "Marker border color" ).field.put( new ColorFormField() ).tooltip.put( "" ).create();
        site.newFormField( new BeanPropertyAdapter( getStyleDao(), StylerDAO.MARKER_STROKE_TRANSPARENCY ) ).label
                .put( "Marker border transparency" ).field.put( new SpinnerFormField( 0, 1, 0.1 ) ).tooltip.put( "" )
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
        // if (ev.getEventCode() == VALUE_CHANGE) {
        // Button okBtn = getButton( IDialogConstants.OK_ID );
        // if (okBtn != null) {
        // okBtn.setEnabled( pageContainer.isValid() && pageContainer.isValid() );
        // }
        // }
    }
}
