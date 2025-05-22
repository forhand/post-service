package org.example.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SkipEventInterceptor implements RequestInterceptor {

  private final String skipEventHeader;

  public SkipEventInterceptor(@Value("${client.skip_event.header}") String skipEventHeader) {
    this.skipEventHeader = skipEventHeader;
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    requestTemplate.header(skipEventHeader, "true");
  }
}
