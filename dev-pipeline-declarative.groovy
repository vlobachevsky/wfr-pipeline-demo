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
                checkoutSVN (
                    credentialsId: svnCredentialsId, 
                    url: 'svn://kap-wfr-svn.int.kronos.com/zeyt'
                )
            }
        }
        stage('Test') {
            steps {
                echo "Step: Test"
            }
        }

    }
}
