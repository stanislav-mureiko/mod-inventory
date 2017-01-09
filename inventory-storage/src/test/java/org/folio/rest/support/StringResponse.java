package org.folio.rest.support;

public class StringResponse extends Response {
    private final String body;

  public StringResponse(int statusCode, String body) {
      super(statusCode);
      this.body = body;
    }

  public String getBody() {
    return body;
  }
}
