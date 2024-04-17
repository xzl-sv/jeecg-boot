#!/bin/sh
exec java -Xmx3G -Xms3G  -Djava.security.egd=file:/dev/./urandom   -jar   /jeecg-boot/jeecg-system-start-3.6.2.jar    --spring.config.location=file:/jeecg-boot/application-dev.yml
