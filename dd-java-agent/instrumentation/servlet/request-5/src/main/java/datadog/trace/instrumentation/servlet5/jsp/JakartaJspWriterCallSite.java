package datadog.trace.instrumentation.servlet5.jsp;

import datadog.trace.agent.tooling.csi.CallSite;
import datadog.trace.api.iast.IastCallSites;
import datadog.trace.api.iast.InstrumentationBridge;
import datadog.trace.api.iast.Sink;
import datadog.trace.api.iast.VulnerabilityTypes;
import datadog.trace.api.iast.sink.XssModule;
import javax.annotation.Nonnull;

@Sink(VulnerabilityTypes.XSS)
@CallSite(spi = IastCallSites.class)
public class JakartaJspWriterCallSite {

  @CallSite.Before("void jakarta.servlet.jsp.JspWriter.print(java.lang.String)")
  @CallSite.Before("void jakarta.servlet.jsp.JspWriter.println(java.lang.String)")
  @CallSite.Before("void jakarta.servlet.jsp.JspWriter.write(java.lang.String)")
  @CallSite.Before("void jakarta.servlet.jsp.JspWriter.write(java.lang.String, int, int)")
  public static void beforeStringParam(@CallSite.Argument(0) @Nonnull final String s) {
    final XssModule module = InstrumentationBridge.XSS;
    if (module != null) {
      try {
        module.onXss(s);
      } catch (final Throwable e) {
        module.onUnexpectedException("beforeStringParam threw", e);
      }
    }
  }

  @CallSite.Before("void jakarta.servlet.jsp.JspWriter.print(char[])")
  @CallSite.Before("void jakarta.servlet.jsp.JspWriter.println(char[])")
  @CallSite.Before("void jakarta.servlet.jsp.JspWriter.write(char[])")
  @CallSite.Before("void jakarta.servlet.jsp.JspWriter.write(char[], int, int)")
  public static void beforeCharArrayParam(@CallSite.Argument(0) @Nonnull final char[] buf) {
    final XssModule module = InstrumentationBridge.XSS;
    if (module != null) {
      try {
        module.onXss(buf);
      } catch (final Throwable e) {
        module.onUnexpectedException("beforeCharArrayParam threw", e);
      }
    }
  }
}
