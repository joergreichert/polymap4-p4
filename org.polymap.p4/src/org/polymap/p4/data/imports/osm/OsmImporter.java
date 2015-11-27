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
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
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
import com.google.common.collect.Sets;

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
public class OsmImporter
        implements Importer {

    @ContextIn
    protected File              file;

    @ContextOut
    protected FeatureCollection features;

    protected ImporterSite      site;

    private Exception           exception;

    private IPanelToolkit       toolkit;

    private CharsetPrompt       charsetPrompt;


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
        String typeName = FilenameUtils.getBaseName( file.getName() );
        TreeMap<String,SortedSet<String>> allTags = new TreeMap<String,SortedSet<String>>();
        try (InputStream in = new FileInputStream( file )) {
            allTags.putAll( getTags( in ) );
            String longKey = "LON:Double";
            String latKey = "LAT:Double";
            allTags.put( longKey, Sets.newTreeSet() );
            allTags.put( latKey, Sets.newTreeSet() );
            StringBuffer typeSpec = new StringBuffer();
            for (String key : allTags.keySet()) {
                if (!StringUtils.isBlank( typeSpec )) {
                    typeSpec.append( "," );
                }
                typeSpec.append( key ).append( ":String" );
            }
            final SimpleFeatureType TYPE = DataUtilities.createType( typeName, typeSpec.toString() );
            final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder( TYPE );
            // TODO: use iterable feature collection
            features = new DefaultFeatureCollection( null, TYPE );
            try (InputStream input = new FileInputStream( file )) {
                OsmIterator iterator = new OsmXmlIterator( input, false );
                for (EntityContainer container : iterator) {
                    if (container.getType() == EntityType.Node) {
                        OsmNode node = (OsmNode)container.getEntity();
                        featureBuilder.add( node.getLongitude() );
                        featureBuilder.add( node.getLatitude() );
                        Map<String,String> tags = OsmModelUtil.getTagsAsMap( node );
                        Object value;
                        for (String key : allTags.keySet()) {
                            if (key.equals( longKey )) {
                                value = node.getLongitude();
                            }
                            else if (key.equals( latKey )) {
                                value = node.getLatitude();
                            }
                            else {
                                value = !tags.containsKey( key ) || tags.get( key ) == null ? "" //$NON-NLS-1$
                                        : tags.get( key );
                            }
                            featureBuilder.add( value );
                        }
                        ((DefaultFeatureCollection)features).add( featureBuilder.buildFeature( null ) );
                    }
                }
            }
            site.ok.set( true );
            exception = null;
        }
        catch (Exception e) {
            site.ok.set( false );
            exception = e;
        }
    }


    private TreeMap<String,SortedSet<String>> getTags( InputStream input ) throws Exception {
        TreeMap<String,SortedSet<String>> allTags = new TreeMap<String,SortedSet<String>>();
        OsmIterator iterator = new OsmXmlIterator( input, false );
        for (EntityContainer container : iterator) {
            if (container.getType() == EntityType.Node) {
                OsmNode node = (OsmNode)container.getEntity();
                Map<String,String> tags = OsmModelUtil.getTagsAsMap( node );
                for (Entry<String,String> entry : tags.entrySet()) {
                    collectTags( allTags, entry.getKey(), entry.getValue() );
                }
            }
        }
        return allTags;
    }


    public void collectTags( SortedMap<String,SortedSet<String>> tags, String key, String value ) {
        SortedSet<String> values = tags.get( key );
        if (values == null) {
            values = new TreeSet<String>();
            tags.put( key, values );
        }
        if (value != null && !values.contains( value )) {
            values.add( value );
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
