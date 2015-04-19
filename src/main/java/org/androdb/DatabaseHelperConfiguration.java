package org.androdb;

import java.util.List;

import org.androdb.annotations.Entity;
import org.androdb.database.OnUpgradeHandler;

import android.content.Context;

/**
 * contains the configuration for the {@link DatabaseHelper}
 * 
 * @author martin.s.schumacher
 * @since 22.01.2010 16:52:16
 * 
 */
public class DatabaseHelperConfiguration {
  private List<Class<?>> entities;
  private int version;
  private String name;
  private Context ctx;
  private OnUpgradeHandler onUpgradeHandler;

  private DatabaseHelperConfiguration(Context ctx, String name, int version, List<Class<?>> entities) {
    this.ctx = ctx;
    this.name = name;
    this.version = version;
    this.entities = entities;
    this.onUpgradeHandler = null;
  }

  /**
   * constructs an config object
   * 
   * @param ctx
   *          the context for the SQLiteDB
   * @param name
   *          the name of the database
   * @param version
   *          the version to setup the database
   * @param entities
   *          a list of Entities ({@link Entity})
   */
  public static DatabaseHelperConfiguration getPersistent(Context ctx, String name, int version, List<Class<?>> entities) {
    return new DatabaseHelperConfiguration(ctx, name, version, entities);
  }

  /**
   * constructs an config object
   * 
   * @param ctx
   *          the context for the SQLiteDB
   * @param entities
   *          a list of Entities ({@link Entity})
   */
  public static DatabaseHelperConfiguration getInMemory(Context ctx, List<Class<?>> entities) {
    return new DatabaseHelperConfiguration(ctx, null, 1, entities);
  }

  public List<Class<?>> getEntities() {
    return this.entities;
  }

  public int getVersion() {
    return this.version;
  }

  public String getName() {
    return this.name;
  }

  public Context getCtx() {
    return this.ctx;
  }

  public DatabaseHelperConfiguration setOnUpgradeHandler(OnUpgradeHandler onUpdateHandler) {
    this.onUpgradeHandler = onUpdateHandler;
    return this;
  }

  public OnUpgradeHandler getOnUpgradeHandler() {
    return this.onUpgradeHandler;
  }
}
