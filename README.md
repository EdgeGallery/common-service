# 公共服务

### 介绍
本仓库用来存储公共服务代码，两个及以上模块用到的公共代码放到本仓库，方便后续代码维护。

### 组件介绍
#### common-service-cbb
代码量比较小的功能统一放到common-service-cbb实现，详情参考common-service-cbb的readme文件；功能相对完整、代码量比较大的功能请单独建立组件，命名规则：common-service-XXX，其中XXX表示组件功能。

### 公共服务集成方式
通过组件内的Dockerfile构建docker镜像，需要使用公共服务的模块在XXX-deployment.yml文件内增加引用公共服务镜像的container节点，部署完成后一个pod内将包含多个container，引用的模块可以通过localhost访问公共服务提供的REST接口，具体配置方式请参考developer-be对common-service-cbb的引用。

### 参与贡献

1.  Fork 本仓库
2.  提交代码
3.  新建 Pull Request
