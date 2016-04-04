package com.smartnsoft.smartci;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.smartnsoft.droid4me.ext.app.ActivityAnnotations.ActionBarBehavior;
import com.smartnsoft.droid4me.ext.app.ActivityAnnotations.ActionBarTitleBehavior;
import com.smartnsoft.droid4me.ext.app.ActivityAnnotations.ActivityAnnotation;

import com.smartnsoft.smartci.fragment.MainFragment;
import com.smartnsoft.smartci.fragment.MenuFragment;

/**
 * The starting screen of the application.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
@ActivityAnnotation(contentViewIdentifier = R.layout.main_activity, fragmentContainerIdentifier = R.id.fragmentContainer, fragmentClass = MainFragment.class, toolbarIdentifier = R.id.toolbar, actionBarTitleBehavior = ActionBarTitleBehavior.UseTitle, actionBarUpBehavior = ActionBarBehavior.ShowAsDrawer)
public final class MainActivity
    extends SmartCIActivity
{

  private DrawerLayout drawerLayout;

  private ActionBarDrawerToggle drawerToggle;

  @Override
  public void onRetrieveDisplayObjects()
  {
    final FragmentTransaction menuTransaction = getSupportFragmentManager().beginTransaction();
    final MenuFragment menuFragment = new MenuFragment();
    menuTransaction.replace(R.id.menuContainer, menuFragment);
    menuTransaction.commit();

    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.applicationName, R.string.applicationName);
    drawerLayout.setDrawerListener(drawerToggle);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState)
  {
    super.onPostCreate(savedInstanceState);
    if (drawerToggle != null)
    {
      drawerToggle.syncState();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (drawerToggle.onOptionsItemSelected(item))
    {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
