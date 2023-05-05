package datadog.trace.instrumentation.vertx_4_0.server;

import io.vertx.ext.web.RoutingContext;
import java.util.Map;
import net.bytebuddy.asm.Advice;

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

    Throwable throwable = PathParameterPublishingHelper.publishParams(params);
    if (t == null) {
      t = throwable;
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

      Throwable throwable = PathParameterPublishingHelper.publishParams(params);
      if (t == null) {
        t = throwable;
      }
    }
  }
}
