package com.smartnsoft.smartci.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.analytics.AnalyticsSender;
import com.smartnsoft.droid4me.app.SmartActionBarActivity;
import com.smartnsoft.droid4me.app.SmartAppCompatActivity;
import com.smartnsoft.droid4me.app.Smartable;
import com.smartnsoft.droid4me.ext.app.ActivityAnnotations.FragmentAnnotation;
import com.smartnsoft.droid4me.ext.app.ActivityInterceptor.BusinessObjectsContainer;
import com.smartnsoft.droid4me.ext.app.FragmentAggregate;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.BusinessObjectsUnavailableExceptionKeeper;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.LoadingErrorAndRetryAggregateProvider;

import com.smartnsoft.smartci.SmartCIApplication;


/**
 * An aggregate which can be shared by all {@link android.app.Fragment fragments}.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public final class SmartCIFragmentAggregate
    extends FragmentAggregate<SmartCIApplication, SmartActionBarActivity<SmartCIActivityAggregate>>
    implements LoadingErrorAndRetryAggregateProvider
{

  private final LoadingErrorAndRetryAggregate loadingErrorAndRetryAggregate = new LoadingErrorAndRetryAggregate();

  private final BusinessObjectsUnavailableExceptionKeeper businessObjectsUnavailableExceptionKeeper = new BusinessObjectsUnavailableExceptionKeeper();

  private final BusinessObjectsContainer businessObjectsContainer = new BusinessObjectsContainer();

  public SmartCIFragmentAggregate(Object fragment, FragmentAnnotation fragmentAnnotation)
  {
    super(fragment, fragmentAnnotation);
  }

  public AnalyticsSender<Bundle> getAnalyticsSender()
  {
    return getApplication().getAnalyticsSender();
  }

  public void rememberBusinessObjectUnavailableException(BusinessObjectUnavailableException exception)
  {
    businessObjectsUnavailableExceptionKeeper.setException(exception);
  }

  public void forgetException()
  {
    businessObjectsUnavailableExceptionKeeper.setException(null);
  }

  public void showBusinessObjectUnavailableException(Activity activity, Smartable<?> smartableFragment,
      BusinessObjectUnavailableException exception)
  {
    rememberBusinessObjectUnavailableException(exception);
    loadingErrorAndRetryAggregate.showBusinessObjectUnavailableException(activity, smartableFragment, exception);
  }

  public void checkException()
      throws BusinessObjectUnavailableException
  {
    businessObjectsUnavailableExceptionKeeper.checkException();
  }

  public void onRestoreInstanceState(Bundle bundle)
  {
    businessObjectsContainer.onRestoreInstanceState(bundle);
  }

  public void onSaveInstanceState(Bundle bundle)
  {
    businessObjectsContainer.onSaveInstanceState(bundle);
  }

  @SuppressLint("NewApi")
  public SmartCIApplication getApplication()
  {
    if (fragment != null)
    {
      return (SmartCIApplication) fragment.getActivity().getApplication();
    }
    else
    {
      return (SmartCIApplication) supportFragment.getActivity().getApplication();
    }
  }

  @Override
  protected Object getActionBar(Activity activity)
  {
    return ((SmartAppCompatActivity) activity).getSupportActionBar();
  }

  @Override
  public LoadingErrorAndRetryAggregate getLoadingErrorAndRetryAggregate()
  {
    return loadingErrorAndRetryAggregate;
  }

  @Override
  public BusinessObjectsUnavailableExceptionKeeper getBusinessUnavailableExceptionKeeper()
  {
    return businessObjectsUnavailableExceptionKeeper;
  }
}
