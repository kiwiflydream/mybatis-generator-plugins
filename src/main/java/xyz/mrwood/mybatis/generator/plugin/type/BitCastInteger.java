/**
 * Copyright (c) 2017, 791650277@qq.com(Mr.kiwi) All Rights Reserved.
 */
package xyz.mrwood.mybatis.generator.plugin.type;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * 项目：mybatis-generator-plugins
 * 包名：xyz.mrwood.mybatis.generator.plugin.type
 * 功能：bit 转 integer
 * 时间：2017-02-28 16:06
 * 作者：Mr.Kiwi
 */
public class BitCastInteger extends JavaTypeResolverDefaultImpl {

    public BitCastInteger() {

        super();
        super.typeMap.put(Types.BIT, new JdbcTypeInformation("INTEGER", //$NON-NLS-1$
            new FullyQualifiedJavaType(Integer.class.getName())));
    }

}
