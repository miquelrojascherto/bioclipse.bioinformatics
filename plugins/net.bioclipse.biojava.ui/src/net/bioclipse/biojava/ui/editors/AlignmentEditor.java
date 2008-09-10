/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.biojava.ui.editors;

import net.bioclipse.biojava.ui.views.outline.AlignmentOutlinePage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class AlignmentEditor extends MultiPageEditorPart {

    private Aligner aligner;
    private AlignmentOutlinePage outlinePage;
    
    @Override
    protected void createPages() {
        setPartName( getEditorInput().getName() );
        try {
            int pageIndex1 = this.addPage( aligner = new Aligner(),
                                           getEditorInput() );
            setPageText(pageIndex1, "Alignment");
            int pageIndex2 = this.addPage( new TextEditor(), getEditorInput() );
            setPageText(pageIndex2, "Source");
        } catch ( PartInitException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void doSave( IProgressMonitor monitor ) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class required) {

       // Adapter for Outline
       if (IContentOutlinePage.class.equals(required))
           return outlinePage
               = outlinePage == null
                   ? new AlignmentOutlinePage(getEditorInput(), this)
                   : outlinePage;
       
       return super.getAdapter(required);
   }

    public void zoomIn() {
        aligner.zoomIn();
    }

    public void zoomOut() {
        aligner.zoomOut();
    }
}
