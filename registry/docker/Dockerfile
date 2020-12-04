FROM java
MAINTAINER fmisser
WORKDIR /ROOT
EXPOSE 8001
CMD ["java", "-version"]
# 这里的jar包名称无法替换，在pom中使用配置方式生成 docker image
ENTRYPOINT ["java", "-jar", "${project.build.finalName}.jar"]