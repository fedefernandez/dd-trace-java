package datadog.trace.instrumentation.java.lang.invoke;

import static java.lang.invoke.MethodType.methodType;
import static java.lang.invoke.StringConcatFactory.makeConcatWithConstants;

import datadog.trace.agent.tooling.csi.CallSite;
import datadog.trace.api.iast.IastAdvice;
import datadog.trace.api.iast.InstrumentationBridge;
import de.thetaphi.forbiddenapis.SuppressForbidden;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.StringConcatException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressForbidden
@CallSite(spi = IastAdvice.class, minJavaVersion = 9)
public class StringConcatFactoryCallSite {

  private static final Logger LOG = LoggerFactory.getLogger(StringConcatFactoryCallSite.class);

  private static final char TAG_ARG = '\u0001';
  private static final char TAG_CONST = '\u0002';
  private static final int NULL_STR_LENGTH = "null".length();
  private static final MethodHandle INSTRUMENTATION_BRIDGE = instrumentationBridgeMethod();

  @CallSite.Around(
      value =
          "java.lang.invoke.CallSite java.lang.invoke.StringConcatFactory.makeConcatWithConstants(java.lang.invoke.MethodHandles$Lookup, java.lang.String, java.lang.invoke.MethodType, java.lang.String, java.lang.Object[])",
      invokeDynamic = true)
  public static java.lang.invoke.CallSite aroundMakeConcatWithConstants(
      @CallSite.Argument final MethodHandles.Lookup lookup,
      @CallSite.Argument final String name,
      @CallSite.Argument final MethodType concatType,
      @CallSite.Argument final String recipe,
      @CallSite.Argument final Object... constants)
      throws StringConcatException {
    if (INSTRUMENTATION_BRIDGE == null) {
      return makeConcatWithConstants(lookup, name, concatType, recipe, constants);
    }
    try {
      final MethodHandle[] toStringMethods = new MethodHandle[concatType.parameterCount()];
      final MethodType stringConcatType = lookupToStringConverters(concatType, toStringMethods);
      java.lang.invoke.CallSite callSite =
          makeConcatWithConstants(lookup, name, stringConcatType, recipe, constants);
      if (!(callSite instanceof ConstantCallSite)) {
        // should not happen but better be prepared
        throw new IllegalArgumentException(
            "Expected ConstantCallSite, received " + callSite.getClass());
      }
      MethodHandle target =
          MethodHandles.insertArguments(
              INSTRUMENTATION_BRIDGE, 2, recipe, constants, preprocessRecipe(recipe, constants));
      target = target.asCollector(1, String[].class, concatType.parameterCount());
      target = MethodHandles.foldArguments(target, callSite.getTarget());
      target = MethodHandles.filterArguments(target, 0, toStringMethods);
      return new ConstantCallSite(target);
    } catch (Throwable e) {
      LOG.error(
          "Failed to instrument makeConcatWithConstants, reverting to default concat logic", e);
      return makeConcatWithConstants(lookup, name, concatType, recipe, constants);
    }
  }

  /**
   * Preprocess the recipe and create an array of offsets where for each offset:
   *
   * <ul>
   *   <li><code>offset < 0</code> length of the current recipe chunk with sign changed
   *   <li><code>offset >= 0</code> index of the argument
   * </ul>
   */
  private static int[] preprocessRecipe(final String recipe, final Object[] constants) {
    final List<Integer> offsets = new ArrayList<>();
    final char[] chars = recipe.toCharArray();
    int count = 0, argIndex = 0, constIndex = 0;
    for (final char value : chars) {
      switch (value) {
        case TAG_ARG:
          if (count > 0) {
            offsets.add(-count);
            count = 0;
          }
          offsets.add(argIndex);
          argIndex++;
          break;
        case TAG_CONST:
          final String constant = getConstant(constants, constIndex);
          constIndex++;
          count += getToStringLength(constant);
          break;
        default:
          count++;
          break;
      }
    }
    if (count > 0) {
      offsets.add(-count);
    }
    final int[] result = new int[offsets.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = offsets.get(i);
    }
    return result;
  }

  private static String getConstant(@Nullable final Object[] constants, final int index) {
    if (constants == null) {
      return "";
    }
    final Object result = constants[index];
    return result instanceof String ? (String) result : result.toString();
  }

  private static int getToStringLength(@Nullable final String s) {
    return s == null ? NULL_STR_LENGTH : s.length();
  }

  private static MethodType lookupToStringConverters(
      final MethodType concatType, final MethodHandle[] toStringMethods) {
    MethodType result = concatType;
    for (int i = 0; i < result.parameterCount(); i++) {
      final Class<?> type = result.parameterType(i);
      toStringMethods[i] = toStringConverterFor(type);
      result = result.changeParameterType(i, String.class);
    }
    return result;
  }

  private static MethodHandle toStringConverterFor(final Class<?> cl) {
    try {
      final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
      if (cl == byte.class || cl == short.class || cl == int.class) {
        return lookup.findStatic(String.class, "valueOf", methodType(String.class, int.class));
      } else if (cl == boolean.class) {
        return lookup.findStatic(String.class, "valueOf", methodType(String.class, boolean.class));
      } else if (cl == char.class) {
        return lookup.findStatic(String.class, "valueOf", methodType(String.class, char.class));
      } else if (cl == long.class) {
        return lookup.findStatic(String.class, "valueOf", methodType(String.class, long.class));
      } else if (cl == float.class) {
        return lookup.findStatic(String.class, "valueOf", methodType(String.class, float.class));
      } else if (cl == double.class) {
        return lookup.findStatic(String.class, "valueOf", methodType(String.class, double.class));
      } else {
        final MethodHandle handle =
            lookup.findStatic(String.class, "valueOf", methodType(String.class, Object.class));
        return handle.asType(methodType(String.class, cl));
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to fetch string converter for " + cl, e);
    }
  }

  private static MethodHandle instrumentationBridgeMethod() {
    try {
      final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
      return lookup.findStatic(
          InstrumentationBridge.class,
          "onStringConcatFactory",
          methodType(
              String.class,
              String.class,
              String[].class,
              String.class,
              Object[].class,
              int[].class));
    } catch (Throwable e) {
      LOG.error(
          "Failed to fetch instrumentation bridge method handle, no invocations will be instrumented",
          e);
      return null;
    }
  }
}
