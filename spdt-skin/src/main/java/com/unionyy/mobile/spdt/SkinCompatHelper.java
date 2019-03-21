package com.unionyy.mobile.spdt;

public abstract class SkinCompatHelper {

    public static final int INVALID_ID = 0;

    final static public int checkResourceId(int resId) {
        return resId < 0 ? INVALID_ID : resId;
    }

    abstract public void applySkin();
}
