import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssertScanner {

    private static final String OUTPUT_FILE = "slicing_criteria.csv";

    public static void main(String[] args) {
        File file = new File("/home/redo/Documents/tesis/Tool/src/test/java/com/DummyTest.java");

        try {

            System.out.println("Analyzing: " + file.getAbsolutePath());
            CompilationUnit ast = StaticJavaParser.parse(file);

            AssertFinder finder = new AssertFinder();

            ast.accept(finder, null);

            List<SlicingCriterion> criteria = finder.getCriteria();

            System.out.println("Slicing criteria found: ");
            for (SlicingCriterion c: criteria) {
                System.out.println("Target: " + c);
            }

            saveCriteriaToFile(criteria);

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    private static void saveCriteriaToFile(List<SlicingCriterion> criteria) {
        try (FileWriter writer = new FileWriter(OUTPUT_FILE)) {
            writer.write("Class Path,Test Class Name,Test Method Name,Test Assert Line\n");

            for (SlicingCriterion c: criteria) {
                writer.write(c.toCSV() + "\n");
            }

            System.out.println("Success! Plan saved to: " + OUTPUT_FILE);
            System.out.println("Total criteria found: " + criteria.size());
        } catch (IOException e) {
            System.out.println("Error writing output file: " + e.getMessage());
        }
    }

    private static class AssertFinder extends VoidVisitorAdapter<Void> {

        private final List<SlicingCriterion> criteria = new ArrayList<>();

        public List<SlicingCriterion> getCriteria() {
            return criteria;
        }

        @Override
        public void visit (MethodCallExpr methodCall, Void arg) {
            super.visit(methodCall, arg);

            if(methodCall.getNameAsString().startsWith("assert")) {
                extractInfo(methodCall, methodCall.getBegin().get().line);
            }
        }

        @Override
        public void visit(AssertStmt assertStmt, Void arg) {
            super.visit(assertStmt, arg);

            int line = assertStmt.getBegin().get().line;
            extractInfo(assertStmt, line);
        }

        private void extractInfo(Node node, int line) {
            Optional<MethodDeclaration> parentMethod = node.findAncestor(MethodDeclaration.class);
            Optional<ClassOrInterfaceDeclaration> parentClass = node.findAncestor(ClassOrInterfaceDeclaration.class);
            Optional<CompilationUnit> cu = node.findAncestor(CompilationUnit.class);


            if (parentMethod.isPresent() && parentClass.isPresent() && cu.isPresent()) {
                String methodName = parentMethod.get().getNameAsString();
                String simpleClassName = parentClass.get().getNameAsString();
                String packageName = cu.get().getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse("");

                String fullPath;
                if (packageName.isEmpty()) {
                    fullPath = simpleClassName;
                } else {
                    fullPath = packageName + "." + simpleClassName;
                }
                criteria.add(new SlicingCriterion(fullPath, simpleClassName, methodName, line));
            }
        }
    }
}