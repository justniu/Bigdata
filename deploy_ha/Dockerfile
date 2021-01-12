FROM openjdk:8-jdk-buster
MAINTAINER <NIU> 
WORKDIR /root
ADD hadoop-2.10.1.tar.gz .
RUN mv hadoop-2.10.1 /usr/local/hadoop 

# hadoop环境变量
ENV HADOOP_HOME /usr/local/hadoop
ENV HADOOP_CONFIG_HOME /usr/local/hadoop/etc/hadoop
ENV HADOOP_LIBEXEC_DIR /usr/local/hadoop/libexec
ENV HADOOP_COMMON_HOME $HADOOP_HOME
ENV HADOOP_HDFS_HOME $HADOOP_HOME
ENV HADOOP_CONF_DIR  /usr/local/hadoop/etc/hadoop
ENV PATH $PATH:/usr/local/hadoop/bin:/usr/local/hadoop/sbin:

RUN apt-get update && apt-get install -y openssh-server vim && \
	ssh-keygen -t rsa -f ~/.ssh/id_rsa -P '' && \
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys && \
	mkdir /run/sshd 

RUN echo "export JAVA_HOME=\"/usr/local/openjdk-8\"" >> .bashrc && \
	echo "export JAVA_VERSION=\"8u275\"" >> .bashrc

RUN echo "root:123" | chpasswd
EXPOSE  22

ENTRYPOINT /usr/sbin/sshd -D
