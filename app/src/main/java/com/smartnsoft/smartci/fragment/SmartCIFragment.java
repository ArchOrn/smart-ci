package com.smartnsoft.smartci.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicyAnnotation;
import com.smartnsoft.droid4me.app.AppPublics.SendLoadingIntentAnnotation;
import com.smartnsoft.droid4me.ext.app.FragmentAggregate.OnBackPressedListener;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.LoadingAndErrorAnnotation;
import com.smartnsoft.droid4me.support.v4.app.SmartFragment;

import com.smartnsoft.smartci.app.SmartCIFragmentAggregate;


/**
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
@LoadingAndErrorAnnotation
@SendLoadingIntentAnnotation
@BusinessObjectsRetrievalAsynchronousPolicyAnnotation
public abstract class SmartCIFragment
    extends SmartFragment<SmartCIFragmentAggregate>
    implements OnBackPressedListener
{

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    getAggregate().onSaveInstanceState(outState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    getAggregate().onRestoreInstanceState(savedInstanceState);
    return inflater.inflate(getAggregate().getFragmentAnnotation().layoutIdentifier(), container, false);
  }

  @Override
  public final void onRetrieveDisplayObjects()
  {
    // Not used in fragment lifecycle
  }

  @Override
  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
    getAggregate().checkException();
  }

  @Override
  public void onSynchronizeDisplayObjects()
  {
    // Should not be used
  }

  @Override
  public void onFulfillDisplayObjects()
  {
    //default implementation
  }

  @Override
  public boolean onBackPressed()
  {
    return false;
  }

}
