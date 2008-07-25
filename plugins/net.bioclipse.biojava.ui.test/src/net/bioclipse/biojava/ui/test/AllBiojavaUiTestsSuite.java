package net.bioclipse.biojava.ui.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { BiojavaTranslateActionsTest.class,
                         SequenceContentProviderTest.class,
                         TestLoadSequences.class } )
public class AllBiojavaUiTestsSuite {

}
