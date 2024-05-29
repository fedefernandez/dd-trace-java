package com.datadog.profiling.controller.openjdk.events;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SmapEntryEventTest {

  @Test
  public void testStop() {
    SmapEntryEvent.emit();
  }
}
