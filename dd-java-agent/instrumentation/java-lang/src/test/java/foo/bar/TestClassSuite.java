package foo.bar;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClassSuite {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestClassSuite.class);

  public static Class forName(final String className) throws ClassNotFoundException {
    LOGGER.debug("Before forName");
    final Class result = Class.forName(className);
    LOGGER.debug("After forName {}", result);
    return result;
  }

  public static Class forName(final String className, boolean initialize, final ClassLoader loader)
      throws ClassNotFoundException {
    LOGGER.debug("Before forName");
    final Class result = Class.forName(className, initialize, loader);
    LOGGER.debug("After forName {}", result);
    return result;
  }

  public static Method getMethod(
      final String method, final Class clazz, final Class<?>... parameterTypes)
      throws NoSuchMethodException {
    LOGGER.debug("Before getMethod");
    final Method result = clazz.getMethod(method, parameterTypes);
    LOGGER.debug("After getMethod {}", result);
    return result;
  }

  public static Method getDeclaredMethod(
      final String method, final Class clazz, final Class<?>... parameterTypes)
      throws NoSuchMethodException {
    LOGGER.debug("Before getDeclaredMethod");
    final Method result = clazz.getDeclaredMethod(method, parameterTypes);
    LOGGER.debug("After getDeclaredMethod {}", result);
    return result;
  }
}
