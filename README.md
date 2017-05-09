# smcodegenerator
intellij idea plugin for ssm

主要功能：
调用外部的程序实现Spring+Mybatis项目代码的自动生成

主要流程：
在idea DataBase视图的工具栏中增加一个按钮，点击之后将选中数据库的链接信息发送到目标exe，目标exe连接数据库获取元数据信息，最终实现代码生成

使用方式：
1、修改JetBrains\IntelliJ IDEA 14.1.6\plugins\DatabaseTools\lib目录下的database-impl.jar文件
   （1）在META-INF\DatabasePlugin.xml文件中增加以下内容：
    <action id="GeneratorCodeAction" class="com.intellij.database.access.GeneratorCodeAction"
            text="GeneratorCode" icon="SMCodeGeneratorIcons.GENERATE_CODE_ACTION">
      <add-to-group group-id="DatabaseViewToolbar" anchor="first"/>
    </action>
   （2）将本项目编译后的内容增加到database-impl.jar文件
2、在JetBrains\IntelliJ IDEA 14.1.6\plugins\DatabaseTools目录下增加smcg项目编译后的可执行文件及相关dll
