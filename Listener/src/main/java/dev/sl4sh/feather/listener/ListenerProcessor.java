package dev.sl4sh.feather.listener;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_16)
@SupportedAnnotationTypes("dev.sl4sh.feather.listener.Listener")
public class ListenerProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Listener.class);

        for (Element element : annotatedElements) {

            if(!(element instanceof ExecutableElement executableElement)){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Listener should be used on a method.", element);
                continue;
            }

            if(!executableElement.getModifiers().contains(Modifier.STATIC)){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Listener should be used on a static method.", element);
                continue;
            }

            if(executableElement.getReturnType().getKind() != TypeKind.VOID){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Listener methods should not return a value.", element);
                continue;
            }

            if(!executableElement.getModifiers().contains(Modifier.PUBLIC)){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Listener methods should be public.", element);
                continue;
            }

            List<? extends VariableElement> params = executableElement.getParameters();

            if(params.size() != 1){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Listener methods should take exactly one argument.", element);
                continue;
            }

            TypeMirror tm = params.get(0).asType();
            TypeElement eventElem = processingEnv.getElementUtils().getTypeElement(FeatherEvent.class.getTypeName());
            TypeMirror eventTm = processingEnv.getTypeUtils().erasure(eventElem.asType());

            boolean isEvent = processingEnv.getTypeUtils().isSubtype(tm, eventTm);

            if(!isEvent){

                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Listener should take a FeatherEvent as an argument.", element);

            }

        }

        return true;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element,
                                                         AnnotationMirror annotation,
                                                         ExecutableElement member,
                                                         String userText) {
        return List.of(Completions.of("Coucou"));
    }

}
