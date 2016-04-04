package com.smartnsoft.smartci;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment.SavedState;
import android.view.MenuItem;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicyAnnotation;
import com.smartnsoft.droid4me.analytics.AnalyticsLogger.AnalyticsDisabledLoggerAnnotation;
import com.smartnsoft.droid4me.app.SmartAppCompatActivity;
import com.smartnsoft.droid4me.app.Smartable;
import com.smartnsoft.droid4me.ext.app.FragmentAggregate.OnBackPressedListener;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.BusinessObjectUnavailableReporter;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.LoadingAndErrorAnnotation;
import com.smartnsoft.droid4me.support.v4.app.SmartFragment;

import com.smartnsoft.smartci.app.SmartCIActivityAggregate;
import com.smartnsoft.smartci.app.SmartCIFragmentAggregate;
import com.smartnsoft.smartci.fragment.SmartCIFragment;

/**
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
@AnalyticsDisabledLoggerAnnotation
@LoadingAndErrorAnnotation(enabled = false, loadingEnabled = false)
@BusinessObjectsRetrievalAsynchronousPolicyAnnotation
public abstract class SmartCIActivity
    extends SmartAppCompatActivity<SmartCIActivityAggregate>
    implements BusinessObjectUnavailableReporter<SmartCIFragmentAggregate>
{

  private final Set<Smartable<?>> businessObjectsUnavailableFragments = Collections.synchronizedSet(new HashSet<Smartable<?>>());

  private boolean hasBeenPaused;

  private boolean setBackMainFragmentRequired;

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    super.onConfigurationChanged(newConfig);

    // This work-around is there to fix the issue when the device orientation has been changed on a stacked Activity, and that the hereby Activity
    // resumes with the new orientation
    if (hasBeenPaused == true)
    {
      setBackMainFragmentRequired = true;
    }
    else
    {
      setBackMainFragmentFollowingOnConfigurationChanged();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    if (getAggregate().getActivityAnnotation().canRotate() == false && SmartCIApplication.getApplicationConstants().canRotate == false)
    {
      // This Activity is not authorized to rotate
      final int requestedOrientation = getRequestedOrientation();

      if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
      {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
      }
    }
  }

  @Override
  protected void onResumeFragments()
  {
    super.onResumeFragments();

    hasBeenPaused = false;

    if (setBackMainFragmentRequired == true)
    {
      setBackMainFragmentRequired = false;
      setBackMainFragmentFollowingOnConfigurationChanged();
    }
  }

  @Override
  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {

  }

  @Override
  public void onFulfillDisplayObjects()
  {

  }

  @Override
  public void onSynchronizeDisplayObjects()
  {

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
    case android.R.id.home:
      //In order to respect the Android navigation guidelines, we should use the NavUtils class but...
      // NavUtils.navigateUpFromSameTask(this);
      finish();

    default:
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onBackPressed()
  {
    final SmartFragment<?> currentFragment = getAggregate().getOpenedFragment();

    if (currentFragment instanceof OnBackPressedListener)
    {
      if (((OnBackPressedListener) currentFragment).onBackPressed() == true)
      {
        return;
      }
    }

    if (isFinishing() == false)
    {
      super.onBackPressed();
    }
  }

  @Override
  public final void reportBusinessObjectUnavailableException(
      Smartable<SmartCIFragmentAggregate> smartableFragment,
      BusinessObjectUnavailableException businessObjectUnavailableException)
  {
    smartableFragment.getAggregate().rememberBusinessObjectUnavailableException(businessObjectUnavailableException);
    businessObjectsUnavailableFragments.add(smartableFragment);
    smartableFragment.getAggregate().getLoadingErrorAndRetryAggregate().showException(this, smartableFragment, businessObjectUnavailableException, new Runnable()
    {

      @Override
      public void run()
      {
        final Set<Smartable<?>> copiedFragments = new HashSet<>(businessObjectsUnavailableFragments);
        businessObjectsUnavailableFragments.clear();

        for (final Smartable<?> smartable : copiedFragments)
        {
          ((Smartable<SmartCIFragmentAggregate>) smartable).getAggregate().forgetException();
          smartable.refreshBusinessObjectsAndDisplay(true, null, false);
        }
      }
    });
  }

  private void setBackMainFragmentFollowingOnConfigurationChanged()
  {
    final SmartFragment<?> opennedFragment = getAggregate().getOpenedFragment();

    if (opennedFragment instanceof SmartCIFragment)
    {
      final SmartCIFragment fragment = (SmartCIFragment) opennedFragment;

      try
      {
        if (fragment.getAggregate().getFragmentAnnotation().surviveOnConfigurationChanged() == true)
        {
          if (log.isDebugEnabled())
          {
            log.debug("The Fragment from class '" + opennedFragment.getClass().getName() + "' will not be recreated, because it is declared as able to survive a configuration change");
          }
          return;
        }
      }
      catch (NullPointerException exception)
      {
        // Catch the NullPointerException if the fragment does not contain a droid4mizer object !
        if (log.isWarnEnabled() == true)
        {
          log.warn("Unable to get the aggregate attached to the Fragment!", exception);
        }
      }
    }
    if (opennedFragment != null)
    {
      try
      {
        final SavedState savedState = getSupportFragmentManager().saveFragmentInstanceState(opennedFragment);
        final Bundle arguments = opennedFragment.getArguments();
        getAggregate().openFragment((Class<? extends SmartFragment<?>>) opennedFragment.getClass(), savedState, arguments);
      }
      catch (IllegalStateException exception)
      {
        if (log.isWarnEnabled() == true)
        {
          log.warn("Unable to retrieve the FragmentManager from main FrBVagment!", exception);
        }
      }
    }
  }
}
