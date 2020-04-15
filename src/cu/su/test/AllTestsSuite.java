package cu.su.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({cu.su.test.Controller.TestSuiteController.class, cu.su.test.model.TestSuiteController.class})
public class AllTestsSuite {

}
