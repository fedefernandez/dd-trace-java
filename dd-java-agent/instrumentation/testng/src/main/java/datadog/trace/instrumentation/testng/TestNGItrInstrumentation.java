package datadog.trace.instrumentation.testng;

import static datadog.trace.agent.tooling.bytebuddy.matcher.NameMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.google.auto.service.AutoService;
import datadog.trace.agent.tooling.Instrumenter;
import datadog.trace.api.Config;
import java.lang.reflect.Method;
import java.util.Set;
import net.bytebuddy.asm.Advice;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;

@AutoService(Instrumenter.class)
public class TestNGItrInstrumentation extends Instrumenter.CiVisibility
    implements Instrumenter.ForKnownTypes {
  public TestNGItrInstrumentation() {
    super("testng", "testng-itr");
  }

  @Override
  public boolean isApplicable(Set<TargetSystem> enabledSystems) {
    Boolean itrKillswitch = Config.get().getCiVisibilityItrEnabled();
    return super.isApplicable(enabledSystems) && (itrKillswitch == null || itrKillswitch);
  }

  @Override
  public String[] knownMatchingTypes() {
    return new String[] {
      "org.testng.internal.MethodInvocationHelper",
      "org.testng.internal.invokers.MethodInvocationHelper"
    };
  }

  @Override
  public void adviceTransformations(AdviceTransformation transformation) {
    transformation.applyAdvice(
        named("invokeMethod")
            .and(takesArguments(3))
            .and(takesArgument(0, Method.class))
            .and(takesArgument(1, Object.class))
            .and(takesArgument(2, Object[].class)),
        TestNGItrInstrumentation.class.getName() + "$InvokeMethodAdvice");
  }

  @Override
  public String[] helperClassNames() {
    return new String[] {
      packageName + ".TestNGUtils",
      packageName + ".TestNGClassListener",
      packageName + ".ItrFilter",
    };
  }

  public static class InvokeMethodAdvice {
    @Advice.OnMethodEnter
    public static void invokeMethod(
        @Advice.Argument(0) final Method method,
        @Advice.Argument(1) final Object instance,
        @Advice.Argument(2) final Object[] parameters) {
      if (ItrFilter.INSTANCE.skip(method, instance, parameters)) {
        throw new SkipException("Skipped by Datadog Intelligent Test Runner");
      }
    }

    // TestNG 6.4 and above
    public static void muzzleCheck(final DataProvider dataProvider) {
      dataProvider.name();
    }
  }
}