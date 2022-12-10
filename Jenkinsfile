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
      stage('Mutation Tests - PIT') {
            steps {
              sh "mvn org.pitest:pitest-maven:mutationCoverage"
            }
            post {
              always {
                pitmutation mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
              }
            }
      }
      //***JENKINS_SERVER_URL***/job/***JENKINS_JOB_NAME***/build?token=***JENKINS_BUILD_TRIGGER_TOKEN***
      stage('SonarQube Analysis') {
        steps {
            sh "mvn clean verify sonar:sonar -Dsonar.projectKey=string-app"
        }
      }

      stage('Docker Build and push') {
        steps {
            withDockerRegistry([credentialsId:"docker-hub",url:""]) {
                sh 'printenv'
                sh 'docker build -t kalyan947/string-app:""$GIT_COMMIT"" .'
                sh 'docker push kalyan947/string-app:""$GIT_COMMIT""'
            }
        }
      }
      stage('Kubernetes Deployment -Dev') {
        steps {
            withKubeConfig([credentialsId: "kubeconfig"]) {
                sh "sed -i 's#replace#kalyan947/string-app:${GIT_COMMIT}#g' k8s_deployment_service.yaml"
                sh "microk8s kubectl apply -f k8s_deployment_service.yaml"
            }
        }
      }
    }
}