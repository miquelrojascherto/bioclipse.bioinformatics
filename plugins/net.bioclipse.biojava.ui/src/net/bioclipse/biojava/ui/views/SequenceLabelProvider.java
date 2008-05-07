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


import net.bioclipse.biojava.domain.BiojavaSequenceList;
import net.bioclipse.biojava.ui.Activator;
import net.bioclipse.core.domain.ISequence;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

/**
 * This Provider provides text, image, and description for Molecules
 * @author ola
 *
 */
public class SequenceLabelProvider implements ILabelProvider, IDescriptionProvider {

    private Image sequenceImage;

    public Image getImage(Object element) {
        if (element instanceof BiojavaSequenceList) {
            if (sequenceImage==null){
                ImageDescriptor descriptor=Activator.getImageDescriptor("icons/sequence4.gif");
                sequenceImage= descriptor.createImage();
            }
            return sequenceImage;
        }
        else if (element instanceof ISequence) {
            if (sequenceImage==null){
                ImageDescriptor descriptor=Activator.getImageDescriptor("icons/sequence4.gif");
                sequenceImage= descriptor.createImage();
            }
            return sequenceImage;
        }
        String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
        return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
    }

    public String getText(Object element) {
        if (element instanceof ISequence) {
            ISequence seq = (ISequence) element;
            return seq.getName();
        }
        else if (element instanceof BiojavaSequenceList) {
            BiojavaSequenceList col = (BiojavaSequenceList) element;
            return col.getName();
        }
        return null;
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public void dispose() {
        if (sequenceImage!=null) sequenceImage.dispose();
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

    public String getDescription(Object anElement) {
        return null;
    }

}
