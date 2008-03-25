/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.biojava.ui.views;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.bioclipse.biojava.business.Activator;
import net.bioclipse.biojava.business.IBiojavaManager;
import net.bioclipse.biojava.domain.BiojavaSequenceList;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.domain.ISequence;
import net.bioclipse.core.util.LogUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

/**
 * This ContentProvider hooks into the CNF to list if IResource contains one 
 * or many ISequences.
 * @author ola
 *
 */
public class SequenceContentProvider implements ITreeContentProvider, 
    IResourceChangeListener, IResourceDeltaVisitor {

    private static final Logger logger = Logger.getLogger(SequenceContentProvider.class);
    
	private static final Object[] NO_CHILDREN = new Object[0];

	private final List<String> ISequence_EXT;

	private final Map<IFile, BiojavaSequenceList> cachedModelMap;

	private StructuredViewer viewer;

	IBiojavaManager biojava;

	//Register us as listener for resource changes
	public SequenceContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				this,
				IResourceChangeEvent.POST_CHANGE);
		cachedModelMap = new HashMap<IFile, BiojavaSequenceList>();
		ISequence_EXT=new ArrayList<String>();
		registerFileExtensions();
		biojava=Activator.getDefault().getBioJavaManager();
	}

	
	private void registerFileExtensions() {
		ISequence_EXT.add("EMBL");
		ISequence_EXT.add("SEQ");
		ISequence_EXT.add("FASTA");
		ISequence_EXT.add("GBK");
		//TODO: add more file extensions
	}

	
	public Object[] getChildren(Object parentElement) {
		
		if(parentElement instanceof IFile) {
			IFile modelFile = (IFile) parentElement;
			if (ISequence_EXT.contains(
					modelFile.getFileExtension().toUpperCase())) {	
				
				BiojavaSequenceList col = cachedModelMap.get(modelFile);
				
				if (col != null) {
					Object[] children = col.toArray(new ISequence[0]);
					return children != null ? children : NO_CHILDREN;
				} 
				else if (updateModel(modelFile) != null) {
					
					BiojavaSequenceList col2 = cachedModelMap.get(modelFile);
					if (col2 != null) {					
						Object[] children = col2.toArray(new ISequence[0]);
						return children != null ? children : NO_CHILDREN;
					}						
				}
			}
		}
		return NO_CHILDREN;
	}

	
	/**
	 * If an ISequence, get the IResource
	 */
	public Object getParent(Object element) {

		if (element instanceof IBioObject) {
			IBioObject bobj = (IBioObject) element;
			if (bobj.getResource() != null) return bobj.getResource();
		} 
		else if (element instanceof IResource) {
			IResource res = (IResource) element;
			if (res.getParent() != null) return res.getParent();
		} 
		return null;
	}

	
	public boolean hasChildren(Object element) {
		if (element instanceof ISequence) {
			return false;		
		} 
		else if(element instanceof IFile) {
			return ISequence_EXT.contains(
					((IFile) element).getFileExtension().toUpperCase());
		}
		return false;
	}

	
	public Object[] getElements(Object parentElement) {
		return getChildren(parentElement);
	}

	
	/**
	 * We need to remove listener and dispose of cache on exit
	 */
	public void dispose() {
		cachedModelMap.clear();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this); 
	}

	
	/**
	 * When input changes, clear cache so that we will reload content later
	 */
	public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
		if (oldInput != null && !oldInput.equals(newInput))
			cachedModelMap.clear();
		viewer = (StructuredViewer) aViewer;
	}

	
	/**
	 * If resources changed
	 */
	public void resourceChanged(IResourceChangeEvent event) {

		//we are only interested in POST_CHANGE events
		if (event.getType() != IResourceChangeEvent.POST_CHANGE)
			return;
		IResourceDelta rootDelta = event.getDelta();
		//get the delta, if any, for the documentation directory
		IResourceDelta docDelta = rootDelta;
		try {
			docDelta.accept(this);
		} catch (CoreException e) {
			logger.error("SequenceContentProvider.resourceChanged() caught "
			        + "CoreException visiting docDelta");
		    logger.debug(LogUtils.traceStringFrom(e));
		}
		if (docDelta == null)
			return;
	}

	
	/**
	 * Handle deltas for resource changes
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {

		IResource resource = delta.getResource();

		// Only care about IFile with correct extension
		if (resource == null) return true;
		if (!(resource.getType() == IResource.FILE && 
				ISequence_EXT.contains(
						resource.getFileExtension().toUpperCase())))
			return true;

		final IFile file = (IFile) resource;

		switch (delta.getKind()) {
		case IResourceDelta.ADDED :
			// handle added resource
			if (logger.isDebugEnabled()) 
			    logger.debug("Not implemented: TODO: Handle added resource: " + file.getName());
			break;
		case IResourceDelta.REMOVED :
			// handle removed resource
		    if (logger.isDebugEnabled()) 
		        logger.debug("Not implemented: TODO: Handle removed resource: " + file.getName());
			//TODO
			break;
		case IResourceDelta.CHANGED :
			// handle changed resource
		    if (logger.isDebugEnabled()) 
		        logger.debug("Not implemented: TODO: Handle changed resource: " + file.getName());
			break;
		}

		//Update the model for the viewer
		updateModel(file);

		//Post the update to the viewer
		Display display = viewer.getControl().getDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				public void run() {
					//make sure the viewer still exists
					if (viewer.getControl().isDisposed())
						return;
					viewer.refresh(file);
				}
			});
		}
		return true;
	}


	/**
	 * Load the model from the given file, if possible.  
	 * @param modelFile The IFile which contains the persisted model 
	 */ 
	private synchronized BiojavaSequenceList updateModel(IFile modelFile) { 

		if(ISequence_EXT.contains(modelFile.getFileExtension().toUpperCase())) {
			BiojavaSequenceList model;
			if (modelFile.exists()) {
				try {
					model = biojava.loadSequences(
					        modelFile.getLocation().toOSString());
				} catch (IOException e) {
					return null;
				} catch (BioclipseException e) {
					return null;
				}

				if (model == null) return null;
				
				if(logger.isDebugEnabled()) {
				    logger.debug("File: "+ modelFile+ " contained: "
				            + model.size() + " ISequences");
				}

				cachedModelMap.put(modelFile, model);
				return model;

			} else {
				cachedModelMap.remove(modelFile);
			}
		}
		return null; 
	}
}
