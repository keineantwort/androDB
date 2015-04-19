/**
 * 
 */
package org.androdb.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * handles the Database Upgrade instead of droping an recreating it.
 * 
 * @author martin.s.schumacher
 * @since 15.04.2010 20:14:51
 * 
 */
public interface OnUpgradeHandler {
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
