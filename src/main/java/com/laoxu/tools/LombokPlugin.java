package com.laoxu.tools;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 功能描述：mybatis 逆向实体生成@Data注解
 * 创建人 ：xdc
 */
public class LombokPlugin extends PluginAdapter {
    private FullyQualifiedJavaType dataAnnotation = new FullyQualifiedJavaType("lombok.Data");

    public LombokPlugin() {
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.addDataAnnotation(topLevelClass);
        return true;
    }

    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.addDataAnnotation(topLevelClass);
        return true;
    }

    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.addDataAnnotation(topLevelClass);
        return true;
    }

    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    protected void addDataAnnotation(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType(this.dataAnnotation);
        topLevelClass.addAnnotation("@Data");
    }
}