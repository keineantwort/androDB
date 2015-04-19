package org.androdb.metadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.androdb.annotations.DefaultValues;
import org.androdb.annotations.Entity;
import org.androdb.sql.SQLCreator;

/**
 * provides meta data for the given type
 * 
 * @author martin.s.schumacher
 * @since 08.01.2010 18:48:12
 * 
 */
public class EntityDescriptor {

  private String tablename;
  private Entity entity;

  private HashMap<String, FieldDescriptor> fields;
  private ArrayList<FieldDescriptor> fieldsList;

  private HashMap<String, AssociationDescriptor> assocs;
  private ArrayList<AssociationDescriptor> assocsList;

  private FieldDescriptor primaryKey;
  private SQLCreator sqlCreator;
  private Class<?> theEntity;

  /**
   * @param type
   *          the type to create a <code>EntityDescriptor</code> for
   */
  public EntityDescriptor(Class<?> type) {
    this.entity = type.getAnnotation(Entity.class);
    this.theEntity = type;
    if (this.isPersistent()) {
      this.init(type);
      this.sqlCreator = new SQLCreator(this);
    }

  }

  private void init(Class<?> type) {

    // getting the table name
    if (this.entity.table().equals(DefaultValues.NOT_SPECIFIED)) {
      this.tablename = type.getName().replaceFirst(type.getPackage().getName() + ".", "").toLowerCase();
    } else {
      this.tablename = this.entity.table();
    }

    this.fields = new HashMap<String, FieldDescriptor>();
    this.fieldsList = new ArrayList<FieldDescriptor>();

    this.assocs = new HashMap<String, AssociationDescriptor>();
    this.assocsList = new ArrayList<AssociationDescriptor>();

    Field[] fieldArray = type.getDeclaredFields();
    for (Field field : fieldArray) {
      if (FieldDescriptor.isPersistentField(field)) {
        FieldDescriptor fd = new FieldDescriptor(field);
        this.fields.put(field.getName(), fd);
        this.fieldsList.add(fd);
        if (fd.isPrimaryKey()) {
          this.primaryKey = fd;
        }
      }
      if (AssociationDescriptor.isPersistentAssociation(field)) {
        AssociationDescriptor ad = new AssociationDescriptor(field);
        this.assocs.put(field.getName(), ad);
        this.assocsList.add(ad);
      }
    }
  }

  public FieldDescriptor getPrimaryKey() {
    return this.primaryKey;
  }

  public SQLCreator getSQLCreator() {
    return this.sqlCreator;
  }

  public boolean isPersistent() {
    return this.entity != null;
  }

  public String getTablename() {
    return this.tablename;
  }

  public List<FieldDescriptor> getAllFieldDescriptors() {
    return this.fieldsList;
  }

  public FieldDescriptor getFieldDescriptor(String fieldName) {
    return this.fields.get(fieldName);
  }

  public Class<?> getTheEntity() {
    return this.theEntity;
  }

  public String[] getColumns() {
    ArrayList<String> c = new ArrayList<String>();
    for (FieldDescriptor field : this.fieldsList) {
      if (field.isPersistent()) {
        c.add(field.getColumn());
      }
    }
    return c.toArray(new String[c.size()]);
  }
}
