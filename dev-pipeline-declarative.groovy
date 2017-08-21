#!/usr/bin/env groovy

pipeline {
    agent any 

    stages {
        stage('Check Out') {
            steps {
                echo "Step: Check Out"
            }
        }
        stage('Build') {
            steps {
                echo "Step: Build"
            }
        }

    }
}
