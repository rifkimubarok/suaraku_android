package net.soradigital.suaraku.classes;

public class Account {
    private String ACC_INDEX;
    private String ACC_DATEREG;
    private String ACC_DATELAST;
    private String PAR_NOREG;
    private String ACC_NOREG;
    private String PEM_CODE;
    private String ACC_KOWIL;
    private String ACC_ALIAS;
    private String ACC_PASSWORD;
    private String ACC_PHONE;
    private String ACC_KEY;
    private String ACC_EMAIL;
    private int ACC_STATUS;
    private String ACC_REFF_ID;
    private String ACC_PARENT_REFF;

    public static final int NOT_ACTIVE = -1;
    public static final int ACTIVE = 1;
    public static final int REGISTER = 0;

    public String getACC_INDEX() {
        return ACC_INDEX;
    }

    public void setACC_INDEX(String ACC_INDEX) {
        this.ACC_INDEX = ACC_INDEX;
    }

    public String getACC_DATEREG() {
        return ACC_DATEREG;
    }

    public void setACC_DATEREG(String ACC_DATEREG) {
        this.ACC_DATEREG = ACC_DATEREG;
    }

    public String getACC_DATELAST() {
        return ACC_DATELAST;
    }

    public void setACC_DATELAST(String ACC_DATELAST) {
        this.ACC_DATELAST = ACC_DATELAST;
    }

    public String getPAR_NOREG() {
        return PAR_NOREG;
    }

    public void setPAR_NOREG(String PAR_NOREG) {
        this.PAR_NOREG = PAR_NOREG;
    }

    public String getACC_NOREG() {
        return ACC_NOREG;
    }

    public void setACC_NOREG(String ACC_NOREG) {
        this.ACC_NOREG = ACC_NOREG;
    }

    public String getPEM_CODE() {
        return PEM_CODE;
    }

    public void setPEM_CODE(String PEM_CODE) {
        this.PEM_CODE = PEM_CODE;
    }

    public String getACC_KOWIL() {
        return ACC_KOWIL;
    }

    public void setACC_KOWIL(String ACC_KOWIL) {
        this.ACC_KOWIL = ACC_KOWIL;
    }

    public String getACC_ALIAS() {
        return ACC_ALIAS;
    }

    public void setACC_ALIAS(String ACC_ALIAS) {
        this.ACC_ALIAS = ACC_ALIAS;
    }

    public String getACC_PASSWORD() {
        return ACC_PASSWORD;
    }

    public void setACC_PASSWORD(String ACC_PASSWORD) {
        this.ACC_PASSWORD = ACC_PASSWORD;
    }

    public String getACC_PHONE() {
        return ACC_PHONE;
    }

    public void setACC_PHONE(String ACC_PHONE) {
        this.ACC_PHONE = ACC_PHONE;
    }

    public String getACC_KEY() {
        return ACC_KEY;
    }

    public void setACC_KEY(String ACC_KEY) {
        this.ACC_KEY = ACC_KEY;
    }

    public String getACC_EMAIL() {
        return ACC_EMAIL;
    }

    public void setACC_EMAIL(String ACC_EMAIL) {
        this.ACC_EMAIL = ACC_EMAIL;
    }

    public int getACC_STATUS() {
        return ACC_STATUS;
    }

    public void setACC_STATUS(int ACC_STATUS) {
        this.ACC_STATUS = ACC_STATUS;
    }

    public String getACC_REFF_ID() {
        return ACC_REFF_ID;
    }

    public void setACC_REFF_ID(String ACC_REFF_ID) {
        this.ACC_REFF_ID = ACC_REFF_ID;
    }

    public String getACC_PARENT_REFF() {
        return ACC_PARENT_REFF;
    }

    public void setACC_PARENT_REFF(String ACC_PARENT_REFF) {
        this.ACC_PARENT_REFF = ACC_PARENT_REFF;
    }
}
