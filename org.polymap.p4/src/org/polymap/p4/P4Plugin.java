/* 
 * polymap.org
 * Copyright (C) 2013-2015, Falko Bräutigam. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.p4;

import java.util.Optional;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.polymap.core.CorePlugin;
import org.polymap.core.data.pipeline.DataSourceDescription;
import org.polymap.core.data.pipeline.Pipeline;
import org.polymap.core.data.pipeline.PipelineProcessor;
import org.polymap.core.project.ILayer;
import org.polymap.core.project.IMap;
import org.polymap.core.ui.StatusDispatcher;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.app.SvgImageRegistryHelper;
import org.polymap.rhei.batik.contribution.ContributionManager;
import org.polymap.rhei.batik.toolkit.BatikStatusAdapter;
import org.polymap.service.geoserver.GeoServerServlet;

import org.polymap.p4.catalog.LocalCatalog;
import org.polymap.p4.catalog.LocalResolver;
import org.polymap.p4.data.P4PipelineIncubator;
import org.polymap.p4.project.NewLayerContribution;
import org.polymap.p4.project.ProjectRepository;

/**
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class P4Plugin
        extends AbstractUIPlugin {

    private static Log              log = LogFactory.getLog( P4Plugin.class );

    public static final String      ID = "org.polymap.p4";

    /** The globale {@link Context} scope for the {@link P4Plugin}. */
    public static final String      Scope = "org.polymap.p4";
    
    public static final String      TOOLBAR_ICON_CONFIG = SvgImageRegistryHelper.WHITE24;
    
    private static P4Plugin         instance;


    public static P4Plugin instance() {
        return instance;
    }

    /**
     * Shortcut for <code>instance().images</code>.
     */
    public static SvgImageRegistryHelper images() {
        return instance().images;
    }
    
    public static LocalCatalog localCatalog() {
        return instance().localCatalog;
    }
    
    public static LocalResolver localResolver() {
        return instance().localResolver;
    }
    
    
    // instance *******************************************

    public SvgImageRegistryHelper   images = new SvgImageRegistryHelper( this );

    private LocalCatalog            localCatalog;

    private LocalResolver           localResolver;

    private ServiceTracker          httpServiceTracker;

    private Optional<HttpService>   httpService = Optional.empty();


    public void start( BundleContext context ) throws Exception {
        super.start( context );
        instance = this;
        
        log.info( "Bundle state: " + getStateLocation() );
        log.info( "Bundle data: " + CorePlugin.getDataLocation( instance() ) );

        // Handling errors in the UI
        StatusDispatcher.registerAdapter( new StatusDispatcher.LogAdapter() );
        StatusDispatcher.registerAdapter( new BatikStatusAdapter() );
        
        localCatalog = new LocalCatalog();
        localResolver = new LocalResolver( localCatalog );

        // register HTTP resource
        httpServiceTracker = new ServiceTracker( context, HttpService.class.getName(), null ) {
            @Override
            public Object addingService( ServiceReference reference ) {
                httpService = Optional.ofNullable( (HttpService)super.addingService( reference ) );

                httpService.ifPresent( service -> {
                    // fake/test GeoServer
                        IMap map = ProjectRepository.newUnitOfWork().entity( IMap.class, "root" );
                        try {
                            service.registerServlet( "/wms", new GeoServerServlet() {
                                @Override
                                public IMap getMap() {
                                    return map;
                                }
                                @Override
                                protected Pipeline createPipeline( ILayer layer,
                                        Class<? extends PipelineProcessor> usecase ) throws Exception {
                                    // resolve service
                                    NullProgressMonitor monitor = new NullProgressMonitor();
                                    DataSourceDescription dsd = LocalResolver
                                            .instance()
                                            .connectLayer( layer, monitor )
                                            .orElseThrow( () -> new RuntimeException( "No data source for layer: " + layer ) );

                                    // create pipeline for it
                                    return P4PipelineIncubator.forLayer( layer ).newPipeline( usecase, dsd, null );
                                }
                            }, null, null );
                        }
                        catch (NoClassDefFoundError e) {
                            log.warn( "No GeoServer plugin found!" );
                        }
                        catch (Exception e) {
                            throw new RuntimeException( e );
                        }
                    } );
                return httpService.get();
            }
        };
        httpServiceTracker.open();

        ContributionManager.addStaticSupplier( ( ) -> new NewLayerContribution() );
    }


    public void stop( BundleContext context ) throws Exception {
        httpServiceTracker.close();
        localCatalog.close();

        instance = null;
        super.stop( context );
    }


    public HttpService httpService() {
        return httpService.orElseThrow( () -> new IllegalStateException( "No HTTP service!" ) );
    }

}
