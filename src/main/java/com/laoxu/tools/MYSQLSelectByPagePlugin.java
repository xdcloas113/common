package com.laoxu.tools;


import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * sqlServer
 */
public class MYSQLSelectByPagePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private void addField(String fieldName, FullyQualifiedJavaType fieldType,
                          TopLevelClass topLevelClass) {
        Field tmpField = new Field(fieldName, fieldType);
        tmpField.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(tmpField);

        Method setMethod = new Method();
        setMethod.setName("set" + fieldName.toUpperCase().substring(0, 1)
                + fieldName.substring(1));
        Parameter param = new Parameter(fieldType, fieldName);
        setMethod.addParameter(param);
        setMethod.setVisibility(JavaVisibility.PUBLIC);
        setMethod.addBodyLine("this." + fieldName + "=" + fieldName + ";");

        topLevelClass.addMethod(setMethod);

        Method getMethod = new Method();
        getMethod.setName("get" + fieldName.toUpperCase().substring(0, 1)
                + fieldName.substring(1));

        getMethod.setReturnType(fieldType);
        getMethod.setVisibility(JavaVisibility.PUBLIC);
        getMethod.addBodyLine("return this." + fieldName + ";");

        topLevelClass.addMethod(getMethod);

    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType intType = FullyQualifiedJavaType
                .getIntInstance();
        addField("pageIndex", intType, topLevelClass);
        addField("pageSize", intType, topLevelClass);

        Method getSkipCountMethod = new Method();
        getSkipCountMethod.setName("getSkipRecordCount");

        getSkipCountMethod.setReturnType(intType);
        getSkipCountMethod.setVisibility(JavaVisibility.PUBLIC);
        getSkipCountMethod
                .addBodyLine("return (this.pageIndex-1)*this.pageSize;");

        topLevelClass.addMethod(getSkipCountMethod);

        Method getEndRecordIndexMethod = new Method();
        getEndRecordIndexMethod.setName("getEndRecordCount");

        getEndRecordIndexMethod.setReturnType(intType);
        getEndRecordIndexMethod.setVisibility(JavaVisibility.PUBLIC);
        getEndRecordIndexMethod
                .addBodyLine("return this.pageIndex*this.pageSize;");

        topLevelClass.addMethod(getEndRecordIndexMethod);


        Method newConstructorMethod = new Method();
        newConstructorMethod.setConstructor(true);
        newConstructorMethod.addParameter(new Parameter(intType, "pageSize"));
        newConstructorMethod.addParameter(new Parameter(intType, "pageIndex"));
        newConstructorMethod.addBodyLine("this();");
        newConstructorMethod.addBodyLine("this.pageSize=pageSize;");
        newConstructorMethod.addBodyLine("this.pageIndex=pageIndex;");
        newConstructorMethod.setVisibility(JavaVisibility.PUBLIC);
        newConstructorMethod.setName(topLevelClass.getType().getShortName());

        topLevelClass.addMethod(newConstructorMethod);
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getExampleType());
        importedTypes.add(type);
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType
                .getNewListInstance();
        FullyQualifiedJavaType listType;
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            listType = new FullyQualifiedJavaType(introspectedTable
                    .getBaseRecordType());
        } else if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            listType = new FullyQualifiedJavaType(introspectedTable
                    .getPrimaryKeyType());
        } else {
            throw new RuntimeException(getString("RuntimeError.12")); //$NON-NLS-1$
        }

        importedTypes.add(listType);
        returnType.addTypeArgument(listType);
        method.setReturnType(returnType);

        method.setName("getPage");
        method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document,
                                           IntrospectedTable introspectedTable) {

        XmlElement parentElement = document.getRootElement();

        XmlElement newResultMapElement = new XmlElement("resultMap");
        newResultMapElement
                .addAttribute(new Attribute("id", "selectPageResult"));
        newResultMapElement.addAttribute(new Attribute("extends",
                "BaseResultMap"));

        String returnType;
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            returnType = introspectedTable.getBaseRecordType();
        } else {
            returnType = introspectedTable.getPrimaryKeyType();
        }

        newResultMapElement.addAttribute(new Attribute("type",
                returnType));
//        newResultMapElement
//                .addElement(new TextElement(
//                        "<!--\r\n     WARNING - @mbg.generated\r\n"
//                                + "     This element is automatically generated by MyBatis Generator, do not modify.\r\n"
//                                + " \r\n     -->"));

        parentElement.addElement(newResultMapElement);


        //以下代码用于生成支持分页的sql片段
        /**
         *注意千万不要随意删除代码中的空格 ！
         *do not remove spaces
         */

        XmlElement outter_where_sql=new XmlElement("sql");
        outter_where_sql.addAttribute(new Attribute("id", "select_by_page_outter_where_sql"));
//        outter_where_sql
//                .addElement(new TextElement(
//                        "<!--\r\n     WARNING - @mbg.generated\r\n"
//                                + "     This element is automatically generated by MyBatis Generator, do not modify.\r\n"
//                                + " \r\n     -->"));

        StringBuilder sb = new StringBuilder();
        sb.append("       <if test=\"oredCriteria[0].criteria.size>0\">");
//        sb.append("          <if test=\"_parameter != null\" > <include refid=\"Example_Where_Clause\"/>          and  </if>");
        sb.append("          <if test=\"_parameter != null\" > <include refid=\"Example_Where_Clause\"/>            </if>");
        sb.append("       </if>");
        sb.append("       <if test=\"oredCriteria[0].criteria.size==0\"> ");
        sb.append("         where ");
        sb.append("       </if> ");
        outter_where_sql.addElement(new TextElement(sb.toString()));
        parentElement.addElement(outter_where_sql);

        XmlElement inner_where_sql=new XmlElement("sql");
        inner_where_sql.addAttribute(new Attribute("id", "select_by_page_inner_where_and_orderby_sql"));
//        inner_where_sql
//                .addElement(new TextElement(
//                        "<!--\r\n     WARNING - @mbg.generated\r\n"
//                                + "     This element is automatically generated by MyBatis Generator, do not modify.\r\n"
//                                + " \r\n     -->"));
        sb.setLength(0);
        sb.append("       <if test=\"oredCriteria[0].criteria.size>0\">");
        sb.append("          <if test=\"_parameter != null\" > <include refid=\"Example_Where_Clause\"/> </if>");
        sb.append("       </if>");
        sb.append("        <if test=\"orderByClause != null\">");
        sb.append("         order by ${orderByClause} ");
        sb.append("       </if> ");
        inner_where_sql.addElement(new TextElement(sb.toString()));
        parentElement.addElement(inner_where_sql);


        XmlElement outter_orderby_sql=new XmlElement("sql");
        outter_orderby_sql.addAttribute(new Attribute("id", "select_by_page_outter_orderby_sql"));
//        outter_orderby_sql
//                .addElement(new TextElement(
//                        "<!--\r\n     WARNING - @mbg.generated\r\n"
//                                + "     This element is automatically generated by MyBatis Generator, do not modify.\r\n"
//                                + " \r\n     -->"));
        sb.setLength(0);
        sb.append("        <if test=\"orderByClause != null\">");
        sb.append("         order by ${orderByClause} ");
        sb.append("       </if> ");
        outter_orderby_sql.addElement(new TextElement(sb.toString()));
        parentElement.addElement(outter_orderby_sql);

        //以上代码用于生成支持分页的sql片段

        String fqjt = introspectedTable.getExampleType();

        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", //$NON-NLS-1$
                "getPage"));
        answer.addAttribute(new Attribute(
                "resultMap","selectPageResult")); //$NON-NLS-1$
        answer.addAttribute(new Attribute("parameterType", fqjt)); //$NON-NLS-1$

        answer.addElement(new TextElement("<!-- 分页查询 -->"));

        sb.setLength(0);
        sb.append("     select a.*  ");
//        sb.append(introspectedTable
//                .getAliasedFullyQualifiedTableNameAtRuntime());
//        sb.append("    a ");
//        List<IntrospectedColumn> pks=introspectedTable.getPrimaryKeyColumns();

        sb.append("      from "+introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()+"  a \r\n");
        sb.append("      <if test=\"oredCriteria[0].criteria.size > 0  \"> \r\n");
        sb.append("          <include refid=\"select_by_page_outter_where_sql\"/> \r\n");
        sb.append("      </if> \r\n");
        sb.append("      <if test=\"oredCriteria[0].criteria.size==0\"> \r\n ");
        sb.append("          <include refid=\"select_by_page_outter_orderby_sql\"/> \r\n");
        sb.append("      </if> \r\n");
        sb.append("         LIMIT  ${(pageIndex-1)*pageSize},${pageSize}");

        answer.addElement(new TextElement(sb.toString()));

        parentElement.addElement(answer);

        return true;
    }

}
