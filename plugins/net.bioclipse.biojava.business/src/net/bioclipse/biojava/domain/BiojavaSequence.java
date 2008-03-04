/*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.biojava.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.bioclipse.core.Recorded;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.ISequence;

import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;

/**
 * @author jonalv, olas
 *
 */
public class BiojavaSequence extends BioObject implements ISequence {

	private RichSequence richSequence;

	/**
	 * Create a BiojavaSequence from a RichSequence
	 * @param richSequence
	 */
	public BiojavaSequence(RichSequence richSequence) {
		super();
		this.richSequence = richSequence;
	}
	
	public BiojavaSequence() {
		// TODO Auto-generated constructor stub
	}

	@Recorded
	public String getPlainSequence() throws IOException {
		String fastaString=toFasta();
		String plainString=StringUtils.removeUntilFirtNewline(fastaString);
		return plainString;
	}

	/**
	 * Convert RichSequence to FAST and return as String
	 * @throws IOException 
	 */
	public String toFasta() throws IOException {

		ByteArrayOutputStream os=new ByteArrayOutputStream();

		Namespace ns = RichObjectFactory.getDefaultNamespace();   
		RichSequence.IOTools.writeFasta(os,richSequence,ns);
		os.close();

		byte[] byteStream = os.toByteArray();

		return new String(byteStream);
	}

	/**
	 * Returns the RichSequence
	 */
	public Object getParsedResource() {
		return richSequence;
	}

	public RichSequence getRichSequence() {
		return richSequence;
	}

	public void setRichSequence(RichSequence richSequence) {
		this.richSequence = richSequence;
	}

	public String getName() {
		return richSequence.getName();
	}
}
