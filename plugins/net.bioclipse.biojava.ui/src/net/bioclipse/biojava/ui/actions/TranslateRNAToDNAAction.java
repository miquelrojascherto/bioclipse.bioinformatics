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

import org.apache.log4j.Logger;

import net.bioclipse.core.domain.ISequence;


/**
 * Action to translate an ISequence to DNA
 * @author ola
 *
 */
public class TranslateRNAToDNAAction extends TranslateAction{

    private static final Logger logger = Logger.getLogger(TranslateRNAToDNAAction.class);
    
	@Override
	public ISequence convert(ISequence sequence) throws IOException {
		logger.warn("TranslateRNAToDNAAction not implemented: TODO");
		return null;
	}	
}
