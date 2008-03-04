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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import net.bioclipse.biojava.business.Activator;
import net.bioclipse.biojava.business.BiojavaManager;
import net.bioclipse.biojava.business.IBiojavaManager;
import net.bioclipse.biojava.domain.BiojavaSequenceList;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IAASequence;
import net.bioclipse.core.domain.IDNASequence;
import net.bioclipse.core.domain.IRNASequence;
import net.bioclipse.core.domain.ISequence;

import org.apache.log4j.Logger;
import org.biojavax.bio.db.HashRichSequenceDB;
import org.biojavax.bio.db.RichSequenceDB;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * This ContentProvider hooks into the CNF to list if IResource contains one 
 * or many ISequences.
 * @author ola
 *
 */
public class SequenceContentProvider implements ITreeContentProvider, 
IResourceChangeListener, IResourceDeltaVisitor {

//	private static final Logger logger = Activator.getLogManager()
//	.getLogger(SequenceContentProvider.class.toString());


	private static final Object[] NO_CHILDREN = new Object[0];

	private final List<String> ISequence_EXT;

	private final Map<IFile, BiojavaSequenceList> cachedModelMap;

	private StructuredViewer viewer;

	IBiojavaManager biojava;

	//Register us as listener for resource changes
	public SequenceContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
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
		Object[] children = null;
		if(parentElement instanceof IFile) {
			/* possible model file */
			IFile modelFile = (IFile) parentElement;
			if(ISequence_EXT.contains(modelFile.getFileExtension().toUpperCase())) {				
				BiojavaSequenceList col=cachedModelMap.get(modelFile);
				if (col!=null){
					children = col.toArray(new ISequence[0]);
					return children != null ? children : NO_CHILDREN;
				}else{
					if (updateModel(modelFile)!=null){
						BiojavaSequenceList col2=cachedModelMap.get(modelFile);
						if (col2!=null){
							children = col2.toArray(new ISequence[0]);
//							if (children!=null){
//								if (children[0] instanceof IDNASequence) {
//									System.out.println("child is DNASeq");
//								}
//								if (children[0] instanceof IRNASequence) {
//									System.out.println("child is RNASeq");
//								}
//								if (children[0] instanceof IAASequence) {
//									System.out.println("child is AASeq");
//								}
//							}
							return children != null ? children : NO_CHILDREN;
						}						
					}
				}
			}
		}   
		return children != null ? children : NO_CHILDREN;
	}

	/**
	 * If an ISequence, get the IResource
	 */
	public Object getParent(Object element) {
		if (element instanceof BiojavaSequenceList) {
			BiojavaSequenceList seq = (BiojavaSequenceList) element;
			return seq.getResource();
		} 
		else if (element instanceof ISequence) {
			//TODO?
		} 
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof ISequence) {
			return false;		
		} else if(element instanceof IFile) {
			return ISequence_EXT.equals(((IFile) element).getFileExtension());
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
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}



	/**
	 * Load the model from the given file, if possible.  
	 * @param modelFile The IFile which contains the persisted model 
	 */ 
	private synchronized BiojavaSequenceList updateModel(IFile modelFile) { 

		if(ISequence_EXT.contains(modelFile.getFileExtension().toUpperCase()) ) {
			BiojavaSequenceList model;
			if (modelFile.exists()) {

				try {
					model= biojava.loadSequences(modelFile.getLocation().toOSString());
				} catch (IOException e) {
					return null;
				} catch (BioclipseException e) {
					return null;
				}

				if (model==null) return null;
				System.out.println("File: " + modelFile + " contained: " + model.size() + " ISequences");

				cachedModelMap.put(modelFile, model);
				return model;

			} else {
				cachedModelMap.remove(modelFile);
			}
		}
		return null; 
	}
}
