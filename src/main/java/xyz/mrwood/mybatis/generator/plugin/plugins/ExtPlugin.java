/**
 * Copyright (c) 2016, 791650277@qq.com(Mr.kiwi) All Rights Reserved.
 */
package xyz.mrwood.mybatis.generator.plugin.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目：mybatis-generator-plugin
 * 包名：xyz.mrwood.mybatis.generator.plugin.plugins
 * 功能：
 * 时间：2016-07-03 18:24
 * 作者：Mr.Kiwi
 */
public class ExtPlugin extends PluginAdapter {

    public static final String SUFFIX = "Ext";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {


        List<GeneratedJavaFile> answer = new ArrayList<>();

        if (!isExistForExtModel(introspectedTable)) {
            answer.add(createExtModel(introspectedTable));
        }

        if (!isExistForExtMapper(introspectedTable)) {
            answer.add(createExtMapper(introspectedTable));
        }
        return answer;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        // 获得mapper的全限定名
        String myBatis3JavaMapperType = introspectedTable.getMyBatis3JavaMapperType();

        // 修改namespace为扩展类的
        XmlElement rootElement = document.getRootElement();
        rootElement.getAttributes().clear();
        rootElement.addAttribute(new Attribute("namespace", myBatis3JavaMapperType + SUFFIX));

        return true;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {

        List<GeneratedXmlFile> answer = new ArrayList<>();

        if (isExistForExtMapperXml(introspectedTable)) {

            return answer;
        }

        Document document =
            new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        // 获得mapper的全限定名
        String myBatis3JavaMapperType = introspectedTable.getMyBatis3JavaMapperType();

        XmlElement root = new XmlElement("mapper"); //$NON-NLS-1$
        String value = myBatis3JavaMapperType + SUFFIX;
        root.addAttribute(new Attribute("namespace", value));
        root.addElement(new TextElement(""));
        document.setRootElement(root);


        String targetPackage = context.getSqlMapGeneratorConfiguration().getTargetPackage();
        String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();

        String myBatis3XmlMapperFileName = introspectedTable.getMyBatis3XmlMapperFileName();
        String[] fileNameArr = myBatis3XmlMapperFileName.split("\\.");

        GeneratedXmlFile gxf = new GeneratedXmlFile(document,
            fileNameArr[0] + SUFFIX + "." + fileNameArr[1], //$NON-NLS-1$ //$NON-NLS-2$
            targetPackage, //$NON-NLS-1$
            targetProject, //$NON-NLS-1$
            false, context.getXmlFormatter());


        answer.add(gxf);

        return answer;
    }

    /**
     * 创建额外MODEL类
     *
     * @param introspectedTable
     * @return
     */
    public GeneratedJavaFile createExtModel(IntrospectedTable introspectedTable) {

        FullyQualifiedJavaType type =
            new FullyQualifiedJavaType(introspectedTable.getBaseRecordType() + SUFFIX);
        TopLevelClass top = new TopLevelClass(type);
        top.setSuperClass(introspectedTable.getBaseRecordType());
        top.setVisibility(JavaVisibility.PUBLIC);

        GeneratedJavaFile javaFile =
            new GeneratedJavaFile(top, context.getJavaModelGeneratorConfiguration().getTargetProject(), new DefaultJavaFormatter());

        return javaFile;
    }

    /**
     * 创建额外mapper接口
     *
     * @param introspectedTable
     * @return
     */
    public GeneratedJavaFile createExtMapper(IntrospectedTable introspectedTable) {

        // 从配置文件中获得targetProject属性
        String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();

        // 获得mapper的全限定名
        String myBatis3JavaMapperType = introspectedTable.getMyBatis3JavaMapperType();

        // 拼装接口
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(myBatis3JavaMapperType + SUFFIX);
        Interface inter = new Interface(type);
        inter.addSuperInterface(new FullyQualifiedJavaType(myBatis3JavaMapperType));
        inter.setVisibility(JavaVisibility.PUBLIC);

        GeneratedJavaFile javaFile =
            new GeneratedJavaFile(inter, targetProject, new DefaultJavaFormatter());

        return javaFile;
    }

    /**
     * 判断mapper扩展是否存在
     */
    public boolean isExistForExtMapper(IntrospectedTable introspectedTable) {
        // 从配置文件中获得targetProject属性
        String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();
        // 获得mapper的全限定名
        String myBatis3JavaMapperType = introspectedTable.getMyBatis3JavaMapperType();
        // Mapper扩展类的文件名
        String fileName =
            targetProject + "/" + myBatis3JavaMapperType.replace('.', '/') + SUFFIX + ".java";
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 判断model扩展是否存在
     */
    public boolean isExistForExtModel(IntrospectedTable introspectedTable) {
        // 从配置文件中获得targetProject属性
        String targetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();
        // 获得mapper的全限定名
        String myBatis3JavaMapperType = introspectedTable.getBaseRecordType();
        // Mapper扩展类的文件名
        String fileName =
            targetProject + "/" + myBatis3JavaMapperType.replace('.', '/') + SUFFIX + ".java";
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 判断mapper.xml扩展是否存在
     */
    public boolean isExistForExtMapperXml(IntrospectedTable introspectedTable) {
        // 从配置文件中获得targetProject属性
        String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();
        String targetPackage = context.getSqlMapGeneratorConfiguration().getTargetPackage();

        String myBatis3XmlMapperFileName = introspectedTable.getMyBatis3XmlMapperFileName();
        String[] fileNameArr = myBatis3XmlMapperFileName.split("\\.");

        // Mapper扩展类的文件名
        String fileName = targetProject + "/" + targetPackage + "/" + fileNameArr[0] + SUFFIX + "." + fileNameArr[1];

        System.err.println(fileName);

        File file = new File(fileName);
        return file.exists();
    }
}
