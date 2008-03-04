package net.bioclipse.biojava.business;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the helper classes for Biojava
 * @author ola
 *
 */
public class BiojavaHelperTest {
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
	/**
	 * Test conversion of SymbolList RNA to DNA
	 */
	public void testRNAtoDNA() {

		try {
			SymbolList rna = RNATools.createRNA("uuaaggccauugaaaaaacc");
			SymbolList revRna=BiojavaHelper.reverseTranscribe(rna);

			//This is the answer we are looking for
			SymbolList dna = DNATools.createDNA("ccaaaaaagttaccggaatt");

			assertEquals(dna.seqString(), revRna.seqString());

		} catch (IllegalSymbolException e) {
			fail(e.getMessage());
		} catch (IllegalAlphabetException e) {
			fail(e.getMessage());
		}

	}


}
