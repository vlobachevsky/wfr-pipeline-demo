#!groovy

@Library('wfr-pipeline-shared') _

pipeline {
    agent {
        label 'pipeline'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    environment {
        svnCredentialsId = 'vital.lobachevskij-wrf-svn'
    }

    stages {
        stage('Build') {
            steps {
                echo "Step: Build"
                checkoutSVN 'Vital'
            }
        }
        stage('Test') {
            steps {
                echo "Step: Test"
            }
        }

    }
}
