package com.smartnsoft.smartci;

import android.util.Log;

import org.apache.http.protocol.HTTP;

/**
 * Gathers in one place the constants of the application.
 *
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public abstract class Constants
{

  /**
   * Indicates the application development status.
   */
  static final boolean DEVELOPMENT_MODE = "".equals("") || BuildConfig.DEBUG == true;

  /**
   * The logging level of the application and of the droid4me framework.
   */
  static final int LOG_LEVEL = Constants.DEVELOPMENT_MODE == true ? Log.DEBUG : Log.WARN;

  /**
   * Indicates whether the the droid4me inspector is enabled.
   */
  public static final boolean IS_DROID4ME_INSPECTOR_ENABLED = Constants.DEVELOPMENT_MODE == true ? true : false;

  /**
   * Indicates whether the analytics are enabled.
   */
  public static final boolean ANALYTICS_ENABLED = Constants.DEVELOPMENT_MODE == true ? false : true;

  /**
   * Indicates whether the crash reporter is enabled.
   */
  public static final boolean CRASH_REPORTING_ENABLED = Constants.DEVELOPMENT_MODE == true ? false : true;

  /**
   * Indicates whether the logentries logger is enabled.
   */
  public static final boolean LOGENTRIES_ENABLED = false;

  /**
   * The LogEntries service application id.
   */
  public static final String LOGENTRIES_API_KEY = "";

  /**
   * The Critercism application identifier.
   */
  public static final String CRITTERCISM_APP_ID = "";

  /**
   * The e-mail that will receive error reports.
   */
  public static final String REPORT_LOG_RECIPIENT_EMAIL = null;

  /**
   * The encoding used for decoding the contents of HTTP requests.
   */
  public static final String WEBSERVICES_CONTENT_ENCODING = HTTP.UTF_8;

  /**
   * The encoding used for wrapping the URL of the HTTP requests.
   */
  public static final String WEBSERVICES_HTML_ENCODING = HTTP.ISO_8859_1;

  // The HTTP requests server side response time-out
  public static final int HTTP_CONNECTION_TIMEOUT_IN_MILLISECONDS = 5000;

  // The HTTP requests socket connection time-out
  public static final int HTTP_SOCKET_TIMEOUT_IN_MILLISECONDS = 5000;

}
