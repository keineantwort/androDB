/**
 * 
 */
package org.androdb.database;

import java.util.List;
import java.util.Map;

import org.androdb.Const;
import org.androdb.metadata.EntityDescriptor;
import org.androdb.sql.SQLCreator;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author martin.s.schumacher
 * @since 02.11.2010 08:04:45
 * 
 */
public abstract class AbstractOnUpgradeHandler implements OnUpgradeHandler {

  private Map<Class<?>, EntityDescriptor> managedEntities;

  public void setEntityDescriptors(Map<Class<?>, EntityDescriptor> desc) {
    this.managedEntities = desc;
  }

  public void createNewEntities(List<Class<?>> newEnties, SQLiteDatabase db) {
    for (Class<?> entity : newEnties) {
      EntityDescriptor desc = this.managedEntities.get(entity);
      if (desc == null) {
        throw new IllegalStateException("no EntityDescriptor found for " + entity.getName()
            + ". Did you include it in the managed entites list in your DatabaseHelperConfig?");
      }
      SQLCreator sc = new SQLCreator(desc);
      Log.d(Const.LOG_CAT, "running create for " + entity.getName());
      db.execSQL(sc.create());
    }
  }
}
