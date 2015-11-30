/*
 * polymap.org Copyright (C) 2015, Falko Bräutigam. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3.0 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.p4.data.imports.osm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polymap.p4.data.imports.ImporterPrompt;
import org.polymap.p4.data.imports.ImporterPrompt.Severity;
import org.polymap.p4.data.imports.ImporterSite;
import org.polymap.p4.data.imports.utils.FilteredListPromptUIBuilder;

import com.google.common.base.Joiner;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class TagPrompt {

    private static Log          log       = LogFactory.getLog( TagPrompt.class );

    private static List<String> DEFAULT   = new ArrayList<String>();

    private ImporterSite        site;

    private List<String>        selection = DEFAULT;


    public TagPrompt( ImporterSite site ) {
        this.site = site;

        String readable = getReadable();
        site.newPrompt( "tagFilter" ).summary.put( "Tag filter" ).description
                .put( "Optional tag filters." ).value.put( readable ).severity
                .put( Severity.VERIFY ).extendedUI.put( new FilteredListPromptUIBuilder() {

            @Override
            public void submit( ImporterPrompt prompt ) {
                prompt.ok.set( true );
                prompt.value.put( getReadable() );
            }


            @Override
            protected String[] listItems() {
                java.util.List<String> keys = new ArrayList<String>();
                try {
                    keys.addAll(TagInfo.getStaticKeys());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return keys.toArray( new String[keys.size()] );
            }


            @Override
            protected String initiallySelectedItem() {
                return getReadable();
            }


            @Override
            protected void handleSelection( String selected ) {
                selection = Arrays.asList(selected.split( "," ));
                assert selection != null;
            }
        } );
    }


    private String getReadable() {
        String readable = Joiner.on( "," ).join( selection);
        if(readable.length() > 30) {
            readable = readable.substring( 0, 30 ) + " ...";
        }
        return readable;
    }


    /**
     * The selected tags to use as filter.
     */
    public List<String> selection() {
        return selection;
    }
}
