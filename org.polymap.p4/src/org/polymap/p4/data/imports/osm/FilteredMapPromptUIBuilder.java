/*
 * polymap.org Copyright (C) 2015 individual contributors as indicated by the
 * 
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

import static org.polymap.core.ui.FormDataFactory.on;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.polymap.core.ui.FormLayoutFactory;
import org.polymap.p4.data.imports.ImporterPrompt;
import org.polymap.p4.data.imports.ImporterPrompt.PromptUIBuilder;
import org.polymap.rhei.batik.toolkit.IPanelToolkit;

/**
 * 
 * @author Joerg Reichert <joerg@mapzone.io>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public abstract class FilteredMapPromptUIBuilder
        implements PromptUIBuilder {

    private Combo keyList, valueList;
    private SimpleContentProposalProvider valueProposalProvider;


    protected abstract SortedMap<String,SortedSet<String>> listItems();


    protected abstract List<Pair<String,String>> initiallySelectedItems();


    protected abstract void handleSelection( Pair<String,String> selectedItem );


    protected abstract void handleUnselection( Pair<String,String> selectedItem );


    @Override
    public void createContents( ImporterPrompt prompt, Composite parent, IPanelToolkit tk ) {
        parent.setLayout( FormLayoutFactory.defaults().spacing( 0 ).create() );

        Set<String> keys = listItems().keySet();
        Pair<String,String> initiallySelectedItem = null;
        if (initiallySelectedItems().size() > 0) {
            initiallySelectedItem = initiallySelectedItems().get( 0 );
        }
        else {
            initiallySelectedItem = Pair.of( "*", "*" );
        }
        SortedSet<String> values = listItems().get( initiallySelectedItem.getKey() );
        if (values == null) {
            values = new TreeSet<String>();
        }
        values.add( "*" );

        Composite filterComposite = new Composite( parent, SWT.NONE );
        filterComposite.setLayout( new GridLayout( 2, false ) );
        createKeyFilter( filterComposite, keys.toArray( new String[keys.size()] ),
                initiallySelectedItem.getKey() );
        createValueFilter( filterComposite, values.toArray( new String[values.size()] ),
                initiallySelectedItem.getValue() );

        Composite selectedFiltersComp = new Composite( parent, SWT.NONE );
        selectedFiltersComp.setLayout( FormLayoutFactory.defaults().spacing( 0 ).create() );
        Label selectedFiltersLabel = on( new Label( selectedFiltersComp, SWT.NONE ) )
                .fill().noBottom().control();
        selectedFiltersLabel.setText( getSelectedLabel() );
        org.eclipse.swt.widgets.List selectedFilters = on(
                new org.eclipse.swt.widgets.List( selectedFiltersComp, SWT.V_SCROLL ) )
                .fill().top( selectedFiltersLabel, 10 ).width( 250 ).height( 150 ).control();
        for (Pair<String,String> item : initiallySelectedItems()) {
            if (!"*".equals( item.getKey() )) {
                selectedFilters.add( item.getKey() + "=" + item.getValue() );
            }
        }
        on( selectedFilters ).fill().top( selectedFiltersLabel );

        Button addButton = new Button( parent, SWT.NONE );
        addButton.setText( "Add Filter" );
        addButton.addSelectionListener( new SelectionAdapter() {

            public void widgetSelected( SelectionEvent e ) {
                Pair<String,String> selection = Pair.of( getSelectedItem( keyList ), getSelectedItem( valueList ) );
                String newItem = selection.getKey() + "=" + selection.getValue();
                if (!Arrays.asList( selectedFilters.getItems() ).contains( newItem )) {
                    selectedFilters.add( newItem );
                    handleSelection( selection );
                }
            }
        } );
        Button deleteButton = new Button( parent, SWT.NONE );
        deleteButton.setText( "Remove Filter" );
        deleteButton.addSelectionListener( new SelectionAdapter() {

            public void widgetSelected( SelectionEvent e ) {
                for (String selectedItem : selectedFilters.getSelection()) {
                    selectedFilters.remove( selectedItem );
                    String[] parts = selectedItem.split( "=" );
                    handleUnselection( Pair.of( parts[0], parts[1] ) );
                }
            }
        } );

        on( filterComposite ).left( 0 ).right( 100 );
        on( addButton ).left( 0 ).right( 100 ).top( filterComposite, 10 );
        on( selectedFiltersComp ).left( 0 ).right( 100 ).top( addButton, 20 );
        on( deleteButton ).left( 0 ).right( 100 ).top( selectedFiltersComp, 10 );

        parent.pack();
    }


    private void createKeyFilter( Composite filterComposite, String[] listItems, String initiallySelectedItem ) {
        Label label = new Label( filterComposite, SWT.NONE );
        label.setText( getKeyLabel() );

        ComboViewer comboViewer = new ComboViewer( filterComposite, SWT.DROP_DOWN );
        keyList = comboViewer.getCombo();
        keyList.setItems( listItems );
        keyList.select( Arrays.asList( listItems ).indexOf( initiallySelectedItem ) );
        keyList.setToolTipText( getKeyFilterTooltip() );

        SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider( keyList.getItems() );
        ContentProposalAdapter proposalAdapter = new ContentProposalAdapter(
                keyList,
                new ComboContentAdapter(),
                proposalProvider,
                getActivationKeystroke(),
                getAutoactivationChars() );
        proposalProvider.setFiltering( true );
        proposalAdapter.setPropagateKeys( true );
        proposalAdapter.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );
        proposalAdapter.addContentProposalListener( prop -> handleKeySelection( prop.getContent() ));

        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        keyList.setLayoutData( data );
    }

    private static final String LCL = "abcdefghijklmnopqrstuvwxyz";
    private static final String UCL = LCL.toUpperCase();
    private static final String NUMS = "0123456789";
    
    // this logic is from swt addons project
    static char[] getAutoactivationChars() {

        // To enable content proposal on deleting a char

        String delete = new String( new char[] { 8 } );
        String allChars = LCL + UCL + NUMS + delete;
        return allChars.toCharArray();
    }

    static KeyStroke getActivationKeystroke() {
        KeyStroke instance = KeyStroke.getInstance(
                new Integer( SWT.CTRL ).intValue(), new Integer( ' ' ).intValue() );
        return instance;
    }


    private void createValueFilter( Composite filterComposite, String[] listItems, String initiallySelectedItem ) {
        Label label = new Label( filterComposite, SWT.NONE );
        label.setText( getValueLabel() );

        ComboViewer comboViewer = new ComboViewer( filterComposite, SWT.DROP_DOWN );
        valueList = comboViewer.getCombo();
        valueList.setItems( listItems );
        valueList.select( Arrays.asList( listItems ).indexOf( initiallySelectedItem ) );
        valueList.setToolTipText( getValueFilterTooltip() );

        valueProposalProvider = new SimpleContentProposalProvider( listItems );
        ContentProposalAdapter proposalAdapter = new ContentProposalAdapter(
                valueList,
                new ComboContentAdapter(),
                valueProposalProvider,
                getActivationKeystroke(),
                getAutoactivationChars() );
        valueProposalProvider.setFiltering( true );
        proposalAdapter.setPropagateKeys( true );
        proposalAdapter.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );

        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        valueList.setLayoutData( data );
    }


    protected void handleKeySelection( String selectedItem ) {
        valueList.removeAll();
        valueList.add( "*" );
        for (String value : listItems().get( selectedItem )) {
            valueList.add( value );
        }
        valueList.select( 0 );
        valueList.setText( "" );
        valueProposalProvider.setProposals( valueList.getItems() );
    }


    protected String getKeyLabel() {
        return "Key:";
    }


    protected String getKeyFilterTooltip() {
        return "Key of tag";
    }


    protected String getValueLabel() {
        return "Value:";
    }


    protected String getValueFilterTooltip() {
        return "Value of tag";
    }


    protected String getSelectedLabel() {
        return "Selected tag filters";
    }


    protected List<String> filterSelectableKeys( String text ) {
        return listItems().keySet().stream()
                .filter( item -> item.toLowerCase().contains( text.toLowerCase() ) )
                .collect( Collectors.toList() );
    }


    protected List<String> filterSelectableValues( String text ) {
        return listItems().get( getSelectedItem( keyList ) ).stream()
                .filter( item -> item.toLowerCase().contains( text.toLowerCase() ) )
                .collect( Collectors.toList() );
    }


    private String getSelectedItem( Combo combo ) {
        return combo.getItem( combo.getSelectionIndex() );
    }
}
