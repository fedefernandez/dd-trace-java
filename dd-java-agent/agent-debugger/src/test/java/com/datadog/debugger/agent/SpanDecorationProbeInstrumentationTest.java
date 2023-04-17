package com.datadog.debugger.agent;

import static com.datadog.debugger.el.DSL.eq;
import static com.datadog.debugger.el.DSL.getMember;
import static com.datadog.debugger.el.DSL.index;
import static com.datadog.debugger.el.DSL.ref;
import static com.datadog.debugger.el.DSL.value;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static utils.InstrumentationTestHelper.compileAndLoadClass;

import com.datadog.debugger.el.DSL;
import com.datadog.debugger.el.ProbeCondition;
import com.datadog.debugger.el.ValueScript;
import com.datadog.debugger.el.expressions.BooleanExpression;
import com.datadog.debugger.el.expressions.ValueExpression;
import com.datadog.debugger.probe.SpanDecorationProbe;
import datadog.trace.agent.tooling.TracerInstaller;
import datadog.trace.api.Config;
import datadog.trace.api.interceptor.MutableSpan;
import datadog.trace.api.interceptor.TraceInterceptor;
import datadog.trace.bootstrap.debugger.DebuggerContext;
import datadog.trace.bootstrap.debugger.MethodLocation;
import datadog.trace.bootstrap.debugger.ProbeId;
import datadog.trace.bootstrap.debugger.ProbeImplementation;
import datadog.trace.core.CoreTracer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.joor.Reflect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpanDecorationProbeInstrumentationTest extends ProbeInstrumentationTest {
  private static final String LANGUAGE = "java";
  private static final ProbeId PROBE_ID = new ProbeId("beae1807-f3b0-4ea8-a74f-826790c5e6f8", 0);

  private TestTraceInterceptor traceInterceptor = new TestTraceInterceptor();

  @BeforeEach
  public void setUp() {
    CoreTracer tracer = CoreTracer.builder().build();
    TracerInstaller.forceInstallGlobalTracer(tracer);
    tracer.addTraceInterceptor(traceInterceptor);
  }

  @Test
  public void methodActiveSpanSimpleTag() throws IOException, URISyntaxException {
    final String CLASS_NAME = "com.datadog.debugger.CapturedSnapshot20";
    SpanDecorationProbe.Decoration decoration = createDecoration("tag1", ref("arg"), "arg");
    installSingleSpanDecoration(
        CLASS_NAME,
        SpanDecorationProbe.TargetSpan.ACTIVE,
        decoration,
        "process",
        "int (java.lang.String)");
    Class<?> testClass = compileAndLoadClass(CLASS_NAME);
    int result = Reflect.on(testClass).call("main", "1").get();
    assertEquals(42, result);
    MutableSpan span = traceInterceptor.getFirstSpan();
    assertEquals("1", span.getTags().get("tag1"));
  }

  @Test
  public void methodActiveSpanTagList() throws IOException, URISyntaxException {
    final String CLASS_NAME = "com.datadog.debugger.CapturedSnapshot20";
    SpanDecorationProbe.Decoration deco1 = createDecoration("tag1", ref("arg"), "arg");
    SpanDecorationProbe.Decoration deco2 =
        createDecoration("tag2", getMember(ref("this"), "intField"), "this.intField");
    SpanDecorationProbe.Decoration deco3 = createDecoration("tag3", ref("strField"), "strField");
    SpanDecorationProbe.Decoration deco4 =
        createDecoration("tag4", index(ref("strList"), value(1)), "strList[1]");
    SpanDecorationProbe.Decoration deco5 =
        createDecoration("tag5", index(ref("map"), value("foo3")), "map['foo3']");
    installSingleSpanDecoration(
        CLASS_NAME,
        SpanDecorationProbe.TargetSpan.ACTIVE,
        Arrays.asList(deco1, deco2, deco3, deco4, deco5),
        "process",
        "int (java.lang.String)");
    Class<?> testClass = compileAndLoadClass(CLASS_NAME);
    int result = Reflect.on(testClass).call("main", "1").get();
    assertEquals(42, result);
    MutableSpan span = traceInterceptor.getFirstSpan();
    assertEquals("1", span.getTags().get("tag1"));
    assertEquals("42", span.getTags().get("tag2"));
    assertEquals("hello", span.getTags().get("tag3"));
    assertEquals("foobar2", span.getTags().get("tag4"));
    assertEquals("bar3", span.getTags().get("tag5"));
  }

  @Test
  public void methodRootSpanTagList() throws IOException, URISyntaxException {
    final String CLASS_NAME = "com.datadog.debugger.CapturedSnapshot21";
    SpanDecorationProbe.Decoration deco1 = createDecoration("tag1", ref("arg"), "arg");
    SpanDecorationProbe.Decoration deco2 =
        createDecoration("tag2", getMember(ref("this"), "intField"), "this.intField");
    SpanDecorationProbe.Decoration deco3 = createDecoration("tag3", ref("strField"), "strField");
    SpanDecorationProbe.Decoration deco4 = createDecoration("tag4", ref("@return"), "@return");
    installSingleSpanDecoration(
        CLASS_NAME,
        SpanDecorationProbe.TargetSpan.ROOT,
        Arrays.asList(deco1, deco2, deco3, deco4),
        "process3",
        "int (java.lang.String)");
    Class<?> testClass = compileAndLoadClass(CLASS_NAME);
    int result = Reflect.on(testClass).call("main", "1").get();
    assertEquals(45, result);
    assertEquals(4, traceInterceptor.getTrace().size());
    MutableSpan span = traceInterceptor.getFirstSpan();
    assertEquals("1", span.getTags().get("tag1"));
    assertEquals("42", span.getTags().get("tag2"));
    assertEquals("hello", span.getTags().get("tag3"));
    assertEquals("42", span.getTags().get("tag4"));
  }

  @Test
  public void methodActiveSpanCondition() throws IOException, URISyntaxException {
    final String CLASS_NAME = "com.datadog.debugger.CapturedSnapshot20";
    SpanDecorationProbe.Decoration decoration =
        createDecoration(eq(ref("arg"), value("5")), "arg == '5'", "tag1", ref("arg"), "arg");
    installSingleSpanDecoration(
        CLASS_NAME,
        SpanDecorationProbe.TargetSpan.ACTIVE,
        decoration,
        "process",
        "int (java.lang.String)");
    Class<?> testClass = compileAndLoadClass(CLASS_NAME);
    for (int i = 0; i < 10; i++) {
      int result = Reflect.on(testClass).call("main", String.valueOf(i)).get();
      assertEquals(42, result);
    }
    assertEquals(10, traceInterceptor.getAllTraces().size());
    MutableSpan span = traceInterceptor.getAllTraces().get(5).get(0);
    assertEquals("5", span.getTags().get("tag1"));
  }

  @Test
  public void nullActiveSpan() throws IOException, URISyntaxException {
    final String CLASS_NAME = "CapturedSnapshot01";
    SpanDecorationProbe.Decoration decoration = createDecoration("tag1", ref("arg"), "arg");
    installSingleSpanDecoration(
        CLASS_NAME,
        SpanDecorationProbe.TargetSpan.ACTIVE,
        decoration,
        "main",
        "int (java.lang.String)");
    Class<?> testClass = compileAndLoadClass(CLASS_NAME);
    int result = Reflect.on(testClass).call("main", "1").get();
    assertEquals(3, result);
    assertEquals(0, traceInterceptor.getAllTraces().size());
  }

  private SpanDecorationProbe.Decoration createDecoration(
      String tagName, ValueExpression<?> valueExpr, String valueDsl) {
    List<SpanDecorationProbe.Tag> tags =
        Arrays.asList(new SpanDecorationProbe.Tag(tagName, new ValueScript(valueExpr, valueDsl)));
    return new SpanDecorationProbe.Decoration(null, tags);
  }

  private SpanDecorationProbe.Decoration createDecoration(
      BooleanExpression expression,
      String dsl,
      String tagName,
      ValueExpression<?> valueExpr,
      String valueDsl) {
    List<SpanDecorationProbe.Tag> tags =
        Arrays.asList(new SpanDecorationProbe.Tag(tagName, new ValueScript(valueExpr, valueDsl)));
    return new SpanDecorationProbe.Decoration(new ProbeCondition(DSL.when(expression), dsl), tags);
  }

  private void installSingleSpanDecoration(
      String typeName,
      SpanDecorationProbe.TargetSpan targetSpan,
      SpanDecorationProbe.Decoration decoration,
      String methodName,
      String signature) {
    installSingleSpanDecoration(
        typeName, targetSpan, Arrays.asList(decoration), methodName, signature);
  }

  private void installSingleSpanDecoration(
      String typeName,
      SpanDecorationProbe.TargetSpan targetSpan,
      List<SpanDecorationProbe.Decoration> decorations,
      String methodName,
      String signature) {
    SpanDecorationProbe probe =
        createProbe(PROBE_ID, targetSpan, decorations, typeName, methodName, signature);
    installSpanDecorationProbes(
        typeName, Configuration.builder().setService(SERVICE_NAME).add(probe).build());
  }

  private static SpanDecorationProbe.Builder createProbeBuilder(
      ProbeId id,
      SpanDecorationProbe.TargetSpan targetSpan,
      List<SpanDecorationProbe.Decoration> decorationList,
      String typeName,
      String methodName,
      String signature,
      String... lines) {
    return SpanDecorationProbe.builder()
        .language(LANGUAGE)
        .probeId(id)
        .where(typeName, methodName, signature, lines)
        .evaluateAt(MethodLocation.EXIT)
        .targetSpan(targetSpan)
        .decorate(decorationList);
  }

  private static SpanDecorationProbe createProbe(
      ProbeId id,
      SpanDecorationProbe.TargetSpan targetSpan,
      List<SpanDecorationProbe.Decoration> decorationList,
      String typeName,
      String methodName,
      String signature,
      String... lines) {
    return createProbeBuilder(
            id, targetSpan, decorationList, typeName, methodName, signature, lines)
        .build();
  }

  private void installSingleSpan(String sourceFile, int lineFrom, int lineTill, String... tags) {
    // SpanDecorationProbe spanProbe = createProbe(PROBE_ID, sourceFile, lineFrom, lineTill, tags);
    // installSpanProbes(spanProbe);
  }

  private void installSpanProbes(String expectedClassName, SpanDecorationProbe... probes) {
    installSpanDecorationProbes(
        expectedClassName,
        Configuration.builder()
            .setService(SERVICE_NAME)
            .addSpanDecorationProbes(Arrays.asList(probes))
            .build());
  }

  private void installSpanDecorationProbes(String expectedClassName, Configuration configuration) {
    Config config = mock(Config.class);
    when(config.isDebuggerEnabled()).thenReturn(true);
    when(config.isDebuggerClassFileDumpEnabled()).thenReturn(true);
    currentTransformer = new DebuggerTransformer(config, configuration);
    instr.addTransformer(currentTransformer);
    mockSink = new MockSink();
    DebuggerContext.init(
        mockSink,
        (id, callingClass) ->
            resolver(id, callingClass, expectedClassName, configuration.getSpanDecorationProbes()),
        null);
    DebuggerContext.initClassFilter(new DenyListHelper(null));
  }

  private ProbeImplementation resolver(
      String id,
      Class<?> callingClass,
      String expectedClassName,
      Collection<SpanDecorationProbe> spanDecorationProbes) {
    Assertions.assertEquals(expectedClassName, callingClass.getName());
    for (SpanDecorationProbe probe : spanDecorationProbes) {
      if (probe.getId().equals(id)) {
        return probe;
      }
    }
    return null;
  }

  private static class TestTraceInterceptor implements TraceInterceptor {
    private Collection<? extends MutableSpan> currentTrace;
    private List<List<? extends MutableSpan>> allTraces = new ArrayList<>();

    @Override
    public Collection<? extends MutableSpan> onTraceComplete(
        Collection<? extends MutableSpan> trace) {
      currentTrace = trace;
      allTraces.add(new ArrayList<>(trace));
      return trace;
    }

    @Override
    public int priority() {
      return 0;
    }

    public Collection<? extends MutableSpan> getTrace() {
      return currentTrace;
    }

    public MutableSpan getFirstSpan() {
      if (currentTrace == null) {
        return null;
      }
      return currentTrace.iterator().next();
    }

    public List<List<? extends MutableSpan>> getAllTraces() {
      return allTraces;
    }
  }
}