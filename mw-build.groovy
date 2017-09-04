#!groovy

// Build PunchMW application
// Requires MS Build Tools installed on slave (for Access Control)

pipeline {
    agent {
        label 'pipeline'
    }

     tools {
        ant "Ant-1.9.6"
     }

    options {
         buildDiscarder(logRotator(numToKeepStr: '5'))
         skipDefaultCheckout(true)
    }

    environment {
        svnCredentialsId = 'vital.lobachevskij-wrf-svn'
        svnRootURL = 'svn://kap-wfr-svn.int.kronos.com'
        envId = 'maindev'
        MiddlewarePath = '\\\\epbyminw1044\\wfr-artifactory'
    }

    stages {
        stage('Build') {
            steps {
                // Checkout PunchMW repo
                checkoutSVN (
                    credentialsId: svnCredentialsId,
                    url: "$svnRootURL/PunchMW"
                )
                // Checkout environment config files
                // checkoutSVN (
                //     credentialsId: svnCredentialsId,
                //     url: "$svnRootURL/Documents/DevOps/Scripts/Env_Configs/$envId",
                //     localDir: 'env'
                // )
                // Compile PunchMW
                buildMW()
            }
        }

        stage('Publish') {
            steps {
                echo 'Publish...'
                publishMW (
//                    repo: '\\\\kap-wfr-fs01\\Build Server Data\\kap-wfr-ap01\\SharedFiles\\hardware\\'
                    repo: '\\\\epbyminw1044\\wfr-artifactory'
                )
            }
        }

    }

}
