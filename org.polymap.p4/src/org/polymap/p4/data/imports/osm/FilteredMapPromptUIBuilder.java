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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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

    private Text keyFilterText, valueFilterText;

    private org.eclipse.swt.widgets.List keyList, valueList;


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

        Composite keyFilter = createKeyFilter( parent, keys.toArray( new String[keys.size()] ),
                initiallySelectedItem.getKey() );
        Composite valueFilter = createValueFilter( parent, values.toArray( new String[values.size()] ),
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
            if(!"*".equals( item.getKey())) {
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

        on( valueFilter ).left( 0 ).right( 100 ).top( keyFilter, 20 );
        on( addButton ).left( 0 ).right( 100 ).top( valueFilter, 10 );
        on( selectedFiltersComp ).left( 0 ).right( 100 ).top( addButton, 20 );
        on( deleteButton ).left( 0 ).right( 100 ).top( selectedFiltersComp, 10 );

        parent.pack();
        keyList.showSelection();
        valueList.showSelection();
    }


    private Composite createKeyFilter( Composite parent, String[] listItems, String initiallySelectedItem ) {
        Composite filterComposite = new Composite( parent, SWT.NONE );
        filterComposite.setLayout( FormLayoutFactory.defaults().spacing( 0 ).create() );
        Label label = on( new Label( filterComposite, SWT.NONE ) )
                .fill().noBottom().control();
        label.setText( getKeyLabel() );

        keyFilterText = on( new Text( filterComposite, SWT.BORDER ) )
                .left( 0 ).top( label ).right( 100 ).control();
        keyFilterText.setToolTipText( getKeyFilterTooltip() );
        keyFilterText.forceFocus();

        keyList = on( new org.eclipse.swt.widgets.List( filterComposite, SWT.V_SCROLL ) )
                .fill().top( keyFilterText, 10 ).width( 250 ).height( 150 ).control();

        keyList.setItems( listItems );
        keyList.setSelection( new String[] { initiallySelectedItem } );
        
        keyList.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                String item = getSelectedItem( keyList );
                handleKeySelection( item );
            }
        } );
        keyFilterText.addModifyListener( new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent event ) {
                List<String> filtered = filterSelectableKeys( keyFilterText.getText() );
                keyList.setItems( filtered.toArray( new String[filtered.size()] ) );
                if (keyList.getItems().length > 0) {
                    keyList.select( 0 );
                    handleKeySelection( getSelectedItem( keyList ) );
                }
            }
        } );
        return filterComposite;
    }


    private Composite createValueFilter( Composite parent, String[] listItems, String initiallySelectedItem ) {
        Composite filterComposite = new Composite( parent, SWT.NONE );
        filterComposite.setLayout( FormLayoutFactory.defaults().spacing( 0 ).create() );
        Label label = on( new Label( filterComposite, SWT.NONE ) )
                .fill().noBottom().control();
        label.setText( getValueLabel() );

        valueFilterText = on( new Text( filterComposite, SWT.BORDER ) )
                .left( 0 ).top( label ).right( 100 ).control();
        valueFilterText.setToolTipText( getValueFilterTooltip() );
        valueFilterText.forceFocus();

        valueList = on( new org.eclipse.swt.widgets.List( filterComposite, SWT.V_SCROLL ) )
                .fill().top( valueFilterText, 10 ).width( 250 ).height( 150 ).control();

        valueList.setItems( listItems );
        valueList.setSelection( new String[] { initiallySelectedItem } );

        valueFilterText.addModifyListener( new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent event ) {
                List<String> filtered = filterSelectableValues( valueFilterText.getText() );
                valueList.setItems( filtered.toArray( new String[filtered.size()] ) );
                if (valueList.getItems().length > 0) {
                    valueList.select( 0 );
                }
            }
        } );
        return filterComposite;
    }


    protected void handleKeySelection( String selectedItem ) {
        valueList.removeAll();
        valueList.add( "*" );
        for (String value : listItems().get( selectedItem )) {
            valueList.add( value );
        }
        valueList.select( 0 );
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


    private String getSelectedItem( org.eclipse.swt.widgets.List list ) {
        return list.getItem( list.getSelectionIndex() );
    }
}
