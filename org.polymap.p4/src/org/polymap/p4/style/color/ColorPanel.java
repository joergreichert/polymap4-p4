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
package org.polymap.p4.style.color;

import java.util.EventObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.polymap.core.runtime.event.EventManager;
import org.polymap.p4.P4Plugin;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.DefaultPanel;
import org.polymap.rhei.batik.PanelIdentifier;
import org.polymap.rhei.batik.PanelPath;
import org.polymap.rhei.batik.Scope;
import org.polymap.rhei.batik.toolkit.md.MdToolkit;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class ColorPanel
        extends DefaultPanel {

    public static final PanelIdentifier ID = PanelIdentifier.parse( "color" );


    private class PaletteListener
            extends MouseAdapter {

        private final Composite panelBody;

        private final RGB       rgb;


        public PaletteListener( Composite panelBody, RGB rgb ) {
            this.panelBody = panelBody;
            this.rgb = rgb;
        }


        @Override
        public void mouseDown( MouseEvent event ) {
            setColorFromPalette( panelBody, rgb );
        }
    }


    private class SpinnerListener
            implements ModifyListener {

        private final Composite panelBody;

        private final Spinner   spinner;

        private final int       colorIndex;


        public SpinnerListener( Composite panelBody, Spinner spinner, int colorIndex ) {
            this.panelBody = panelBody;
            this.spinner = spinner;
            this.colorIndex = colorIndex;
        }


        public void modifyText( ModifyEvent event ) {
            setColorFomSpinner( panelBody, colorIndex, spinner.getSelection() );
        }
    }

    private static final int    PALETTE_BOX_SIZE        = 12;

    private static final int    PALETTE_BOXES_IN_ROW    = 14;

    private static final int    COLOR_DISPLAY_BOX_SIZE  = 76;

    private static final int    MAX_RGB_COMPONENT_VALUE = 255;

    // Color components
    private static final int    RED                     = 0;

    private static final int    GREEN                   = 1;

    private static final int    BLUE                    = 2;

    // Palette colors
    private static final RGB[]  PALETTE_COLORS          = new RGB[] { new RGB( 0, 0, 0 ), new RGB( 70, 70, 70 ),
            new RGB( 120, 120, 120 ), new RGB( 153, 0, 48 ), new RGB( 237, 28, 36 ), new RGB( 255, 126, 0 ),
            new RGB( 255, 194, 14 ), new RGB( 255, 242, 0 ), new RGB( 168, 230, 29 ), new RGB( 34, 177, 76 ),
            new RGB( 0, 183, 239 ), new RGB( 77, 109, 243 ), new RGB( 47, 54, 153 ), new RGB( 111, 49, 152 ),
            new RGB( 255, 255, 255 ), new RGB( 220, 220, 220 ), new RGB( 180, 180, 180 ), new RGB( 156, 90, 60 ),
            new RGB( 255, 163, 177 ), new RGB( 229, 170, 122 ), new RGB( 245, 228, 156 ), new RGB( 255, 249, 189 ),
            new RGB( 211, 249, 188 ), new RGB( 157, 187, 97 ), new RGB( 153, 217, 234 ), new RGB( 112, 154, 209 ),
            new RGB( 84, 109, 142 ), new RGB( 181, 165, 213 ) };

    private RGB                 rgb;

    private Label               colorDisplay;

    private Spinner             spRed;

    private Spinner             spBlue;

    private Spinner             spGreen;

    private Label               colorHex;

    private MdToolkit           toolkit;

    private Button              applyButton;

    @Scope(P4Plugin.Scope)
    private Context<IColorInfo> colorInfo;


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.rhei.batik.IPanel#createContents(org.eclipse.swt.widgets.Composite
     * )
     */
    @Override
    public void createContents( Composite panelBody ) {
        getSite().setTitle( "Color Selection" );
        toolkit = (MdToolkit)getSite().toolkit();
        panelBody.setLayout( new GridLayout( 1, false ) );
        prepareOpen( panelBody );
    }


    /**
     * Returns the currently selected color in the receiver.
     *
     * @return the RGB value for the selected color, may be null
     * @see PaletteData#getRGBs
     */
    public RGB getRGB() {
        return rgb;
    }


    /**
     * Sets the receiver's selected color to be the argument.
     *
     * @param rgb the new RGB value for the selected color, may be null to let the
     *        platform select a default when open() is called
     * @see PaletteData#getRGBs
     */
    public void setRGB( RGB rgb ) {
        this.rgb = rgb;
    }


    protected void prepareOpen( Composite panelBody ) {
        createControls( panelBody );
        if (rgb == null) {
            rgb = new RGB( 255, 255, 255 );
        }
        updateColorDisplay( panelBody );
        updateSpinners();
    }


    private void createControls( Composite panelBody ) {
        
        createColorArea( panelBody );
        createPalette( panelBody );
        Composite buttonBar = toolkit.createComposite( panelBody, SWT.NONE );
        buttonBar.setLayout( new GridLayout(1, false) );
        applyButton = createApplyButton( buttonBar );
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.RIGHT;
        applyButton.setData( gridData );
    }


    private Button createApplyButton( Composite comp ) {
        Button applyButton = toolkit.createButton( comp, "Apply selection", SWT.PUSH );
        applyButton.setEnabled( getRGB() != null );
        applyButton.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                colorInfo.get().setColor( getRGB() );
                PanelPath path = getSite().getPath();
                getContext().closePanel( path );
                EventManager.instance().publish( new EventObject( colorInfo.get() ) );
            }
        } );
        return applyButton;
    }


    private Composite createPalette( Composite panelBody ) {
        Composite paletteComp = new Composite( panelBody, SWT.NONE );
        GridData palData = new GridData( SWT.CENTER, SWT.CENTER, true, false );
        paletteComp.setLayoutData( palData );
        paletteComp.setLayout( new GridLayout( PALETTE_BOXES_IN_ROW, true ) );
        Label title = new Label( paletteComp, SWT.NONE );
        String titleText = "Basic colors";
        title.setText( titleText );
        GridData titleData = new GridData( SWT.LEFT, SWT.CENTER, true, false );
        titleData.horizontalSpan = PALETTE_BOXES_IN_ROW;
        title.setLayoutData( titleData );
        for (int i = 0; i < PALETTE_COLORS.length; i++) {
            createPaletteColorBox( paletteComp, PALETTE_COLORS[i] );
        }
        return paletteComp;
    }


    private Composite createColorArea( Composite panelBody ) {
        // Current color selection display
        Composite areaComp = new Composite( panelBody, SWT.NONE );
        GridData compData = new GridData( SWT.CENTER, SWT.CENTER, true, false );
        areaComp.setLayoutData( compData );
        areaComp.setLayout( new GridLayout( 2, true ) );
        Composite displayComp = new Composite( areaComp, SWT.NONE );
        displayComp.setLayout( new GridLayout( 1, false ) );
        colorDisplay = new Label( displayComp, SWT.BORDER | SWT.FLAT );
        GridData data = new GridData();
        data.widthHint = COLOR_DISPLAY_BOX_SIZE;
        data.heightHint = COLOR_DISPLAY_BOX_SIZE;
        colorDisplay.setLayoutData( data );
        // Color components spinners
        Composite spinComp = new Composite( areaComp, SWT.NONE );
        spinComp.setLayout( new GridLayout( 2, true ) );
        Label rLabel = new Label( spinComp, SWT.NONE );
        rLabel.setText( "Red" );
        spRed = new Spinner( spinComp, SWT.BORDER );
        spRed.setMaximum( MAX_RGB_COMPONENT_VALUE );
        spRed.addModifyListener( new SpinnerListener( panelBody, spRed, RED ) );
        //
        Label gLabel = new Label( spinComp, SWT.NONE );
        gLabel.setText( "Green" );
        spGreen = new Spinner( spinComp, SWT.BORDER );
        spGreen.setMaximum( MAX_RGB_COMPONENT_VALUE );
        spGreen.addModifyListener( new SpinnerListener( panelBody, spGreen, GREEN ) );
        //
        Label bLabel = new Label( spinComp, SWT.NONE );
        bLabel.setText( "Blue" );
        spBlue = new Spinner( spinComp, SWT.BORDER );
        spBlue.setMaximum( MAX_RGB_COMPONENT_VALUE );
        spBlue.addModifyListener( new SpinnerListener( panelBody, spBlue, BLUE ) );
        return areaComp;
    }


    private void updateColorDisplay( Composite panelBody ) {
        if (rgb != null) {
            colorDisplay.setBackground( new Color( panelBody.getDisplay(), rgb ) );
        }
        else {
            colorDisplay.setBackground( null );
        }
        if (applyButton != null) {
            applyButton.setEnabled( rgb != null );
        }
    }


    private void updateSpinners() {
        spRed.setSelection( rgb.red );
        spGreen.setSelection( rgb.green );
        spBlue.setSelection( rgb.blue );
    }


    private Label createPaletteColorBox( Composite parent, RGB color ) {
        Label result = new Label( parent, SWT.BORDER | SWT.FLAT );
        result.setBackground( new Color( parent.getDisplay(), color ) );
        GridData data = new GridData();
        data.widthHint = PALETTE_BOX_SIZE;
        data.heightHint = PALETTE_BOX_SIZE;
        result.setLayoutData( data );
        result.addMouseListener( new PaletteListener( parent, color ) );
        return result;
    }


    private void setColorFomSpinner( Composite parent, int colorIndex, int value ) {
        switch (colorIndex) {
            case RED:
                rgb.red = value;
                break;
            case GREEN:
                rgb.green = value;
                break;
            case BLUE:
                rgb.blue = value;
                break;
        }
        updateColorDisplay( parent );
    }


    private void setColorFromPalette( Composite parent, RGB selectedColor ) {
        if (selectedColor != null) {
            if (rgb == null) {
                rgb = new RGB( 0, 0, 0 );
            }
            rgb.blue = selectedColor.blue;
            rgb.green = selectedColor.green;
            rgb.red = selectedColor.red;
        }
        else {
            rgb = null;
        }
        updateColorDisplay( parent );
        updateSpinners();
    }
}
