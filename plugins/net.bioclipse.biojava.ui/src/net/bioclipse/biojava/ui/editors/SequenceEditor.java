/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 *     
 *******************************************************************************/
package net.bioclipse.biojava.ui.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * An MultiPageEditor with Molecules on first tabl, Descriptors on second, and 
 * source in XML on third.
 * 
 * @author ola
 *
 */
public class SequenceEditor extends MultiPageEditorPart implements IResourceChangeListener, IAdaptable{

	/** The text editor used in page 1. */
	private TextEditor textEditor;

	/** Content of the file */
	String content;


	/**
	 * Creates a multi-page editor example.
	 */
	public SequenceEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor,
	 * which consists of the molecules section
	 */
	void createPage0() {
		Composite parent = new Composite(getContainer(), SWT.NONE);
		FillLayout layout=new FillLayout();
		parent.setLayout(layout);

		Text txt=new Text(parent, SWT.NONE);
		txt.setText("This is where the sequence(s) should be visualized. TODO: Implement");

		int index = addPage(parent);
		setPageText(index, "Sequence");

	}


	/**
	 * Creates page 2 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage2() {
		try {
			textEditor = new TextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, "Source");
		} catch (PartInitException e) {
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating nested text editor",
					null,
					e.getStatus());
		}
	}


	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0(); //Molecules
//		createPage1(); //Descriptors
		createPage2(); //qsar.xml


//		getSite().getPage().addSelectionListener(this);
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		textEditor.doSave(monitor);	//Use text editor to save
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 2's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
		//TODO: should it be possible to save as?
		//First we need to update the source ofthe textEditor

		textEditor.doSaveAs();
		setPageText(2, textEditor.getTitle());
		setInput(textEditor.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(1);		//Only page2 has markers currently
		IDE.gotoMarker(textEditor, marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
	throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 0) {
			//TODO: implement!
		}
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)textEditor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(textEditor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}

	/**
	 * 
	 * Provide Adapters for the JmolEditor
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class required) {
		return super.getAdapter(required);
	}

	public IEditorPart getPart(){
		return this;
	}
}