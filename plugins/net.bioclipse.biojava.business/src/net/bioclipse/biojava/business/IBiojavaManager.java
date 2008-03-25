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

package net.bioclipse.biojava.business;

import java.io.IOException;
import java.io.InputStream;

import net.bioclipse.biojava.domain.BiojavaAASequence;
import net.bioclipse.biojava.domain.BiojavaDNASequence;
import net.bioclipse.biojava.domain.BiojavaRNASequence;
import net.bioclipse.biojava.domain.BiojavaSequence;
import net.bioclipse.biojava.domain.BiojavaSequenceList;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;

/**
 * @author jonalv, ola
 */
public interface IBiojavaManager extends IBioclipseManager {

	/**
	 * Enum over the sequence formats supported by the Biojavamanager
	 * 
	 * @author jonalv
	 *
	 */
	public enum SequenceFormat {
		FASTA, 
		EMBL,
		GENBANK,
		UNIPROT;
	}

	/**
	 * Converts a BiojavaSequence to a String representation in a given format
	 * 
	 * @param format the format of the resulting string
	 * @param seq the sequence to convert
	 * @return a string representation of a Biojavasequence
	 * @throws IOException 
	 * @throws ScriptingException 
	 */
	@Recorded
	public String renderAs( SequenceFormat format, 
			                BiojavaSequence seq) throws IOException;

	//TODO renderAs giving a stream?
	
	/**
	 * Loads a BiojavaSequence from file
	 * 
	 * @param path The path to the file
	 * @return a BioJavaSequence object
	 * @throws IOException 
 	 */
	@Recorded
	public BiojavaSequence loadSequence( String path ) throws IOException;
	

	 /**
	 * Load sequenceCollection from InputStream
	  * @param instream the Stream to read from
	  * @return A BiojavaSequenceCollection object
	 * @throws BioclipseException 
	  */
	@Recorded
	public BiojavaSequenceList loadSequences(InputStream instream) throws BioclipseException;

	 /**
	 * Load sequenceCollection from InputStream
	  * @param instream the Stream to read from
	  * @return A BiojavaSequenceCollection object
	 * @throws BioclipseException 
	  */
	@Recorded
	public BiojavaSequenceList loadSequences( String path ) throws IOException, BioclipseException;
	
	
	/**
	 * Saves a Biojava sequence to the file it is connected to using the format 
	 * of the file it is connected to 
	 * 
	 * @param seq Biojava Sequence to save
	 * @throws IllegalStateException if the sequence not connected to a file
	 */
	@Recorded
	public void saveSequence(BiojavaSequence seq) throws IllegalStateException;

	/**
	 * Saves a Biojava sequence to a given path using the default format
	 * 
	 * @param seq Sequence to save
	 * @param fileName name of file to save to
	 */
	@Recorded
	public void saveSequence( BiojavaSequence seq, String path );
	
	/**
	 * Saves a Biojava sequence to a given path using a given format
	 * 
	 * @param seq
	 * @param format
	 * @param path
	 */
	@Recorded
	public void saveSequence( BiojavaSequence seq, 
			                  String path,
			                  SequenceFormat format );

	/**
	 * Create sequence from a plain sequence string. Will set name to empty.
	 * @throws ScriptingException 
	 */
	@Recorded
	public BiojavaSequence createSequence(String content);

	/**
	 * Create sequence from a plain sequence string. Will set name to empty.
	 */
	@Recorded
	public BiojavaSequence createSequenceFromFasta(String content);

	/**
	 * Create sequence from a name and a plain sequence string (content).
	 */
	@Recorded
	public BiojavaSequence createSequence(String name, String content);	
	
	/**
	 * Load sequence from InputStream
	 * @param instream to be loaded
	 * @return loaded sequence
	 */
	@Recorded
	public BiojavaSequence loadSequence(InputStream instream);

	
	/**
	 * Converts a DNA sequence into RNA (transcription)
	 * 
	 * @param sequence to be converted
	 * @return an RNA sequence
	 */
	@Recorded
	public BiojavaRNASequence DNAtoRNA( BiojavaDNASequence sequence );

	/**
	 * Convert a BiojavaDNASequence into a BiojavaAASequence. Trancription + translation
	 * @throws IllegalArgumentException 
	 */
	@Recorded
	public BiojavaAASequence DNAToProtein(BiojavaDNASequence sequence);

	/**
	 * Converts a RNA sequence into DNA (reverse transcription)
	 * 
	 * @param sequence to be converted
	 * @return an DNA sequence
	 */
	@Recorded
	public BiojavaDNASequence RNAtoDNA(BiojavaRNASequence rnaSeq);

	/**
	 * Convert a BiojavaRNASequence into a BiojavaAASequence. Biological meaning: Translation.
	 * @throws IllegalArgumentException 
	 */
	@Recorded
	public BiojavaAASequence RNAtoProtein(BiojavaRNASequence sequence);


}
