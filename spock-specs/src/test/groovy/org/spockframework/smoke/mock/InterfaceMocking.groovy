/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.smoke.mock

import org.spockframework.mock.TooFewInvocationsError
import spock.lang.FailsWith
import spock.lang.Specification

class InterfaceMocking extends Specification {
  static interface A {
    int getValue()
    void check() throws Exception
  }

  def "should use stubbed value"() {
    def a = Spy(A) {
      getValue() >> 33
      check() >> {}
    }

    when:
    def val = a.getValue()
    a.check()

    then:
    val == 33
  }

  def "should throw exception"() {
    def a = Spy(A) {
      getValue() >> 33
      check() >> {throw new Exception("Stubbed exception")}
    }

    when:
    def val = a.getValue()
    a.check()

    then:
    Exception e = thrown()
    e.message == "Stubbed exception"
    val == 33
  }

  def "equals should not match"() {
    def a1 = Spy(A) {
      getValue() >> 33
      check() >> {}
    }
    def a2 = Spy(A) {
      getValue() >> 33
      check() >> {}
    }

    expect:
    !a1.equals(a2)
  }

}
