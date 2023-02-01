package datadog.trace.api;

import datadog.trace.context.ContextElement;
import java.util.Map;

/**
 * {@link Baggage} is an immutable key-value store for contextual information shared between spans.
 */
public interface Baggage extends ContextElement {
  /**
   * Get baggage item value from its key.
   *
   * @param key The baggage item key to get the value.
   * @return The baggage item value, <code>null</code> if no baggage with the given key.
   */
  String getItemValue(String key);

  /**
   * Get the baggage items as map.
   *
   * @return An immutable map representing baggage items.
   */
  Map<String, String> asMap();

  /**
   * Get the baggage item count.
   *
   * @return The baggage item count.
   */
  int size();

  /**
   * Check whether the baggage is empty.
   *
   * @return <code>true</code> if the baggage has no item, <code>false</code> otherwise.
   */
  default boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Create a {@link BaggageBuilder} with all the items of the {@link Baggage} instance.
   *
   * @return A {@link BaggageBuilder} with all the items of this instance.
   */
  BaggageBuilder toBuilder();

  // TODO Javadoc
  interface BaggageBuilder {
    BaggageBuilder put(String key, String value);

    BaggageBuilder remove(String key);

    Baggage build();
  }
}