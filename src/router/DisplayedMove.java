package router;

public class DisplayedMove {
    private String method;
    private String methodValue;
    private String moveName;
    private String type;
    private String power;
    private String accuracy;
    private int pp;
    private String pokemonID;

    public DisplayedMove() {
        method = "Level";
        methodValue = "1";
        moveName = "Pound";
        type = "Normal";
        power = "40";
        accuracy = "100";
        pp = 35;
        pokemonID = "1";
    }

    public DisplayedMove(String method, String methodValue, String moveName,
                            String type, String power, String accuracy, int pp, String ID) {
        switch(method) {
            case "1":
                this.method = "TM";
                break;
            case "2":
                this.method = "HM";
                break;
            case "3":
                this.method = "Tutor";
                break;
            case "4":
                this.method = "Transfer";
                break;
            case "5":
                this.method = "Event";
                break;
            case "0":
            default:
                this.method = "Level";
                break;
        }

        if (this.method.equals("TM") || this.method.equals("HM")) {
            if (methodValue.length() == 1) this.methodValue = this.method + "0" + methodValue;
            else this.methodValue = this.method + methodValue;
        }
        else this.methodValue = methodValue;
        
        this.moveName = moveName;
        this.type = type;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.pokemonID = ID;
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

    public String getPokemonID() {
        return this.pokemonID;
    }

    public void setPokemonID(String pokemonID) {
        this.pokemonID = pokemonID;
    }
    
}
