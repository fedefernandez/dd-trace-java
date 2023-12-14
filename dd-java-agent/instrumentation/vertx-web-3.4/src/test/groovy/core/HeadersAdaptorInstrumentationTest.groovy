package core

import datadog.trace.agent.test.AgentTestRunner
import datadog.trace.api.iast.InstrumentationBridge
import datadog.trace.api.iast.SourceTypes
import datadog.trace.api.iast.propagation.PropagationModule
import groovy.transform.CompileDynamic
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http2.DefaultHttp2Headers
import io.vertx.core.MultiMap
import io.vertx.core.http.impl.HeadersAdaptor
import io.vertx.core.http.impl.Http2HeadersAdaptor
import org.junit.Assume

@CompileDynamic
class HeadersAdaptorInstrumentationTest extends AgentTestRunner {

  @Override
  protected void configurePreAgent() {
    injectSysConfig('dd.iast.enabled', 'true')
  }

  void 'test that get() is instrumented'() {
    given:
    final module = Mock(PropagationModule)
    InstrumentationBridge.registerIastModule(module)
    addAll([key: 'value'], headers)

    when:
    headers.get('key')

    then:
    1 * module.taintIfTainted('value', headers, SourceTypes.REQUEST_HEADER_VALUE, 'key')

    where:
    headers        | _
    httpAdaptor()  | _
    http2Adaptor() | _
  }

  void 'test that getAll() is instrumented'() {
    given:
    final module = Mock(PropagationModule)
    InstrumentationBridge.registerIastModule(module)
    addAll([[key: 'value1'], [key: 'value2']], headers)

    when:
    headers.getAll('key')

    then:
    1 * module.isTainted(headers) >> { false }
    0 * _

    when:
    headers.getAll('key')

    then:
    1 * module.isTainted(headers) >> { true }
    1 * module.taint(_, 'value1', SourceTypes.REQUEST_HEADER_VALUE, 'key')
    1 * module.taint(_, 'value2', SourceTypes.REQUEST_HEADER_VALUE, 'key')

    where:
    headers        | _
    httpAdaptor()  | _
    http2Adaptor() | _
  }

  void 'test that names() is instrumented'() {
    given:
    final module = Mock(PropagationModule)
    InstrumentationBridge.registerIastModule(module)
    addAll([[key: 'value1'], [key: 'value2']], headers)

    when:
    headers.names()

    then:
    1 * module.isTainted(headers) >> { false }
    0 * _

    when:
    headers.names()

    then:
    1 * module.isTainted(headers) >> { true }
    1 * module.taint(_, 'key', SourceTypes.REQUEST_HEADER_NAME, 'key')

    where:
    headers        | _
    httpAdaptor()  | _
    http2Adaptor() | _
  }

  void 'test that entries() is instrumented'() {
    given:
    // latest versions of vertx 3.x define the entries in the MultiMap interface, so we will lose propagation
    Assume.assumeTrue(hasMethod(headers.getClass(), 'entries'))
    final module = Mock(PropagationModule)
    InstrumentationBridge.registerIastModule(module)
    addAll([[key: 'value1'], [key: 'value2']], headers)

    when:
    final result = headers.entries()

    then:
    1 * module.isTainted(headers) >> { false }
    0 * _

    when:
    headers.entries()

    then:
    1 * module.isTainted(headers) >> { true }
    result.collect { it.key }.unique().each {
      1 * module.taint(_, it, SourceTypes.REQUEST_HEADER_NAME, it)
    }
    result.each {
      1 * module.taint(_, it.value, SourceTypes.REQUEST_HEADER_VALUE, it.key)
    }

    where:
    headers        | _
    httpAdaptor()  | _
    http2Adaptor() | _
  }

  private static boolean hasMethod(final Class<?> target, final String name, final Class<?>... types) {
    try {
      target.getDeclaredMethod(name, types) != null
    } catch (Throwable e) {
      return false
    }
  }

  private static MultiMap httpAdaptor() {
    return new HeadersAdaptor(new DefaultHttpHeaders())
  }

  private static MultiMap http2Adaptor() {
    return new Http2HeadersAdaptor(new DefaultHttp2Headers())
  }

  private static void addAll(final Map<String, String> map, final MultiMap headers) {
    map.each { key, value -> headers.add(key, value) }
  }

  private static void addAll(final List<Map<String, String>> list, final MultiMap headers) {
    list.each { addAll(it, headers) }
  }
}