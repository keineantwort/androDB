package org.androdb.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

import org.androdb.annotations.Association;

/**
 * provides information about the given association
 * 
 * @author martin.s.schumacher
 * @since 27.01.2010 21:22:30
 * 
 */
public class AssociationDescriptor {

  private Association assoc;
  private Field theField;
  private Class<?> theEntityType;

  /**
   * @param type
   *          the type to create a <code>AssociationDescriptor</code> for
   */
  public AssociationDescriptor(Field field) {
    this.assoc = field.getAnnotation(Association.class);
    this.theField = field;

    this.init(field);
  }

  private void init(Field field) {
    if (field.getType() == List.class || field.getType() == Set.class) {
      // multiple
      ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
      this.theEntityType = (Class<?>) fieldType.getActualTypeArguments()[0];
    } else {
      // single
      this.theEntityType = field.getType();
    }
  }

  public static boolean isPersistentAssociation(Field field) {
    return field.getAnnotation(Association.class) != null;
  }

}
