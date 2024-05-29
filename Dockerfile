FROM openjdk:22-jdk
#ARG JAR_FILE=target/*.jar
COPY ./target/Ledger-0.0.1.jar LedgerApp.jar
ENTRYPOINT ["java","-jar", "/LedgerApp.jar"]