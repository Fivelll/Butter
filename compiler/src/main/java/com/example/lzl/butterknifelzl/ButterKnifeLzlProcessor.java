package com.example.lzl.butterknifelzl;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class ButterKnifeLzlProcessor extends AbstractProcessor{

    private Messager mMessager;
    private Filer mFilter;
    private Elements mElements;
    private Types mTypes;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mFilter = processingEnvironment.getFiler();
        mElements = processingEnvironment.getElementUtils();
        mTypes = processingEnvironment.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypeSet = new LinkedHashSet<>();
        annotationTypeSet.add(Bind.class.getCanonicalName());
        annotationTypeSet.add(Click.class.getCanonicalName());
        return annotationTypeSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, set.size() + "");
        Map<TypeElement, List<Element>> classToAnnotation = new HashMap();
        for (TypeElement annotationElement : set){
            Set<? extends Element> annotationSet = roundEnvironment.getElementsAnnotatedWith(annotationElement);
            for (Element element : annotationSet){
                if (element.getModifiers().contains(Modifier.PRIVATE)){
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "Can't process private annotation elememt");
                }
                TypeElement classElement = (TypeElement) element.getEnclosingElement();
                List<Element> annotationList;
                if (classToAnnotation.containsKey(classElement)){
                    annotationList = classToAnnotation.get(classElement);
                } else {
                    annotationList = new ArrayList();
                    classToAnnotation.put(classElement, annotationList);
                }
                annotationList.add(element);
            }
        }

        generateJavaFile(classToAnnotation);


//        try {
//            JavaFileObject jfo = mFilter.createSourceFile("com.example.lzl.butterknifelzl.MainActivity" + ".ViewBinding", new Element[]{});
//            Writer writer = jfo.openWriter();
//            writer.write("package" + " com.example.lzl.butterknifelzl;");
//            writer.close();
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return false;
    }

    public void generateJavaFile(Map<TypeElement, List<Element>> classToAnnotation){

        String packageName, className, annotationGenerateClassName;

        for (Map.Entry<TypeElement, List<Element>> entry : classToAnnotation.entrySet()){
            if (entry.getValue() == null){
                continue;
            }
            //packageName = ((PackageElement) entry.getKey().getEnclosingElement()).getQualifiedName().toString();
            packageName = mElements.getPackageOf(entry.getKey()).getQualifiedName().toString();
            className = entry.getKey().getQualifiedName().toString().substring(packageName.length() + 1);
            annotationGenerateClassName = className + "_Binding";
            MethodSpec.Builder methodSpec = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess(className), "target", Modifier.FINAL);
            for (Element element : entry.getValue()){
                //ClassName viewClass =  ClassName.bestGuess(element.asType().toString());
                if (element.getAnnotation(Bind.class) != null){
                    methodSpec.addStatement("target.$L = ($T) target.findViewById($L)", element.getSimpleName().toString()
                            , ClassName.bestGuess(element.asType().toString()), element.getAnnotation(Bind.class).value());
                }
                if (element.getAnnotation(Click.class) != null){
                    MethodSpec innerMethod = MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(void.class)
                           // .addParameter(ClassName.get("android.view", "View"), "v")
                            .addParameter(ClassName.bestGuess("android.view.View"), "v")
                           // .addParameter(ClassName.bestGuess("android.view.View"), "v")
                            //.addParameter(java.lang.String.class, "s")
                            .addStatement("target.$L()", element.getSimpleName().toString())
                            .build();
                    TypeSpec innerTypeSpec = TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(ClassName.bestGuess("View.OnClickListener"))
                            .addMethod(innerMethod)
                            .build();
                    methodSpec.addStatement("(target.findViewById($L)).setOnClickListener($L)"
                            , element.getAnnotation(Click.class).value(), innerTypeSpec);
                } // end if
            } // end for
            TypeSpec typeSpec = TypeSpec.classBuilder(annotationGenerateClassName)
                    .addModifiers( Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodSpec.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .addFileComment("Generate code form ButterKnife, do not edit!")
                    .build();
            try {
                javaFile.writeTo(mFilter);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } // for
    }  // end method()
}
