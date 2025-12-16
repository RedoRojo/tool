import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssertScanner {

    public static void main(String[] args) {
        File file = new File("/home/redo/Documents/tesis/Tool/src/test/java/DummyTest.java");


        try {
            CompilationUnit ast = StaticJavaParser.parse(file);

            AssertFinder finder = new AssertFinder();

            ast.accept(finder, null);

            List<SlicingCriterion> criteria = finder.getCriteria();

            System.out.println("Slicing criteria found: ");
            for (SlicingCriterion c: criteria) {
                System.out.println("Target: " + c);
            }

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
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
                addCriterion(methodCall, methodCall.getBegin().get().line);
            }
        }

        @Override
        public void visit(AssertStmt assertStmt, Void arg) {
            super.visit(assertStmt, arg);

            int line = assertStmt.getBegin().get().line;
            addCriterion(assertStmt, line);
        }

        private void addCriterion(Node node, int line) {
            Optional<MethodDeclaration> parentMethod = node.findAncestor(MethodDeclaration.class);
            Optional<ClassOrInterfaceDeclaration> parentClass = node.findAncestor(ClassOrInterfaceDeclaration.class);

            if (parentMethod.isPresent() && parentClass.isPresent()) {
                String methodName = parentMethod.get().getNameAsString();
                String className = parentClass.get().getNameAsString();

                criteria.add(new SlicingCriterion(className, methodName, line));
            }
        }
    }
}