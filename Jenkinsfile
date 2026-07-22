pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Downloading PulseGuard from GitHub'
                checkout scm
            }
        }

        stage('Environment Check') {
            steps {
                bat 'java -version'
                bat 'git --version'
                bat 'mvnw.cmd -version'
            }
        }

        stage('Compile') {
            steps {
                echo 'Compiling PulseGuard'
                bat 'mvnw.cmd -B clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running automated tests'
                bat 'mvnw.cmd -B test'
            }
        }

        stage('Package') {
            steps {
                echo 'Creating PulseGuard JAR'
                bat 'mvnw.cmd -B package -DskipTests'
            }
        }
        stage('Docker Check') {
            steps {
                echo 'Checking Docker availability'

                bat 'docker version'
                bat 'docker compose version'
            }
        }
    }

    post {
        always {
            junit(
                allowEmptyResults: true,
                testResults: 'target/surefire-reports/*.xml'
            )
        }

        success {
            archiveArtifacts(
                artifacts: 'target/*.jar',
                fingerprint: true
            )

            echo 'PulseGuard pipeline completed successfully'
        }

        failure {
            echo 'PulseGuard pipeline failed. Check the console output.'
        }
    }
}