/*
 * Copyright 2015 the original author or authors.
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

import org.spockframework.mock.CannotCreateMockException;
import org.spockframework.mock.IMockInvocation;
import org.spockframework.mock.IResponseGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultMethodInvoker implements IResponseGenerator {
  private final Object target;
  private final Method method;
  private final Object[] arguments;

  public DefaultMethodInvoker(Object target, Method method, Object[] arguments) {
    this.target = target;
    this.method = method;
    this.arguments = arguments;
  }

  public Object respond(IMockInvocation invocation) {
    try {
      // The commented out code below uses classes from the java.lang.invoke package, which is only available since Java 7.
      // In order to preserve the compatibility of spock-core with older versions of Java, we rewrite this code using reflection.
/*
      final Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      field.setAccessible(true);
      final MethodHandles.Lookup lookup = (MethodHandles.Lookup) field.get(null);
      Object args = (arguments == null) ? new Object[0] : arguments;
      final Object result = lookup
        .unreflectSpecial(method, method.getDeclaringClass())
        .bindTo(target)
        .invokeWithArguments(args);
*/
      Class<?> lookupClass = Class.forName("java.lang.invoke.MethodHandles$Lookup");
      final Field field = lookupClass.getDeclaredField("IMPL_LOOKUP");
      field.setAccessible(true);
      Object implLookup = field.get(null);
      Method unreflectSpecialMethod = lookupClass.getMethod("unreflectSpecial", Method.class, Class.class);
      Object specialHandle = unreflectSpecialMethod.invoke(implLookup, method, method.getDeclaringClass());
      Method bindToMethod = specialHandle.getClass().getMethod("bindTo", Object.class);
      Object bindHandle = bindToMethod.invoke(specialHandle, target);
      Method invokeWithArgumentsMethod = bindHandle.getClass().getMethod("invokeWithArguments", Object[].class);
      Object args = (arguments == null) ? new Object[0] : arguments;
      Object result = invokeWithArgumentsMethod.invoke(bindHandle, args);
      return result;
    } catch (Exception e) {
      throw new CannotCreateMockException(target.getClass(), "Failed to invoke default method '" + method.getName() + "'", e);
    }
  }
}
