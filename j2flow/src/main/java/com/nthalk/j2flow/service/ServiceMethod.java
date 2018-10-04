package com.nthalk.j2flow.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMethod;

public class ServiceMethod {

  private List<ServiceParameter> parameters = new ArrayList<>();
  private List<ServiceParameter> headerParameters = new ArrayList<>();
  private List<ServiceParameter> pathParameters = new ArrayList<>();
  private List<ServiceParameter> queryParameters = new ArrayList<>();
  private String consumesMimeType;
  private String producesMimeType;
  private String path;
  private RequestMethod method;
  private Type returnType;
  private String name;
  private ServiceParameter bodyParameter;

  public ServiceMethod setBodyParameter(ServiceParameter bodyParameter) {
    this.bodyParameter = bodyParameter;
    return this;
  }

  public ServiceParameter getBodyParameter() {
    return bodyParameter;
  }

  public List<ServiceParameter> getParameters() {
    return parameters;
  }

  public List<ServiceParameter> getHeaderParameters() {
    return headerParameters;
  }

  public List<ServiceParameter> getPathParameters() {
    return pathParameters;
  }

  public List<ServiceParameter> getQueryParameters() {
    return queryParameters;
  }

  public String getConsumesMimeType() {
    return consumesMimeType;
  }

  public ServiceMethod setConsumesMimeType(String consumesMimeType) {
    this.consumesMimeType = consumesMimeType;
    return this;
  }

  public String getProducesMimeType() {
    return producesMimeType;
  }

  public ServiceMethod setProducesMimeType(String producesMimeType) {
    this.producesMimeType = producesMimeType;
    return this;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void setPath(String[] path1, String[] path2) {
    if (path1.length > 0) {
      setPath(path1[0]);
    } else if (path2.length > 0) {
      setPath(path2[0]);
    } else {
      setPath("");
    }
  }

  public String getMethod() {
    return method.name();
  }

  public void setMethod(RequestMethod method) {
    this.method = method;
  }

  public Type getReturnType() {
    return returnType;
  }

  public void setReturnType(Type returnType) {
    this.returnType = returnType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void add(ServiceParameter methodParameter) {
    parameters.add(methodParameter);
    switch (methodParameter.getParamType()) {
      case PATH:
        pathParameters.add(methodParameter);
        break;
      case HEADER:
        headerParameters.add(methodParameter);
        break;
      case BODY:
        bodyParameter = methodParameter;
        break;
      case QUERY:
        queryParameters.add(methodParameter);
        break;
    }
  }

  public void setProduces(String[] produces) {
    if (produces != null && produces.length > 0) {
      setProducesMimeType(produces[0]);
    }
  }

  public void setConsumes(String[] consumes) {
    if (consumes != null && consumes.length > 0) {
      setConsumesMimeType(consumes[0]);
    }
  }
}
