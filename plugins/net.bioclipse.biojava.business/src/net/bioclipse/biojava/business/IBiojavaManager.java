 /*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
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
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
/**
 * @author jonalv, ola
 */
@PublishedClass("Provides bioinformatics services through the BioJava project.")
public interface IBiojavaManager extends IBioclipseManager {
    /**
     * Enum over the sequence formats supported by the BiojavaManager.
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
     * Converts a BiojavaSequence to a String representation in a given format.
     *
     * @param format the format of the resulting string
     * @param seq the sequence to convert
     * @return a string representation of a Biojavasequence
     * @throws IOException
     * @throws ScriptingException
     */
    @Recorded
    @PublishedMethod(params="SequenceFormat format",
    		     methodSummary="Converts a BiojavaSequence to a String "
                         + "representation in a given format.")
    public String renderAs( SequenceFormat format,
                            BiojavaSequence seq) throws IOException;
    //TODO renderAs giving a stream?
    /**
     * Loads a BiojavaSequence from file.
     *
     * @param path The path to the file
     * @return a BioJavaSequence object
     * @throws IOException
     */
    @Recorded
    @PublishedMethod(params="String path",
                     methodSummary="Loads a BiojavaSequence from file.")
    public BiojavaSequence loadSequence( String path ) throws IOException;
     /**
      * Loads sequenceCollection from InputStream.
      * 
      * @param instream the Stream to read from
      * @return A BiojavaSequenceCollection object
      * @throws BioclipseException
      */
    @Recorded
    @PublishedMethod(params="InputStream instream",
                     methodSummary="Loads sequenceCollection from InputStream.")
    public BiojavaSequenceList loadSequences(InputStream instream) throws BioclipseException;
     /**
      * Loads sequenceCollection from InputStream.
      * 
      * @param instream the Stream to read from
      * @return A BiojavaSequenceCollection object
      * @throws BioclipseException
      */
    @Recorded
    @PublishedMethod(params="String path",
                     methodSummary="Loads sequenceCollection from InputStream.")
    public BiojavaSequenceList loadSequences( String path ) throws IOException, BioclipseException;
    /**
     * Saves a Biojava sequence to the file it is connected to using the format
     * of the file it is connected to.
     *
     * @param seq Biojava Sequence to save
     * @throws IllegalStateException if the sequence not connected to a file
     */
    @Recorded
    @PublishedMethod(params="BiojavaSequence seq",
                     methodSummary="Saves a Biojava sequence to the file it is connected to using the format of the file it is connected to.")
    public void saveSequence(BiojavaSequence seq) throws IllegalStateException;
    /**
     * Saves a Biojava sequence to a given path using the default format.
     *
     * @param seq Sequence to save
     * @param fileName name of file to save to
     */
    @Recorded
    @PublishedMethod(params="BiojavaSequence seq, String path",
                     methodSummary="Saves a Biojava sequence to a given path using the default format.")
    public void saveSequence( BiojavaSequence seq, String path );
    /**
     * Saves a Biojava sequence to a given path using a given format.
     *
     * @param seq
     * @param format
     * @param path
     */
    @Recorded
    @PublishedMethod(params="BiojavaSequence seq, String path, SequenceFormat format",
                     methodSummary="Saves a Biojava sequence to a given path using a given format.")
    public void saveSequence( BiojavaSequence seq,
                              String path,
                              SequenceFormat format );
    /**
     * Creates sequence from a plain sequence string. Will set name to empty.
     * 
     * @throws ScriptingException
     */
    @Recorded
    @PublishedMethod(params="String content",
                     methodSummary="Creates sequence from a plain sequence string. Will set name to empty.")
    public BiojavaSequence createSequence(String content);
    /**
     * Creates sequence from a plain sequence string. Will set name to empty.
     */
    @Recorded
    @PublishedMethod(params="String content",
                     methodSummary="Creates sequence from a plain sequence string. Will set name to empty.")
    public BiojavaSequence createSequenceFromFasta(String content);
    /**
     * Creates sequence from a name and a plain sequence string (content).
     */
    @Recorded
    @PublishedMethod(params="String name, String content",
                     methodSummary="Creates sequence from a name and a plain sequence string (content).")
    public BiojavaSequence createSequence(String name, String content);
    /**
     * Loads sequence from InputStream.
     * 
     * @param instream to be loaded
     * @return loaded sequence
     */
    @Recorded
    @PublishedMethod(params="InputStream instream",
                     methodSummary="Loads sequence from InputStream.")
    public BiojavaSequence loadSequence(InputStream instream);
    /**
     * Converts a DNA sequence into RNA (transcription).
     *
     * @param sequence to be converted
     * @return an RNA sequence
     */
    @Recorded
    @PublishedMethod(params="BiojavaDNASequence sequence",
                     methodSummary="Converts a DNA sequence into RNA (transcription).")
    public BiojavaRNASequence DNAtoRNA( BiojavaDNASequence sequence );
    /**
     * Converts a BiojavaDNASequence into a BiojavaAASequence.
     * Trancription + translation.
     * 
     * @throws IllegalArgumentException
     */
    @Recorded
    @PublishedMethod(params="BiojavaDNASequence sequence",
                     methodSummary="Converts a BiojavaDNASequence into a BiojavaAASequence.")
    public BiojavaAASequence DNAToProtein(BiojavaDNASequence sequence);
    /**
     * Converts a RNA sequence into DNA (reverse transcription).
     *
     * @param sequence to be converted
     * @return an DNA sequence
     */
    @Recorded
    @PublishedMethod(params="BiojavaRNASequence rnaSeq",
                     methodSummary="Converts a RNA sequence into DNA (reverse transcription).")
    public BiojavaDNASequence RNAtoDNA(BiojavaRNASequence rnaSeq);
    /**
     * Converts a BiojavaRNASequence into a BiojavaAASequence.
     * Biological meaning: Translation.
     * 
     * @throws IllegalArgumentException
     */
    @Recorded
    @PublishedMethod(params="BiojavaRNASequence sequence",
                     methodSummary="Convert a BiojavaRNASequence into a BiojavaAASequence.")
    public BiojavaAASequence RNAtoProtein(BiojavaRNASequence sequence);
}
