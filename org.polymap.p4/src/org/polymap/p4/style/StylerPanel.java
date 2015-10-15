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

import static org.polymap.rhei.batik.toolkit.md.dp.dp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.styling.StyledLayerDescriptor;
import org.polymap.core.ui.FormDataFactory;
import org.polymap.core.ui.FormLayoutFactory;
import org.polymap.p4.P4Plugin;
import org.polymap.p4.map.ProjectMapPanel;
import org.polymap.p4.style.StylerDAO.FeatureType;
import org.polymap.p4.style.color.IColorInfo;
import org.polymap.p4.style.font.IFontInfo;
import org.polymap.p4.style.icon.IImageInfo;
import org.polymap.p4.style.pages.LabelPage;
import org.polymap.p4.style.pages.StyleIdentPage;
import org.polymap.p4.style.pages.StylePage;
import org.polymap.rhei.batik.Context;
import org.polymap.rhei.batik.DefaultPanel;
import org.polymap.rhei.batik.PanelIdentifier;
import org.polymap.rhei.batik.Scope;
import org.polymap.rhei.batik.toolkit.md.AbstractFeedbackComponent;
import org.polymap.rhei.batik.toolkit.md.MdListViewer;
import org.polymap.rhei.batik.toolkit.md.MdTabFolder;
import org.polymap.rhei.batik.toolkit.md.MdToast;
import org.polymap.rhei.batik.toolkit.md.MdToolkit;
import org.polymap.rhei.form.batik.BatikFormContainer;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public class StylerPanel
        extends DefaultPanel {

    public static final PanelIdentifier ID          = PanelIdentifier.parse( "styler" );

    private BatikFormContainer          styleIdentPageContainer;

    private BatikFormContainer          labelPageContainer;

    private BatikFormContainer          stylePageContainer;

    private StylerDAO                   styleDAO;

    @Scope(P4Plugin.Scope)
    private Context<IImageInfo>         imageInfo;

    @Scope(P4Plugin.Scope)
    private Context<IColorInfo>         colorInfo;

    @Scope(P4Plugin.Scope)
    private Context<IFontInfo>          fontInfo;

    private StyleIdentPage              identPage;

    private LabelPage                   labelPage;

    private StylePage                   stylePage;

    private String                      lastOpenTab = null;

    private Button                      newButton, loadButton, saveButton, deleteButton;

    private List<StyledLayerDescriptor> styles      = new ArrayList<StyledLayerDescriptor>();

    private MdListViewer                styleList;

    private Text                        styleListFilter;

    private MdToast mdToast;


    @Override
    public boolean wantsToBeShown() {
        return parentPanel().filter( parent -> parent instanceof ProjectMapPanel ).map( parent -> {
            setTitle();
            getSite().setPreferredWidth( 350 );
            return true;
        } ).orElse( false );
    }


    private void setTitle() {
        getSite().setTitle( "Styler" );
    }


    @Override
    public void createContents( Composite parent ) {
        setTitle();
        parent.setLayout( FormLayoutFactory.defaults().spacing( dp( 16 ).pix() ).create() );
        
        mdToast = ((MdToolkit) getSite().toolkit()).createToast( 60, SWT.NONE );

        styleDAO = new StylerDAO();
        identPage = new StyleIdentPage( getSite(), styleDAO );
        styleIdentPageContainer = new BatikFormContainer( identPage );
        labelPage = new LabelPage( getContext(), getSite(), styleDAO, fontInfo );
        labelPageContainer = new BatikFormContainer( labelPage );
        stylePage = new StylePage( getContext(), getSite(), styleDAO, imageInfo, colorInfo );
        stylePageContainer = new BatikFormContainer( stylePage );

        internalCreateContents( parent );
    }


    private void internalCreateContents( Composite parent ) {
        MdToolkit tk = (MdToolkit)getSite().toolkit();
        Function<Composite,Composite> styleIdentTabItemContent = createStyleIdentTabItemContent( tk );
        Function<Composite,Composite> labelTabItemContent = createLabelTabItemContent( tk );
        Function<Composite,Composite> styleTabItemContent = createStyleTabItemContent( tk );
        String styleIdentStr = "Identification", labelStr = "Label", styleStr = "Geometry Style";
        List<String> tabItems = new ArrayList<String>();
        tabItems.add( styleIdentStr );
        tabItems.add( labelStr );
        tabItems.add( styleStr );
        Map<String,Function<Composite,Composite>> tabContents = new HashMap<String,Function<Composite,Composite>>();
        tabContents.put( styleIdentStr, styleIdentTabItemContent );
        tabContents.put( labelStr, labelTabItemContent );
        tabContents.put( styleStr, styleTabItemContent );
        MdTabFolder tabFolder = tk.createTabFolder( parent, tabItems, tabContents );
        tabFolder.openTab( getLastOpenTab() );
        tabFolder.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                setLastOpenTab( ((Button)e.widget).getText() );
            }
        } );
        FormDataFactory.on( tabFolder ).left( 0 ).right( 100 );

        Composite styleListFilterComp = createStyleListFilter( parent, tk );
        FormDataFactory.on( styleListFilterComp ).top( tabFolder, dp( 30 ).pix() ).left( 0 ).right( 100 );

        createStyleList( parent, tk );

        FormDataFactory.on( styleList.getControl() ).top( styleListFilterComp, dp( 30 ).pix() ).left( 0 ).right( 100 );

        createButtons( parent, tk );

        // try {
        // new StylePreview().createPreviewMap( parent, getStylerDao() );
        // }
        // catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }


    private void createButtons( Composite parent, MdToolkit tk ) {
        Composite buttonBar = tk.createComposite( parent, SWT.NONE );
        FormDataFactory.on( buttonBar ).top( styleList.getControl(), dp( 30 ).pix() ).left( 0 ).right( 100 );
        buttonBar.setLayout( FormLayoutFactory.defaults().spacing( dp( 16 ).pix() ).create() );

        createNewButton( tk, buttonBar );
        createSaveButton( tk, buttonBar );
        createLoadButton( tk, buttonBar );
        createDeleteButton( tk, buttonBar );

        FormDataFactory.on( newButton );
        FormDataFactory.on( saveButton ).left( newButton, dp( 30 ).pix() );
        FormDataFactory.on( loadButton ).left( saveButton, dp( 30 ).pix() );
        FormDataFactory.on( deleteButton ).left( loadButton, dp( 30 ).pix() );
    }


    private void createDeleteButton( MdToolkit tk, Composite buttonBar ) {
        deleteButton = tk.createButton( buttonBar, "Delete Style", SWT.NONE );
        deleteButton.addSelectionListener( new SelectionAdapter() {

            public void widgetSelected( SelectionEvent e ) {
                IStructuredSelection selection = (IStructuredSelection)styleList.getSelection();
                StyledLayerDescriptor style = (StyledLayerDescriptor)selection.getFirstElement();
                // TODO delete
                styles.remove( style );
                styleList.refresh();
                styleDAO = new StylerDAO();
            }
        } );
    }


    private void createLoadButton( MdToolkit tk, Composite buttonBar ) {
        loadButton = tk.createButton( buttonBar, "Load Style", SWT.NONE );
        loadButton.addSelectionListener( new SelectionAdapter() {

            public void widgetSelected( SelectionEvent e ) {
                IStructuredSelection selection = (IStructuredSelection)styleList.getSelection();
                StyledLayerDescriptor style = (StyledLayerDescriptor)selection.getFirstElement();
                styleDAO = new StylerDAO( style );
            }
        } );
    }

    private void createNewButton( MdToolkit tk, Composite buttonBar ) {
        newButton = tk.createButton( buttonBar, "New Style", SWT.NONE );
        newButton.addSelectionListener( new SelectionAdapter() {

            public void widgetSelected( SelectionEvent e ) {
                styleDAO = new StylerDAO();
                try {
                    styleIdentPageContainer.reloadEditor();
                    labelPageContainer.reloadEditor();
                    stylePageContainer.reloadEditor();
                } catch(Exception exc) {
                    mdToast.showIssue( AbstractFeedbackComponent.MessageType.ERROR, exc.getMessage() );
                }
            }
        } );
    }


    private void createSaveButton( MdToolkit tk, Composite buttonBar ) {
        saveButton = tk.createButton( buttonBar, "Save Style", SWT.NONE );
        saveButton.addSelectionListener( new SelectionAdapter() {

            public void widgetSelected( SelectionEvent e ) {
                try {
                    styleIdentPageContainer.submit();
                    labelPageContainer.submit();
                    stylePageContainer.submit();
                } catch(Exception exc) {
                    mdToast.showIssue( AbstractFeedbackComponent.MessageType.ERROR, exc.getMessage() );
                }
                StyledLayerDescriptor sld = styleDAO.toSLD();
                styles.add( sld );
                styleList.setInput( styles );
                styleList.setSelection( new StructuredSelection(sld));
            }
        } );
    }


    private void createStyleList( Composite parent, MdToolkit tk ) {
        styleList = tk.createListViewer( parent, SWT.NONE );
        styleList.firstLineLabelProvider.set( new CellLabelProvider() {

            @Override
            public void update( ViewerCell cell ) {
                StyledLayerDescriptor desc = (StyledLayerDescriptor)cell.getElement();
                cell.setText( desc.getTitle() == null ? desc.getName() : desc.getTitle() );
            }
        } );
        styleList.setContentProvider( new ITreeContentProvider() {

            List<StyledLayerDescriptor> styles;


            @SuppressWarnings("unchecked")
            @Override
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
                styles = (List<StyledLayerDescriptor>)newInput;
            }


            @Override
            public void dispose() {
            }


            @Override
            public boolean hasChildren( Object element ) {
                return false;
            }


            @Override
            public Object getParent( Object element ) {
                return null;
            }


            @Override
            public Object[] getElements( Object inputElement ) {
                return styles.toArray();
            }


            @Override
            public Object[] getChildren( Object parentElement ) {
                return null;
            }
        } );
        styleList.setInput( styles );
    }


    private Composite createStyleListFilter( Composite parent, MdToolkit tk ) {
        Composite styleListFilterForm = tk.createComposite( parent, SWT.NONE );
        styleListFilterForm.setLayout( FormLayoutFactory.defaults().spacing( dp( 16 ).pix() ).create() );

        Label label = tk.createLabel( styleListFilterForm, "Filter:", SWT.NONE );
        
        styleListFilter = tk.createText( styleListFilterForm, "", SWT.NONE );
        styleListFilter.addModifyListener( new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent event ) {
                styleList.setInput( styles.stream().filter(
                        style -> style.getName().startsWith( styleListFilter.getText() ) ) );
            }
        } );
        
        FormDataFactory.on( styleListFilter ).left( label, dp( 30 ).pix() ).right( 100 );
        
        return styleListFilterForm;
    }


    public String getLastOpenTab() {
        return this.lastOpenTab;
    }


    public void setLastOpenTab( String tabname ) {
        this.lastOpenTab = tabname;
    }


    private Function<Composite,Composite> createStyleIdentTabItemContent( MdToolkit tk ) {
        return new Function<Composite,Composite>() {

            @Override
            public Composite apply( Composite parent ) {
                return createStyleIdentTabItemContent( tk, parent );
            }
        };
    }


    private Function<Composite,Composite> createStyleTabItemContent( MdToolkit tk ) {
        return new Function<Composite,Composite>() {

            @Override
            public Composite apply( Composite parent ) {
                return createStyleTabItemContent( tk, parent );
            }
        };
    }


    private Function<Composite,Composite> createLabelTabItemContent( MdToolkit tk ) {
        return new Function<Composite,Composite>() {

            @Override
            public Composite apply( Composite parent ) {
                return createLabelTabItemContent( tk, parent );
            }
        };
    }


    private Composite createStyleIdentTabItemContent( MdToolkit tk, Composite parent ) {
        Composite styleIdentComposite = tk.createComposite( parent, SWT.NONE );
        styleIdentPageContainer.createContents( styleIdentComposite );
        return styleIdentComposite;
    }


    private Composite createLabelTabItemContent( MdToolkit tk, Composite parent ) {
        Composite labelComposite = tk.createComposite( parent, SWT.NONE );
        labelPageContainer.createContents( labelComposite );
        return labelComposite;
    }


    private Composite createStyleTabItemContent( MdToolkit tk, Composite parent ) {
        Composite styleComposite = tk.createComposite( parent, SWT.NONE );
        // FeatureSource fs = null;
        // Style style = new DefaultStyles().findStyle( fs );
        FeatureType featureType = styleDAO.getFeatureType();
        switch (featureType) {
            case POINT: {
                stylePageContainer.createContents( styleComposite );
                break;
            }
            case LINE_STRING: {
                stylePageContainer.createContents( styleComposite );
                break;
            }
            case POLYGON: {
                stylePageContainer.createContents( styleComposite );
                break;
            }
            case RASTER: {
                stylePageContainer.createContents( styleComposite );
                break;
            }
        }
        return styleComposite;
    }
}
