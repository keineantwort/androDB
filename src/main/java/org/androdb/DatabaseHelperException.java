package org.androdb;

/**
 * default exception for the {@link DatabaseHelper}
 * 
 * @author martin.s.schumacher
 * @since 27.01.2010 21:30:30
 * 
 */
public class DatabaseHelperException extends AndroDBException {

  private static final long serialVersionUID = 1L;

  public DatabaseHelperException() {
  }

  public DatabaseHelperException(String detailMessage) {
    super(detailMessage);
  }

  public DatabaseHelperException(Throwable throwable) {
    super(throwable);
  }

  public DatabaseHelperException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

}
