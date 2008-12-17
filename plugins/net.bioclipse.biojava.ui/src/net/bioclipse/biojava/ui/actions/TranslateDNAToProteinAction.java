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
package net.bioclipse.biojava.ui.actions;
import java.io.IOException;
import net.bioclipse.biojava.domain.BiojavaDNASequence;
import net.bioclipse.core.domain.IDNASequence;
import net.bioclipse.core.domain.ISequence;
public class TranslateDNAToProteinAction extends TranslateAction{
    @Override
    public ISequence convert(ISequence sequence) throws IOException {
        //Verify input is IDNASequence
        if (!(sequence instanceof IDNASequence)){
            showMessage("Input is not a DNA sequence.");
            return null;
        }
        IDNASequence dnaSequence = (IDNASequence) sequence;
        BiojavaDNASequence bjDNASeq=null;
        //If not a BioJavaSequence, construct one from PlainString
        if (dnaSequence instanceof BiojavaDNASequence) {
            bjDNASeq = (BiojavaDNASequence) dnaSequence;
        }else {
            ISequence crSeq=getBiojava().createSequence(dnaSequence.getPlainSequence());
            if (crSeq instanceof BiojavaDNASequence) {
                bjDNASeq = (BiojavaDNASequence) crSeq;
            }else {
                throw new IllegalArgumentException();
            }
        }
        return getBiojava().DNAToProtein(bjDNASeq);
    }
}
