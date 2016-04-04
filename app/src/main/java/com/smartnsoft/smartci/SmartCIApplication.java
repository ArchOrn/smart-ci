package com.smartnsoft.smartci;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectUnavailableException;
import com.smartnsoft.droid4me.analytics.AnalyticsSender;
import com.smartnsoft.droid4me.analytics.IssueReporter;
import com.smartnsoft.droid4me.app.ActivityController;
import com.smartnsoft.droid4me.app.ActivityController.Interceptor;
import com.smartnsoft.droid4me.app.ActivityController.IssueAnalyzer;
import com.smartnsoft.droid4me.app.ActivityController.SystemServiceProvider;
import com.smartnsoft.droid4me.app.Droid4mizer;
import com.smartnsoft.droid4me.app.ExceptionHandlers.DefaultExceptionHandler;
import com.smartnsoft.droid4me.app.SmartApplication;
import com.smartnsoft.droid4me.app.SmartCommands;
import com.smartnsoft.droid4me.app.Smartable;
import com.smartnsoft.droid4me.app.Smarted;
import com.smartnsoft.droid4me.bo.Business.InputAtom;
import com.smartnsoft.droid4me.cache.DbPersistence;
import com.smartnsoft.droid4me.cache.Persistence;
import com.smartnsoft.droid4me.debug.Droid4meDebugInterceptor;
import com.smartnsoft.droid4me.download.BasisDownloadInstructions;
import com.smartnsoft.droid4me.download.BitmapDownloader;
import com.smartnsoft.droid4me.download.DownloadInstructions;
import com.smartnsoft.droid4me.download.DownloadSpecs;
import com.smartnsoft.droid4me.ext.app.ConnectivityListener;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor;
import com.smartnsoft.droid4me.ext.app.LoadingAndErrorInterceptor.BusinessObjectUnavailableReporter;
import com.smartnsoft.droid4me.ext.widget.SmartRowListAdapter;
import com.smartnsoft.droid4me.ws.WebServiceCaller;
import com.smartnsoft.droid4me.ws.WebServiceClient.CallException;

import com.smartnsoft.smartci.app.SmartCIActivityAggregate.SmartCIErrorAndRetryManager;
import com.smartnsoft.smartci.app.SmartCIConnectivityListener;
import com.smartnsoft.smartci.app.SmartCIFragmentAggregate;
import com.smartnsoft.smartci.app.SmartCIInterceptor;
import com.crittercism.app.Crittercism;
import com.smartnsoft.layoutinflator.graphics.TypefaceManager;
import com.smartnsoft.layoutinflator.graphics.TypefaceManager.SimpleTypefaceable;
import com.smartnsoft.layoutinflator.graphics.TypefaceManager.TypefaceLocation;
import com.smartnsoft.layoutinflator.view.SmartLayoutInflater;
import com.smartnsoft.layoutinflator.view.SmartLayoutInflater.OnViewInflatedListener;
import org.apache.http.HttpStatus;

/**
 * The entry point of the application.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public final class SmartCIApplication
    extends SmartApplication
{

  public final static class ApplicationConstants
  {

    public final boolean isSmartphone;

    public final boolean isPhablet;

    public final boolean isTablet;

    public final boolean canRotate;

    private ApplicationConstants(Resources resources)
    {
      this.isSmartphone = resources.getBoolean(R.bool.isSmartphone);
      this.isPhablet = resources.getBoolean(R.bool.isPhablet);
      this.isTablet = resources.getBoolean(R.bool.isTablet);
      this.canRotate = resources.getBoolean(R.bool.canRotate);
    }
  }

  public static final class CacheInstructions
      extends DownloadInstructions.AbstractInstructions
  {

    @Override
    public Bitmap hasTemporaryBitmap(View imageView, String imageUid, Object imageSpecs)
    {
      if (imageSpecs instanceof DownloadSpecs.TemporaryImageSpecs)
      {
        final DownloadSpecs.TemporaryImageSpecs temporaryImageSpecs = (DownloadSpecs.TemporaryImageSpecs) imageSpecs;
        return temporaryImageSpecs.imageResourceId != -1 ? BitmapFactory.decodeResource(imageView.getContext().getResources(), temporaryImageSpecs.imageResourceId) : null;
      }
      return null;
    }

    @Override
    public void onBindTemporaryBitmap(View imageView, Bitmap bitmap, String imageUid, Object imageSpecs)
    {
      ((ImageView) imageView).setImageBitmap(bitmap);
    }

    @Override
    public boolean onBindBitmap(boolean downloaded, View imageView, Bitmap bitmap, String imageUid, Object imageSpecs)
    {
      ((ImageView) imageView).setImageBitmap(bitmap);
      return true;
    }

    @Override
    public InputStream getInputStream(String imageUid, Object imageSpecs, String url,
        BasisDownloadInstructions.InputStreamDownloadInstructor downloadInstructor)
        throws IOException
    {
      final InputAtom inputAtom = Persistence.getInstance(SmartCIApplication.IMAGES_IMAGES_PERSISTENCE_INSTANCE).extractInputStream(url);
      return inputAtom == null ? null : inputAtom.inputStream;
    }

    @Override
    public InputStream onInputStreamDownloaded(String imageUid, Object imageSpecs, String url, InputStream inputStream)
    {
      final InputAtom inputAtom = Persistence.getInstance(SmartCIApplication.IMAGES_IMAGES_PERSISTENCE_INSTANCE).flushInputStream(url, new InputAtom(new Date(), inputStream));
      return inputAtom == null ? null : inputAtom.inputStream;
    }

  }

  public static final int DATA_PERSISTENCE_INSTANCE = 0;

  public static final int IMAGES_IMAGES_PERSISTENCE_INSTANCE = 1;

  public static final int USER_PERSISTENCE_INSTANCE = 2;

  public final static DownloadInstructions.Instructions CACHE_IMAGE_INSTRUCTIONS = new SmartCIApplication.CacheInstructions();

  private static final String USER_DATA_FILE_NAME = "user.db";

  private ConnectivityListener connectivityListener;

  private final static class DummyAnalyticsSender<DictionaryType>
      implements AnalyticsSender<DictionaryType>, Interceptor
  {

    @SuppressWarnings("unused")
    public boolean isEnabled;

    @Override
    public void logEvent(Context context, String tag, DictionaryType dictionary)
    {
    }

    @Override
    public void logError(Context context, String s, DictionaryType dictionaryType)
    {

    }

    @Override
    public void onStartActivity(Activity activity, String tag, DictionaryType dictionary)
    {
    }

    @Override
    public void onEndActivity(Activity activity)
    {
    }

    @Override
    public void onLifeCycleEvent(Activity activity, Object component, InterceptorEvent event)
    {
    }

  }

  private final DummyAnalyticsSender<Bundle> analyticsSender = new DummyAnalyticsSender<>();

  public static final class Typefaces
      extends SimpleTypefaceable
  {

    public static final Typefaces RobotoBold = new Typefaces(TypefaceLocation.Assets, "Roboto-Bold.ttf");

    public static final Typefaces RobotoCondensedRegular = new Typefaces(TypefaceLocation.Assets, "RobotoCondensed-Regular.ttf");

    public static final Typefaces RobotoLight = new Typefaces(TypefaceLocation.Assets, "Roboto-Light.ttf");

    public static final Typefaces RobotoMedium = new Typefaces(TypefaceLocation.Assets, "Roboto-Medium.ttf");

    public static final Typefaces RobotoItalic = new Typefaces(TypefaceLocation.Assets, "Roboto-Italic.ttf");

    private Typefaces(TypefaceLocation location, String fileName)
    {
      super(location, fileName);
    }
  }

  private static TypefaceManager<Typefaces> typefaceManager;

  public static TypefaceManager<Typefaces> getTypefaceManager()
  {
    if (typefaceManager == null)
    {
      typefaceManager = new TypefaceManager<>();
    }
    return typefaceManager;
  }

  public ConnectivityListener getConnectivityListener()
  {
    return connectivityListener;
  }

  public AnalyticsSender<Bundle> getAnalyticsSender()
  {
    return analyticsSender;
  }

  private static ApplicationConstants applicationConstants;

  public static ApplicationConstants getApplicationConstants()
  {
    return applicationConstants;
  }

  @Override
  protected int getLogLevel()
  {
    return Constants.LOG_LEVEL;
  }

  @Override
  protected I18N getI18N()
  {
    return new I18N(getText(R.string.problem), getText(R.string.unavailableItem), getText(R.string.unavailableService), getText(R.string.connectivityProblem), getText(R.string.connectivityProblemRetry), getText(R.string.unhandledProblem), getString(R.string.applicationName), getText(R.string.dialogButton_unhandledProblem), getString(R.string.progressDialogMessage_unhandledProblem));
  }

  @Override
  protected String getLogReportRecipient()
  {
    return Constants.REPORT_LOG_RECIPIENT_EMAIL;
  }

  @Override
  protected void onSetupExceptionHandlers()
  {
    if (Constants.CRASH_REPORTING_ENABLED == true)
    {
      Crittercism.initialize(getApplicationContext(), Constants.CRITTERCISM_APP_ID);
    }
  }

  @Override
  public void onCreateCustom()
  {
    applicationConstants = new ApplicationConstants(getResources());

    // We tune droid4me's logging level and other components log level
    if (Constants.DEVELOPMENT_MODE == true)
    {
      Droid4mizer.ARE_DEBUG_LOG_ENABLED = true;
      WebServiceCaller.ARE_DEBUG_LOG_ENABLED = true;
      SmartLayoutInflater.DEBUG_LOG_ENABLED = true;
      SmartRowListAdapter.DEBUG_LOG_ENABLED = true;
    }

    super.onCreateCustom();

    logDeviceInformation();

    // We listen to the network connectivity events
    connectivityListener = new SmartCIConnectivityListener(getApplicationContext());

    // We initialize the analytics sender
    analyticsSender.isEnabled = Constants.ANALYTICS_ENABLED;

    // We initialize the cache persistence
    final String cacheDirectoryPath = getExternalCacheDir() != null ? getExternalCacheDir().getAbsolutePath() : getCacheDir().getAbsolutePath();
    final String userDataDirectoryPath = getExternalFilesDir(null) != null ? getExternalFilesDir(null).getAbsolutePath() : getFilesDir().getAbsolutePath();
    Persistence.CACHE_DIRECTORY_PATHS = new String[] { cacheDirectoryPath, cacheDirectoryPath, userDataDirectoryPath };
    DbPersistence.FILE_NAMES = new String[] { DbPersistence.DEFAULT_FILE_NAME, DbPersistence.DEFAULT_FILE_NAME, SmartCIApplication.USER_DATA_FILE_NAME };
    DbPersistence.TABLE_NAMES = new String[] { "data", "images", "user" };
    Persistence.INSTANCES_COUNT = 3;
    Persistence.IMPLEMENTATION_FQN = DbPersistence.class.getName();

    // We set the BitmapDownloader instances
    BitmapDownloader.IS_DEBUG_TRACE = false;
    BitmapDownloader.IS_DUMP_TRACE = false;
    BitmapDownloader.INSTANCES_COUNT = 1;
    BitmapDownloader.HIGH_LEVEL_MEMORY_WATER_MARK_IN_BYTES = new long[] { 3 * 1024 * 1024 };
    BitmapDownloader.LOW_LEVEL_MEMORY_WATER_MARK_IN_BYTES = new long[] { 1 * 1024 * 1024 };
    BitmapDownloader.setPreThreadPoolSize(4);
    BitmapDownloader.setDownloadThreadPoolSize(4);
  }

  @Override
  protected ActivityController.Redirector getActivityRedirector()
  {
    return new ActivityController.Redirector()
    {
      public Intent getRedirection(Activity activity)
      {
        return null;
      }
    };
  }

  @Override
  protected Interceptor getInterceptor()
  {
    final SmartCIInterceptor applicationInterceptor = new SmartCIInterceptor();
    final LoadingAndErrorInterceptor loadingAndErrorInterceptor = new LoadingAndErrorInterceptor()
    {
      @Override
      protected ErrorAndRetryManagerProvider getErrorAndRetryAttributesProvider()
      {
        return new ErrorAndRetryManagerProvider()
        {
          @Override
          public ErrorAndRetryManager getErrorAndRetryManager(View view)
          {
            return new SmartCIErrorAndRetryManager(view);
          }

          @Override
          public View getLoadingAndRetryView(View view)
          {
            return view.findViewById(R.id.loadingErrorAndRetry);
          }

          @Override
          public View getLoadingView(View view)
          {
            return view.findViewById(R.id.loading);
          }

          @Override
          public View getProgressBar(View view)
          {
            return view.findViewById(R.id.progressBar);
          }

          @Override
          public TextView getTextView(View view)
          {
            return (TextView) view.findViewById(R.id.text);
          }

          @Override
          public View getErrorAndRetryView(View view)
          {
            return view.findViewById(R.id.errorAndRetry);
          }

          @Override
          public CharSequence getErrorText(Context context)
          {
            return context.getString(R.string.unavailableItem);
          }

          @Override
          public CharSequence getLoadingText(Context context)
          {
            return context.getString(R.string.loading);
          }
        };
      }
    };

    final Droid4meDebugInterceptor droid4meDebugInterceptor;
    if (Constants.IS_DROID4ME_INSPECTOR_ENABLED == true)
    {
      droid4meDebugInterceptor = new Droid4meDebugInterceptor(R.id.drawerLayout, 400, 300);
    }
    else
    {
      droid4meDebugInterceptor = null;
    }

    return new Interceptor()
    {
      public void onLifeCycleEvent(Activity activity, Object component, InterceptorEvent event)
      {
        applicationInterceptor.onLifeCycleEvent(activity, component, event);
        loadingAndErrorInterceptor.onLifeCycleEvent(activity, component, event);
        connectivityListener.onLifeCycleEvent(activity, component, event);
        analyticsSender.onLifeCycleEvent(activity, component, event);

        if (droid4meDebugInterceptor != null)
        {
          droid4meDebugInterceptor.onLifeCycleEvent(activity, component, event);
        }
      }
    };
  }

  @Override
  protected ActivityController.ExceptionHandler getExceptionHandler()
  {
    return new DefaultExceptionHandler(getI18N(), new IssueReporter(getApplicationContext(), null), getLogReportRecipient())
    {

      @Override
      protected boolean onBusinessObjectAvailableExceptionFallback(final Activity activity, Object component,
          final BusinessObjectUnavailableException exception)
      {
        if (component instanceof Smartable<?> && activity instanceof SmartCIActivity)
        {
          // We focus on Fragments
          final Smartable<?> containerActivity = (Smartable<?>) activity;
          final Smartable<?> fragment = (Smartable<?>) component;

          if (containerActivity instanceof BusinessObjectUnavailableReporter)
          {
            ((BusinessObjectUnavailableReporter) containerActivity).reportBusinessObjectUnavailableException(fragment, exception);
          }
          else if (fragment.getAggregate() instanceof SmartCIFragmentAggregate)
          {
            containerActivity.getHandler().post(new Runnable()
            {
              @Override
              public void run()
              {
                ((Smartable<SmartCIFragmentAggregate>) fragment).getAggregate().showBusinessObjectUnavailableException(activity, fragment, exception);
              }
            });
          }

          return true;
        }

        if (checkNotFoundException(activity, exception) == true)
        {
          return true;
        }

        reportIssueIfNecessary(true, exception);

        if (super.onBusinessObjectAvailableExceptionFallback(activity, component, exception) == true)
        {
          return true;
        }

        return false;
      }

      @Override
      protected boolean handleConnectivityProblemInCause(Activity activity, Object component, Throwable throwable,
          ConnectivityUIExperience connectivityUIExperience)
      {
        final Throwable exceptionCause = IssueAnalyzer.searchForCause(throwable, BusinessObjectUnavailableException.class);

        if (exceptionCause != null)
        {
          // We handle this connectivity issue has BusinessObjectAvailableException to display better error interface.
          return onBusinessObjectAvailableExceptionFallback(activity, component, (BusinessObjectUnavailableException) exceptionCause);
        }
        else
        {
          return super.handleConnectivityProblemInCause(activity, component, throwable, connectivityUIExperience);
        }
      }

      @Override
      public boolean onActivityExceptionFallback(Activity activity, Object component, Throwable throwable)
      {
        reportIssueIfNecessary(true, throwable);

        if (super.onActivityExceptionFallback(activity, component, throwable) == true)
        {
          return true;
        }

        return false;
      }

      @Override
      protected boolean onContextExceptionFallback(boolean isRecoverable, Context context, Throwable throwable)
      {
        reportIssueIfNecessary(isRecoverable, throwable);

        if (super.onContextExceptionFallback(isRecoverable, context, throwable) == true)
        {
          return true;
        }

        return false;
      }

      @Override
      protected boolean onExceptionFallback(boolean isRecoverable, Throwable throwable)
      {
        reportIssueIfNecessary(isRecoverable, throwable);

        if (super.onExceptionFallback(isRecoverable, throwable) == true)
        {
          return true;
        }

        return false;
      }

      @Override
      protected boolean handleOtherCauses(Activity activity, Object component, Throwable throwable)
      {
        if (checkDetachedFragmentProblem(component, throwable) == true)
        {
          return true;
        }
        else if (checkGuardedException(activity, throwable) == true)
        {
          return true;
        }

        return super.handleOtherCauses(activity, component, throwable);
      }

      private void reportIssueIfNecessary(boolean isRecoverable, final Throwable throwable)
      {
        if (isRecoverable == false)
        {
          return;
        }

        if (Constants.CRASH_REPORTING_ENABLED == true)
        {
          Crittercism.logHandledException(throwable);
        }
      }

      private boolean checkGuardedException(final Activity activity, Throwable throwable)
      {
        if (throwable instanceof SmartCommands.GuardedException)
        {
          final SmartCommands.GuardedException guardedException = (SmartCommands.GuardedException) throwable;
          final String toastMessage = IssueAnalyzer.isAConnectivityProblem(throwable) == true ? getString(R.string.connectivityProblem) : guardedException.displayMessage;

          activity.runOnUiThread(new Runnable()
          {
            @Override
            public void run()
            {
              Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show();
            }
          });
          return true;
        }
        return false;
      }

      private boolean checkDetachedFragmentProblem(Object component, Throwable throwable)
      {
        if (component != null && IssueAnalyzer.searchForCause(throwable, IllegalStateException.class) != null)
        {
          /**
           * Manage the IllegalStateException and sound off this Exception. When a Fragment is not attached in an Activity, it's cause an
           * IllegalStateException when the fragment need to get strings or resources.
           */
          return true;
        }

        return false;
      }

      private boolean checkNotFoundException(Object component, BusinessObjectUnavailableException exception)
      {
        final CallException callException = (CallException) IssueAnalyzer.searchForCause(exception, CallException.class);

        if (callException != null && callException.getStatusCode() >= HttpStatus.SC_BAD_REQUEST && callException.getStatusCode() < HttpStatus.SC_INTERNAL_SERVER_ERROR)
        {
          // This is a 40X exception
          if (component instanceof Smarted<?>)
          {
            @SuppressWarnings("unchecked") final Smarted<SmartCIFragmentAggregate> fragment = (Smarted<SmartCIFragmentAggregate>) component;
            fragment.getAggregate().getLoadingErrorAndRetryAggregate().reportBusinessObjectUnavailableException(exception);

            return true;
          }
        }

        return false;
      }
    };
  }

  @Override
  protected SystemServiceProvider getSystemServiceProvider()
  {
    final OnViewInflatedListener onViewInflatedListener = new OnViewInflatedListener()
    {

      @Override
      public void onViewInflated(Context context, View view, AttributeSet attrs)
      {
        if (view instanceof TextView)
        {
          final TypedArray typedArray = obtainStyledAttributes(attrs, R.styleable.Widgets);
          final int typeface = typedArray.getInt(R.styleable.Widgets_typeface, 0);
          final TextView textView = (TextView) view;

          typedArray.recycle();

          switch (typeface)
          {
          case 0:
            textView.setTypeface(getTypefaceManager().getTypeface(getApplicationContext(), Typefaces.RobotoBold));
            break;
          case 1:
            textView.setTypeface(getTypefaceManager().getTypeface(getApplicationContext(), Typefaces.RobotoCondensedRegular));
            break;
          case 2:
            textView.setTypeface(getTypefaceManager().getTypeface(getApplicationContext(), Typefaces.RobotoLight));
            break;
          case 3:
            textView.setTypeface(getTypefaceManager().getTypeface(getApplicationContext(), Typefaces.RobotoMedium));
            break;
          case 4:
            textView.setTypeface(getTypefaceManager().getTypeface(getApplicationContext(), Typefaces.RobotoItalic));
            break;
          }
        }
      }

    };

    return new SystemServiceProvider()
    {

      @Override
      public Object getSystemService(final Activity activity, String name, Object defaultService)
      {
        if (LAYOUT_INFLATER_SERVICE.equals(name) == true)
        {
          final LayoutInflater defaultLayoutInflater = (LayoutInflater) defaultService;
          return SmartLayoutInflater.getLayoutInflater(defaultLayoutInflater, activity, onViewInflatedListener);
        }

        return defaultService;
      }

    };
  }

}
