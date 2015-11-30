/*
 * polymap.org Copyright (C) 2015 individual contributors as indicated by the
 * 
 * @authors tag. All rights reserved.
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
package org.polymap.p4.data.imports.osm;

import static org.polymap.rhei.batik.app.SvgImageRegistryHelper.NORMAL24;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.geotools.feature.SchemaException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.data.imports.ContextIn;
import org.polymap.p4.data.imports.ContextOut;
import org.polymap.p4.data.imports.Importer;
import org.polymap.p4.data.imports.ImporterSite;
import org.polymap.p4.data.imports.shapefile.CharsetPrompt;
import org.polymap.p4.data.imports.shapefile.ShpFeatureTableViewer;
import org.polymap.rhei.batik.toolkit.IPanelToolkit;

import com.google.common.collect.Lists;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class OsmFileImporter
        implements Importer {

    @ContextIn
    protected File                      file;

    @ContextOut
    protected IterableFeatureCollection features;

    protected ImporterSite              site;

    private Exception                   exception;

    private IPanelToolkit               toolkit;

    private CharsetPrompt               charsetPrompt;


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.data.imports.Importer#site()
     */
    @Override
    public ImporterSite site() {
        return site;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.p4.data.imports.Importer#init(org.polymap.p4.data.imports.ImporterSite
     * , org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void init( ImporterSite site, IProgressMonitor monitor ) throws Exception {
        this.site = site;

        site.icon.set( P4Plugin.images().svgImage( "file-multiple.svg", NORMAL24 ) );
        site.summary.set( "OSM-Import" );
        site.description.set( "Importing an OSM XML file." );
        site.terminal.set( "osm".equalsIgnoreCase( FilenameUtils.getExtension( file.getName() ) ) );
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.p4.data.imports.Importer#createPrompts(org.eclipse.core.runtime
     * .IProgressMonitor)
     */
    @Override
    public void createPrompts( IProgressMonitor monitor ) throws Exception {
        charsetPrompt = new CharsetPrompt( site, Lists.newArrayList( file ) );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.data.imports.Importer#verify(org.eclipse.core.runtime.
     * IProgressMonitor)
     */
    @Override
    public void verify( IProgressMonitor monitor ) {
        TreeMap<String,SortedSet<String>> tags = new TreeMap<String,SortedSet<String>>();
        try (InputStream in = new FileInputStream( file )) {
            tags.putAll( TagInfo.getTagsFromContent( in ) );
            site.ok.set( true );
            exception = null;
        }
        catch (Exception e) {
            site.ok.set( false );
            exception = e;
        }
        if (site.ok.isPresent() && site.ok.get()) {
            ArrayList<String> keys = new ArrayList<String>( tags.keySet() );
            try {
                features = new IterableFeatureCollection( FilenameUtils.getBaseName( file.getName() ), file, keys );
            }
            catch (FileNotFoundException | SchemaException e) {
                site.ok.set( false );
                exception = e;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.p4.data.imports.Importer#createResultViewer(org.eclipse.swt.widgets
     * .Composite, org.polymap.rhei.batik.toolkit.IPanelToolkit)
     */
    @Override
    public void createResultViewer( Composite parent, IPanelToolkit toolkit ) {
        if (exception != null) {
            toolkit.createFlowText( parent, "\nUnable to read the data.\n\n" + "**Reason**: " + exception.getMessage() );
        }
        else {
            SimpleFeatureType schema = (SimpleFeatureType)features.getSchema();
            ShpFeatureTableViewer table = new ShpFeatureTableViewer( parent, schema );
            table.setContent( features );
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.data.imports.Importer#execute(org.eclipse.core.runtime.
     * IProgressMonitor)
     */
    @Override
    public void execute( IProgressMonitor monitor ) throws Exception {
        // create all params for contextOut
        // all is done in verify
    }
}
