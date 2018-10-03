package com.nthalk.j2flow.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface J2FlowConfiguration {

  String root();

  boolean enumerateOverloadedServiceMethods() default true;

  String[] builtinConvertedTypes() default {
      // Strings
      "java.lang.String:string",
      // Booleans (boolean:boolean is auto)
      "java.lang.Boolean:boolean",
      // Numbers
      "short:number",
      "int:number",
      "long:number",
      "double:number",
      "float:number",
      "java.lang.Short:number",
      "java.lang.Integer:number",
      "java.lang.Long:number",
      "java.lang.Double:number",
      "java.lang.Float:number",
      // Bigger numbers
      "java.math.BigInteger:number",
      "java.math.BigDecimal:number",

  };

  String[] convertedTypes() default {};
}
