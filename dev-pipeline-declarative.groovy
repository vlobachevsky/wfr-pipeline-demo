#!/usr/bin/env groovy

pipeline {
    agent {
        label 'pipeline'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

/*
    environment {

    }
*/

    stages {
        stage('Build') {
            steps {
                echo "Step: Build"
            }
        }
        stage('Test') {
            steps {
                echo "Step: Test"
            }
        }

    }
}
