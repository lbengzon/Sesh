package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.sql.SQLException;

/**
 * The proxy interface. You must be able to fill your bean if you implement
 * this.
 *
 * @author leandro
 */
public interface Proxy {

  /**
   * If the bean is null, fills the bean.
   *
   * @throws SQLException
   *           Throws an sql exception if there is an issue getting a connection
   *           to the database.
   */
  default void fill() throws SQLException {
    if (isBeanNull()) {

      fillBean();

    }
  }

  /**
   * Fill the bean.
   *
   * @throws SQLException
   *           If there is a problem accessing the database.
   */
  void fillBean() throws SQLException;

  /**
   * Checks if the bean is null.
   *
   * @return Whether the bean is null or not.
   */
  boolean isBeanNull();

}
