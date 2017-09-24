package com.utsavoza.otter.processor;

import javax.lang.model.element.TypeElement;

public class OtterActivityClass {

  private TypeElement annotatedClass;
  private String simpleClassName;
  private String qualifiedClassName;

  public OtterActivityClass(TypeElement annotatedClass) {
    this.annotatedClass = annotatedClass;
    simpleClassName = annotatedClass.getSimpleName().toString();
    qualifiedClassName = annotatedClass.getQualifiedName().toString();
  }

  public String getQualifiedClassName() {
    return qualifiedClassName;
  }

  public String getSimpleClassName() {
    return simpleClassName;
  }

  public TypeElement getAnnotatedClass() {
    return annotatedClass;
  }
}