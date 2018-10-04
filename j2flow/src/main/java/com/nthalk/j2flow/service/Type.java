package com.nthalk.j2flow.service;

import java.util.ArrayList;
import java.util.List;

public class Type {

  private final String javaType;
  private final String flowType;
  private final List<TypeField> fields = new ArrayList<>();

  public Type(String javaType, String flowType) {
    this.javaType = javaType;
    this.flowType = flowType;
  }

  public List<TypeField> getFields() {
    return fields;
  }

  public String getJavaType() {
    return javaType;
  }

  public String getFlowType() {
    return flowType;
  }
}
