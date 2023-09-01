package router;

public class DisplayedMove {
    private String method;
    private String methodValue;
    private String moveName;
    private String type;
    private String power;
    private String accuracy;
    private int pp;

    public DisplayedMove() {
        method = "Level";
        methodValue = "1";
        moveName = "Pound";
        type = "Normal";
        power = "40";
        accuracy = "100";
        pp = 35;
    }

    public DisplayedMove(String method, String methodValue, String moveName,
                            String type, String power, String accuracy, int pp) {
        this.method = method;

        if (this.method.equals("TM") || this.method.equals("HM")) {
            if (methodValue.length() == 1) this.methodValue = method + "0" + methodValue;
            else this.methodValue = method + methodValue;
        }
        else this.methodValue = methodValue;
        
        this.moveName = moveName;
        this.type = type;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodValue() {
        return methodValue;
    }

    public void setMethodValue(String methodValue) {
        this.methodValue = methodValue;
    }

    public String getMoveName() {
        return moveName;
    }

    public void setMoveName(String moveName) {
        this.moveName = moveName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }

    
}
