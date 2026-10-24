pipeline {
    agent any

    environment {
        SERVER_IP = credentials('maven-server-ip')
        APP_NAME = "Tesla-mechanism"
    }

    stages {

        stage('checkout'){
            steps{
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                sh 'javac -version'
            }
        }

        stage('maven build and deploy'){
            steps {
                sshagent(credentials: ['server-ssh-p']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=yes ubuntu@${SERVER_IP} "
                            rm -rf ${APP_NAME}
                            git clone https://github.com/Sivachaithanya-Organisation/Tesla-mechanism.git
                            cd ${APP_NAME}
                            mvn clean package
                            sudo cp target/aerocar.war /opt/tomcat/webapps/
                        "
                    '''
                }
            }
        }
    }
}
