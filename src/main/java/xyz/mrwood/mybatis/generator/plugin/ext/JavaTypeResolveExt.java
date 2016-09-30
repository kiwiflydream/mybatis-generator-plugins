/**
 * Copyright (c) 2016, 791650277@qq.com(Mr.kiwi) All Rights Reserved.
 */
package xyz.mrwood.mybatis.generator.plugin.ext;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * 项目：mybatis-generator-plugin
 * 包名：xyz.mrwood.mybatis.generator.plugin.ext
 * 功能：
 * 时间：2016-09-30 16:22
 * 作者：Mr.Kiwi
 */
public class JavaTypeResolveExt extends JavaTypeResolverDefaultImpl {

    public JavaTypeResolveExt() {

        super();
        super.typeMap.put(Types.BIT, new JdbcTypeInformation("BIT", //$NON-NLS-1$
            new FullyQualifiedJavaType(Integer.class.getName())));
    }
}
