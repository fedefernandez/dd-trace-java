[ {
  "content" : {
    "duration" : ${content_duration},
    "error" : 0,
    "meta" : {
      "_dd.profiling.ctx" : "test",
      "component" : "junit",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "env" : "none",
      "language" : "jvm",
      "library_version" : ${content_meta_library_version},
      "os.architecture" : ${content_meta_os_architecture},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "runtime-id" : ${content_meta_runtime_id},
      "runtime.name" : ${content_meta_runtime_name},
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "runtime.version" : ${content_meta_runtime_version},
      "span.kind" : "test_session_end",
      "test.command" : "junit-4.10",
      "test.framework" : "junit4",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.itr.tests_skipping.enabled" : "true",
      "test.itr.tests_skipping.type" : "test",
      "test.status" : "fail",
      "test.type" : "test"
    },
    "metrics" : {
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0,
      "process_id" : ${content_metrics_process_id},
      "test.itr.tests_skipping.count" : 0
    },
    "name" : "junit.test_session",
    "resource" : "junit-4.10",
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "start" : ${content_start},
    "test_session_id" : ${content_test_session_id}
  },
  "type" : "test_session_end",
  "version" : 1
}, {
  "content" : {
    "duration" : ${content_duration_2},
    "error" : 0,
    "meta" : {
      "component" : "junit",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "env" : "none",
      "library_version" : ${content_meta_library_version},
      "os.architecture" : ${content_meta_os_architecture},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "runtime.name" : ${content_meta_runtime_name},
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "runtime.version" : ${content_meta_runtime_version},
      "span.kind" : "test_module_end",
      "test.framework" : "junit4",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.itr.tests_skipping.enabled" : "true",
      "test.itr.tests_skipping.type" : "test",
      "test.module" : "junit-4.10",
      "test.status" : "fail",
      "test.type" : "test"
    },
    "metrics" : {
      "test.itr.tests_skipping.count" : 0
    },
    "name" : "junit.test_module",
    "resource" : "junit-4.10",
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "start" : ${content_start_2},
    "test_module_id" : ${content_test_module_id},
    "test_session_id" : ${content_test_session_id}
  },
  "type" : "test_module_end",
  "version" : 1
}, {
  "content" : {
    "duration" : ${content_duration_3},
    "error" : 0,
    "meta" : {
      "component" : "junit",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "env" : "none",
      "library_version" : ${content_meta_library_version},
      "os.architecture" : ${content_meta_os_architecture},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "runtime.name" : ${content_meta_runtime_name},
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "runtime.version" : ${content_meta_runtime_version},
      "span.kind" : "test_suite_end",
      "test.framework" : "junit4",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.module" : "junit-4.10",
      "test.source.file" : "dummy_source_path",
      "test.status" : "fail",
      "test.suite" : "org.example.TestFailedAndSucceed",
      "test.type" : "test"
    },
    "metrics" : { },
    "name" : "junit.test_suite",
    "resource" : "org.example.TestFailedAndSucceed",
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "start" : ${content_start_3},
    "test_module_id" : ${content_test_module_id},
    "test_session_id" : ${content_test_session_id},
    "test_suite_id" : ${content_test_suite_id}
  },
  "type" : "test_suite_end",
  "version" : 1
}, {
  "content" : {
    "duration" : ${content_duration_4},
    "error" : 0,
    "meta" : {
      "_dd.profiling.ctx" : "test",
      "component" : "junit",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "env" : "none",
      "language" : "jvm",
      "library_version" : ${content_meta_library_version},
      "os.architecture" : ${content_meta_os_architecture},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "runtime-id" : ${content_meta_runtime_id},
      "runtime.name" : ${content_meta_runtime_name},
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "runtime.version" : ${content_meta_runtime_version},
      "span.kind" : "test",
      "test.codeowners" : "[\"owner1\",\"owner2\"]",
      "test.framework" : "junit4",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.module" : "junit-4.10",
      "test.name" : "test_another_succeed",
      "test.source.file" : "dummy_source_path",
      "test.source.method" : "test_another_succeed()V",
      "test.status" : "pass",
      "test.suite" : "org.example.TestFailedAndSucceed",
      "test.type" : "test"
    },
    "metrics" : {
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0,
      "process_id" : ${content_metrics_process_id},
      "test.source.end" : 18,
      "test.source.start" : 12
    },
    "name" : "junit.test",
    "parent_id" : ${content_parent_id},
    "resource" : "org.example.TestFailedAndSucceed.test_another_succeed",
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "span_id" : ${content_span_id},
    "start" : ${content_start_4},
    "test_module_id" : ${content_test_module_id},
    "test_session_id" : ${content_test_session_id},
    "test_suite_id" : ${content_test_suite_id},
    "trace_id" : ${content_trace_id}
  },
  "type" : "test",
  "version" : 2
}, {
  "content" : {
    "duration" : ${content_duration_5},
    "error" : 1,
    "meta" : {
      "_dd.profiling.ctx" : "test",
      "component" : "junit",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "env" : "none",
      "error.stack" : ${content_meta_error_stack},
      "error.type" : "java.lang.AssertionError",
      "language" : "jvm",
      "library_version" : ${content_meta_library_version},
      "os.architecture" : ${content_meta_os_architecture},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "runtime-id" : ${content_meta_runtime_id},
      "runtime.name" : ${content_meta_runtime_name},
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "runtime.version" : ${content_meta_runtime_version},
      "span.kind" : "test",
      "test.codeowners" : "[\"owner1\",\"owner2\"]",
      "test.framework" : "junit4",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.module" : "junit-4.10",
      "test.name" : "test_failed",
      "test.source.file" : "dummy_source_path",
      "test.source.method" : "test_failed()V",
      "test.status" : "fail",
      "test.suite" : "org.example.TestFailedAndSucceed",
      "test.type" : "test"
    },
    "metrics" : {
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0,
      "process_id" : ${content_metrics_process_id},
      "test.source.end" : 18,
      "test.source.start" : 12
    },
    "name" : "junit.test",
    "parent_id" : ${content_parent_id},
    "resource" : "org.example.TestFailedAndSucceed.test_failed",
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "span_id" : ${content_span_id_2},
    "start" : ${content_start_5},
    "test_module_id" : ${content_test_module_id},
    "test_session_id" : ${content_test_session_id},
    "test_suite_id" : ${content_test_suite_id},
    "trace_id" : ${content_trace_id_2}
  },
  "type" : "test",
  "version" : 2
}, {
  "content" : {
    "duration" : ${content_duration_6},
    "error" : 0,
    "meta" : {
      "_dd.profiling.ctx" : "test",
      "component" : "junit",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "env" : "none",
      "language" : "jvm",
      "library_version" : ${content_meta_library_version},
      "os.architecture" : ${content_meta_os_architecture},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "runtime-id" : ${content_meta_runtime_id},
      "runtime.name" : ${content_meta_runtime_name},
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "runtime.version" : ${content_meta_runtime_version},
      "span.kind" : "test",
      "test.codeowners" : "[\"owner1\",\"owner2\"]",
      "test.framework" : "junit4",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.module" : "junit-4.10",
      "test.name" : "test_succeed",
      "test.source.file" : "dummy_source_path",
      "test.source.method" : "test_succeed()V",
      "test.status" : "pass",
      "test.suite" : "org.example.TestFailedAndSucceed",
      "test.type" : "test"
    },
    "metrics" : {
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0,
      "process_id" : ${content_metrics_process_id},
      "test.source.end" : 18,
      "test.source.start" : 12
    },
    "name" : "junit.test",
    "parent_id" : ${content_parent_id},
    "resource" : "org.example.TestFailedAndSucceed.test_succeed",
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "span_id" : ${content_span_id_3},
    "start" : ${content_start_6},
    "test_module_id" : ${content_test_module_id},
    "test_session_id" : ${content_test_session_id},
    "test_suite_id" : ${content_test_suite_id},
    "trace_id" : ${content_trace_id_3}
  },
  "type" : "test",
  "version" : 2
} ]