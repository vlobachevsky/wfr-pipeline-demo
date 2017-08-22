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
        svnRootURL = 'svn://kap-wfr-svn.int.kronos.com'
    }

    stages {
        stage('Build') {
            parallel (
                "Step 1": {
                    echo "Build Java"
                    },
                    "Step 2": {
                        echo "Build JS"
                    }
                    )
            steps {
                checkoutSVN(svnCredentialsId, "$svnRootURL/zeyt")
            }
        }
        stage('Unit Tests') {
            steps {
                echo "Step: Test"
            }
        }

    }
}
