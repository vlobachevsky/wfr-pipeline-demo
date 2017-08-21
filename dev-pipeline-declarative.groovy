#!/usr/bin/env groovy

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
                echo "svnCredentialsId: $svnCredentialsId"
            }
        }
        stage('Test') {
            steps {
                echo "Step: Test"
            }
        }

    }
}
