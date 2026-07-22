pipeline {
    agent any

    triggers {
            pollSCM('* * * * *')
    }

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

        stage('Docker Check') {
                    steps {
                        echo 'Checking Docker availability'

                        bat 'docker version'
                        bat 'docker compose version'
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

        stage('Prepare Docker Environment') {
            steps {
                echo 'Preparing Docker environment variables'

                withCredentials([
                    file(
                        credentialsId: 'pulseguard-env',
                        variable: 'PULSEGUARD_ENV_FILE'
                    )
                ]) {
                    bat 'copy /Y "%PULSEGUARD_ENV_FILE%" .env'
                }

                bat 'docker compose -p pulseguard config'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building the latest PulseGuard Docker image'

                bat 'docker compose -p pulseguard build pulseguard'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying PulseGuard and MySQL'

                bat 'docker compose -p pulseguard up -d --remove-orphans'
                bat 'docker compose -p pulseguard ps'
            }
        }

        stage('Health Verification') {
            steps {
                echo 'Waiting for PulseGuard to become healthy'

                powershell '''
                    $healthy = $false

                    for ($attempt = 1; $attempt -le 30; $attempt++) {
                        Write-Host "Health check attempt $attempt of 30"

                        try {
                            $response = Invoke-RestMethod `
                                -Uri "http://localhost:8080/actuator/health" `
                                -TimeoutSec 5

                            if ($response.status -eq "UP") {
                                $healthy = $true
                                break
                            }
                        }
                        catch {
                            Write-Host "Application is not ready yet"
                        }

                        Start-Sleep -Seconds 2
                    }

                    if (-not $healthy) {
                        throw "PulseGuard did not become healthy"
                    }

                    Write-Host "PulseGuard deployment is healthy"
                '''
            }
        }


    }

    post {
        always {
            junit(
                allowEmptyResults: true,
                testResults: 'target/surefire-reports/*.xml'
            )

            bat 'if exist .env del /Q .env'
        }

        success {
            archiveArtifacts(
                artifacts: 'target/*.jar',
                fingerprint: true
            )

            echo 'PulseGuard CI/CD pipeline completed successfully'
        }

        failure {
            bat(
                returnStatus: true,
                script: 'docker compose -p pulseguard logs --tail=100 pulseguard'
            )

            echo 'PulseGuard pipeline failed. Check the failed stage and logs.'
        }
    }
}