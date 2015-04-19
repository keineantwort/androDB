package org.androdb.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * describes an Entity for the database O/R-Mapping
 * </p>
 * 
 * <p>
 * If the <code>table</code>-attribute is not set, the class name will be used
 * as table name
 * </p>
 * 
 * @author martin.s.schumacher
 * @since 08.01.2010 18:20:13
 * 
 */
@Target(ElementType.TYPE)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
  /**
   * 
   * @return the table name to save the data
   */
  public String table() default DefaultValues.NOT_SPECIFIED;
}
