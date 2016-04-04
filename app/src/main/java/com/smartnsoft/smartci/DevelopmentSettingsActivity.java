package com.smartnsoft.smartci;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.Preference;

import com.smartnsoft.droid4me.app.SmartCommands;
import com.smartnsoft.droid4me.app.SmartCommands.GuardedCommand;
import com.smartnsoft.droid4me.app.SmartPreferenceActivity;
import com.smartnsoft.droid4me.cache.Persistence;
import com.smartnsoft.droid4me.cache.Persistence.PersistenceException;
import com.smartnsoft.droid4me.cache.Values.Caching;
import com.smartnsoft.droid4me.download.BitmapDownloader;

/**
 * The activity which enables to tune the application.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public final class DevelopmentSettingsActivity
    extends SmartPreferenceActivity<Void>
{

  @Override
  public void onRetrieveDisplayObjects()
  {
    addPreferencesFromResource(R.xml.development_settings);
  }

  @Override
  public void onFulfillDisplayObjects()
  {
    super.onFulfillDisplayObjects();

    findPreference("cache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      @Override
      public boolean onPreferenceClick(Preference preference)
      {
        SmartCommands.execute(new GuardedCommand<Context>(getApplicationContext())
        {
          @Override
          protected void runGuarded()
              throws Exception
          {
            emptyCache();
            finish();
          }
        });
        return true;
      }
    });

    try
    {
      findPreference("version").setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
    }
    catch (NameNotFoundException exception)
    {
      if (log.isWarnEnabled())
      {
        log.warn("Cannot retrieved the version name");
      }
    }
  }

  private void emptyCache()
      throws PersistenceException
  {
    Persistence.clearAll();
    BitmapDownloader.clearAll();
    Caching.emptyAll();
  }
}
