package datadog.trace.civisibility.communication;

import datadog.trace.civisibility.utils.IOThrowingFunction;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.RequestBody;

public interface BackendApi {

  <T> T post(String uri, RequestBody requestBody, IOThrowingFunction<InputStream, T> responseParser)
      throws IOException;
}
