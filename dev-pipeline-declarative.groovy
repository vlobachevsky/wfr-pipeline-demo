#!/usr/bin/env groovy

pipeline {
    agent {
        node {
            label: 'master'
        }
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
