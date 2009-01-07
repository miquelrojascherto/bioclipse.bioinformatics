/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package testData;

public abstract class TestData {

    public static String getPathToAAFastaSequence() {
        return TestData.class
                       .getClassLoader()
                       .getResource("testData/AASequence.fasta").getPath();
    }


    public static String getPathToFosbFastaSequence() {
        return TestData.class
                       .getClassLoader()
                       .getResource("testData/fosb.fasta").getPath();
    }

    /**
     * Contains 2 fasta strings in the file
     * @return
     */
    public static String getPathToMultipleSequences() {
        return TestData.class
                       .getClassLoader()
                       .getResource("testData/sequence3.fasta").getPath();
    }

    public static String getPathToDNAFastaSequence() {
        return TestData.class
                       .getClassLoader()
                       .getResource("testData/DNASequence.fasta").getPath();
    }
    
    public static String getPathToRNAFastaSequence() {
        return TestData.class
                       .getClassLoader()
                       .getResource("testData/RNASequence.fasta").getPath();
    }
}
