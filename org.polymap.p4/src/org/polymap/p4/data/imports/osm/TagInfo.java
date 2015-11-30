/*
 * polymap.org Copyright (C) 2015 individual contributors as indicated by the
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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
public class TagInfo {

    private static List<String> keys = null;
    
    private static SortedMap<String,SortedSet<String>> tags = null;

    public static List<String> getStaticKeys() throws IOException {
        if (keys == null) {
            getStaticTags();
        }
        return new ArrayList<String>( keys);
    }

    public static SortedMap<String,SortedSet<String>> getStaticTags() throws IOException {
        if (tags == null) {
            TreeSet<String> keySet = new TreeSet<String>();
            tags = new TreeMap<String,SortedSet<String>>();
            String jsonTxt = IOUtils.readLines( TagInfo.class.getResourceAsStream( "tags.json" ) ).stream()
                    .collect( Collectors.joining( "\n" ) );
            JSONObject json = new JSONObject( jsonTxt );
            JSONArray root = json.getJSONArray( "tags" );
            String key;
            SortedSet<String> set;
            JSONArray values;
            for (int i = 0; i < root.length(); i++) {
                key = root.getJSONObject( i ).getString( "key" );
                keySet.add(key);
                values = root.getJSONObject( i ).getJSONArray( "values" );
                set = new TreeSet<String>();
                if (values != null) {
                    for (int j = 0; j < values.length(); j++) {
                        set.add( values.getString( j ) );
                    }
                }
                tags.put( key, set );
            }
            keys = new ArrayList<String>(keySet);
        }
        return tags;
    }
    


    public static TreeMap<String,SortedSet<String>> getTagsFromContent( InputStream input ) throws Exception {
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


    private static void collectTags( SortedMap<String,SortedSet<String>> tags, String key, String value ) {
        SortedSet<String> values = tags.get( key );
        if (values == null) {
            values = new TreeSet<String>();
            tags.put( key, values );
        }
        if (value != null && !values.contains( value )) {
            values.add( value );
        }
    }
}
