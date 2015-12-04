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
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.opengis.feature.simple.SimpleFeatureType;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.data.imports.ContextIn;
import org.polymap.p4.data.imports.ContextOut;
import org.polymap.p4.data.imports.Importer;
import org.polymap.p4.data.imports.ImporterSite;
import org.polymap.p4.data.imports.shapefile.CharsetPrompt;
import org.polymap.p4.data.imports.shapefile.ShpFeatureTableViewer;
import org.polymap.rhei.batik.toolkit.IPanelToolkit;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class OsmPbfFileImporter
        implements Importer {

    @ContextIn
    protected File                            file;

    @ContextOut
    protected OsmPbfIterableFeatureCollection features;

    protected ImporterSite                    site;

    private Exception                         exception;

    private IPanelToolkit                     toolkit;

    private CharsetPrompt                     charsetPrompt;

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
        site.description.set( "Importing an OSM PBF file." );
        site.terminal.set( true );
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
                List<Pair<String,String>> tagFilters = tagPrompt.selection();
                features = new OsmPbfIterableFeatureCollection( "osm", file, tagFilters );
                if (features.iterator().hasNext() && features.getException() == null) {
                    site.ok.set( true );
                }
                else {
                    exception = features.getException();
                    site.ok.set( false );
                }
            }
            catch (IOException e) {
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
        if (tagPrompt.isOk()) {
            if (exception != null) {
                toolkit.createFlowText( parent,
                        "\nUnable to read the data.\n\n" + "**Reason**: " + exception.getMessage() );
            }
            else {
                SimpleFeatureType schema = (SimpleFeatureType)features.getSchema();
                ShpFeatureTableViewer table = new ShpFeatureTableViewer( parent, schema );
                table.setContentProvider( new FeatureLazyContentProvider( features ) );
                table.setInput( features );
            }
        }
        else {
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
