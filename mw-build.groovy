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
        SVN_CREDENTIALS_ID = 'vital.lobachevskij-wrf-svn'
        svnRootURL = 'svn://kap-wfr-svn.int.kronos.com'
        envId = 'maindev'
        repo = '\\\\epbyminw1044.minsk.epam.com\\wfr-artifactory\\'
    }

    stages {
        stage('Build') {
            steps {
                // Checkout PunchMW repo
                checkoutSVN (
                    credentialsId: SVN_CREDENTIALS_ID,
                    url: "$env.svnRootURL/PunchMW"
                )
                // Checkout environment config files
                checkoutSVN (
                    credentialsId: SVN_CREDENTIALS_ID,
                    url: "$svnRootURL/Documents/DevOps/Scripts/Env_Configs/$envId",
                    localDir: 'env'
                )
                // Compile PunchMW
                buildMW()
            }
        }

        stage('Publish') {
            steps {
                // Zip MW and copy to shared folder
                publishMW (
                    repo: "$repo"
                )
            }
        }

    }

}
