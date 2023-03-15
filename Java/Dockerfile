# Copy the jar, database and test scenarios into image
FROM openjdk:11
WORKDIR /usr/cs6310
COPY CS6310-G65-A5.jar ./
COPY test_scenarios ./
COPY test_results ./
COPY CS6310Group65.db ./
COPY apache-jmeter-5.4.1 ./apache-jmeter-5.4.1
RUN chmod +x apache-jmeter-5.4.1/bin/jmeter

# Install jmeter int image
#RUN mkdir /jmeterdocker
#RUN mkdir -p /jmeterdocker/test
#RUN mkdir -p /jmeterdocker/test/input/jmx
#RUN mkdir -p /jmeterdocker/test/input/testdata
#RUN mkdir -p /jmeterdocker/test/report/html
#RUN mkdir -p /jmeterdocker/test/report/jtl
#RUN mkdir -p /jmeterdocker/test/report/outputdata
#RUN chmod -R 777 /jmeterdocker
#
#RUN cd /jmeterdocker
#
#ENV JMETER_VERSION=5.4.1
#ENV JMETER_HOME=/jmeterdocker/apache-jmeter-${JMETER_VERSION}
#ENV JMETER_PATH=${JMETER_HOME}/bin:${PATH}
#
#RUN wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz
#RUN tar -zxvf apache-jmeter-${JMETER_VERSION}.tgz
#RUN rm apache-jmeter-${JMETER_VERSION}.tgz