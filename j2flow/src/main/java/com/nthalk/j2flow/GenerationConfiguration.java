package com.nthalk.j2flow;

import java.util.HashMap;
import java.util.Map;

public class GenerationConfiguration {

  private String root;
  private boolean enumerateOverloadedServiceMethods;
  private Map<String, String> builtinConvertedTypes = new HashMap<>();
  private Map<String, String> convertedTypes = new HashMap<>();

  public String getRoot() {
    return root;
  }

  public GenerationConfiguration setRoot(String root) {
    this.root = root;
    return this;
  }

  public boolean getEnumerateOverloadedServiceMethods() {
    return enumerateOverloadedServiceMethods;
  }

  public GenerationConfiguration setEnumerateOverloadedServiceMethods(
      boolean enumerateOverloadedServiceMethods) {
    this.enumerateOverloadedServiceMethods = enumerateOverloadedServiceMethods;
    return this;
  }

  public GenerationConfiguration setBuiltinConvertedTypes(String[] builtinConvertedTypes) {
    if (builtinConvertedTypes != null) {
      for (String builtinConvertedType : builtinConvertedTypes) {
        String[] parts = builtinConvertedType.split(":", 2);
        this.builtinConvertedTypes.put(parts[0], parts[1]);
      }
    }
    return this;
  }

  public GenerationConfiguration setConvertedTypes(String[] convertedTypes) {
    if (convertedTypes != null) {
      for (String convertedType : convertedTypes) {
        String[] parts = convertedType.split(":", 2);
        this.convertedTypes.put(parts[0], parts[1]);
      }
    }
    return this;
  }

  public String getConvertedType(String type) {
    if (convertedTypes.containsKey(type)) {
      return convertedTypes.get(type);
    }
    if (builtinConvertedTypes.containsKey(type)) {
      return builtinConvertedTypes.get(type);
    }
    return type;
  }
}
