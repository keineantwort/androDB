package org.androdb;

import java.util.HashMap;

import org.androdb.access.DataAccessObject;
import org.androdb.database.AbstractOnUpgradeHandler;
import org.androdb.database.OnUpgradeHandler;
import org.androdb.metadata.EntityDescriptor;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * provides database access to the using app
 * 
 * @author martin.s.schumacher
 * @since 27.01.2010 21:29:59
 * 
 */
public class DatabaseHelper {

  private DatabaseHelperConfiguration config;
  private HashMap<Class<?>, EntityDescriptor> entityDescriptorList;
  @SuppressWarnings("unchecked")
  private HashMap<Class<?>, DataAccessObject> daos;
  // private boolean initFinished = false;
  private SQLiteOpenHelper dbhelper;

  @SuppressWarnings("unchecked")
  private DatabaseHelper(DatabaseHelperConfiguration config) {
    this.config = config;

    this.entityDescriptorList = new HashMap<Class<?>, EntityDescriptor>();
    this.daos = new HashMap<Class<?>, DataAccessObject>();
    Log.d(Const.LOG_CAT, "managing " + this.config.getEntities().size() + " Entities:");
    for (Class<?> entity : this.config.getEntities()) {
      Log.d(Const.LOG_CAT, "instanciating EntityDescriptor for " + entity.getName());
      EntityDescriptor entityDescriptor = new EntityDescriptor(entity);
      this.entityDescriptorList.put(entity, entityDescriptor);
    }
    this.dbhelper = new SQLiteOpenHelper(config.getCtx(), config.getName(), null, config.getVersion()) {

      @Override
      public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        DatabaseHelper.this.onUpgrade(arg0, arg1, arg2);
      }

      @Override
      public void onCreate(SQLiteDatabase arg0) {
        DatabaseHelper.this.onCreate(arg0);
      }
    };
    for (Class<?> entity : this.config.getEntities()) {
      Log.d(Const.LOG_CAT, "creating DAO for " + entity.getName());
      this.daos.put(entity, new DataAccessObject(this.entityDescriptorList.get(entity), this.dbhelper.getWritableDatabase()));
    }
    Log.d(Const.LOG_CAT, "DatabaseHandler init finished...");
  }

  public static DatabaseHelper createDatabaseHelper(DatabaseHelperConfiguration config) {
    return new DatabaseHelper(config);
  }

  @SuppressWarnings("unchecked")
  public <T> DataAccessObject<T> getDataAccessObject(Class<T> entity) {
    return this.daos.get(entity);
  }

  public void onCreate(SQLiteDatabase db) {
    try {
      Log.d(Const.LOG_CAT, "creating database");
      for (EntityDescriptor ed : this.entityDescriptorList.values()) {
        String sql = ed.getSQLCreator().create();
        Log.d(Const.LOG_CAT, "running sql for entity \"" + ed.getTheEntity().getName() + "\"");
        db.execSQL(sql);
      }
    } catch (Exception e) {
      Log.e(Const.LOG_CAT, "Could not create Database!", e);
      throw new DatabaseHelperException("Could not create Database!", e);
    }
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    OnUpgradeHandler upgradeHandler = this.config.getOnUpgradeHandler();
    if (upgradeHandler != null) {
      if (upgradeHandler instanceof AbstractOnUpgradeHandler) {
        Log.d(Const.LOG_CAT, "setting managed entities.");
        ((AbstractOnUpgradeHandler) upgradeHandler).setEntityDescriptors(this.entityDescriptorList);
      }

      Log.d(Const.LOG_CAT, "running OnUpgradeHandler " + upgradeHandler.getClass().getName());
      upgradeHandler.onUpgrade(db, oldVersion, newVersion);
    } else {
      Log.d(Const.LOG_CAT, "droping old database and recreating it...");
      for (EntityDescriptor ed : this.entityDescriptorList.values()) {
        String sql = ed.getSQLCreator().dropTable();
        Log.d(Const.LOG_CAT, "running drop for entity \"" + ed.getTheEntity().getName() + "\"");
        try {
          db.execSQL(sql);
        } catch (SQLException e) {
          Log.e(Const.LOG_CAT, "could not drop table with sql '" + sql + "'", e);
          throw e;
        }
      }
      this.onCreate(db);
    }
  }
  /*
   * 
   * ---- ------ Preparing automatic update of all tables ----
   * 
   * (11) How do I add or delete columns from an existing table in SQLite.
   * 
   * SQLite has limited ALTER TABLE support that you can use to add a column to
   * the end of a table or to change the name of a table. If you want to make
   * more complex changes in the structure of a table, you will have to recreate
   * the table. You can save existing data to a temporary table, drop the old
   * table, create the new table, then copy the data back in from the temporary
   * table.
   * 
   * For example, suppose you have a table named "t1" with columns names "a",
   * "b", and "c" and that you want to delete column "c" from this table. The
   * following steps illustrate how this could be done:
   * 
   * BEGIN TRANSACTION; CREATE TEMPORARY TABLE t1_backup(a,b); INSERT INTO
   * t1_backup SELECT a,b FROM t1; DROP TABLE t1; CREATE TABLE t1(a,b); INSERT
   * INTO t1 SELECT a,b FROM t1_backup; DROP TABLE t1_backup; COMMIT;
   */

  // List<String> tables = this.getExistingTables(db);
  // Map<String, EntityDescriptor> identifiableEds = new HashMap<String,
  // EntityDescriptor>();
  // for (EntityDescriptor ed : this.entityDescriptorList) {
  // identifiableEds.put(ed.getTablename(), ed);
  // }
  //
  // for (String table : tables) {
  // EntityDescriptor ed = identifiableEds
  // }

  // private List<String> getExistingTables(SQLiteDatabase db) {
  // // SELECT name FROM sqlite_master WHERE type='table' ORDER BY name
  // String[] cols = { "name" };
  // String[] args = { "table" };
  // Cursor tablesResult = db.query("sqlite_master", cols, "type = ?", args, "",
  // "", "");
  //
  // ArrayList<String> list = new ArrayList<String>();
  // tablesResult.moveToFirst();
  // if (tablesResult.getCount() > 0) {
  // do {
  // String table = tablesResult.getString(tablesResult.getColumnIndex("name"));
  // list.add(table);
  // } while (tablesResult.moveToNext());
  // tablesResult.close();
  // }
  // return list;
  //
  // }
}
