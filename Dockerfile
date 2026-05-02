FROM eclipse-temurin:25-jdk AS builder

RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /build

RUN wget -O TimoCloud.jar \
https://jenkins.timo.cloud/job/TimoCloud/job/master/lastSuccessfulBuild/artifact/TimoCloud-Universal/target/TimoCloud.jar

FROM eclipse-temurin:25-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends screen \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/TimoCloud

COPY --from=builder /build/TimoCloud.jar ./TimoCloud.jar

ENTRYPOINT ["java","-jar","/opt/TimoCloud/TimoCloud.jar"]

EXPOSE 5000 25565
