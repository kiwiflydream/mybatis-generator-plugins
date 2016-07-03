# mybatis-generator-plugins
mybatis generator 自定义插件


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
```
