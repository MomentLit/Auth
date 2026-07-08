package com.example.auth.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class DownstreamServiceException extends RuntimeException {

  private final String serviceName;
  private final HttpStatusCode statusCode;
  private final String responseBody;

  public DownstreamServiceException(
          String serviceName,
          HttpStatusCode statusCode,
          String message,
          String responseBody
  ) {
    super(message);
    this.serviceName = serviceName;
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }
}