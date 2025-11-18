<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [如果要实现用户注册登录功能，要怎么实现呢？要考虑什么呢？](#%E5%A6%82%E6%9E%9C%E8%A6%81%E5%AE%9E%E7%8E%B0%E7%94%A8%E6%88%B7%E6%B3%A8%E5%86%8C%E7%99%BB%E5%BD%95%E5%8A%9F%E8%83%BD%E8%A6%81%E6%80%8E%E4%B9%88%E5%AE%9E%E7%8E%B0%E5%91%A2%E8%A6%81%E8%80%83%E8%99%91%E4%BB%80%E4%B9%88%E5%91%A2)
- [Spring MVC vs Spring WebFlux](#spring-mvc-vs-spring-webflux)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

### 如果要实现用户注册登录功能，要怎么实现呢？要考虑什么呢？



### Spring MVC vs Spring WebFlux

不太推荐上 WebFlux 的情况：
- 典型后台 CRUD 系统 + 传统关系型 DB（JPA/MyBatis） + 阻塞驱动。
- 团队对 reactive 思维和调试手法不熟。
- 性能瓶颈不在线程/连接数，而是业务逻辑或数据库本身。
