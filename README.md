# mybatis-generator-plugins
mybatis generator 自定义插件

> LombokPlugin 增加对lombok的支持
> ExtPlugin model,dao,xml 都会生成一个对应的 xxxExt.java OR xxxExt.xml，它们与原生文件是继承关系，这些扩展文件只会生成一次，原生文件每运行一
次就生成一次，这样的好处是，你把sql或者配置写到扩展文件里面，以后数据表变了代码不用改


- pom.xml
``` xml
	<!--mybatis生成代码插件-->
			<plugin>
				<groupId>org.mybatis.generator</groupId>
				<artifactId>mybatis-generator-maven-plugin</artifactId>
				<version>1.3.2</version>
				<configuration>
					<verbose>true</verbose>
					<overwrite>true</overwrite>
				</configuration>
				<dependencies>
					<dependency>
						<groupId>xyz.mrwood.mybatis.generator.plugin</groupId>
						<artifactId>mybatis-generator-plugin</artifactId>
						<version>1.0-SNAPSHOT</version>
					</dependency>
				</dependencies>
			</plugin>
```

- generatorConfig.xml
``` xml
<!-- 集成lombok -->
<plugin type="xyz.mrwood.mybatis.generator.plugin.plugins.LombokPlugin"/>
<!--生成拓展类-->
<plugin type="xyz.mrwood.mybatis.generator.plugin.plugins.ExtPlugin" />
```
