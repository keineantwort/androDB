package org.androdb.metadata;

import java.io.IOException;
import java.util.Properties;

import org.androdb.Const;

import android.util.Log;

/**
 * provides type mapping from android types to database types
 * 
 * @author martin.s.schumacher
 * @since 27.01.2010 21:27:04
 * 
 */
public class DBTypeMapper {

  private static DBTypeMapper _instance;
  private Properties props;

  private DBTypeMapper() {
    this.props = new Properties();
    try {
      this.props.load(DBTypeMapper.class.getResourceAsStream("/org/androdb/metadata/types.properties"));
    } catch (IOException e) {
      Log.w(Const.LOG_CAT, "Could not instanciate the datatypes mapping file.", e);
    }
  }

  public static DBTypeMapper getInstance() {
    if (_instance == null) {
      _instance = new DBTypeMapper();
    }
    return _instance;
  }

  /**
   * 
   * @param type
   *          to get the DB type for
   * @return the coresponding DB type for the given android type
   */
  public String get(Class<?> type) {
    if (type == null) {
      return null;
    }
    return this.props.getProperty(type.getName());
  }

  /**
   * 
   * @param type
   *          to get the DB type for
   * @return the coresponding DB type for the given android type
   */
  public String get(String type) {
    if (type == null) {
      return null;
    }
    return this.props.getProperty(type);
  }

}
