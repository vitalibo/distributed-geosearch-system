FROM adoptopenjdk:11-jre

ARG artifact_name="geosearch-subject"
ARG artifact_version="1.0.0"

COPY "target/$artifact_name-$artifact_version.jar" "/opt/app.jar"

ENTRYPOINT ["java", "-jar", "/opt/app.jar"]
CMD ["subject"]
