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
package org.polymap.p4.style.daos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.builder.StyledLayerDescriptorBuilder;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StyleLabelDao
        extends AbstractStyleSymbolizerDao {

    public static final String LABEL_TEXT       = "labelText";

    public static final String LABEL_FONT_DATA  = "labelFontData";

    public static final String LABEL_FONT_COLOR = "labelFontColor";

    public static final String LABEL_OFFSET     = "labelOffset";

    private String             labelText;

    private FontData           labelFontData;

    private RGB                labelFontColor;

    private Integer            labelOffset;


    public StyleLabelDao() {
    }


    /**
     * @param style
     */
    public StyleLabelDao( StyledLayerDescriptor style ) {
        fromSLD( style );
    }


    public String getLabelText() {
        return labelText;
    }


    public void setLabelText( String labelText ) {
        this.labelText = labelText;
    }


    public FontData getLabelFontData() {
        return labelFontData;
    }


    public void setLabelFontData( FontData labelFontData ) {
        this.labelFontData = labelFontData;
    }


    public String getLabelFont() {
        if (getLabelFontData() != null) {
            return getLabelFontData().getName();
        }
        return null;
    }


    public Integer getLabelFontSize() {
        if (getLabelFontData() != null) {
            return getLabelFontData().getHeight();
        }
        return null;
    }


    public String getLabelFontWeight() {
        if (getLabelFontData() != null) {
            if ((getLabelFontData().getStyle() & SWT.BOLD) != 1) {
                return "bold";
            }
            else {
                return "normal";
            }
        }
        return null;
    }


    public String getLabelFontStyle() {
        if (getLabelFontData() != null) {
            if ((getLabelFontData().getStyle() & SWT.ITALIC) != 1) {
                return "italic";
            }
            else {
                return "normal";
            }
        }
        return null;
    }


    public RGB getLabelFontColor() {
        return labelFontColor;
    }


    public void setLabelFontColor( RGB labelFontColor ) {
        this.labelFontColor = labelFontColor;
    }


    public Integer getLabelOffset() {
        return labelOffset;
    }


    public void setLabelOffset( Integer labelOffset ) {
        this.labelOffset = labelOffset;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.style.daos.IStyleDao#fromSLD(org.geotools.styling.
     * StyledLayerDescriptor)
     */
    @Override
    public void fromSLD( StyledLayerDescriptor style ) {
        style.accept( new StyleLabelFromSLDVisitor( this ) );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.polymap.p4.style.daos.IStyleDao#fillSLD(org.geotools.styling.builder.
     * StyledLayerDescriptorBuilder)
     */
    @Override
    public void fillSLD( StyledLayerDescriptorBuilder builder ) {
        new StyleLabelToSLDVisitor( this ).fillSLD( builder );
    }


    /**
     * @param accept
     */
    public void setLabelFont( String labelFont ) {
        nullSafeGetLabelFontData().setName( labelFont );
    }


    private FontData nullSafeGetLabelFontData() {
        if (getLabelFontData() == null) {
            FontData fontData = new FontData();
            setLabelFontData( fontData );
        }
        return getLabelFontData();
    }

    public void setLabelFontSize( Integer size ) {
        nullSafeGetLabelFontData().setHeight( size );
    }


    public void setLabelFontWeight( String weightString ) {
        int existingStyle = nullSafeGetLabelFontData().getStyle();
        if(!"bold".equals( weightString ) && (existingStyle & SWT.BOLD) != 1) {
            existingStyle = existingStyle | SWT.BOLD;
        } else if("bold".equals( weightString ) && (existingStyle & SWT.BOLD) == 1) {
            existingStyle = existingStyle & SWT.BOLD;
        }
    }
    
    public void setLabelFontStyle( String styleString ) {
        int existingStyle = nullSafeGetLabelFontData().getStyle();
        if(!"italic".equals( styleString ) && (existingStyle & SWT.ITALIC) != 1) {
            existingStyle = existingStyle | SWT.ITALIC;
        } else if("italic".equals( styleString ) && (existingStyle & SWT.ITALIC) == 1) {
            existingStyle = existingStyle & SWT.ITALIC;
        }
    }    
}
