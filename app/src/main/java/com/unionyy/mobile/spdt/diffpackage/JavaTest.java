package com.unionyy.mobile.spdt.diffpackage;

import com.unionyy.mobile.spdt.annotation.SpdtActual;
import com.unionyy.mobile.spdt.annotation.XiaoMiFlavor;
import com.unionyy.mobile.spdt.api.DefaultFlavor;

public class JavaTest {

    interface UI {

        void a();
    }

    @SpdtActual(values = {DefaultFlavor.class, XiaoMiFlavor.class})
    public static class AUI implements UI {

        @Override
        public void a() {
            //DO NOTHING
        }
    }
}
