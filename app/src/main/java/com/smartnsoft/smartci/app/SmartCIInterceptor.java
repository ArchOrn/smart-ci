package com.smartnsoft.smartci.app;

import android.app.Activity;

import com.smartnsoft.droid4me.app.SmartApplication;
import com.smartnsoft.droid4me.app.Smartable;
import com.smartnsoft.droid4me.ext.app.ActivityAggregate;
import com.smartnsoft.droid4me.ext.app.ActivityAnnotations.ActivityAnnotation;
import com.smartnsoft.droid4me.ext.app.ActivityAnnotations.FragmentAnnotation;
import com.smartnsoft.droid4me.ext.app.ActivityInterceptor;
import com.smartnsoft.droid4me.ext.app.FragmentAggregate;

/**
 * Is responsible for intercepting life-cycle events.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public final class SmartCIInterceptor
    extends ActivityInterceptor
{

  @Override
  protected ActivityAggregate<? extends SmartApplication> instantiateActivityAggregate(Activity activity,
      Smartable smartable, ActivityAnnotation activityAnnotation)
  {
    return new SmartCIActivityAggregate(activity, smartable, activityAnnotation);
  }

  @Override
  protected FragmentAggregate<? extends SmartApplication, ? extends Activity> instantiateFragmentAggregate(
      Smartable smartable, FragmentAnnotation fragmentAnnotation)
  {
    return new SmartCIFragmentAggregate(smartable, fragmentAnnotation);
  }

}
