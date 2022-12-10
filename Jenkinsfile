pipeline {
  agent any

  stages {
      stage('Build Artifact- Maven') {
            steps {
              sh "mvn clean package -DskipTests=true"
              archive 'target/*.jar'
            }
        }
      stage('Unit Tests -JUnit and Jacoco') {
            steps {
              sh "mvn test"
            }
            post {
              always {
                junit 'target/surefire-reports/*.xml'
                jacoco execPattern: 'target/jacoco.exec'
              }
            }
      }
      stage('Docker Build and push') {
        steps {
            sh 'printenv'
            sh 'docker build -t kalyan947/string-app:""$GIT_COMMIT"" .'
            sh 'docker push kalyan947/string-apps:""$GIT_COMMIT""'
        }
      }
    }
}