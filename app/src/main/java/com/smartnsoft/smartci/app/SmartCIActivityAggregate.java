package com.smartnsoft.smartci.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.smartnsoft.droid4me.analytics.AnalyticsSender;
import com.smartnsoft.droid4me.app.ActivityController.IssueAnalyzer;
import com.smartnsoft.droid4me.app.SmartAppCompatActivity;
import com.smartnsoft.droid4me.app.Smartable;
import com.smartnsoft.droid4me.ext.app.ActivityAggregate;
import com.smartnsoft.droid4me.ext.app.ActivityAnnotations.ActivityAnnotation;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.BusinessObjectsUnavailableExceptionKeeper;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.ErrorAndRetryManager;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.LoadingErrorAndRetryAggregateProvider;

import com.smartnsoft.smartci.SmartCIApplication;
import com.smartnsoft.smartci.R;

/**
 * An aggregate which can be shared by all {@link Activity activities}.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public final class SmartCIActivityAggregate
    extends ActivityAggregate<SmartCIApplication>
    implements LoadingErrorAndRetryAggregateProvider
{

  public final static class SmartCIErrorAndRetryManager
      implements ErrorAndRetryManager
  {

    private final View errorAndRetry;

    private final TextView errorText;

    private final View retry;

    public SmartCIErrorAndRetryManager(View view)
    {
      errorAndRetry = view.findViewById(R.id.errorAndRetry);
      errorText = (TextView) view.findViewById(R.id.errorText);
      retry = view.findViewById(R.id.retry);
    }

    @Override
    public void showError(final Activity activity, Throwable throwable, boolean fromGuiThread,
        final Runnable onCompletion)
    {
      if (IssueAnalyzer.isAConnectivityProblem(throwable) == true)
      {
        errorText.setText(R.string.connectivityProblem);
      }
      else
      {
        errorText.setText(R.string.unavailableService);
      }

      retry.setOnClickListener(new OnClickListener()
      {

        @Override
        public void onClick(View view)
        {
          if (onCompletion != null)
          {
            onCompletion.run();
          }
        }
      });

      errorAndRetry.post(new Runnable()
      {

        @Override
        public void run()
        {
          errorAndRetry.setVisibility(View.VISIBLE);
        }
      });
    }

    @Override
    public void hide()
    {
      errorAndRetry.setVisibility(View.GONE);
    }
  }

  private final LoadingErrorAndRetryAggregate loadingErrorAndRetryAggregate = new LoadingErrorAndRetryAggregate();

  private final BusinessObjectsUnavailableExceptionKeeper businessObjectsUnavailableExceptionKeeper = new BusinessObjectsUnavailableExceptionKeeper();

  public SmartCIActivityAggregate(Activity activity, Smartable<?> smartable,
      ActivityAnnotation activityAnnotation)
  {
    super(activity, smartable, activityAnnotation);
  }

  public SmartCIApplication getApplication()
  {
    return (SmartCIApplication) activity.getApplication();
  }

  @Override
  protected Object getActionBar(Activity activity)
  {
    return ((SmartAppCompatActivity) activity).getSupportActionBar();
  }

  public AnalyticsSender<Bundle> getAnalyticsSender()
  {
    return getApplication().getAnalyticsSender();
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
