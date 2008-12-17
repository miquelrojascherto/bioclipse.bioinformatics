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
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;
import net.bioclipse.biojava.domain.BiojavaAASequence;
import net.bioclipse.biojava.domain.BiojavaDNASequence;
import net.bioclipse.biojava.domain.BiojavaRNASequence;
import net.bioclipse.biojava.domain.BiojavaSequence;
import net.bioclipse.biojava.domain.BiojavaSequenceList;
import net.bioclipse.core.business.BioclipseException;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.utils.ChangeVetoException;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.db.HashRichSequenceDB;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
/**
 * Manager for BioJava. Performs the actual BioJava calls.
 * @author ola
 *
 */
public class BiojavaManager implements IBiojavaManager {
    private static final Logger logger = Logger.getLogger(BiojavaManager.class);
    private static final String[] FormatReaderClassNames = {
        "org.biojavax.bio.seq.io.EMBLFormat",
        "org.biojavax.bio.seq.io.FastaFormat",
        "org.biojavax.bio.seq.io.GenbankFormat",
        "org.biojavax.bio.seq.io.INSDseqFormat",
        "org.biojavax.bio.seq.io.EMBLxmlFormat",
        "org.biojavax.bio.seq.io.UniProtFormat",
        "org.biojavax.bio.seq.io.UniProtXMLFormat",
        "org.biojavax.bio.seq.io.RichSequenceFormat"
    };
    public BiojavaManager() {
        // Introduce the allowed formats
        for (String name : FormatReaderClassNames) {
           try {
               logger.debug("Loading format reader: " + name);
               Class.forName(name);
           }
           catch(ClassNotFoundException e) {
               logger.error("Class " + name + "not found: " + e);
           }
        }
    }
    public String renderAs( SequenceFormat format,
                            BiojavaSequence seq) throws IOException {
        if (format==SequenceFormat.FASTA){
            return seq.toFasta();
        }
        // TODO Add more formats here
        logger.warn("BioJavaManager.convertTo() with format: "
                + format.toString() + " is not implemented.");
        throw new IllegalArgumentException("Format: " + format.toString()
                + " not supported.");
    }
    /**
     * Create sequence from a plain sequence string. Will set name to a unique
     * identifier.
     * 
     * @throws ScriptingException
     */
    public BiojavaSequence createSequence(String content) {
        return createSequence("seq" + System.currentTimeMillis(), content);
    }
    /**
     * Create sequence from a plain sequence string. Will set name to empty.
     * @throws ScriptingException
     */
    public BiojavaSequence createSequenceFromFasta(String content) {
        ByteArrayInputStream stream =
            new ByteArrayInputStream(content.getBytes());
        return loadSequence(stream);
    }
    /**
     * Create sequence from a name and a plain sequence string (content).
     * @throws ScriptingException
     */
    public BiojavaSequence createSequence(String name, String content) {
        //Construct a fasta string as
        String fastaString = ">" + name + "\n" + content;
        ByteArrayInputStream stream =
            new ByteArrayInputStream(fastaString.getBytes());
        return loadSequence(stream);
    }
    /**
     * Load sequence from file
     * 
     * @throws IOException if the file could not be read
     * @throws ScriptingException
     */
    public BiojavaSequence loadSequence(String path) {
        File file=new File(path);
        if (!file.canRead()) {
            throw new IllegalArgumentException("Could not read file: "
                                               + file.getPath());
        }
        FileInputStream stream;
        try {
            stream = new FileInputStream(file);
            return loadSequence(stream);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Could not read file: "
                                               + file.getPath());
        }
    }
    /**
     * Load sequenceCollection from file
     * 
     * @throws IllegalArgumentException
     * @throws BioclipseException
     */
    public BiojavaSequenceList loadSequences(String path)
        throws IOException, BioclipseException {
        File file = new File(path);
        if (!file.canRead())
            throw new IllegalArgumentException("Could not read file: "
                                               + file.getPath());
        FileInputStream stream = new FileInputStream(file);
        return loadSequences(stream);
    }
    /**
     * Load sequenceCollection from InputStream
     * @throws BioclipseException
     */
    public BiojavaSequenceList loadSequences(InputStream instream)
        throws BioclipseException {
        //Buffer the input stream
        BufferedInputStream bufferedStream=new BufferedInputStream(instream);
        Namespace ns = RichObjectFactory.getDefaultNamespace();
        RichSequenceIterator seqit = null;
        int noseqs=0;
        try {
            seqit = RichSequence.IOTools.readStream(bufferedStream, ns);
        } catch (IOException e) {
            throw new BioclipseException(e.getMessage());
        }
        if (seqit == null) {
            throw new BioclipseException("Sequence is null");
        }
        // Collection of sequences
        BiojavaSequenceList sequenceCollection=new BiojavaSequenceList();
        // Add all sequences to collection
        try {
            while (seqit.hasNext()){
                noseqs++;
                RichSequence rseq = seqit.nextRichSequence();
                if (rseq != null) {
                    if (rseq.getAlphabet().getName().equals("DNA")) {
                        sequenceCollection.add(new BiojavaDNASequence(rseq));
                    }
                    else if (rseq.getAlphabet().getName().equals("RNA")) {
                        sequenceCollection.add(new BiojavaRNASequence(rseq));
                    }
                    else if (rseq.getAlphabet().getName().equals("PROTEIN-TERM")) {
                        sequenceCollection.add(new BiojavaAASequence(rseq));
                    }
                }
            }
        } catch (NoSuchElementException e) {
            throw new BioclipseException(e.getMessage());
        } catch (BioException e) {
            throw new BioclipseException(e.getMessage());
        } catch (ChangeVetoException e) {
            throw new BioclipseException(e.getMessage());
        }
        return sequenceCollection;
    }
    /**
     * Load sequence from InputStream
     * @throws ScriptingException
     */
    public BiojavaSequence loadSequence(InputStream instream) {
        // Buffer the inputstream
        BufferedInputStream bufferedStream=new BufferedInputStream(instream);
        Namespace ns = RichObjectFactory.getDefaultNamespace();
        RichSequenceIterator seqit=null;
        int noseqs=0;
        try {
            seqit = RichSequence.IOTools.readStream(bufferedStream, ns);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not parse input into a sequence.", e);
        }
        if (seqit == null) {
            logger.warn("Could not read any sequences.");
            return null;
        }
        //Database for sequences
        HashRichSequenceDB db = new HashRichSequenceDB();
        // Iterate over sequences and put in array
        // ArrayList<RichSequence> seqs=new ArrayList<RichSequence>();
        RichSequence rseq = null;
        try {
            while (seqit.hasNext()) {
                noseqs++;
                rseq = seqit.nextRichSequence();
                if (rseq!=null)
                    db.addRichSequence(rseq);
//                seqs.add(rseq);
            }
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (BioException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (ChangeVetoException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (noseqs > 1) {
            logger.warn("Biojava expected only 1 sequence but parsed "
                               + noseqs + " sequences successfully." +
                               " Returning only last sequence.");
        }
        if (rseq.getAlphabet().getName().equals("DNA")) {
            return new BiojavaDNASequence(rseq);
        }
        else if (rseq.getAlphabet().getName().equals("RNA")) {
            return new BiojavaRNASequence(rseq);
        }
        else if (rseq.getAlphabet().getName().equals("PROTEIN-TERM")) {
            return new BiojavaAASequence(rseq);
        }
        return new BiojavaSequence(rseq);
    }
    public void saveSequence(BiojavaSequence seq) {
        // TODO Auto-generated method stub
        logger.warn("BioJavaManager.saveSequence() FIXME");
    }
    public void saveSequence(BiojavaSequence seq, String path) {
        // TODO Auto-generated method stub
        logger.warn("BioJavaManager.saveSequence() FIXME");
    }
    public void saveSequence(BiojavaSequence seq, String path,
            SequenceFormat format) {
        // TODO Auto-generated method stub
        logger.warn("BioJavaManager.saveSequence() FIXME");
    }
    /**
     * Convert a BiojavaDNASequence into a BiojavaRNASequence. Biological meaning: Transcription
     * @throws IllegalArgumentException
     */
    public BiojavaRNASequence DNAtoRNA(BiojavaDNASequence sequence) {
        RichSequence seq=sequence.getRichSequence();
        RichSequence rseq=null;
        SymbolList symlist;
        try {
            symlist = DNATools.toRNA(seq);
            rseq = RichSequence.Tools.createRichSequence(seq.getName(), symlist);
        } catch (IllegalAlphabetException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return new BiojavaRNASequence(rseq);
    }
    /**
     * Convert a BiojavaRNASequence into a BiojavaDNASequence. Biological meaning: Reverse-transcription
     * @throws IllegalArgumentException
     */
    public BiojavaDNASequence RNAtoDNA(BiojavaRNASequence sequence) {
        RichSequence seq = sequence.getRichSequence();
        RichSequence rseq = null;
        SymbolList rna=seq.getInternalSymbolList();
        try {
            SymbolList dna = BiojavaHelper.reverseTranscribe(rna);
            rseq=RichSequence.Tools.createRichSequence(seq.getName(), dna);
        } catch (IllegalSymbolException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IllegalAlphabetException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return new BiojavaDNASequence(rseq);
    }
    /**
     * Convert a BiojavaRNASequence into a BiojavaAASequence. Biological meaning: Translation.
     * @throws IllegalArgumentException
     */
    public BiojavaAASequence RNAtoProtein(BiojavaRNASequence sequence) {
        RichSequence seq=sequence.getRichSequence();
        RichSequence rseq=null;
        try {
            SymbolList aa = RNATools.translate(seq.getInternalSymbolList());
            rseq = RichSequence.Tools.createRichSequence(seq.getName(), aa);
        } catch (IllegalAlphabetException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return new BiojavaAASequence(rseq);
    }
    /**
     * Convert a BiojavaDNASequence into a BiojavaAASequence. Trancription + translation
     * @throws IllegalArgumentException
     */
    public BiojavaAASequence DNAToProtein(BiojavaDNASequence sequence) {
        RichSequence seq = sequence.getRichSequence();
        RichSequence rseq = null;
        try {
            SymbolList rna = DNATools.toRNA(seq);
            SymbolList aa= RNATools.translate(rna);
            rseq=RichSequence.Tools.createRichSequence(seq.getName(), aa);
        } catch (IllegalAlphabetException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return new BiojavaAASequence(rseq);
    }
    public String getNamespace() {
        return "biojava";
    }
}
