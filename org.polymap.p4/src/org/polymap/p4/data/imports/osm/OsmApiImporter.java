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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.data.imports.ContextOut;
import org.polymap.p4.data.imports.Importer;
import org.polymap.p4.data.imports.ImporterSite;
import org.polymap.rhei.batik.toolkit.IPanelToolkit;
import org.polymap.rhei.table.DefaultFeatureTableColumn;
import org.polymap.rhei.table.FeatureTableViewer;

import com.google.common.base.Joiner;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class OsmApiImporter
        implements Importer {

    @ContextOut
    protected IterableFeatureCollection features;

    protected ImporterSite              site;

    private Exception                   exception;

    private IPanelToolkit               toolkit;

    private TagFilterPrompt                   tagPrompt;


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
        site.description.set( "Importing OpenStreetMap data via API." );
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
        tagPrompt = new TagFilterPrompt( site );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.data.imports.Importer#verify(org.eclipse.core.runtime.
     * IProgressMonitor)
     */
    @Override
    public void verify( IProgressMonitor monitor ) {
        if (tagPrompt.isOk()) {
            try {
                // TODO get from CRS Prompt (not yet merged to master)
                CoordinateReferenceSystem crs = CRS.decode( "EPSG:4326" );
                // TODO get from currently visible map
                String bboxStr = getBBOXStr( crs );
                List<Pair<String,String>> tags = tagPrompt.selection();
                List<String> selectedKeys = tags.stream().map( tag -> tag.getKey() ).collect( Collectors.toList() );
                String filterStr = getFilterString( tagPrompt.selection() );
                URL url = new URL( "http://www.overpass-api.de/api/xapi?map?" + bboxStr + filterStr );
                features = new IterableFeatureCollection( "osm", url, selectedKeys );
            }
            catch (SchemaException | IOException | FactoryException e) {
                site.ok.set( false );
                exception = e;
            }
        }
    }


    private String getFilterString( List<Pair<String,String>> filters ) throws UnsupportedEncodingException {
        if (filters.size() > 0 && !"*".equals( filters.get( 0 ).getKey() )) {
            // TODO make encoding configurable?
            return "&" + URLEncoder.encode( "node[" + Joiner.on( "|" ).join( filters ) + "]", "UTF-8" );
        }
        else {
            return "";
        }
    }


    private String getBBOXStr( CoordinateReferenceSystem crs ) throws UnsupportedEncodingException {
        ReferencedEnvelope bbox = getBBOX( crs );
        List<Double> values = Arrays.asList( bbox.getMinX(), bbox.getMaxX(), bbox.getMinY(), bbox.getMaxY() );
        // TODO make encoding configurable?
        return URLEncoder.encode( "bbox=" + Joiner.on( "," ).join( values ), "UTF-8" );
    }


    private ReferencedEnvelope getBBOX( CoordinateReferenceSystem crs ) {
        return getPlagwitzBBOX( crs );
    }


    private ReferencedEnvelope getLeipzigBBOX( CoordinateReferenceSystem crs ) {
        double minLon = 12.263489;
        double maxLon = 51.28597;
        double minLat = 12.453003;
        double maxLat = 51.419764;
        return new ReferencedEnvelope( minLon, maxLon, minLat, maxLat, crs );
    }


    private ReferencedEnvelope getPlagwitzBBOX( CoordinateReferenceSystem crs ) {
        double minLon = 12.309451;
        double maxLon = 51.320662;
        double minLat = 12.348933;
        double maxLat = 51.331309;
        return new ReferencedEnvelope( minLon, maxLon, minLat, maxLat, crs );
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
        if (tagPrompt.isOk()) {
            if (exception != null) {
                toolkit.createFlowText( parent,
                        "\nUnable to read the data.\n\n" + "**Reason**: " + exception.getMessage() );
            }
            else {
                SimpleFeatureType schema = (SimpleFeatureType)features.getSchema();
                FeatureTableViewer table = new FeatureTableViewer(parent, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
                for (PropertyDescriptor prop : schema.getDescriptors()) {
                    if ("LAT".equals( prop.getName().toString()) || "LON".equals( prop.getName().toString())) {
                        // skip Geometry
                    }
                    else {
                        table.addColumn( new DefaultFeatureTableColumn( prop ) );
                    }
                }
                table.setContentProvider( new FeatureLazyContentProvider( features ) );
            }
        } else {
            toolkit.createFlowText( parent,
                    "\nOSM Importer is currently deactivated" );
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
