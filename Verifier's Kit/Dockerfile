FROM frolvlad/alpine-oraclejdk8:slim
EXPOSE 8080
RUN mkdir -p /app/
ADD build/libs/gs-accessing-data-mysql-0.1.0 /app/gs-accessing-data-mysql-0.1.0
ENTRYPOINT ["java", "-jar", "/app/gs-accessing-data-mysql-0.1.0"]
