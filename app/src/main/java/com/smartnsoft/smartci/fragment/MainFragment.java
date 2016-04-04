package com.smartnsoft.smartci.fragment;

import com.smartnsoft.droid4me.ext.app.ActivityAnnotations.FragmentAnnotation;

import com.smartnsoft.smartci.R;


/**
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
@FragmentAnnotation(layoutIdentifier = R.layout.main_fragment, fragmentTitleIdentifier = R.string.applicationName)
public class MainFragment
    extends SmartCIFragment
{

  @Override
  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
    super.onRetrieveBusinessObjects();
  }

  @Override
  public void onFulfillDisplayObjects()
  {

  }

}
