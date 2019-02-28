package com.unionyy.mobile.spdt.compiler.inject;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.unionyy.mobile.spdt.annotation.SpdtInject;
import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.IProcessor;
import com.unionyy.mobile.spdt.compiler.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class InjectProcessor implements IProcessor {

    private Logger logger;

    @Override
    public void process(
            Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception {
        logger = env.logger;

        // Java中的变量类型为Symbol$VarSymbol，Kotlin中的变量类型为Symbol$MethodSymbol
        Set<Symbol> injectedSymbols
                = (Set<Symbol>) roundEnvironment.getElementsAnnotatedWith(SpdtInject.class);

        Map<ClassSymbol, Set<Symbol>> classifiedSymbols = classifySymbols(injectedSymbols);

        generateInjetorClass(classifiedSymbols, env);
    }

    private Map<ClassSymbol, Set<Symbol>> classifySymbols(Set<Symbol> injectedSymbols) {
        Map<ClassSymbol, Set<Symbol>> result = new LinkedHashMap<>();
        for (Symbol symbol : injectedSymbols) {
            ClassSymbol classSymbol = symbol.enclClass();
            Set<Symbol> symbols = result.get(classSymbol);
            if (symbols == null) {
                symbols = new LinkedHashSet<>();
                result.put(classSymbol, symbols);
            }
            symbols.add(symbol);
        }
        return result;
    }

    private void generateInjetorClass(Map<ClassSymbol, Set<Symbol>> classifiedSymbols, Env env) {
        for (ClassSymbol classSymbol : classifiedSymbols.keySet()) {
            TypeName factoryCls = ClassName.get(classSymbol.packge().toString()
                    , "AppidGetter$$SpdtFactory");

            MethodSpec.Builder methodBuild = MethodSpec
                    .methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassName.get(classSymbol.asType()), "activity")
                    .returns(TypeName.VOID);

            for (Symbol symbol : classifiedSymbols.get(classSymbol)) {
                // Kotlin中的变量名字会有 $annotation 后缀？
                String symbolName = String.valueOf(symbol.name.toString().contains("$annotation") ?
                        symbol.name.subSequence(0, symbol.name.toString().indexOf("$annotation"))
                        : symbol.name);

                // 变量不能被修饰为private或者protected
                if (symbol.getModifiers().contains(Modifier.PRIVATE) ||
                        symbol.getModifiers().contains(Modifier.PROTECTED)) {
                    logger.error(String.format("The property '%s' of '%s' should not be modified with" +
                            " private or protected.", symbolName, classSymbol.getSimpleName()));
                }

                // 变量的类型必须为AppidGetter

                methodBuild.addStatement("activity.$N = new $T().create()", symbolName, factoryCls);
            }

            TypeSpec injectCls = TypeSpec
                    .classBuilder(classSymbol.getSimpleName() + "$$SpdtInjector")
                    .addModifiers(Modifier.FINAL)
                    .addMethod(methodBuild.build())
                    .build();

            try {
                JavaFile.builder(classSymbol.packge().toString(), injectCls).build().writeTo(env.filer);
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    @Override
    public Collection<String> getSupportAnnotations() {
        return Collections.singletonList(
                "com.unionyy.mobile.spdt.annotation.SpdtInject");
    }
}
