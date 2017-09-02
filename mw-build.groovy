#!groovy

// Build PunchMW application
// Requires MS Build Tools installed on slave (for Access Control)

pipeline {
    agent {
        label 'pipeline'
    }

    tools {
       ant 'Ant-1.9.6'
       msbuild 'MSBuild-Default'
    }

    options {
         buildDiscarder(logRotator(numToKeepStr: '5'))
         skipDefaultCheckout(true)
    }

    environment {
        svnCredentialsId = 'vital.lobachevskij-wrf-svn'
        svnRootURL = 'svn://kap-wfr-svn.int.kronos.com'
        envId = 'maindev'
    }

    stages {
        stage('Build') {
            steps {
                // Checkout PunchMW repo
                checkoutSVN(svnCredentialsId, "$svnRootURL/PunchMW")
                // Checkout environment config files
                checkoutSVN(svnCredentialsId, "$svnRootURL/Documents/DevOps/Scripts/Env_Configs/$envId", 'env')
                // Compile PunchMW
                buildMW()
            }
        }

        stage('Publish') {
            steps {
                echo 'Publish...'
            }
        }

    }

}
