#FROM openjdk:22-jdk
##ARG JAR_FILE=target/*.jar
#COPY ./target/Ledger-0.0.1.jar LedgerApp.jar
#ENTRYPOINT ["java","-jar", "/LedgerApp.jar"]


# select parent image
FROM maven:3.9.7-sapmachine-22
#maven:3.6.3-jdk-8 --- make sure your maven image is using the correct jdk version or else you will get an error such as:
#"class file has wrong version 61.0, should be 52.0"

# copy the source tree and the pom.xml to our new container
COPY ./ ./

# package our application code
RUN mvn clean package -X

# set the startup command to execute the jar
CMD ["java", "-jar", "/Ledger-0.0.1.jar"]