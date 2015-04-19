/**
 * 
 */
package org.androdb.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * marks a field for saving in the database
 * 
 * @author martin.s.schumacher
 * @since 08.01.2010 18:20:13
 * 
 */
@Target(ElementType.FIELD)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
  /**
   * <p>
   * defines the datatype in the Database
   * </p>
   * <p>
   * If not specified, androdb will try to determine an automatic mapping
   * </p>
   * 
   */
  String type() default DefaultValues.NOT_SPECIFIED;

  /**
   * <p>
   * defines the column name.
   * </p>
   * <p>
   * If not specified, the field name is taken
   * </p>
   */
  String column() default DefaultValues.NOT_SPECIFIED;

  /**
   * <p>
   * defines if this field is nullable, primary keys ({@link Key}) are not
   * nullable by default
   * </p>
   * <p>
   * default is <code>true</code>
   * </p>
   */
  boolean nullable() default true;

  /**
   * <p>
   * defines the default value for this field. without surrounding '.
   * </p>
   * <p>
   * there's no need to set this
   * </p>
   */
  String defaultValue() default DefaultValues.NOT_SPECIFIED;
}
