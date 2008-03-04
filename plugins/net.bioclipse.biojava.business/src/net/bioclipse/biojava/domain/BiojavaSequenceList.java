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
package net.bioclipse.biojava.domain;


import java.util.ArrayList;

import net.bioclipse.biojava.business.IBiojavaManager;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.BioObjectList;
import net.bioclipse.core.domain.ISequence;

/**
 * Holds a number of BioJavaSequences in an arraylist
 * @author ola
 *
 */
public class BiojavaSequenceList extends BioObjectList<BiojavaSequence> {

	public BiojavaSequenceList() {
		super();
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}