package org.androdb.sql;

import java.util.List;

import org.androdb.metadata.EntityDescriptor;
import org.androdb.metadata.FieldDescriptor;
import org.androdb.util.StringUtils;

/**
 * creates sql-statements for the given {@link EntityDescriptor}
 * 
 * @author martin.s.schumacher
 * @since 27.01.2010 21:28:00
 * 
 */
public class SQLCreator {
  private EntityDescriptor entity;

  public SQLCreator(EntityDescriptor entity) {
    this.entity = entity;
  }

  public String create() {
    String stmt = "CREATE TABLE";

    stmt = stmt + " " + this.entity.getTablename() + "(";

    List<FieldDescriptor> fields = this.entity.getAllFieldDescriptors();
    for (FieldDescriptor fd : fields) {
      if (fd.isPersistent()) {
        stmt = stmt + fd.getColumn() + " ";
        stmt = stmt + fd.getDatabaseType() + " ";
        stmt = stmt + (fd.isNullable() ? "" : "NOT NULL ");
        if (fd.isPrimaryKey()) {
          stmt = stmt + "PRIMARY KEY ";
          stmt = stmt + (fd.isAutoincrement() ? "AUTOINCREMENT " : "");
        }
        stmt = StringUtils.removeLastChar(stmt) + ", ";
      }
    }
    stmt = StringUtils.removeLastChar(stmt);
    stmt = StringUtils.removeLastChar(stmt);
    stmt = stmt + ")";
    return stmt;
  }

  public String dropTable() {
    String stmt = "DROP TABLE IF EXISTS " + this.entity.getTablename();
    return stmt;
  }

}
