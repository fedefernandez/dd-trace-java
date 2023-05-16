package com.datadog.debugger.agent;

import static com.datadog.debugger.util.ValueScriptHelper.serializeValue;

import com.datadog.debugger.el.EvaluationException;
import com.datadog.debugger.el.Value;
import com.datadog.debugger.el.ValueScript;
import com.datadog.debugger.el.values.StringValue;
import com.datadog.debugger.instrumentation.LogOriginInstrumentor;
import com.datadog.debugger.probe.LogProbe;
import datadog.trace.bootstrap.debugger.CapturedContext;
import datadog.trace.bootstrap.debugger.EvaluationError;
import java.util.List;

public class LogMessageTemplateBuilder {
  /**
   * Serialization limits for log messages. Most values are lower than snapshot because you can
   * directly reference values that are in your interest with Expression Language:
   * obj.field.deepfield or array[1001]
   */
  private final List<LogProbe.Segment> segments;

  public LogMessageTemplateBuilder(List<LogProbe.Segment> segments) {
    this.segments = segments;
  }

  static LogOriginInstrumentor instrumentor;

  public String evaluate(CapturedContext context, LogProbe.LogStatus status) {
    if (segments == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (LogProbe.Segment segment : segments) {
      ValueScript parsedExr = segment.getParsedExpr();
      if (segment.getStr() != null) {
        sb.append(segment.getStr());
      } else {
        if (parsedExr != null) {
          try {
            Value<?> result = parsedExr.execute(context);
            if (result.isUndefined()) {
              sb.append(result.getValue());
            } else if (result.isNull()) {
              sb.append("null");
            } else if (result instanceof StringValue
                && ((StringValue) result).getValue().startsWith("setLogLevel_")) {
              String level = ((StringValue) result).getValue();
              level = level.substring(level.lastIndexOf("_") + 1);
              DebuggerAgent.getLoggingTracking().switchAll(level);
            }
            if (result instanceof StringValue
                && ((StringValue) result).getValue().startsWith("getLogOrigin_")) {
              String logPattern = ((StringValue) result).getValue();
              logPattern = logPattern.substring(logPattern.indexOf("_") + 1);
              logPattern = logPattern.replace('_', ' ');
              System.out.println("searching for log: " + logPattern);
              instrumentor =
                  new LogOriginInstrumentor(DebuggerAgent.getLoggingTracking(), logPattern);
              instrumentor.instrument();
            } else {
              serializeValue(sb, segment.getParsedExpr().getDsl(), result.getValue(), status);
            }
          } catch (EvaluationException ex) {
            status.addError(new EvaluationError(ex.getExpr(), ex.getMessage()));
            status.setLogTemplateErrors(true);
            sb.append("{").append(ex.getMessage()).append("}");
          }
        }
      }
    }
    return sb.toString();
  }
}
