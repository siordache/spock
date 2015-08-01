package org.spockframework.smoke.mock

import spock.lang.Specification

class PartialMockingInterfacesWithDefaultMethods extends Specification {
  def "ISquare with stubbed getLength()"() {
    given:
    ISquare square = Spy() {
      2 * getLength() >> 3
    }
    when:
    def area = square.area
    then:
    area == 9
  }
}
