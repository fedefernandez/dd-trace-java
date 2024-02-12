package datadog.trace.instrumentation.couchbase.client;

import static datadog.trace.agent.tooling.bytebuddy.matcher.NameMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.returns;

import com.couchbase.client.java.CouchbaseCluster;
import com.google.auto.service.AutoService;
import datadog.trace.agent.tooling.Instrumenter;
import datadog.trace.agent.tooling.InstrumenterModule;
import datadog.trace.bootstrap.CallDepthThreadLocalMap;
import java.lang.reflect.Method;
import net.bytebuddy.asm.Advice;
import rx.Observable;

@AutoService(Instrumenter.class)
public class CouchbaseClusterInstrumentation extends InstrumenterModule.Tracing
    implements Instrumenter.ForKnownTypes {

  public CouchbaseClusterInstrumentation() {
    super("couchbase");
  }

  @Override
  public String[] knownMatchingTypes() {
    return new String[] {
      "com.couchbase.client.java.cluster.DefaultAsyncClusterManager",
      "com.couchbase.client.java.CouchbaseAsyncCluster"
    };
  }

  @Override
  public String[] helperClassNames() {
    return new String[] {
      "rx.DDTracingUtil",
      "datadog.trace.instrumentation.rxjava.SpanFinishingSubscription",
      "datadog.trace.instrumentation.rxjava.TracedSubscriber",
      "datadog.trace.instrumentation.rxjava.TracedOnSubscribe",
      packageName + ".CouchbaseClientDecorator",
      packageName + ".CouchbaseOnSubscribe",
      packageName + ".CouchbaseOnSubscribe$1"
    };
  }

  @Override
  public void methodAdvice(MethodTransformer transformer) {
    transformer.applyAdvice(
        isMethod().and(isPublic()).and(returns(named("rx.Observable"))).and(not(named("core"))),
        CouchbaseClusterInstrumentation.class.getName() + "$CouchbaseClientAdvice");
  }

  public static class CouchbaseClientAdvice {

    @Advice.OnMethodEnter
    public static int trackCallDepth() {
      return CallDepthThreadLocalMap.incrementCallDepth(CouchbaseCluster.class);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void subscribeResult(
        @Advice.Enter final int callDepth,
        @Advice.Origin final Method method,
        @Advice.Return(readOnly = false) Observable result) {
      if (callDepth > 0) {
        return;
      }
      CallDepthThreadLocalMap.reset(CouchbaseCluster.class);
      result = Observable.create(new CouchbaseOnSubscribe(result, method, null));
    }
  }
}
