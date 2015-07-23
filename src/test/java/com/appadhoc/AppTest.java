package com.appadhoc;

import com.appadhoc.javasdk.AdhocSdk;
import com.appadhoc.javasdk.ExperimentFlags;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple .
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        AdhocSdk.getInstance().init("ADHOC_50000000000000ad80c23462");
        String cliend_id = AdhocSdk.getInstance().generateClientId();
        assertTrue(cliend_id.length() > 0);
        ExperimentFlags flag = AdhocSdk.getInstance().getExperimentFlags(cliend_id);
        assertTrue(flag.getRawFlags().toString().equals("{}"));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ExperimentFlags flag2 = AdhocSdk.getInstance().getExperimentFlags(cliend_id);
        if(flag.getBooleanFlag("btn_color",false)){

        }
        assertTrue(flag2.getRawFlags().toString().length() > "{}".length());
        AdhocSdk.getInstance().incrementStat(cliend_id, "buy_success", 1);

    }
}
