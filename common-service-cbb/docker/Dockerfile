#
#    Copyright 2020 Huawei Technologies Co., Ltd.
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#
FROM swr.cn-north-4.myhuaweicloud.com/eg-common/openjdk:8u201-jre-alpine
#define all environment variable here
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV TZ='Asia/Shanghai'
ENV APP_FILE common-service-cbb.jar
ENV APP_HOME /usr/app
ENV UID=166
ENV GID=166
ENV USER_NAME=eguser
ENV GROUP_NAME=eggroup
ENV ENV="/etc/profile"

# # CREATE APP USER ##
# Set umask
RUN sed -i "s|umask 022|umask 027|g" /etc/profile

# Create the home directory for the new app user.
RUN mkdir -p /usr/app
RUN mkdir -p /usr/app/bin

# Create an app user so our program doesn't run as root.
RUN apk update &&\
    apk add shadow &&\
    groupadd -r -g $GID $GROUP_NAME

RUN apk add openssh
RUN  useradd -r -u $UID -g $GID -d $APP_HOME -s /sbin/nologin -c "Docker image user" $USER_NAME
RUN groupadd docker && \
    gpasswd -a $USER_NAME docker

WORKDIR $APP_HOME

RUN chmod -R 750 $APP_HOME &&\
    chmod -R 550 $APP_HOME/bin &&\
    mkdir -p -m 750 $APP_HOME/config &&\
    mkdir -p -m 750 $APP_HOME/log &&\
    mkdir -p -m 700 $APP_HOME/ssl &&\
    mkdir -p -m 700 $APP_HOME/certs &&\
    mkdir -p -m 750 $APP_HOME/user &&\
    chown -R $USER_NAME:$GROUP_NAME $APP_HOME &&\
    chown -R $USER_NAME:$GROUP_NAME $APP_HOME/log &&\
    chown -R $USER_NAME:$GROUP_NAME $APP_HOME/user

RUN apk add nginx &&\
    mkdir -p /run/nginx &&\
    rm -f /etc/nginx/conf.d/default.conf &&\
    mkdir -p /var/log/nginx &&\
    mkdir -p /var/tmp/nginx &&\
    chmod -R 777 /var/tmp/nginx &&\
    chmod -R 777 /etc/nginx &&\
    chmod -R 777 /var/log/nginx &&\
    chmod -R 777 /var/lib/nginx &&\
    chmod -R 777 /run/nginx

COPY --chown=$USER_NAME:$GROUP_NAME target/*.jar $APP_HOME/bin

EXPOSE 30101
EXPOSE 30111-30120

# Change to the app user.
USER $USER_NAME

# Execute script & application
ENTRYPOINT ["sh", "-c"]
CMD ["nginx && exec java -Dlog4j2.formatMsgNoLookups=true -jar ./bin/$APP_FILE"]
