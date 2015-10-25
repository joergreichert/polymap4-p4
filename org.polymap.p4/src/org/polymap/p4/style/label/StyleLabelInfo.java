/*
 * polymap.org 
 * Copyright (C) @year@ individual contributors as indicated by the @authors tag. 
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
package org.polymap.p4.style.label;

import org.polymap.p4.style.AbstractFormFieldInfo;
import org.polymap.p4.style.entities.StyleLabel;

/**
 * @author "Joerg Reichert <joerg@mapzone.io>"
 *
 */
public class StyleLabelInfo
        extends AbstractFormFieldInfo
        implements IStyleLabelInfo {
    
    private StyleLabel styleLabel;

    @Override
    public StyleLabel getStyleLabel() {
        return styleLabel;
    }

    @Override
    public void setStyleLabel( StyleLabel styleLabel ) {
        this.styleLabel = styleLabel;
    }
}
