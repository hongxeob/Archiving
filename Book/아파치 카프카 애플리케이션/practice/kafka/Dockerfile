FROM adoptopenjdk/openjdk11
LABEL authors="hongxeob"
ENV CLI_DIR=/cli

RUN mkdir ${CLI_DIR}
WORKDIR ${CLI_DIR}
RUN curl https://archive.apache.org/dist/kafka/2.5.0/kafka_2.12-2.5.0.tgz --output kafka.tgz --output kafka.tgz
#RUN curl https://archive.apache.org/dist/kafka/3.6.0/kafka_2.12-3.6.0.tgz --output kafka.tgz --output kafka.tgz
RUN tar -xvf kafka.tgz
ENTRYPOINT ["top", "-b"]
