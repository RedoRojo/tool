import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.

public class AssertScanner {

    public static void main(String[] args) {
        File file = new File("/home/redo/Documents/tesis/Tool/src/test/java/DummyTest.java");

        try {
            CompilationUnit ast = StaticJavaParser.parse(file);

            System.out.println("Scanning file: " + file.getName());
            ast.accept(new AssertFinder(), null);

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    private static class AssertFinder extends VoidVisitorAdapter<Void> {

        @Override
        public void visit (MethodCallExpr methodCall, Void arg) {
            super.visit(methodCall, arg);

            String methodName = methodCall.getNameAsString();

            if (methodName.startsWith("assert")) {
                int line = methodCall.getBegin().get().line;
                System.out.println("[JUnit Assert] found in line " + line + ": " + methodCall);
            }
        }

        @Override
        public void visit(AssertStmt assertStmt, Void arg) {
            super.visit(assertStmt, arg);

            int line = assertStmt.getBegin().get().line;
            System.out.println("[Java Native Assert] found in line " + line + ": " + assertStmt);
        }
    }
}