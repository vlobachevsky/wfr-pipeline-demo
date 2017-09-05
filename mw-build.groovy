#!groovy

// Build PunchMW application
// Requires MS Build Tools installed on slave (for Access Control)

/*
Params:
-- Node
-- Shared Folder URI
*/

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
        SVN_ROOT_URL = 'svn://kap-wfr-svn.int.kronos.com'
        ENV_ID = 'maindev'
        SHARED_FOLDER_URI = '\\\\epbyminw1044.minsk.epam.com\\wfr-artifactory\\'
    }

    stages {
        // TODO: Temporary. Remove when KAP-WFR-MW0* jobs are migrated
        stage('Stop MW') {
            stopMW()
        }

        stage('Build') {
            steps {
                // Checkout PunchMW repo
                checkoutSVN (
                    credentialsId: SVN_CREDENTIALS_ID,
                    url: "$SVN_ROOT_URL/PunchMW"
                )
                // Checkout environment config files
                checkoutSVN (
                    credentialsId: SVN_CREDENTIALS_ID,
                    url: "$SVN_ROOT_URL/Documents/DevOps/Scripts/Env_Configs/$ENV_ID",
                    localDir: 'env'
                )
                // Compile PunchMW
                buildMW()
            }
        }

        stage('Start MW') {
            startMW()
        }

        stage('Publish') {
            steps {
                // Zip MW and copy to shared folder
                publishMW (
                    repo: SHARED_FOLDER_URI
                )
            }
        }



    }

}
