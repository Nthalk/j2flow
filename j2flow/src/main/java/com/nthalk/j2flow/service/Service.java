package com.nthalk.j2flow.service;

import java.util.ArrayList;
import java.util.List;

public class Service {

  private final List<ServiceMethod> methods = new ArrayList<>();
  private String name;
  private String path;

  public List<ServiceMethod> getMethods() {
    return methods;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public void add(ServiceMethod method) {
    methods.add(method);
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
