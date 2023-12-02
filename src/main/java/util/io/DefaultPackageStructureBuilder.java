package util.io;

import com.squareup.javapoet.*;
import org.junit.jupiter.api.Test;
import util.common.Solver;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

// TODO refactor it, this class fully depends on maven default standards and not parameterizable
public class DefaultPackageStructureBuilder {

    public static final int DAYS = 25;

    public static void main(String[] args) {
        createPackageStructure();
        writeTemplateResources();
    }

    public static void createPackageStructure() {
        try {
            String basePath = "src/main/java";
            Path days = Path.of(basePath, "day");
            if (!Files.exists(days)) {
                Files.createDirectory(days);
            }
            IntStream.rangeClosed(1, DAYS)
                    .boxed()
                    .forEach(day -> {
                        if (!Files.exists(Path.of(days.toString(), "_" + day))) {
                            TypeSpec main = writeMain(basePath, day);
                            TypeSpec solver = writeSolver(basePath, day);
                            TypeSpec testSolver = writeSolverTest(basePath.replace("main", "test"), day, solver);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static TypeSpec writeMain(String basePath, Integer day) {
        String inputFileName = "\"day" + day + ".txt\"";
        String packageName = "day._" + day;
        String className = "Application";

        MethodSpec method = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("new Day" + day + "Solver(" + inputFileName + ").printResults();")
                .build();

        TypeSpec classSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(method)
                .build();

        writeSource(basePath, JavaFile.builder(packageName, classSpec).build());
        return classSpec;
    }

    private static TypeSpec writeSolver(String basePath, Integer day) {
        String packageName = "day._" + day;
        String className = "Day" + day + "Solver";

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "fileName")
                .addStatement("super(fileName)")
                .build();


        MethodSpec method1 = MethodSpec.methodBuilder("solvePartOne")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Integer.class)
                .addStatement("return null")
                .build();

        MethodSpec method2 = MethodSpec.methodBuilder("solvePartTwo")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Integer.class)
                .addStatement("return null")
                .build();

        TypeSpec classSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(Solver.class, Integer.class))
                .addMethod(constructor)
                .addMethod(method1)
                .addMethod(method2)
                .build();

        writeSource(basePath, JavaFile.builder(packageName, classSpec).build());
        return classSpec;
    }

    private static TypeSpec writeSolverTest(String basePath, Integer day, TypeSpec typeSpec) {
        String inputFileName = "\"day" + day + "-test.txt\"";
        String packageName = "day._" + day;
        String className = "Day" + day + "SolverTest";

        FieldSpec fieldSpec = FieldSpec.builder(ParameterizedTypeName.get(Solver.class, Integer.class), "solver", Modifier.PRIVATE).build();

        MethodSpec method1 = MethodSpec.methodBuilder("testPartOne")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Test.class)
                .returns(void.class)
                .addStatement("solver = new " + typeSpec.name + "(" + inputFileName + ");")
                .build();

        MethodSpec method2 = MethodSpec.methodBuilder("testPartTwo")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Test.class)
                .returns(void.class)
                .addStatement("solver = new " + typeSpec.name + "(" + inputFileName + ");")
                .build();

        TypeSpec classSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addField(fieldSpec)
                .addMethod(method1)
                .addMethod(method2)
                .build();

        writeSource(basePath, JavaFile.builder(packageName, classSpec).build());
        return classSpec;
    }

    private static void writeSource(String basePath, JavaFile app) {
        try {
            app.writeToPath(Path.of(basePath));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static void writeTemplateResources() {
        IntStream.rangeClosed(1, DAYS)
                .boxed()
                .forEach(day -> {
                    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of("src/main/resources", "day" + day + ".txt"), StandardOpenOption.CREATE)) {
                        bufferedWriter.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of("src/test/resources", "day" + day + "-test.txt"), StandardOpenOption.CREATE)) {
                        bufferedWriter.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
