package datadog.trace.instrumentation.vertx_3_4.server;

import io.vertx.ext.web.RoutingContext;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.Throw;

class RouteMatchesAdvice {
  @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
  static void after(@Advice.Return int ret,
                    @Advice.Argument(0) final RoutingContext ctx,
                    @Advice.Thrown(readOnly = false) Throwable t) {
    if (ret != 0) {
      return;
    }
    Map<String, String> params = ctx.pathParams();
    if (params.isEmpty()) {
      return;
    }

    Throwable resThr = PathParameterPublishingHelper.publishParams(params);
    if (t == null) {
      t = resThr;
    }
  }

  static class BooleanReturnVariant {
    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void after(@Advice.Return boolean ret,
                      @Advice.Argument(0) final RoutingContext ctx,
                      @Advice.Thrown(readOnly = false) Throwable t) {
      if (!ret) {
        return;
      }
      Map<String, String> params = ctx.pathParams();
      if (params.isEmpty()) {
        return;
      }

      Throwable resThr = PathParameterPublishingHelper.publishParams(params);
      if (t == null) {
        t = resThr;
      }
    }
  }
}
