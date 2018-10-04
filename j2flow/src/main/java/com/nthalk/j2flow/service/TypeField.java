package com.nthalk.j2flow.service;

public class TypeField {

  final String name;
  final Type type;

  public TypeField(String name, Type type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }
}
