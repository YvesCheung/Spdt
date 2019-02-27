package com.unionyy.mobile.spdt.compiler;

import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class Env {

    public Filer filer; //文件相关的辅助类
    public Elements elements; //元素相关的辅助类
    public Logger logger; //日志相关的辅助类
    public Types types;
    public Map<String, String> options;

    public String packageName;
}
