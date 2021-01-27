FROM hashicorp/terraform:0.14.5

MAINTAINER Vitaliy Boyarsky <boyarsky.vitaliy@live.com>

ENV KAFKA_BROKER_HOST='localhost'
ENV KAFKA_BROKER_PORT=9092

WORKDIR /opt/
COPY resources.tf .
RUN terraform init

RUN echo $'#!/bin/sh \n\
\n\
while ! nc -vz "$KAFKA_BROKER_HOST" "$KAFKA_BROKER_PORT" ; do \n\
    sleep 1; \n\
done \n\
\n\
terraform "$@" \n\
' > entrypoint.sh
RUN chmod +x entrypoint.sh

ENTRYPOINT [ "./entrypoint.sh" ]
CMD [ "apply", "-auto-approve" ]
