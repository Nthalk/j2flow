package com.nthalk.j2flow.service;

public class ServiceParameter {

  private final boolean required;
  private final String name;
  private final ParamType paramType;
  private final Type type;

  public ServiceParameter(
      Type type,
      String name,
      boolean required,
      ParamType paramType) {
    this.type = type;
    this.name = name;
    this.required = required;
    this.paramType = paramType;
  }

  public boolean getRequired() {
    return required;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public ParamType getParamType() {
    return paramType;
  }

  public enum ParamType {
    PATH,
    HEADER,
    BODY,
    QUERY
  }
}
