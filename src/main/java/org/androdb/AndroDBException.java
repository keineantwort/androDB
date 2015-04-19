package org.androdb;

import android.util.Log;

/**
 * a generic Exception for AndroDB
 * 
 * @author martin.s.schumacher
 * @since 27.01.2010 21:28:41
 * 
 */
public class AndroDBException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AndroDBException() {
    Log.e(Const.LOG_CAT, "an unknown error occured.");
  }

  public AndroDBException(String message) {
    super(message);
    Log.e(Const.LOG_CAT, message);
  }

  public AndroDBException(Throwable cause) {
    super(cause);
    Log.e(Const.LOG_CAT, "an unknown error occured.", cause);
  }

  public AndroDBException(String message, Throwable cause) {
    super(message, cause);
    Log.e(Const.LOG_CAT, message, cause);
  }

}
