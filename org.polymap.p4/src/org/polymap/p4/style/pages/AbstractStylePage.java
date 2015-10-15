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
package org.polymap.p4.style.pages;

import org.polymap.p4.style.daos.IStyleDao;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.IPanelSite;
import org.polymap.rhei.field.IFormFieldListener;
import org.polymap.rhei.form.DefaultFormPage;

/**
 * @author Joerg Reichert <joerg@mapzone.io>
 *
 */
public abstract class AbstractStylePage<T extends IStyleDao>
        extends DefaultFormPage
        implements IFormFieldListener {

    private final IAppContext context;

    private final IPanelSite  panelSite;

    private T                 dao;


    public AbstractStylePage( IAppContext context, IPanelSite panelSite ) {
        this.context = context;
        this.panelSite = panelSite;
        this.dao = createEmptyDao();
    }


    public abstract T createEmptyDao();


    protected IAppContext getContext() {
        return context;
    }


    protected IPanelSite getPanelSite() {
        return panelSite;
    }


    public T getDao() {
        return dao;
    }


    public void setDao( T dao ) {
        this.dao = dao;
    }
}
