平台架构 -- 微服务

app -->android端：用户服务（三层架构）
web -->网页端：管理员服务（三层架构）
common  --- 共共服务（实体类、枚举类、异常类、工具类）

集成架构：
后端（qhq+liuyichao）--移动端android（qhq+liuyichao）--网页端vue（qhq+liuyichao）
--服务器端docker（lihao）--数据库设计mysql(lihao)

后端（qhq+liuyichao）：
运行环境：（部分考虑版本冲突）
jdk版本：17
maven版本：3.9.9
springboot版本：3.0.5
mybatis-plus版本：3.5.3.1

服务器端（lihao）：
虚拟机架设
docker环境
mysql容器
minio容器
nacos容器 等...
