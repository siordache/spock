package org.spockframework.smoke.mock;

public interface ISquare {
  double getLength();

  default double getArea() {
    return getLength() * getLength();
  }
}
