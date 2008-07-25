package net.bioclipse.biojava.business;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { BiojavaHelperTest.class,
                         BiojavaManagerTest.class } )
public class AllBiojavaBusinessTestsSuite {

}
