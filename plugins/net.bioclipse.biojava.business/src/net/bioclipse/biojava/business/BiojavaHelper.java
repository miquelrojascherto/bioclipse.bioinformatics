/*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.biojava.business;

import java.util.Iterator;
import java.util.Set;

import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.ManyToOneTranslationTable;
import org.biojava.bio.symbol.ReversibleTranslationTable;
import org.biojava.bio.symbol.SimpleSymbolListFactory;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.bio.symbol.SymbolListFactory;
import org.biojava.bio.symbol.SymbolListViews;

/**
 * Helper classes for Biojava
 * @author ola
 *
 */
public class BiojavaHelper {


	/**
	 * ReverseTranscribe RNA to DNA
	 * @param aa SymbolList of RNS sequence
	 * @return SymbolList of DNA sequence
	 * @throws IllegalSymbolException
	 * @throws IllegalAlphabetException
	 */
	public static SymbolList reverseTranscribe(SymbolList rna) 
	throws IllegalSymbolException, IllegalAlphabetException{

		ReversibleTranslationTable rtt = RNATools.transcriptionTable();
		Symbol[] syms = new Symbol[rna.length()];

		//reverse RNA
		rna = SymbolListViews.reverse(rna);

		for(int i = 1; i <= rna.length(); i++){
			syms[i-1] = rtt.untranslate(rna.symbolAt(i));
		}

		SymbolListFactory fact = new SimpleSymbolListFactory();

		SymbolList dna = fact.makeSymbolList(syms, syms.length, 
				rtt.getSourceAlphabet());

		return dna;
	}

	/**
	 * ReverseTranslate Protein to RNA. This is not used so far but could serve as 
	 * starting point for future functionality. Reverse-translation is not common.
	 * 
	 * @param aa SymbolList of protein sequence
	 * @return SymbolList of RNA sequence
	 * @throws IllegalSymbolException
	 * @throws IllegalAlphabetException
	 */
	public static SymbolList reverseTranslate(SymbolList aa) 
		throws IllegalSymbolException, IllegalAlphabetException{
		ManyToOneTranslationTable rtt = RNATools.getGeneticCode("UNIVERSAL");
		Symbol[] syms = new Symbol[aa.length()];

		for(int i = 1; i <= aa.length(); i++){
			System.out.println("aa:" + aa.symbolAt(i).getName());
			Set set= rtt.untranslate(aa.symbolAt(i));
			for(Iterator it=set.iterator();it.hasNext();){
				Object obj=it.next();
				if (obj instanceof Symbol) {
					Symbol sym = (Symbol) obj;
					System.out.println("   -> "+ sym.getName());
				}
			}
		}

		SymbolListFactory fact = new SimpleSymbolListFactory();

		SymbolList rna = fact.makeSymbolList(syms, syms.length, 
				rtt.getSourceAlphabet());

		return rna;
	} 
}
