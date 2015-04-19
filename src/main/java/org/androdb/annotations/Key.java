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
 * marks an field as key of this entity
 * 
 * @author martin.s.schumacher
 * @since 08.01.2010 18:20:13
 * 
 */
@Target(ElementType.FIELD)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {
  /**
   * <p>
   * defines if the key should be generated automatically by incrementing by 1
   * </p>
   * <p>
   * If not defined, the default value is <code>true</code>
   * </p>
   */
  boolean autoincrement() default true;
}
