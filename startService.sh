cd /home/ubuntu/BOTgether
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum -y install -y apache-maven
sudo yum -y install java-1.8.0-openjdk-devel
sudo export JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk.x86_64
sudo mvn spring-boot:run