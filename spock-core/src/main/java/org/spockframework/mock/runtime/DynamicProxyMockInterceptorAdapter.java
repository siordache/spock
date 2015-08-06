/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.mock.runtime;

import org.spockframework.mock.IResponseGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class DynamicProxyMockInterceptorAdapter implements InvocationHandler {
  private final IProxyBasedMockInterceptor interceptor;

  public DynamicProxyMockInterceptorAdapter(IProxyBasedMockInterceptor interceptor) {
    this.interceptor = interceptor;
  }

  public Object invoke(Object target, Method method, Object[] arguments) throws Throwable {
    IResponseGenerator realMethodInvoker = isDefault(method)
      ? new DefaultMethodInvoker(target, method, arguments)
      : new FailingRealMethodInvoker("Cannot invoke real method on interface based mock object");
    return interceptor.intercept(target, method, arguments, realMethodInvoker);
  }

  /**
   * Returns {@code true} if the argument {@code m} is a default method; returns {@code false} otherwise.
   * <br/>This method is used instead of {@link Method#isDefault()} in order to preserve the compatibility with Java versions prior to java 8.
   *
   * @param m the method to be checked whether it is default or not
   * @return true if and only if the argument {@code m} is a default method as defined by the Java Language Specification.
   */
  public static boolean isDefault(Method m) {
    // Default methods are public non-abstract instance methods declared in an interface.
    return ((m.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) ==
      Modifier.PUBLIC) && m.getDeclaringClass().isInterface();
  }

}
