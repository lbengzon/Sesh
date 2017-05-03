package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.Hashtable;

/**
 * Abstract class that models an object pool.
 *
 * @author Matt
 *
 * @param <T>
 *          - Type of object to hold
 */
public abstract class ObjectPool<T> {
  protected long expirationTime;

  private Hashtable<T, Long> locked, unlocked;

  /**
   * Public constructor.
   */
  public ObjectPool() {
    locked = new Hashtable<>();
    unlocked = new Hashtable<>();
  }

  /**
   * Creates an object.
   *
   * @return - object
   */
  protected abstract T create();

  /**
   * Validates an object.
   *
   * @param o
   *          - object
   * @return - boolean
   */
  protected abstract boolean validate(Object o);

  /**
   * Expires an object.
   *
   * @param o
   *          - object
   */
  protected abstract void expire(Object o);

  /**
   * Checks an object out of the pool.
   *
   * @return - object
   */
  synchronized T checkOut() {
    long now = System.currentTimeMillis();
    if (!unlocked.isEmpty()) {
      for (T o : unlocked.keySet()) {
        if (now - (long) unlocked.get(o) > expirationTime) {
          /* object has expired */
          unlocked.remove(o);
          expire(o);
        } else {
          /* still fresh */
          if (!validate(o)) {
            /* object is invalid */
            unlocked.remove(o);
            expire(o);
          } else {
            /* object is valid */
            unlocked.remove(o);
            locked.put((T) o, new Long(now));
            System.out.println("ping!");
            return o;
          }

        }
      }
    }
    T o = create();
    locked.put(o, new Long(now));
    return o;
  }

  /**
   * Checks an object back in to the pool.
   *
   * @param o
   *          - object
   */
  synchronized void checkIn(T o) {
    locked.remove(o);
    unlocked.put(o, new Long(System.currentTimeMillis()));
  }
}
