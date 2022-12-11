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
            /* post {
              always {
                junit 'target/surefire-reports *//*.xml'
                jacoco execPattern: 'target/jacoco.exec'
              }
            } */
      }
      stage('Mutation Tests - PIT') {
            steps {
              sh "mvn org.pitest:pitest-maven:mutationCoverage"
            }
            /* post {
              always {
                pitmutation mutationStatsFile: '**//* target/pit-reports *//**//* mutations.xml'
              }
            } */
      }
      //***JENKINS_SERVER_URL***/job/***JENKINS_JOB_NAME***/build?token=***JENKINS_BUILD_TRIGGER_TOKEN***
      stage('SonarQube - SAST') {
        steps {
            withSonarQubeEnv('sonarqube') {
                sh "mvn sonar:sonar -Dsonar.projectKey=string-app -Dsonar.host.url=http://localhost:9000"
            }
            timeout(time: 2, unit: 'MINUTES') {
                script {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
      }

      stage('Vulnerability Scan - Docker') {
            steps {
                parallel(
                    "Dependency Scan": {
                        sh "mvn dependency-check:check"
                    },
                    "Trivy Scan":{
                        sh "bash trivy-docker-image-scan.sh"
                    },
                    "OPA Conftest":{
                        sh 'docker run --rm -v $(pwd):/project openpolicyagent/conftest test --policy opa-docker-security.rego Dockerfile'
                    }
                )
                //sh "mvn dependency-check:check"
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

      stage('Vulnerability Scan - k8s') {
            steps {
                sh 'docker run --rm -v $(pwd):/project openpolicyagent/conftest test --policy opa-k8s-security.rego k8s_deployment_service.yaml'
            }
      }

      /* stage('Kubernetes Deployment -Dev') {
        steps {
            withKubeConfig([credentialsId: "kubeconfig"]) {
                sh "sed -i 's#replace#kalyan947/string-app:${GIT_COMMIT}#g' k8s_deployment_service.yaml"
                sh "microk8s kubectl apply -f k8s_deployment_service.yaml"
            }
        }
      } */

      stage('Kubernetes Deployment -Dev') {
        steps {
            parallel(
                "Deployment": {
                    withKubeConfig([credentialsId: "kubeconfig"]) {
                        sh "bash k8s_deployment.sh"
                    }
                },
                "Rollout Status": {
                   withKubeConfig([credentialsId: "kubeconfig"]) {
                       sh "bash k8s_rollout.sh"
                   }
                }
            )
        }
      }
    }

    post {
      always {
        junit 'target/surefire-reports/*.xml'
        jacoco execPattern: 'target/jacoco.exec'
        pitmutation mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
        dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
      }
    }
}