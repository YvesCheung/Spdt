package com.unionyy.mobile.spdt.diffpackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.unionyy.mobile.spdt.Spdt;

@SuppressWarnings("WeakerAccess")
public class MainActivity {

    B theB = Spdt.of(B.class);

    AppidGetter $ss = Spdt.of(AppidGetter.class);

    @SuppressLint("ToastUsage")
    public void test(Context context) {
        Toast.makeText(context, theB.print() + $ss.getAppid(), Toast.LENGTH_LONG).show();
    }
}
