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
package org.polymap.p4.style.font;

import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.polymap.core.runtime.event.EventManager;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.style.color.ColorInfo;
import org.polymap.p4.style.color.ColorPanel;
import org.polymap.p4.style.color.IColorInfo;
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
public class FontPanel
        extends DefaultPanel {

    public static final PanelIdentifier ID = PanelIdentifier.parse( "font" );

    private FontData                    fontData;

    private RGB                         rgb;

    private Text                        txtFontFamily;

    private List                        lstFontFamily;

    private Spinner                     spFontSize;

    private Button                      cbBold;

    private Button                      cbItalic;

    private Label                       lblColor;

    private Label                       lblPreview;

    private MdToolkit                   toolkit;

    private Button                      applyButton;

    @Scope(P4Plugin.Scope)
    private Context<IFontInfo>          fontInfo;

    @Scope(P4Plugin.Scope)
    private Context<IColorInfo>         colorInfo;


    
    public FontPanel() {
//        ColorInfo colorInfoImpl = new ColorInfo();
//        colorInfo.set( colorInfoImpl );
//
//        EventManager.instance().subscribe( colorInfo, ev -> ev.getSource() instanceof IColorInfo );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.polymap.rhei.batik.IPanel#createContents(org.eclipse.swt.widgets.Composite
     * )
     */
    @Override
    public void createContents( Composite panelBody ) {
        getSite().setTitle( "Font Selection" );
        toolkit = (MdToolkit)getSite().toolkit();
        panelBody.setLayout( new GridLayout( 1, false ) );
        prepareOpen( panelBody );
    }


    /**
     * Returns a FontData set describing the font that was selected in the dialog, or
     * null if none is available.
     *
     * @return the FontData for the selected font, or null
     */
    public FontData[] getFontList() {
        FontData[] result = null;
        if (fontData != null) {
            result = new FontData[1];
            result[0] = fontData;
        }
        return result;
    }


    /**
     * Sets the set of FontData objects describing the font to be selected by default
     * in the dialog, or null to let the platform choose one.
     *
     * @param fontData the set of FontData objects to use initially, or null to let
     *        the platform select a default when open() is called
     * @see Font#getFontData
     */
    public void setFontList( FontData[] fontData ) {
        if (fontData != null && fontData.length > 0) {
            this.fontData = fontData[0];
        }
        else {
            this.fontData = null;
        }
    }


    /**
     * Returns an RGB describing the color that was selected in the dialog, or null
     * if none is available.
     *
     * @return the RGB value for the selected color, or null
     * @see PaletteData#getRGBs
     */
    public RGB getRGB() {
        return rgb;
    }


    /**
     * Sets the RGB describing the color to be selected by default in the dialog, or
     * null to let the platform choose one.
     *
     * @param rgb the RGB value to use initially, or null to let the platform select
     *        a default when open() is called
     * @see PaletteData#getRGBs
     */
    public void setRGB( RGB rgb ) {
        this.rgb = rgb;
    }


    protected void prepareOpen( Composite parent ) {
        initializeDefaults( parent );
        createControls( parent );
        updateControls( parent );
        addChangeListeners( parent );
    }


    private void initializeDefaults( Composite parent ) {
        if (fontData == null) {
            FontData systemFontData = parent.getDisplay().getSystemFont().getFontData()[0];
            String fontName = getFirstFontName( systemFontData.getName() );
            int fontHeight = systemFontData.getHeight();
            int fontStyle = systemFontData.getStyle();
            fontData = new FontData( fontName, fontHeight, fontStyle );
        }
        if (rgb == null) {
            rgb = new RGB( 0, 0, 0 );
        }
    }


    static String getFirstFontName( String fontName ) {
        String result = fontName;
        int index = result.indexOf( ',' );
        if (index != -1) {
            result = result.substring( 0, index );
        }
        result = result.trim();
        if (result.length() > 2) {
            char firstChar = result.charAt( 0 );
            char lastChar = result.charAt( result.length() - 1 );
            boolean isQuoted = (firstChar == '\'' && lastChar == '\'') || (firstChar == '"' && lastChar == '"');
            if (isQuoted) {
                result = result.substring( 1, result.length() - 1 );
            }
        }
        return result;
    }


    private void createControls( Composite parent ) {
        GridLayout mainLayout = new GridLayout( 2, true );
        mainLayout.marginWidth = 10;
        mainLayout.marginHeight = 10;
        mainLayout.horizontalSpacing = 10;
        mainLayout.verticalSpacing = 10;
        parent.setLayout( mainLayout );
        createLeftArea( parent );
        createRightArea( parent );
        createPreviewArea( parent );
        fillAvailableFonts( parent );
        createButtonBar( parent );
    }


    private void createButtonBar( Composite panelBody ) {
        Composite buttonBar = toolkit.createComposite( panelBody, SWT.NONE );
        buttonBar.setLayout( new GridLayout( 1, false ) );
        applyButton = createApplyButton( buttonBar );
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.RIGHT;
        gridData.grabExcessHorizontalSpace = true;
        applyButton.setData( gridData );
    }


    private Button createApplyButton( Composite comp ) {
        Button applyButton = toolkit.createButton( comp, "Apply selection", SWT.PUSH );
        applyButton.setEnabled( getRGB() != null );
        applyButton.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                fontInfo.get().setColor( getRGB() );
                fontInfo.get().setFontData( getFontList() );
                PanelPath path = getSite().getPath();
                getContext().closePanel( path );
                EventManager.instance().publish( new EventObject( fontInfo.get() ) );
            }
        } );
        return applyButton;
    }


    private void createLeftArea( Composite parent ) {
        Composite leftArea = createVerticalArea( parent );
        createFontFamilyGroup( leftArea );
    }


    private void createRightArea( Composite parent ) {
        Composite rightArea = createVerticalArea( parent );
        createFontSizeGroup( rightArea );
        createFontStyleGroup( rightArea );
        createFontColorGroup( rightArea );
    }


    private static Composite createVerticalArea( Composite parent ) {
        Composite result = new Composite( parent, SWT.NONE );
        result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        result.setLayout( layout );
        return result;
    }


    private void createFontFamilyGroup( Composite parent ) {
        Group result = new Group( parent, SWT.NONE );
        result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        result.setText( "Font family" );
        result.setLayout( new GridLayout() );
        txtFontFamily = new Text( result, SWT.BORDER );
        GridData textData = new GridData( SWT.FILL, SWT.CENTER, true, false );
        txtFontFamily.setLayoutData( textData );
        lstFontFamily = new List( result, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER );
        GridData listData = new GridData( SWT.FILL, SWT.FILL, true, true );
        lstFontFamily.setLayoutData( listData );
        lstFontFamily.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent event ) {
                int selectionIndex = lstFontFamily.getSelectionIndex();
                if (selectionIndex != -1) {
                    txtFontFamily.setText( lstFontFamily.getItem( selectionIndex ) );
                }
            }
        } );
    }


    private void createFontSizeGroup( Composite parent ) {
        Group result = new Group( parent, SWT.NONE );
        result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
        result.setText( "Font size" );
        result.setLayout( new GridLayout() );
        spFontSize = new Spinner( result, SWT.BORDER );
        spFontSize.setDigits( 0 );
        spFontSize.setMinimum( 0 );
        spFontSize.setMaximum( 200 );
        GridData spinnerData = new GridData( SWT.FILL, SWT.FILL, true, true );
        spFontSize.setLayoutData( spinnerData );
    }


    private void createFontStyleGroup( Composite parent ) {
        Display display = parent.getDisplay();
        Group result = new Group( parent, SWT.NONE );
        result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        result.setText( "Font style" );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        result.setLayout( layout );
        cbBold = new Button( result, SWT.CHECK );
        cbBold.setText( "Bold" );
        FontData normalFont = cbBold.getFont().getFontData()[0];
        Font boldFont = new Font( display, normalFont.getName(), normalFont.getHeight(), SWT.BOLD );
        cbBold.setFont( boldFont );
        cbItalic = new Button( result, SWT.CHECK );
        cbItalic.setText( "Italic" );
        Font italicFont = new Font( display, normalFont.getName(), normalFont.getHeight(), SWT.ITALIC );
        cbItalic.setFont( italicFont );
    }


    private void createFontColorGroup( Composite parent ) {
        Group result = new Group( parent, SWT.NONE );
        result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
        result.setText( "Color" );
        result.setLayout( new GridLayout( 2, false ) );
        lblColor = new Label( result, SWT.BORDER );
        lblColor.setLayoutData( new GridData( 20, 20 ) );
        Button changeColorButton = new Button( result, SWT.PUSH );
        changeColorButton.setText( "Select color" );
        changeColorButton.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent event ) {
                colorInfo.get().setFormField( fontInfo.get().getFormField() );
                colorInfo.get().setColor( getRGB() );
                getContext().openPanel( getSite().getPath(), ColorPanel.ID );
            }
        } );
    }


    private void addChangeListeners( Composite parent ) {
        SelectionListener selectionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent event ) {
                updateFontData( parent );
            }
        };
        spFontSize.addSelectionListener( selectionListener );
        cbBold.addSelectionListener( selectionListener );
        cbItalic.addSelectionListener( selectionListener );
        txtFontFamily.addModifyListener( new ModifyListener() {

            public void modifyText( ModifyEvent event ) {
                String text = txtFontFamily.getText();
                selectFontFamilyInList( text );
                updateFontData( parent );
            }
        } );
    }


    private void createPreviewArea( Composite parent ) {
        Composite previewArea = new Composite( parent, SWT.BORDER );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 );
        gridData.minimumWidth = 300;
        previewArea.setLayoutData( gridData );
        previewArea.setLayout( new GridLayout() );
        lblPreview = new Label( previewArea, SWT.CENTER );
        GridData labelData = new GridData( SWT.FILL, SWT.CENTER, true, true );
        lblPreview.setLayoutData( labelData );
        lblPreview.setText( "Font preview" );
        Color bgColor = parent.getDisplay().getSystemColor( SWT.COLOR_LIST_BACKGROUND );
        previewArea.setBackground( bgColor );
        previewArea.setBackgroundMode( SWT.INHERIT_DEFAULT );
    }


    private void fillAvailableFonts( Composite parent ) {
        Collection<String> fontFamilies = new HashSet<String>();
        FontData[] fontList = parent.getDisplay().getFontList( null, true );
        if (fontList != null) {
            for (int i = 0; i < fontList.length; i++) {
                fontFamilies.add( fontList[i].getName() );
            }
        }
        String[] availableFontNames = fontFamilies.toArray( new String[fontFamilies.size()] );
        Arrays.sort( availableFontNames );
        lstFontFamily.setItems( availableFontNames );
    }


    private void updateControls( Composite parent ) {
        String fontName = fontData.getName();
        if (!txtFontFamily.getText().equals( fontName )) {
            txtFontFamily.setText( fontName );
        }
        selectFontFamilyInList( fontName );
        spFontSize.setSelection( fontData.getHeight() );
        cbBold.setSelection( (fontData.getStyle() & SWT.BOLD) != 0 );
        cbItalic.setSelection( (fontData.getStyle() & SWT.ITALIC) != 0 );
        updatePreview( parent );
    }


    private void selectFontFamilyInList( String fontFamily ) {
        lstFontFamily.deselectAll();
        String[] items = lstFontFamily.getItems();
        for (int i = 0; i < items.length; i++) {
            String item = items[i].toLowerCase( Locale.ENGLISH );
            if (fontFamily.toLowerCase( Locale.ENGLISH ).equals( item )) {
                lstFontFamily.select( i );
            }
        }
    }


    private void updatePreview( Composite parent ) {
        if (lblPreview != null) {
            Display display = parent.getDisplay();
            Font font = new Font( display, fontData );
            lblPreview.setFont( font );
            Color color = new Color( display, rgb );
            lblPreview.setForeground( color );
            lblColor.setBackground( color );
            lblPreview.getParent().layout( true );
        }
    }


    private void updateFontData( Composite parent ) {
        String name = txtFontFamily.getText();
        int height = spFontSize.getSelection();
        int style = SWT.NORMAL;
        if (cbBold.getSelection()) {
            style |= SWT.BOLD;
        }
        if (cbItalic.getSelection()) {
            style |= SWT.ITALIC;
        }
        fontData = new FontData( name, height, style );
        updateControls( parent );
    }
}
