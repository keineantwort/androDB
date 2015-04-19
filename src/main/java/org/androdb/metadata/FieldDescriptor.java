package org.androdb.metadata;

import java.lang.reflect.Field;

import org.androdb.DatabaseHelperException;
import org.androdb.annotations.DefaultValues;
import org.androdb.annotations.Key;

/**
 * provides meta data for the given field
 * 
 * @author martin.s.schumacher
 * @since 08.01.2010 18:48:12
 * 
 */
public class FieldDescriptor {
  private org.androdb.annotations.Field field;
  private Key key;
  private String column;
  private String type;
  private Field theField;

  /**
   * @param type
   *          the type to create a <code>EntityDescriptor</code> for
   */
  public FieldDescriptor(Field field) {
    this.field = field.getAnnotation(org.androdb.annotations.Field.class);
    this.theField = field;

    if (this.isPersistent()) {
      this.init(field);
    }
  }

  private void init(Field field) {

    // getting the column name
    if (this.field.column().equals(DefaultValues.NOT_SPECIFIED)) {
      this.column = field.getName().toLowerCase();
    } else {
      this.column = this.field.column();
    }

    // getting the type
    if (this.field.type() == DefaultValues.NOT_SPECIFIED) {
      this.type = DBTypeMapper.getInstance().get(field.getType());
      if (this.type == null) {
        throw new DatabaseHelperException("the field " + field.getName() + " has no valid databasetype!");
      }
    } else {
      this.type = this.field.type();
    }

    this.key = field.getAnnotation(Key.class);
  }

  public boolean isPrimaryKey() {
    return this.key != null;
  }

  public boolean isAutoincrement() {
    if (!this.isPrimaryKey()) {
      return false;
    } else {
      return this.key.autoincrement();
    }
  }

  public boolean isNullable() {
    if (this.isPersistent()) {
      return this.field.nullable();
    } else {
      return false;
    }
  }

  public String getColumn() {
    return column;
  }

  public String getDatabaseType() {
    return this.type;
  }

  public Field getField() {
    return this.theField;
  }

  public boolean isPersistent() {
    return this.field != null;
  }

  public static boolean isPersistentField(Field field) {
    return field.getAnnotation(org.androdb.annotations.Field.class) != null;
  }
}
