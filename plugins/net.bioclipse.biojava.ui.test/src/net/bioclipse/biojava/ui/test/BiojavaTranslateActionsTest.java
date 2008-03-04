package net.bioclipse.biojava.ui.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import net.bioclipse.biojava.business.BiojavaManager;
import net.bioclipse.biojava.business.IBiojavaManager;
import net.bioclipse.biojava.domain.BiojavaSequence;
import net.bioclipse.biojava.ui.actions.TranslateAction;
import net.bioclipse.biojava.ui.actions.TranslateDNAToProteinAction;
import net.bioclipse.biojava.ui.actions.TranslateDNAToRNAAction;
import net.bioclipse.biojava.ui.actions.TranslateRNAToDNAAction;
import net.bioclipse.biojava.ui.actions.TranslateRNAToProteinAction;
import net.bioclipse.core.domain.IDNASequence;
import net.bioclipse.core.domain.IRNASequence;
import net.bioclipse.core.domain.ISequence;

import org.eclipse.jface.viewers.StructuredSelection;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class BiojavaTranslateActionsTest {

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
	public void testTranslateDNAToRNAAction() {
		BiojavaSequence seq=biojava.createSequence("CTCCTCGCGAAACGATACGAT");
		assertTrue(seq instanceof IDNASequence);
		
		TranslateAction action=new TranslateDNAToRNAAction();
		action.setSelection(new StructuredSelection(seq));
		action.setBiojava(biojava);
		action.run(null);

		ISequence convSeq=action.getConvertedSeq();
		
		try {
			assertEquals("CUCCUCGCGAAACGAUACGAU", convSeq.getPlainSequence().trim().toUpperCase());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testTranslateDNAToProteinAction() {
		BiojavaSequence seq=biojava.createSequence("CTCCTCGCGAAACGATACGAT");
		assertTrue(seq instanceof IDNASequence);
		
		TranslateAction action=new TranslateDNAToProteinAction();
		action.setSelection(new StructuredSelection(seq));
		action.setBiojava(biojava);
		action.run(null);

		ISequence convSeq=action.getConvertedSeq();
		
		try {
			assertEquals("LLAKRYD", convSeq.getPlainSequence().trim().toUpperCase());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testTranslateRNAToDNAAction() {
		fail("Not implemented as read RNA is not supported");

		BiojavaSequence seq=biojava.createSequence("CUCCUCGCGAAACGAUACGAU");
		assertTrue(seq instanceof IRNASequence);
		
		TranslateAction action=new TranslateRNAToDNAAction();
		action.setSelection(new StructuredSelection(seq));
		action.setBiojava(biojava);
		action.run(null);

		ISequence convSeq=action.getConvertedSeq();
		
		try {
			assertEquals("CTCCTCGCGAAACGATACGAT", convSeq.getPlainSequence().trim().toUpperCase());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testTranslateRNAToProteinAction() {
		fail("Not implemented as read RNA is not supported");

		BiojavaSequence seq=biojava.createSequence("CUCCUCGCGAAACGAUACGAU");
		assertTrue(seq instanceof IRNASequence);
		
		TranslateAction action=new TranslateRNAToProteinAction();
		action.setSelection(new StructuredSelection(seq));
		action.setBiojava(biojava);
		action.run(null);

		ISequence convSeq=action.getConvertedSeq();
		
		try {
			assertEquals("LLAKRYD", convSeq.getPlainSequence().trim().toUpperCase());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

}
