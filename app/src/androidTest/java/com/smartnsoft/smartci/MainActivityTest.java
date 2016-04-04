package com.smartnsoft.smartci;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * The functional test class starting screen of the application.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public final class MainActivityTest
    extends ActivityInstrumentationTestCase2<MainActivity>
{

  public MainActivityTest()
  {
    super(MainActivity.class);
  }

  @Override
  public void setUp()
      throws Exception
  {
    super.setUp();
    // Espresso will not launch our activity for us, we must launch it via getActivity().
    getActivity();
  }

  @SmallTest
  public void testMainActivityInitialState()
  {
  }
}
