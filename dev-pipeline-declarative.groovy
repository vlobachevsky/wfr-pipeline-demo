#!groovy

@Library('wfr-pipeline-shared') _

pipeline {
    agent {
        label 'pipeline'
    }

    tools {
       ant 'Ant-1.9.6'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    environment {
        svnCredentialsId = 'vital.lobachevskij-wrf-svn'
        svnRootURL = 'svn://kap-wfr-svn.int.kronos.com'
    }

    stages {
        stage('Build') {
            steps {
                checkoutSVN(svnCredentialsId, "$svnRootURL/zeyt")
                buildZeyt
            }
        }
        stage('Unit Tests') {
            steps {
                echo "Step: Test"
            }
        }

    }
}
