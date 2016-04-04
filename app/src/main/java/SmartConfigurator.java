import com.smartnsoft.droid4me.log.AndroidLogger;
import com.smartnsoft.droid4me.log.Logger;
import com.smartnsoft.droid4me.log.LoggerWrapper;

import com.smartnsoft.smartci.Constants;
import com.smartnsoft.logentries.LogentriesLoggerConfigurator;
import com.smartnsoft.logentries.log.LogentriesLogger;

/**
 * @author Raphaël Kiffer
 * @since 2016.04.04
 */
public final class SmartConfigurator
    extends LogentriesLoggerConfigurator
{

  public SmartConfigurator()
  {
    LogentriesLogger.setLogPrefix("[SmartCI] ");
  }

  @Override
  public Logger getLogger(String category)
  {
    return new LoggerWrapper(category, Constants.LOGENTRIES_ENABLED == true ? new LogentriesLogger(category, Constants.LOGENTRIES_API_KEY) : new AndroidLogger(category));
  }

  @Override
  public Logger getLogger(Class<?> theClass)
  {
    return new LoggerWrapper(theClass, Constants.LOGENTRIES_ENABLED == true ? new LogentriesLogger(theClass, Constants.LOGENTRIES_API_KEY) : new AndroidLogger(theClass));
  }

}
