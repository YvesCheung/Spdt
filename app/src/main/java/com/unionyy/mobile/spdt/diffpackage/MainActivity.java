package com.unionyy.mobile.spdt.diffpackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.unionyy.mobile.spdt.Spdt;
import com.unionyy.mobile.spdt.annotation.SpdtInject;

@SuppressWarnings("WeakerAccess")
public class MainActivity {

    @SpdtInject
    B theB;

    @SpdtInject
    AppidGetter $ss;

    @SuppressLint("ToastUsage")
    public void test(Context context) {
        Spdt.inject(this);

        Toast.makeText(context, /*theB.print() +*/ $ss.getAppid(), Toast.LENGTH_LONG).show();
    }
}
