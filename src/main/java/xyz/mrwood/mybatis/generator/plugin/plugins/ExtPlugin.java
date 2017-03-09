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
import org.mybatis.generator.config.Context;
import xyz.mrwood.mybatis.generator.plugin.common.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 项目：mybatis-generator-plugin
 * 包名：xyz.mrwood.mybatis.generator.plugin.plugins
 * 功能：类型转换
 * 时间：2016-07-03 18:24
 * 作者：Mr.Kiwi
 */
public class ExtPlugin extends PluginAdapter {

    /**
     * 后缀
     */
    private static String suffix;
    /**
     * mapper 名称
     */
    private static String mapperName;
    /**
     * mapper 扩展类名称
     */
    private static String mapperExtName;
    /**
     * mapper xml名称
     */
    private static String mapperXmlName;
    /**
     * mapper xml 扩展xml名称
     */
    private static String mapperExtXmlName;
    /**
     * model 名称
     */
    private static String modelName;
    /**
     * model ext 名称
     */
    private static String modelExtName;

    Logger logger = Logger.getLogger("ExtPlugin");

    /**
     * 验证是否通过
     *
     * @param warnings
     * @return
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }



    /**
     * 初始化对像，这里可以修改生成的类
     *
     * @param introspectedTable
     */
    @Override
    public void initialized(IntrospectedTable introspectedTable) {

        // 设置后缀
        setSuffix();

        // 设置model 与 model ext名称
        modelName = getModel(context, introspectedTable.getBaseRecordType());
        modelExtName = modelName + suffix;

        // 设置mapper 与 mapper ext 名称
        mapperName = getMapperName(context, introspectedTable.getMyBatis3JavaMapperType());
        mapperExtName = mapperName + suffix;

        // 设置mapper xml 与 mapper ext xml 名称
        mapperXmlName = getMapperXmlName(context, introspectedTable.getMyBatis3XmlMapperFileName());
        mapperExtXmlName = mapperXmlName.split("\\.")[0] + suffix + ".xml";

        // 修改model类的名称
        introspectedTable.setBaseRecordType(modelName);
        // 修改mapper类的名称
        introspectedTable.setMyBatis3JavaMapperType(mapperName);
        // 修改mapper xml类的名称
        introspectedTable.setMyBatis3XmlMapperFileName(mapperXmlName);

        logger.info("初始化："
            + "\n modleName = " + modelName
            + "\n modelExtName = " + modelExtName
            + "\n mapperName = " + mapperName
            + "\n mapperExtName = " + mapperExtName
            + "\n mapperXmlName = " + mapperXmlName
            + "\n mapperExtXmlName = " + mapperExtXmlName
        );

    }

    /**
     * 生成额外的javaFiles列表
     *
     * @param introspectedTable
     * @return
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {


        List<GeneratedJavaFile> answer = new ArrayList<>();

        /**
         * 扩展modle类是否存在
         */
        if (!isExistForExtModel()) {

            answer.add(createExtModel());

            logger.info(modelExtName + " -> 生成");
        }else {

            logger.info(modelExtName + " -> 已存在，不生成");
        }

        /**
         * 扩展的mapper类是否存在
         */
        if (!isExistForExtMapper()) {

            answer.add(createExtMapper());
            logger.info(mapperExtName + " -> 生成");

        }else {

            logger.info(mapperExtName + " -> 已存在，不生成");
        }
        return answer;
    }

    /**
     * @param document
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        // 修改namespace为扩展类的
        XmlElement rootElement = document.getRootElement();
        rootElement.getAttributes().clear();
        rootElement.addAttribute(new Attribute("namespace", mapperExtName));

        return true;
    }

    /**
     * 生成额外的xml文件
     *
     * @param introspectedTable
     * @return
     */
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {

        List<GeneratedXmlFile> answer = new ArrayList<>();

        // 判断扩展的xml是否已经存在
        if (isExistForExtMapperXml()) {

            logger.info(mapperExtXmlName + " -> 已存在，不生成");
            return answer;
        }else {

            logger.info(mapperExtXmlName + " -> 生成");
        }

        // 创建文档对象
        Document document =
            new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        // 创建根节点
        XmlElement root = new XmlElement("mapper");
        root.addAttribute(new Attribute("namespace", mapperExtName));
        root.addElement(new TextElement(""));

        document.setRootElement(root);


        String targetPackage = context.getSqlMapGeneratorConfiguration().getTargetPackage();
        String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();

        // 生成ext xml文件
        GeneratedXmlFile gxf =
            new GeneratedXmlFile(document, mapperExtXmlName, targetPackage, targetProject, false, context.getXmlFormatter());

        answer.add(gxf);

        return answer;
    }

    /**
     * 创建额外MODEL类
     *
     * @return
     */
    public GeneratedJavaFile createExtModel() {

        // 获得model的全限定名
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(modelExtName);
        TopLevelClass top = new TopLevelClass(type);
        top.setSuperClass(getModel(context, modelName));
        top.setVisibility(JavaVisibility.PUBLIC);

        GeneratedJavaFile javaFile =
            new GeneratedJavaFile(top, context.getJavaModelGeneratorConfiguration().getTargetProject(), new DefaultJavaFormatter());

        return javaFile;
    }

    /**
     * 创建额外mapper接口
     *
     * @return
     */
    public GeneratedJavaFile createExtMapper() {

        // 从配置文件中获得targetProject属性
        String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();

        // 拼装接口
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(mapperExtName);
        Interface inter = new Interface(type);
        inter.addSuperInterface(new FullyQualifiedJavaType(mapperName));
        inter.setVisibility(JavaVisibility.PUBLIC);

        // 添加注解 与 注解引入
        String anno = context.getProperty(Constants.KEY_EXT_CLASS_ADD_ANNO);
        String annoClass = context.getProperty(Constants.KEY_EXT_CLASS_ADD_ANNO_CLASS);

        System.out.println("anno = " + anno);
        System.out.println("annoClass = " + annoClass);

        if (anno != null) {

            inter.addAnnotation(anno);
        }

        if (annoClass != null) {

            inter.addImportedType(new FullyQualifiedJavaType(annoClass));
        }


        GeneratedJavaFile javaFile =
            new GeneratedJavaFile(inter, targetProject, new DefaultJavaFormatter());

        return javaFile;
    }

    /**
     * 判断mapper扩展是否存在
     */
    public boolean isExistForExtMapper() {
        // 从配置文件中获得targetProject属性
        String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();

        // Mapper扩展类的文件名
        String fileName = targetProject + "/" + mapperExtName.replace('.', '/') + ".java";

        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 判断model扩展是否存在
     */
    public boolean isExistForExtModel() {
        // 从配置文件中获得targetProject属性
        String targetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();
        // Mapper扩展类的文件名
        String fileName = targetProject + "/" + modelExtName.replace('.', '/') + ".java";
        return new File(fileName).exists();
    }

    /**
     * 判断mapper.xml扩展是否存在
     */
    public boolean isExistForExtMapperXml() {
        // 从配置文件中获得targetProject属性
        String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();
        String targetPackage = context.getSqlMapGeneratorConfiguration().getTargetPackage();

        // Mapper扩展类的文件名
        String fileName = targetProject + "/" + targetPackage + "/" + mapperExtXmlName;

        return new File(fileName).exists();
    }


    /**
     * 获得mapper文件名
     *
     * @param context
     * @param mapperFileName
     * @return
     */
    public String getMapperName(Context context, String mapperFileName) {

        // 移除类前缀
        String classRemovePrefix = context.getProperty(Constants.KEY_CLASS_REMOVE_PREFIX);
        if (classRemovePrefix != null && !classRemovePrefix.equals("")) {

            mapperFileName = mapperFileName.replace(classRemovePrefix, "");
        }

        return mapperFileName;
    }

    /**
     * 获得mapper xml文件名
     *
     * @param context
     * @param mapperXmlName
     * @return
     */
    public String getMapperXmlName(Context context, String mapperXmlName) {

        // 移除类前缀
        String classRemovePrefix = context.getProperty(Constants.KEY_CLASS_REMOVE_PREFIX);
        if (classRemovePrefix != null && !classRemovePrefix.equals("")) {

            mapperXmlName = mapperXmlName.replace(classRemovePrefix, "");
        }

        return mapperXmlName;
    }

    /**
     * 获得model名
     *
     * @param context
     * @param modelName
     * @return
     */
    public String getModel(Context context, String modelName) {

        // 移除类前缀
        String classRemovePrefix = context.getProperty(Constants.KEY_CLASS_REMOVE_PREFIX);
        if (classRemovePrefix != null && !classRemovePrefix.equals("")) {

            modelName = modelName.replace(classRemovePrefix, "");
        }

        return modelName;
    }


    /**
     * 设置后缀
     */
    public void setSuffix() {

        suffix = "Ext";
        String extSuffix = context.getProperty(Constants.KEY_EXT_CLASS_SUFFIX);
        if (extSuffix != null && !extSuffix.equals("")) {

            suffix = extSuffix;
        }
    }
}
