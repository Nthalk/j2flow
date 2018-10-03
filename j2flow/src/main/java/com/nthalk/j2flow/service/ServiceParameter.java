package com.nthalk.j2flow.service;

import com.nthalk.j2flow.GenerationConfiguration;
import javax.lang.model.element.VariableElement;

public class ServiceParameter {

  private final GenerationConfiguration generationConfiguration;
  private final boolean required;
  private final String name;
  private final String type;
  private final Type paramType;

  public ServiceParameter(GenerationConfiguration generationConfiguration,
      boolean required, VariableElement sourceParam,
      Type paramType) {
    this.generationConfiguration = generationConfiguration;
    this.required = required;
    name = sourceParam.getSimpleName().toString();
    type = sourceParam.asType().toString();
    this.paramType = paramType;
  }

  public String getConvertedType() {
    return generationConfiguration.getConvertedType(getType());
  }

  public boolean getRequired() {
    return required;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Type getParamType() {
    return paramType;
  }

  public enum Type {
    PATH,
    HEADER,
    BODY,
    QUERY
  }
}
