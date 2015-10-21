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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.geoserver.catalog.SLDHandler;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;
import org.geotools.util.Version;
import org.junit.Assert;
import org.polymap.p4.style.entities.StyleIdent;
import org.polymap.p4.style.entities.StyleLabel;
import org.polymap.p4.style.entities.StyleLine;
import org.polymap.p4.style.entities.StylePoint;
import org.polymap.p4.style.entities.StylePolygon;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public abstract class AbstractSLDTest {

    protected void assertRoundtrip( String fileName ) throws IOException, TransformerException, URISyntaxException {
        StyledLayerDescriptor sld = getSLD( fileName );
        StyleIdent ident = new StyleIdent();
        StyleLabel label = new StyleLabel();
        StylePoint point = new StylePoint();
        StyleLine line = new StyleLine();
        StylePolygon polygon = new StylePolygon();
        ident.fromSLD( sld );
        label.fromSLD( sld );
        point.fromSLD( sld );
        line.fromSLD( sld );
        polygon.fromSLD( sld );
        StyledLayerDescriptorBuilder wrappedBuilder = new StyledLayerDescriptorBuilder();
        SLDBuilder builder = new SLDBuilder( wrappedBuilder );
        ident.fillSLD( builder );
        label.fillSLD( builder );
        point.fillSLD( builder );
        line.fillSLD( builder );
        polygon.fillSLD( builder );
        assertWrittenSLD( builder, fileName );
    }


    private StyledLayerDescriptor getSLD( String fileName ) throws IOException {
        try (FileInputStream fis = new FileInputStream( new File( "resources/slds/" + fileName ) )) {
            return getSLD( fis );
        }
    }


    protected StyledLayerDescriptor getSLD( InputStream is ) throws IOException {
        InputStreamReader reader = new InputStreamReader( is );
        Version styleVersion = new Version( "1.0.0" );
        return new SLDHandler().parse( reader, styleVersion, null, null );
    }


    private void assertWrittenSLD( SLDBuilder builder, String fileName ) throws TransformerException, IOException,
            URISyntaxException {
        assertWrittenSLD( builder, new File( "resources/slds/" + fileName ).toURI().toURL() );
    }


    protected void assertWrittenSLD( SLDBuilder builder, URL url ) throws TransformerException, IOException,
            URISyntaxException {
        StyledLayerDescriptor sld = builder.build();
        String actual = writeSLDToString( sld );
        String expected = FileUtils.readFileToString( new File( url.toURI() ) );
        Assert.assertEquals( trimHeader( expected ), trimHeader( actual ) );
    }


    private String trimHeader( String sldContent ) {
        int index = sldContent.indexOf( "<NamedLayer" );
        if (index > 0) {
            sldContent = sldContent.substring( index );
        }
        sldContent = sldContent.replace( "      <Name/>\n", "" ).replace( "            <Font />\n", "" );
        // TODO: handle vendor extensions
        sldContent = sldContent.replace( "<VendorOption name=\"maxDisplacement\">400</VendorOption>", "" ).replace(
                "\t\t\t\n", "" );
        return sldContent;
    }


    private String writeSLDToString( StyledLayerDescriptor sld ) throws TransformerException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        SLDTransformer tx = new SLDTransformer();
        // cannot replace prefix sld: in serialized XML, as hard coded in
        // org.geotools.styling.SLDTransformer.SLDTranslator
        tx.setEncoding( Charset.forName( "UTF-8" ) );
        tx.setNamespaceDeclarationEnabled( true );
        tx.setIndentation( 2 );
        tx.transform( sld, byteOut );
        String output = new String( byteOut.toByteArray() );
        output = output.replace( "<sld:", "<" ).replace( "</sld:", "</" );
        return output;
    }
}
