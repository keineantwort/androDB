package org.androdb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.androdb.AndroDBException;
import org.androdb.Const;
import org.androdb.metadata.EntityDescriptor;
import org.androdb.metadata.FieldDescriptor;
import org.androdb.util.StringUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * provides data access functionality for the given type <code>T</code>
 * 
 * @author martin.s.schumacher
 * @since 08.01.2010 18:48:12
 * 
 * @param <T>
 *          the type to create a DataAccessObject for
 * 
 */
public class DataAccessObject<T> {

  /**
   * 
   */
  private static final String GETTER_PREFIX = "get";
  private EntityDescriptor descriptor;
  private String entityName;
  private SQLiteDatabase db;

  /**
   * @param type
   *          the type to create a DataAccessObejct for
   */
  public DataAccessObject(EntityDescriptor entityDescriptor, SQLiteDatabase db) {
    this.descriptor = entityDescriptor;
    this.entityName = this.descriptor.getTheEntity().getName();
    this.db = db;
  }

  /**
   * copies a given DAO for handling overriding
   * 
   * @param copy
   *          the dao to copy
   */
  protected DataAccessObject(DataAccessObject<T> copy) {
    this.db = copy.db;
    this.descriptor = copy.descriptor;
    this.entityName = this.descriptor.getTheEntity().getName();
  }

  /**
   * saves the given entity. decides with the value of the PrimaryKey, if the
   * entity should be inserted or updated.
   * 
   * @param entity
   *          the entity to save
   */
  public void save(T entity) {
    try {
      // looking if we have to update or to insert
      T old = null;
      FieldDescriptor pk = this.descriptor.getPrimaryKey();
      Class<T> clazz = this.getEntity();
      Method pkGetter = clazz.getMethod(GETTER_PREFIX + StringUtils.firstCharToUpper(pk.getField().getName()));

      Object key = pkGetter.invoke(entity);

      if (key != null) {
        old = this.findByPrimaryKey(key);
      }

      ContentValues values = new ContentValues();
      for (FieldDescriptor field : this.descriptor.getAllFieldDescriptors()) {
        if (field.isPersistent()) {
          String prefix = GETTER_PREFIX;
          if ((field.getField().getType() == Boolean.class) || (field.getField().getType() == boolean.class)) {
            prefix = "is";
          }
          Method m = clazz.getMethod(prefix + StringUtils.firstCharToUpper(field.getField().getName()));
          this.addToContentValues(values, field.getColumn(), m.invoke(entity));
        }
      }

      long l = 0;
      if (old == null) {
        l = this.db.insert(this.descriptor.getTablename(), null, values);
      } else {
        String whereClause = pk.getColumn() + " = ?";
        String[] whereArgs = { key.toString() };
        l = this.db.update(this.descriptor.getTablename(), values, whereClause, whereArgs);
      }
      if (l == 0) {
        throw new AndroDBException("could not insert values for " + this.entityName + ".");
      }
    } catch (SecurityException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    } catch (NoSuchMethodException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    } catch (IllegalArgumentException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    } catch (IllegalAccessException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    } catch (InvocationTargetException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    }
  }

  public T findByPrimaryKey(Object key) {

    try {
      FieldDescriptor primaryKey = this.descriptor.getPrimaryKey();
      if (primaryKey == null) {
        throw new AndroDBException("the primary key of " + this.entityName + " cannot be null!");
      }
      if (key.getClass() != this.descriptor.getPrimaryKey().getField().getType()) {
        throw new AndroDBException("the configured key for " + this.entityName + " is " + primaryKey.getField().getName() + " which type is "
            + primaryKey.getField().getType().getName() + ". But you passed " + key.getClass().getName() + ".");
      }

      String tablename = this.descriptor.getTablename();
      String[] columns = this.descriptor.getColumns();
      String selection = this.descriptor.getPrimaryKey().getColumn() + " = ?";
      String[] selectionargs = { String.valueOf(key) };
      String orderBy = this.descriptor.getPrimaryKey().getColumn();
      Cursor result = this.db.query(tablename, columns, selection, selectionargs, null, null, orderBy);
      if (result.getCount() > 1) {
        throw new AndroDBException("there can just be one result, while finding by primary key.");
      }
      // nothing found
      if (result.getCount() == 0) {
        return null;
      }
      result.moveToFirst();

      return this.makeInstance(result);

    } catch (SecurityException e) {
      throw new AndroDBException("Could not find " + this.entityName + " by primary key.", e);
    } catch (IllegalArgumentException e) {
      throw new AndroDBException("Could not find " + this.entityName + " by primary key.", e);
    }
  }

  /**
   * finds all Entitys of type <code>T</code>
   * 
   * @return a list with all entities
   */
  public List<T> findAll() {
    return this.find(null, null, null, null, this.descriptor.getPrimaryKey().getColumn());
  }

  public List<T> find(String selection, String[] selectionargs, String groupBy, String having, String orderBy) {
    String tablename = this.descriptor.getTablename();
    String[] columns = this.descriptor.getColumns();
    Cursor result = this.db.query(tablename, columns, selection, selectionargs, null, null, orderBy);
    if (result.getCount() == 0) {
      return new ArrayList<T>();
    }

    return this.makeInstanceOfList(result);
  }

  public List<T> findByExample(T example) {
    try {
      String tablename = this.descriptor.getTablename();
      String[] columns = this.descriptor.getColumns();

      String selection = null;
      ArrayList<String> selectionArgList = new ArrayList<String>();

      Class<T> clazz = this.getEntity();

      for (FieldDescriptor field : this.descriptor.getAllFieldDescriptors()) {
        if (field.isPersistent()) {
          String prefix = GETTER_PREFIX;
          Log.d(Const.LOG_CAT, field.getField().getName() + "::" + field.getField().getType());
          if ((field.getField().getType() == Boolean.class) || (field.getField().getType() == boolean.class)) {
            prefix = "is";
          }
          Method m = clazz.getMethod(prefix + StringUtils.firstCharToUpper(field.getField().getName()));

          Object val = m.invoke(example);
          if (val != null) {
            selection = (selection == null ? "" : selection + " and ");
            selection = selection + field.getColumn() + " = ?";
            selectionArgList.add(val.toString());
          }
        }
      }

      String[] selectionargs = (selectionArgList.size() > 0 ? selectionArgList.toArray(new String[0]) : null);
      Cursor result = this.db.query(tablename, columns, selection, selectionargs, null, null, null);

      return this.makeInstanceOfList(result);

    } catch (SecurityException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    } catch (NoSuchMethodException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    } catch (IllegalArgumentException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    } catch (IllegalAccessException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    } catch (InvocationTargetException e) {
      throw new AndroDBException("Could not save " + this.entityName + ".", e);
    }
  }

  /**
   * deletes the given entity
   * 
   * @param entity
   *          the entity to delete
   */
  public void delete(T entity) {
    try {
      FieldDescriptor primaryKey = this.descriptor.getPrimaryKey();
      if (primaryKey == null) {
        throw new AndroDBException("the primary key of " + this.entityName + " cannot be null!");
      }

      String tablename = this.descriptor.getTablename();
      String selection = this.descriptor.getPrimaryKey().getColumn() + " = ?";

      Method m = this.getEntity().getMethod(GETTER_PREFIX + StringUtils.firstCharToUpper(this.descriptor.getPrimaryKey().getField().getName()));

      Object key = m.invoke(entity);
      if (key == null) {
        throw new IllegalArgumentException("The primary key field '" + this.descriptor.getPrimaryKey().getField().getName()
            + "' of the given entity was null!");
      }

      String[] selectionargs = { String.valueOf(key) };
      this.db.delete(tablename, selection, selectionargs);

    } catch (SecurityException e) {
      throw new AndroDBException("Could not delete " + this.entityName + " by primary key.", e);
    } catch (IllegalArgumentException e) {
      throw new AndroDBException("Could not delete " + this.entityName + " by primary key.", e);
    } catch (NoSuchMethodException e) {
      throw new AndroDBException("Could not delete " + this.entityName + " by primary key.", e);
    } catch (IllegalAccessException e) {
      throw new AndroDBException("Could not delete " + this.entityName + " by primary key.", e);
    } catch (InvocationTargetException e) {
      throw new AndroDBException("Could not delete " + this.entityName + " by primary key.", e);
    }
  }

  private List<T> makeInstanceOfList(Cursor result) {
    ArrayList<T> list = new ArrayList<T>();
    result.moveToFirst();
    if (result.getCount() > 0) {
      do {
        T instance = this.makeInstanceInternal(result);
        list.add(instance);
      } while (result.moveToNext());
      result.close();
    }
    return list;
  }

  protected T makeInstance(Cursor result) {
    T instance = this.makeInstanceInternal(result);
    result.close();
    return instance;
  }

  private T makeInstanceInternal(Cursor result) {
    try {
      Class<T> clazz = this.getEntity();
      T instance = clazz.newInstance();

      for (FieldDescriptor field : this.descriptor.getAllFieldDescriptors()) {
        if (field.isPersistent()) {
          String setterName = "set" + StringUtils.firstCharToUpper(field.getField().getName());
          Method m = clazz.getMethod(setterName, field.getField().getType());
          Object args = this.getResultValue(result, result.getColumnIndex(field.getColumn()), field.getField().getType());
          m.invoke(instance, args);
        }
      }

      return instance;
    } catch (InstantiationException e) {
      throw new AndroDBException("Could not create instance of " + this.entityName + ".", e);
    } catch (IllegalAccessException e) {
      throw new AndroDBException("Could not create instance of " + this.entityName + ".", e);
    } catch (SecurityException e) {
      throw new AndroDBException("Could not create instance of " + this.entityName + ".", e);
    } catch (NoSuchMethodException e) {
      throw new AndroDBException("Could not create instance of " + this.entityName + ".", e);
    } catch (IllegalArgumentException e) {
      throw new AndroDBException("Could not create instance of " + this.entityName + ".", e);
    } catch (InvocationTargetException e) {
      throw new AndroDBException("Could not create instance of " + this.entityName + ".", e);
    }
  }

  @SuppressWarnings("unchecked")
  private Class<T> getEntity() {
    return (Class<T>) this.descriptor.getTheEntity();
  }

  private Object getResultValue(Cursor result, int columnIndex, Class<?> type) {

    if (type == String.class) {
      return result.getString(columnIndex);
    }
    if ((type == Long.class) || (type == long.class)) {
      return result.getLong(columnIndex);
    }
    if ((type == Integer.class) || (type == int.class)) {
      return result.getInt(columnIndex);
    }
    if ((type == Short.class) || (type == short.class)) {
      return result.getShort(columnIndex);
    }
    if ((type == Boolean.class) || (type == boolean.class)) {
      return result.getInt(columnIndex) == 1;
    }
    if ((type == Double.class) || (type == double.class)) {
      return result.getDouble(columnIndex);
    }
    if ((type == Float.class) || (type == float.class)) {
      return result.getFloat(columnIndex);
    }
    return null;
  }

  private void addToContentValues(ContentValues values, String key, Object value) {
    if (value instanceof String) {
      values.put(key, (String) value);
    }
    if ((value instanceof Long) || ((value != null) && (value.getClass() == long.class))) {
      values.put(key, (Long) value);
    }
    if ((value instanceof Integer) || ((value != null) && (value.getClass() == int.class))) {
      values.put(key, (Integer) value);
    }
    if ((value instanceof Short) || ((value != null) && (value.getClass() == short.class))) {
      values.put(key, (Short) value);
    }
    if ((value instanceof Boolean) || ((value != null) && (value.getClass() == boolean.class))) {
      values.put(key, (Boolean) value);
    }
    if ((value instanceof Double) || ((value != null) && (value.getClass() == double.class))) {
      values.put(key, (Double) value);
    }
    if ((value instanceof Float) || ((value != null) && (value.getClass() == float.class))) {
      values.put(key, (Float) value);
    }
    if ((value instanceof Byte) || ((value != null) && (value.getClass() == byte.class))) {
      values.put(key, (Byte) value);
    }

  }
}
