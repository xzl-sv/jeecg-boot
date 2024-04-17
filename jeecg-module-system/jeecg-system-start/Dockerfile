#FROM anapsix/alpine-java:8_server-jre_unlimited
FROM anapsix/alpine-java:8_jdk_unlimited
#FROM openjdk:8-jre-alpine


MAINTAINER qianshiva

RUN #apk add jattach
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN apk --no-cache add curl

#RUN mkdir -p /jeecg-boot/config/jeecg/

WORKDIR /jeecg-boot

EXPOSE 8080

#ADD ./src/main/resources/jeecg ./config/jeecg
ADD ./target/jeecg-system-start-3.6.2.jar ./

COPY entrypoint.sh entrypoint.sh

ENTRYPOINT ["/bin/sh","entrypoint.sh"]

CMD sleep 10;