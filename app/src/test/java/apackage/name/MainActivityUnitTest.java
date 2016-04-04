package com.smartnsoft.smartci;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * The functional test class starting screen of the application.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityUnitTest
{

  @Test
  public void testSomething() throws Exception {
    assertTrue(Robolectric.setupActivity(MainActivity.class) != null);
  }

}
