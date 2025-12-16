public class SlicingCriterion {
    private final String fullClassPath;
    private final String simpleClassName;
    private final String methodName;
    private final Integer lineNumber;

    public SlicingCriterion(String fullClassPath, String className, String methodName, Integer lineNumber) {
        this.fullClassPath = fullClassPath;
        this.simpleClassName = className;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
    }

    public String getFullClassPath() {
        return fullClassPath;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    @Override
    public String toString() {
        return fullClassPath + "." + methodName + ":" + lineNumber;
    }

    public String toCSV() {
        return fullClassPath + "," + simpleClassName + ","  + methodName + "," + lineNumber;
    }
}
