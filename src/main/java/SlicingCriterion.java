public class SlicingCriterion {
    private String className;
    private String methodName;
    private Integer lineNumber;

    public SlicingCriterion(String className, String methodName, Integer lineNumber) {
        this.className = className;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return className + "." + methodName + ":" + lineNumber;
    }
}
