/* 
 * polymap.org
 * Copyright (C) 2015, Falko Bräutigam. All rights reserved.
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

import org.eclipse.swt.widgets.Label;

import org.polymap.rhei.batik.toolkit.md.MdAppDesign;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class P4AppDesign
        extends MdAppDesign {

    private Label               title;

    
//    protected Composite fillHeaderArea( Composite parent ) {
//        Composite result = new Composite( parent, SWT.NO_FOCUS | SWT.BORDER );
//        UIUtils.setVariant( result, CSS_HEADER );
//        result.setLayout( FormLayoutFactory.defaults().margins( 0, 0 ).create() );
//        title = UIUtils.setVariant( new Label( result, SWT.NONE ), CSS_HEADER );
//        title.setText( "P4" );
//        return result;
//    }
    
    
    public void setAppTitle( String title ) {
        if (this.title != null) {
            this.title.setText( title );
        }
    }
    
}
