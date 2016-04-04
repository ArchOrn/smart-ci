package com.smartnsoft.smartci.app;

import android.content.Context;

import com.smartnsoft.droid4me.app.SmartApplication;
import com.smartnsoft.droid4me.app.Smarted;
import com.smartnsoft.droid4me.download.BitmapDownloader;
import com.smartnsoft.droid4me.ext.app.ConnectivityListener;

import com.smartnsoft.smartci.ws.SmartCIServices;

/**
 * Responsible for propagating connectivity events to all application components.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public final class SmartCIConnectivityListener
    extends ConnectivityListener
{

  public SmartCIConnectivityListener(Context context)
  {
    super(context);
  }

  @Override
  protected void notifyServices(boolean hasConnectivity)
  {
    if (SmartApplication.isOnCreatedDone() == false)
    {
      return;
    }

    SmartCIServices.getInstance().setConnected(hasConnectivity);

    for (int index = 0; index < BitmapDownloader.INSTANCES_COUNT; index++)
    {
      BitmapDownloader.getInstance(index).setConnected(hasConnectivity);
    }
  }

  @Override
  protected void updateActivity(Smarted<?> smartedActivity)
  {
  }

}