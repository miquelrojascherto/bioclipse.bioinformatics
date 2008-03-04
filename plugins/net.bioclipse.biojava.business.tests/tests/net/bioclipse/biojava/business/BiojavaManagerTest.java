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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.bioclipse.biojava.business.BiojavaManager;
import net.bioclipse.biojava.business.IBiojavaManager;
import net.bioclipse.biojava.domain.BiojavaAASequence;
import net.bioclipse.biojava.domain.BiojavaDNASequence;
import net.bioclipse.biojava.domain.BiojavaRNASequence;
import net.bioclipse.biojava.domain.BiojavaSequence;
import net.bioclipse.biojava.domain.BiojavaSequenceList;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.ISequence;

import org.junit.Before;
import org.junit.Test;

import testData.TestData;
import static org.junit.Assert.*;

/**
 * @author jonalv, ola
 *
 */
public class BiojavaManagerTest {

	private IBiojavaManager biojava;

	@Before
	public void setup() {
		biojava = new BiojavaManager();  


		//Introduce the allowed formats
		try{
			Class.forName("org.biojavax.bio.seq.io.EMBLFormat");
			Class.forName("org.biojavax.bio.seq.io.FastaFormat");
			Class.forName("org.biojavax.bio.seq.io.GenbankFormat");
			Class.forName("org.biojavax.bio.seq.io.INSDseqFormat");
			Class.forName("org.biojavax.bio.seq.io.EMBLxmlFormat");
			Class.forName("org.biojavax.bio.seq.io.UniProtFormat");
			Class.forName("org.biojavax.bio.seq.io.UniProtXMLFormat");
			Class.forName("org.biojavax.bio.seq.io.RichSequenceFormat");
		}
		catch(ClassNotFoundException e){
			System.out.println("Class not found" + e);
		}		
	}

	
	@Test
	public void testCreateSequences() {
		
		//A DNA Sequence
		ISequence seq=biojava.createSequence("CCCTCGGCTTC");
		try {
			assertEquals("CCCTCGGCTTC", seq.getPlainSequence().trim().toUpperCase());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		//A Protein Sequence
		seq=biojava.createSequence("SEQUENCE");
		try {
			assertEquals("SEQUENCE", seq.getPlainSequence().trim().toUpperCase());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		/*
		//An RNA Sequence
		seq=biojava.createSequence("CCCUCGGCUUC");
		try {
			assertEquals("CCCUCGGCUUC", seq.getPlainSequence());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		*/
	}

	@Test
	public void testCreateNamedSequences() {
		
		//A DNA Sequence
		ISequence seq=biojava.createSequence("sequence1","CCCTCGGCTTC");
		try {
			assertEquals("CCCTCGGCTTC", seq.getPlainSequence().trim().toUpperCase());
			
			//Right now: don't care about name as it is set by BioJava
			
		} catch (IOException e) {
			fail(e.getMessage());
		}

		//A Protein Sequence
		seq=biojava.createSequence("sequence2","SEQUENCE");
		try {
			assertEquals("SEQUENCE", seq.getPlainSequence().trim().toUpperCase());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		/*
		//An RNA Sequence
		seq=biojava.createSequence("CCCUCGGCUUC");
		try {
			assertEquals("CCCUCGGCUUC", seq.getPlainSequence());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		*/
	}

	
	@Test
	public void testCreateFastaSequences() {
		
		//A DNA Sequence
		ISequence seq=biojava.createSequenceFromFasta(">sequence1\nCCCTCGGCTTC");
		try {
			assertEquals("CCCTCGGCTTC", seq.getPlainSequence().trim().toUpperCase());
			
			//Right now: don't care about name as it is set by BioJava
			
		} catch (IOException e) {
			fail(e.getMessage());
		}

		//A Protein Sequence
		seq=biojava.createSequenceFromFasta(">sequence2\nSEQUENCE");
		try {
			assertEquals("SEQUENCE", seq.getPlainSequence().trim().toUpperCase());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		/*
		//An RNA Sequence
		seq=biojava.createSequence("CCCUCGGCUUC");
		try {
			assertEquals("CCCUCGGCUUC", seq.getPlainSequence());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		*/
	}
	
	
	
	
	@Test
	public void testToFasta() {
		BiojavaSequence seq=null;
		try {
			seq = biojava.loadSequence( TestData.getPathToAAFastaSequence() );
//			seq = biojava.loadSequence( TestData.getPathToFosbFastaSequence() );
		} catch (IOException e) {
			e.printStackTrace();
			fail (e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
		assertNotNull(seq);
//		assertEquals( "Sequence", seq.getName()); 

		try {
			assertEquals( "SEQUENCE", seq.getPlainSequence().trim() );
		} catch (IOException e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
	}

	@Test
	public void testLoadFastaSequences() {
		BiojavaSequence seq=null;
		try {
			//Test read Protein as fasta
			seq = biojava.loadSequence( TestData.getPathToAAFastaSequence() );
			assertNotNull( seq );
			assertEquals( "SEQUENCE", seq.getPlainSequence().trim().toUpperCase() );

			//Test read DNA as fasta
			seq = biojava.loadSequence( TestData.getPathToDNAFastaSequence() );
			assertNotNull( seq );
			assertEquals( "GATTACA", seq.getPlainSequence().trim().toUpperCase() );

			/*
			//Test read RNA as fasta
			seq = biojava.loadSequence( TestData.getPathToRNAFastaSequence() );
			assertNotNull( seq );
			assertEquals( "GAUUACA", seq.getPlainSequence().trim().toUpperCase() );
*/
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}

	}

	@Test
	public void testDNAtoRNA() {
		BiojavaSequence seq=null;
		try {
			seq = biojava.loadSequence( TestData.getPathToDNAFastaSequence() );
		} catch (IOException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}

		assertNotNull(seq);

		assertTrue(seq instanceof BiojavaDNASequence);
		BiojavaDNASequence dnaSeq = (BiojavaDNASequence) seq;

		try {
			assertEquals( "GATTACA", seq.getPlainSequence().trim().toUpperCase() );
			BiojavaRNASequence rnaSeq;
			rnaSeq = biojava.DNAtoRNA(dnaSeq);
			assertEquals( "GAUUACA", rnaSeq.getPlainSequence().trim().toUpperCase() );
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDNAtoProtein() {
		BiojavaSequence seq=null;
			seq = biojava.createSequence("CTCCTCGCGAAACGATACGAT");

		assertNotNull(seq);

		assertTrue(seq instanceof BiojavaDNASequence);
		BiojavaDNASequence dnaSeq = (BiojavaDNASequence) seq;

		try {

			BiojavaAASequence rnaSeq = biojava.DNAToProtein(dnaSeq);
			assertEquals( "LLAKRYD", rnaSeq.getPlainSequence().trim().toUpperCase() );
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testRNAtoProtein() {
		fail("Not implemented");
	}

	@Test
	public void testRNAtoDNA() {
		fail("Not implemented");
	}
	

	/*
	@Test
	public void testRNAtoDNA() {

		BiojavaSequence seq = biojava.createSequence("GAUUACA");

		assertNotNull(seq);

		assertTrue(seq instanceof BiojavaRNASequence);
		BiojavaRNASequence rnaSeq = (BiojavaRNASequence) seq;

		try {
			//Check correctly read
			assertEquals( "GAUUACA", rnaSeq.getPlainSequence().trim().toUpperCase() );

			BiojavaDNASequence dnaSeq = biojava.RNAtoDNA(rnaSeq);

			//Check correctly converted
			assertEquals( "GATTACA", dnaSeq.getPlainSequence().trim().toUpperCase() );
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
*/
	@Test
	public void testLoadFastaSequenceCollection() throws BioclipseException, IOException {
		BiojavaSequenceList sequenceCollection=null;
		sequenceCollection = biojava.loadSequences( TestData.getPathToMultipleSequences() );
		assertNotNull( sequenceCollection );
		assertEquals( 2, sequenceCollection.size() );

		//Check individual sequences
		BiojavaSequence seq1=sequenceCollection.get(0);
		BiojavaSequence seq2=sequenceCollection.get(1);

		try {
			assertEquals(341, seq1.getPlainSequence().length());
			assertEquals(342, seq2.getPlainSequence().length());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
