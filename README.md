# Bean Searcher 4.1.2 + Mybatis 3.5.10 + MybatisPlus + Springboot 2.7.8 + jdk1.8

### 介绍

本项目主要用于演示 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 与 mybatis 合并，不变更 bs 源码的基础上，通过继承与覆盖bean的方式加以改造，将 bs 的功能融合到 mybatis，目的是让 bs 支持 mybatis 拦截器，常见的场景有权限控制拦截器，权限范围控制拦截器。

### 本项目测试方式


```bash
1. 访问请求，调用 Bean Searcher框架提供API
2. mybatis 拦截器打印日志
3. 查看日志，完成本项目目的。
```

##### 样例日志

实际请求

```go
请求链接:
    POST http://localhost/test/project/list
请求头：
    Content-Type: application/x-www-form-urlencoded
请求参数：
    areaName_op:il
    areaName:其他,市直
    
    beginDate_op:bt
    beginDate:1997-01-01,2023-01-01
```

```bash
2023-03-19 16:31:41.156   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]com.xunmo.project4sp.global.GlobalAspect-->  
【1f8a9c1b5b04471f882abd8dce617d8e】【请求 URL】：POST http://localhost/test/project/list
【1f8a9c1b5b04471f882abd8dce617d8e】【请求方法】：com.xunmo.biz.project.controller.ProjectController.list()
【1f8a9c1b5b04471f882abd8dce617d8e】【请求参数】：{"areaName":["其他,市直"],"areaName_op":["il"],"beginDate":["1997-01-01,2023-01-01"],"beginDate_op":["bt"]}
【1f8a9c1b5b04471f882abd8dce617d8e】【body】：[{}]
2023-03-19 16:31:41.157   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]com.xunmo.bs.config.bs.MySqlExecutor-->  select count(*) s_count from "T_XM" where ("AREA" in (#{areaName_0}, #{areaName_1})) and ("KGRQ" between #{beginDate_0} and #{beginDate_1})
2023-03-19 16:31:41.195   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]p6spy-->   Execute statement SQL：select 1
2023-03-19 16:31:41.199   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]com.xunmo.current.dataScope.ProjectDataScope-->  进入项目判断权限范围
2023-03-19 16:31:41.223   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]p6spy-->   Execute statement SQL：SELECT count(*) s_count FROM "T_XM" WHERE ("AREA" IN ('其他', '市直')) AND ("KGRQ" BETWEEN '1997-01-01T00:00:00.000+0800' AND '2023-01-01T00:00:00.000+0800')
2023-03-19 16:31:41.298   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]com.xunmo.current.dataScope.ProjectDataScope-->  进入项目判断权限范围
2023-03-19 16:31:41.431   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]p6spy-->   Execute statement SQL：SELECT "XMID" c_0, "XMMC" c_1, "JDH" c_2, "JSDWID" c_3, "JSDW" c_4, "XMDZ" c_5, "GCLX" c_6, "GCSX" c_7, "AREA" c_8, "GCZLDJ" c_9, "GLDJ" c_10, "GLLB" c_11, "LCS" c_12, "JAF" c_13, "KGRQ" c_14, "WGRQ" c_15, "GCSX1" c_16, "XMJK" c_17, "XMGK" c_18, "JDFZR" c_19, "JDR" c_20, "FGLD" c_21, "XMZT" c_22, "ADDTIME" c_23, "ADDUSER" c_24, (SELECT "REALNAME" FROM "T_UNIT_USER" WHERE "USERID" = "ADDUSER") c_25, "JDFZRXM" c_26, "XMZT1" c_27, "XMZT2" c_28, "BWDJ" c_29, "SJTTL" c_30, "BWS" c_31, "MTCD" c_32, "MTKD" c_33, "YQCD" c_34, "YQKD" c_35, "DCMJ" c_36, "JZWS" c_37, "JZMJ" c_38, "DLDJ" c_39, "XTLX" c_40, "YLDJ" c_41, "CZSL" c_42, "BFB" c_43, "GLLJKD" c_44, "GLQLCD" c_45, "GLQLKD" c_46, "GLSJSD" c_47, "GLQLSJHZ" c_48, "GCGS" c_49, "GQ" c_50, "RYBDSM" c_51, "GLDQSL" c_52, "GLDQYM" c_53, "GLZXQSL" c_54, "GLZXQYM" c_55, "GLHTLJSL" c_56, "GLHTLJYM" c_57, "SDXCLJSL" c_58, "SDXCLJCD" c_59, "GLXSQSL" c_60, "GLXSQYM" c_61, "GLXLQSL" c_62, "GLXLQYM" c_63, "GLGQSL" c_64, "GLGQYM" c_65, "LXGGQSL" c_66, "LXGGQYM" c_67, "DMCZSL" c_68, "DXCZSL" c_69, "GJCZSL" c_70, "ABBREVIATION" c_71, "JDGLDW" c_72, "XGSJ" c_73, "DRSJ" c_74, "SFDR" c_75, (SELECT string_agg("REALNAME", ',') FROM "T_UNIT_USER" WHERE "USERID" IN (SELECT unnest(string_to_array("T_XM"."JDR", ',')))) c_76, (SELECT "REALNAME" FROM "T_UNIT_USER" WHERE "USERID" = "T_XM"."FGLD") c_77, (SELECT "ZT1" FROM "T_XM_ZT" WHERE "XMID" = "T_XM"."XMID" AND ("BDID" = '' OR "BDID" IS NULL) ORDER BY "KSSJ" DESC LIMIT 1) c_78, (SELECT "BFB" FROM "T_XM_ZT" WHERE "XMID" = "T_XM"."XMID" AND ("BDID" = '' OR "BDID" IS NULL) ORDER BY "KSSJ" DESC LIMIT 1) c_79 FROM "T_XM" WHERE ("AREA" IN ('其他', '市直')) AND ("KGRQ" BETWEEN '1997-01-01T00:00:00.000+0800' AND '2023-01-01T00:00:00.000+0800') ORDER BY "XMID" LIMIT 15 OFFSET 0
2023-03-19 16:31:41.446   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]com.xunmo.project4sp.global.GlobalAspect-->  
【1f8a9c1b5b04471f882abd8dce617d8e】【请求耗时】：290毫秒
【1f8a9c1b5b04471f882abd8dce617d8e】【已分配内存】：453m 【已分配内存中的剩余空间】：286m 【浏览器类型】：UNKNOWN 【操作系统】：UNKNOWN
【1f8a9c1b5b04471f882abd8dce617d8e】【原始User-Agent】：apifox/1.0.0 (https://www.apifox.cn)
2023-03-19 16:31:41.446   INFO  [1f8a9c1b5b04471f882abd8dce617d8e]com.xunmo.utils.LogUtil-->  ----------------------------------------------------------------
```

```go
请求链接:
    POST http://localhost/test/project/list
请求头：
    Content-Type: application/x-www-form-urlencoded
请求参数：
    areaName_op:il
    areaName:其他,市直
    
    beginDate_op:bt
    beginDate:1997-01-01,2023-01-01
    
    buildingArea_op:gt
    buildingArea:1
    
    pageNo:1
    pageSize:20
    
    orderBy:id:desc
```

```bash
2023-03-19 16:41:26.532   INFO  [3fc2d50c389a419c9c84e0cffd582550]com.xunmo.project4sp.global.GlobalAspect-->  
【3fc2d50c389a419c9c84e0cffd582550】【请求 URL】：POST http://localhost/test/project/list
【3fc2d50c389a419c9c84e0cffd582550】【请求方法】：com.xunmo.biz.project.controller.ProjectController.list()
【3fc2d50c389a419c9c84e0cffd582550】【请求参数】：{"areaName_op":["il"],"buildingArea_op":["gt"],"pageSize":["20"],"orderBy":["id:desc"],"buildingArea":["1"],"beginDate":["1997-01-01,2023-01-01"],"beginDate_op":["bt"],"areaName":["其他,市直"],"pageNo":["1"]}
【3fc2d50c389a419c9c84e0cffd582550】【body】：[{}]
2023-03-19 16:41:26.533   INFO  [3fc2d50c389a419c9c84e0cffd582550]com.xunmo.bs.config.bs.MySqlExecutor-->  select count(*) s_count from "T_XM" where ("JZMJ" > #{buildingArea}) and ("AREA" in (#{areaName_0}, #{areaName_1})) and ("KGRQ" between #{beginDate_0} and #{beginDate_1})
2023-03-19 16:41:26.560   INFO  [3fc2d50c389a419c9c84e0cffd582550]p6spy-->   Execute statement SQL：select 1
2023-03-19 16:41:26.564   INFO  [3fc2d50c389a419c9c84e0cffd582550]com.xunmo.current.dataScope.ProjectDataScope-->  进入项目判断权限范围
2023-03-19 16:41:26.625   INFO  [3fc2d50c389a419c9c84e0cffd582550]p6spy-->   Execute statement SQL：SELECT count(*) s_count FROM "T_XM" WHERE ("JZMJ" > 1.0) AND ("AREA" IN ('其他', '市直')) AND ("KGRQ" BETWEEN '1997-01-01T00:00:00.000+0800' AND '2023-01-01T00:00:00.000+0800')
2023-03-19 16:41:26.676   INFO  [3fc2d50c389a419c9c84e0cffd582550]com.xunmo.current.dataScope.ProjectDataScope-->  进入项目判断权限范围
2023-03-19 16:41:26.727   INFO  [3fc2d50c389a419c9c84e0cffd582550]p6spy-->   Execute statement SQL：SELECT "XMID" c_0, "XMMC" c_1, "JDH" c_2, "JSDWID" c_3, "JSDW" c_4, "XMDZ" c_5, "GCLX" c_6, "GCSX" c_7, "AREA" c_8, "GCZLDJ" c_9, "GLDJ" c_10, "GLLB" c_11, "LCS" c_12, "JAF" c_13, "KGRQ" c_14, "WGRQ" c_15, "GCSX1" c_16, "XMJK" c_17, "XMGK" c_18, "JDFZR" c_19, "JDR" c_20, "FGLD" c_21, "XMZT" c_22, "ADDTIME" c_23, "ADDUSER" c_24, (SELECT "REALNAME" FROM "T_UNIT_USER" WHERE "USERID" = "ADDUSER") c_25, "JDFZRXM" c_26, "XMZT1" c_27, "XMZT2" c_28, "BWDJ" c_29, "SJTTL" c_30, "BWS" c_31, "MTCD" c_32, "MTKD" c_33, "YQCD" c_34, "YQKD" c_35, "DCMJ" c_36, "JZWS" c_37, "JZMJ" c_38, "DLDJ" c_39, "XTLX" c_40, "YLDJ" c_41, "CZSL" c_42, "BFB" c_43, "GLLJKD" c_44, "GLQLCD" c_45, "GLQLKD" c_46, "GLSJSD" c_47, "GLQLSJHZ" c_48, "GCGS" c_49, "GQ" c_50, "RYBDSM" c_51, "GLDQSL" c_52, "GLDQYM" c_53, "GLZXQSL" c_54, "GLZXQYM" c_55, "GLHTLJSL" c_56, "GLHTLJYM" c_57, "SDXCLJSL" c_58, "SDXCLJCD" c_59, "GLXSQSL" c_60, "GLXSQYM" c_61, "GLXLQSL" c_62, "GLXLQYM" c_63, "GLGQSL" c_64, "GLGQYM" c_65, "LXGGQSL" c_66, "LXGGQYM" c_67, "DMCZSL" c_68, "DXCZSL" c_69, "GJCZSL" c_70, "ABBREVIATION" c_71, "JDGLDW" c_72, "XGSJ" c_73, "DRSJ" c_74, "SFDR" c_75, (SELECT string_agg("REALNAME", ',') FROM "T_UNIT_USER" WHERE "USERID" IN (SELECT unnest(string_to_array("T_XM"."JDR", ',')))) c_76, (SELECT "REALNAME" FROM "T_UNIT_USER" WHERE "USERID" = "T_XM"."FGLD") c_77, (SELECT "ZT1" FROM "T_XM_ZT" WHERE "XMID" = "T_XM"."XMID" AND ("BDID" = '' OR "BDID" IS NULL) ORDER BY "KSSJ" DESC LIMIT 1) c_78, (SELECT "BFB" FROM "T_XM_ZT" WHERE "XMID" = "T_XM"."XMID" AND ("BDID" = '' OR "BDID" IS NULL) ORDER BY "KSSJ" DESC LIMIT 1) c_79 FROM "T_XM" WHERE ("JZMJ" > 1.0) AND ("AREA" IN ('其他', '市直')) AND ("KGRQ" BETWEEN '1997-01-01T00:00:00.000+0800' AND '2023-01-01T00:00:00.000+0800') ORDER BY c_0 DESC LIMIT 20 OFFSET 0
2023-03-19 16:41:26.729   INFO  [3fc2d50c389a419c9c84e0cffd582550]com.xunmo.project4sp.global.GlobalAspect-->  
【3fc2d50c389a419c9c84e0cffd582550】【请求耗时】：197毫秒
【3fc2d50c389a419c9c84e0cffd582550】【已分配内存】：423m 【已分配内存中的剩余空间】：269m 【浏览器类型】：UNKNOWN 【操作系统】：UNKNOWN
【3fc2d50c389a419c9c84e0cffd582550】【原始User-Agent】：apifox/1.0.0 (https://www.apifox.cn)
2023-03-19 16:41:26.729   INFO  [3fc2d50c389a419c9c84e0cffd582550]com.xunmo.utils.LogUtil-->  ----------------------------------------------------------------
```



### 运行 DEMO

```xml
1. 修改MySQL相关配置: application.properties
2. 运行 Application
```

### 如果是您自己的项目,食用方法

加入依赖
```xml
<dependency>
    <groupId>com.xunmo</groupId>
    <artifactId>bean-searcher-adapt-mybatis-starter</artifactId>
    <version>4.1.2</version>
</dependency>
```

加入个人仓库
```xml
<repositories>
    <!-- 曾玉飞 maven 个人仓库 -->
    <repository>
        <id>maven-repo-master</id>
        <url>https://raw.github.com/zengyufei/maven-repo/master/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

如果无法获取依赖, 刷新多几次, 因为正常访问 github 不太稳定

### 支持数据库

#### 目前仅支持 `Mysql` 和 `PostgreSql` 两种, 其他方案, 可以参考这两个自己写, 源码包已提供, 通过maven拉取即可.


### 以下全部内容来自 Bean Searcher 框架：


### 运行效果

##### 1. 打开浏览器访问：[http://localhost:8080/](http://localhost:8080/) 效果如下：

![输入图片说明](https://images.gitee.com/uploads/images/2020/1231/163659_08cb49b0_1393412.png "屏幕截图.png")

##### 2. 如上图，本示例展示了一个简单的员工列表页面，实现了如下功能：

* 各种复杂条件组合过滤

* 年龄统计（支持多字段统计）

* 任意字段后端排序（点击表头）

* 分页查询功能

* 总条数统计

OK，页面做的虽然粗糙，但是一个列表检索的功能基本上展示了，下面主要看下在后端, Bean Searcher 是如何简化我们的代码。

### 代码分析

##### 控制层代码

有同学看到这会想，若要实现以上演示的的可以按照各种条件 **组合检索**、**排序**、**分页** 和 **统计** 的功能，那后端的代码量至少也得上百行吧。Bean Searcher 告诉你，不用，关键代码，就一句！啥？我怎么不信？请看代码：

```java
@RestController
public class DemoController {

    @Autowired
    private Searcher searcher;
    
    /**
     * 列表检索接口
     */
    @GetMapping("/employee/index")
    public Object index(HttpServletRequest request) {
        // 组合检索、排序、分页 和 统计 都在这一句代码中实现了
        return searcher.search(Employee.class,              // 指定实体类
                MapUtils.flat(request.getParameterMap()),   // 收集页面请求参数
                new String[] { "age" });                    // 统计字段：年龄
    }

}
```

检索条件呢？检索方式呢？排序呢？分页呢？通通都交给 Bean Sarcher 去实现啦，世界突然如此美好！

**咦！**,这方法的 **返回值怎么是 Object**，**接收参数怎么是 HttpServletRequest**，这让我的 **文档工具 Swagger 怎么用**?

可能看到此处很多人都由此疑问，实际上这些与 Bean Searcher 都没有关系，它只是需要一个 `Map<String, Object>` 类型的参数，其它的你爱咋写就咋写，比如你可以把它等效的写成这样：

```java
@GetMapping("/employee/index")
public SearchResult<Employee> index1(String name, String department, Integer page, Integer size, String sort, String order,
            @RequestParam(value = "name-op", required = false) String name_op,
            @RequestParam(value = "name-ic", required = false) String name_ic,
            @RequestParam(value = "age-0", required = false) Integer age_0,
            @RequestParam(value = "age-1", required = false) Integer age_1,
            @RequestParam(value = "age-op", required = false) String age_op,
            @RequestParam(value = "department-op", required = false) String department_op,
            @RequestParam(value = "department-ic", required = false) String department_ic,
            @RequestParam(value = "entryDate-0", required = false) String entryDate_0,
            @RequestParam(value = "entryDate-1", required = false) String entryDate_1,
            @RequestParam(value = "entryDate-op", required = false) String entryDate_op) {
    // 使用 MapUtils 构建检索参数
    Map<String, Object> params = MapUtils.builder()
        .field(Employee::getName, name).op(name_op).ic(name_ic)
        .field(Employee::getAge, age_0, age_1).op(age_op)
        .field(Employee::getDepartment, department).op(department_op).ic(department_ic)
        .field(Employee::getEntryDate, entryDate_0, entryDate_1).op(entryDate_op)
        .orderBy(sort, order)
        .page(page, size)
        .build();
    // 组合检索、排序、分页 和 统计 都在这一句代码中实现了
    return searcher.search(Employee.class, params, new String[] { "age" });
}
```

因为该例支持的参数比较多，所以这种写法看起来就稍微臃肿一点，但 **实际检索的地方仍只是最后一行代码**！

至于为什么可以支持这么多的参数，请参阅 [Bean Searcher 的文档的参数章节](https://bs.zhxu.cn/guide/params.html)，本例重在体验，具体细节不做讨论。

##### 检索实体类

细心的同学会发现在上述代码里用到一个 Employee 这个类。没错，它就是用来告诉 bean-searcher 如何与数据库字段映射的一个实体类：

```java
@SearchBean(
    tables = "employee e, department d",  // 员工表 与 部门表
    joinCond = "e.department_id = d.id"   // 连接条件
)
public class Employee {

    @DbField("e.id")
    private Long id;

    @DbField("e.name")
    private String name;

    @DbField("e.age")
    private Integer age;

    @DbField("d.name")
    private String department;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DbField("e.entry_date")
    private Date entryDate;

    // Getter and Setter ...
}
```

##### 检索器配置

Bean Searcher 2.x 版本，已经实现了 spring-boot-starter 化，所在，在spring-boot 项目里，它只需要在`application.properties`里配几个必须的信息即可：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/012140_aa87c91d_1393412.png "微信图片_20190707012120.png")

另外，在 IDEA 里，bean-searcher的配置是有提示的哦：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/012532_e81dee9c_1393412.png "微信图片_20190707012518.png")

### 总结

- [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 设计的目标并不是替代某个ORM框架，它只是为了弥补现有ORM框架在复杂列表检索中的不便，实际项目中，配合使用它们，效果或会更好。
- 本例只是 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 在联表检索中的一个简单的演示，更多用法，请参阅: [https://bs.zhxu.cn](https://bs.zhxu.cn)
- 看完这些，大家有没有觉得 [Bean Searcher](https://gitee.com/troyzhxu/bean-searcher) 正好可以帮到你呢？如果是，就点个 Star 吧 ^_^

### 参与贡献

1. Fork Bean Searcher 仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
