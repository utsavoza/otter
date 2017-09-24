package com.utsavoza.otter.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class OtterActivitySet {

  private static final ClassName INTENT = ClassName.get("android.content", "Intent");
  private static final ClassName CONTEXT = ClassName.get("android.content", "Context");

  private String qualifiedClassName;
  private Map<String, OtterActivityClass> activityMap = new LinkedHashMap<>();

  public OtterActivitySet(String qualifiedClassName) {
    this.qualifiedClassName = qualifiedClassName;
  }

  public void addActivity(OtterActivityClass activity) {
    OtterActivityClass activityClass = activityMap.get(activity.getQualifiedClassName());
    if (activityClass == null) {
      activityMap.put(activity.getQualifiedClassName(), activity);
    }
  }

  public JavaFile writeFactory(Elements elementUtils) {
    TypeElement annotation = elementUtils.getTypeElement(qualifiedClassName);
    PackageElement pkg = elementUtils.getPackageOf(annotation);
    String packageName = pkg.getQualifiedName().toString() + ".factory";
    String factoryClassName = "Otter";

    TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(factoryClassName)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    for (Map.Entry<String, OtterActivityClass> entry : activityMap.entrySet()) {
      OtterActivityClass activityClass = entry.getValue();
      String methodName = "start" + activityClass.getSimpleClassName();
      String qualifiedActivityName = activityClass.getQualifiedClassName() + ".class";

      MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
          .returns(TypeName.VOID)
          .addParameter(CONTEXT, "context");

      method.beginControlFlow("if (context == null)")
          .addStatement("throw new IllegalArgumentException($S)", "context is null")
          .endControlFlow();

      method.addStatement("$T intent = new $T()", INTENT, INTENT)
          .addStatement("intent.setClass(context, " + qualifiedActivityName + ")")
          .addStatement("context.startActivity(intent)");


      typeSpecBuilder.addMethod(method.build());
    }

    return JavaFile.builder(packageName, typeSpecBuilder.build()).build();
  }
}
