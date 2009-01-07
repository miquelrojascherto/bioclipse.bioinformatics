/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *
 ******************************************************************************/
package net.bioclipse.biojava.ui.test;

import java.io.InputStream;

import net.bioclipse.biojava.business.BiojavaManager;
import net.bioclipse.biojava.business.IBiojavaManager;
import net.bioclipse.biojava.domain.BiojavaSequence;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.ISequence;

import org.junit.Before;
import org.junit.Test;

public class TestLoadSequences {

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
    public void TestBiolist(){

        BioList<BiojavaSequence> list=new BioList<BiojavaSequence>();

        InputStream stream = getClass().getResourceAsStream("/net/bioclipse/biojava/ui/test/resources/111076.gbk");
        BiojavaSequence tseq=biojava.loadSequence(stream);
        list.add(tseq);
        for (ISequence seq : list){
            System.out.println(seq.getName());
        }

        stream = getClass().getResourceAsStream("/net/bioclipse/biojava/ui/test/resources/111077.gbk");
        System.out.println("--");
        tseq=biojava.loadSequence(stream);
        list.add(tseq);
        for (ISequence seq : list){
            System.out.println(seq.getName());
        }




    }

}
