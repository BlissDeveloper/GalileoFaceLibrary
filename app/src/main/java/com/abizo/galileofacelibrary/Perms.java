package com.abizo.galileofacelibrary;

import java.util.ArrayList;

public class Perms {
    boolean isPermitted;
    ArrayList<String> perms;

    public Perms() {

    }

    public boolean isPermitted() {
        return isPermitted;
    }

    public void setPermitted(boolean permitted) {
        isPermitted = permitted;
    }

    public ArrayList<String> getPerms() {
        return perms;
    }

    public void setPerms(ArrayList<String> perms) {
        this.perms = perms;
    }
}
