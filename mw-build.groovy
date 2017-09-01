#!groovy

// Build PunchMW application
// Requires MS Build Tools installed on slave (for Access Control)

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
        envId = 'maindev'
    }

    stages {
        stage('Build') {
            steps {
                checkoutSVN(svnCredentialsId, "$svnRootURL/PunchMW")
                checkoutSVN(svnCredentialsId, "$svnRootURL/Documents/DevOps/Scripts/Env_Configs/$envId", 'env')
                buildMW()
                // Build Access Control
                powershell(script: 'Write-Host aaa')
            }
        }

        stage('Publish') {
            echo 'Publish...'
        }

    }

}
