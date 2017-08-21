#!/usr/bin/env groovy

pipeline {
    agent {
        label: 'pipeline'
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
