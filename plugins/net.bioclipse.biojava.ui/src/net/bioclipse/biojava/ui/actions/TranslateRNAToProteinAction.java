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
package net.bioclipse.biojava.ui.actions;

import java.io.IOException;

import net.bioclipse.core.domain.ISequence;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Action to translate an ISequence to DNA
 * @author ola
 *
 */
public class TranslateRNAToProteinAction extends TranslateAction{

	@Override
	public ISequence convert(ISequence sequence) throws IOException {
		
		System.out.println("Not implemented: TODO");

		return null;
	}
	
}
