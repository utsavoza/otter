package com.utsavoza.otter.processor;

import android.app.Activity;
import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.utsavoza.otter.OtterActivity;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class) public final class OtterProcessor extends AbstractProcessor {

  private static final String ACTIVITY_TYPE = "android.app.Activity";

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;
  private Messager messager;

  @Override public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
      types.add(annotation.getCanonicalName());
    }
    return types;
  }

  private Set<Class<? extends Annotation>> getSupportedAnnotations() {
    Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
    annotations.add(OtterActivity.class);
    return annotations;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override public boolean process(Set<? extends TypeElement> types, RoundEnvironment env) {
    Map<TypeElement, OtterActivityClass> activityMap = findTargetActivities(env);
    OtterActivitySet activitySet = new OtterActivitySet(OtterActivity.class.getCanonicalName());

    // process all the annotated activities
    for (Map.Entry<TypeElement, OtterActivityClass> entry : activityMap.entrySet()) {
      OtterActivityClass activity = entry.getValue();
      activitySet.addActivity(activity);
    }

    JavaFile javaFile = activitySet.writeFactory(elementUtils);
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      // print message
    }

    return true;
  }

  /** Look for valid annotated activities */
  private Map<TypeElement, OtterActivityClass> findTargetActivities(RoundEnvironment env) {
    Map<TypeElement, OtterActivityClass> activityMap = new LinkedHashMap<>();

    for (Element annotatedElement : env.getElementsAnnotatedWith(OtterActivity.class)) {
      if (!SuperficialValidation.validateElement(annotatedElement)) continue;
      if (annotatedElement.getKind() != ElementKind.CLASS) {
        error(annotatedElement, "Only classes can be annotated with @%s",
            OtterActivity.class.getSimpleName());
        continue;
      }

      TypeElement typeElement = (TypeElement) annotatedElement;
      if (!isValidAnnotatedActivity(typeElement)) {
        error(annotatedElement, "Annotated class should inherit from base class %s",
            Activity.class.getSimpleName());
        continue;
      }

      OtterActivityClass activity = new OtterActivityClass(typeElement);
      activityMap.put(typeElement, activity);
    }
    return activityMap;
  }

  /** Checks whether the annotated class extends an {@link Activity} */
  private boolean isValidAnnotatedActivity(TypeElement typeElement) {
    TypeElement parentType = findParentType(typeElement);
    return parentType.toString().equals(ACTIVITY_TYPE);
  }

  private TypeElement findParentType(TypeElement typeElement) {
    TypeElement currentClass = typeElement;
    while (true) {
      TypeMirror superClassType = typeElement.getSuperclass();
      if (superClassType.getKind() == TypeKind.NONE) {
        return currentClass;
      }
      if (superClassType.toString().equals(ACTIVITY_TYPE)) {
        return (TypeElement) typeUtils.asElement(superClassType);
      }
      currentClass = (TypeElement) typeUtils.asElement(superClassType);
    }
  }

  private void error(Element element, String message, Object... args) {
    messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
  }
}
