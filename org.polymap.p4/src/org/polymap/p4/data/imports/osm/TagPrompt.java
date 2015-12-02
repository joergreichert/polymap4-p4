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
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polymap.p4.data.imports.ImporterPrompt;
import org.polymap.p4.data.imports.ImporterPrompt.Severity;
import org.polymap.p4.data.imports.ImporterSite;

import com.google.common.base.Joiner;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class TagPrompt {

    private static Log                       log       = LogFactory.getLog( TagPrompt.class );

    private static List<Pair<String,String>> DEFAULT   = new ArrayList<Pair<String,String>>();

    private ImporterSite                     site;

    private List<Pair<String,String>>        selection = DEFAULT;

    private final ImporterPrompt             prompt;


    public TagPrompt( ImporterSite site ) {
        this.site = site;

        prompt = site.newPrompt( "tagFilter" ).summary.put( "Tag filter" ).description
                .put( "Filter features to import by their tags." ).value
                .put( getReadable() ).severity
                .put( Severity.REQUIRED ).ok.put( false ).
                extendedUI.put( new FilteredMapPromptUIBuilder() {

                    private SortedMap<String,SortedSet<String>> tags = null;


                    @Override
                    public void submit( ImporterPrompt prompt ) {
                        prompt.value.put( getReadable() );
                        prompt.ok.set( true );
                    }


                    @Override
                    protected SortedMap<String,SortedSet<String>> listItems() {
                        if (tags == null) {
                            tags = new TreeMap<String,SortedSet<String>>();
                            TreeSet<String> star = new TreeSet<String>();
                            star.add( "*" );
                            tags.put( "*", star );
                            try {
                                tags.putAll( TagInfo.getStaticTags() );
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return tags;
                    }


                    @Override
                    protected List<Pair<String,String>> initiallySelectedItems() {
                        if (selection.isEmpty()) {
                            return Arrays.asList( Pair.of( "*", "*" ) );
                        }
                        return selection;
                    }


                    @Override
                    protected void handleSelection( Pair<String,String> selected ) {
                        selection.add( selected );
                        assert selection != null;
                    }


                    @Override
                    protected void handleUnselection( Pair<String,String> selected ) {
                        selection.remove( selected );
                        assert selection != null;
                    }
                } );
        prompt.ok.set( false );
    }


    private String getReadable() {
        String readable = Joiner.on( "," ).join(
                selection.stream().map( filter -> filter.getKey() + "=" + filter.getValue() )
                        .collect( Collectors.toList() ) );
        if (readable.length() > 80) {
            readable = readable.substring( 0, 80 ) + " ...";
        }
        return readable;
    }


    /**
     * The selected tags to use as filter.
     */
    public List<Pair<String,String>> selection() {
        return selection;
    }


    public boolean isOk() {
        return prompt.ok.get();
    }
}
