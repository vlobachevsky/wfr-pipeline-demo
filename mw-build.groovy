#!groovy

// Build PunchMW application
// Requires MS Build Tools installed on slave (for Access Control)

/*
Params:
-- Node
-- Shared Folder URI
*/

// TODO: Send notifications

pipeline {
    parameters {
        string(name: 'LABEL', defaultValue: 'MSBuild', description: 'Restrict where this project can be run (node name or lable).')
        string(name: 'WORKSPACE', defaultValue: 'D:\\TA2', description: 'Custom workspace for the project.')
        string(name: 'SHARED_FOLDER_URI', defaultValue: '\\\\kap-wfr-fs01\\Build Server Data\\kap-wfr-ap01\\SharedFiles\\test', description: 'Where to publish the project artifacts?')
        // TODO: Add SKIP_PUBLISH param
    }

    agent {
        node {
            label "$params.LABEL"
            customWorkspace "$params.WORKSPACE"
        }
    }

    tools {
        ant "Ant 1.9.6"
    }

    options {
         buildDiscarder(logRotator(numToKeepStr: '5'))
         skipDefaultCheckout(true)
    }

    environment {
        PROJECT_RECIPIENT_LIST = 'Vital.Lobachevskij@Kronos.com'
        ENV_ID = 'maindev'
    }

    stages {
        // TODO: Temporary. Remove when KAP-WFR-MW0* jobs are migrated
        stage('Stop MW') {
            steps {
                stopMW()
            }
        }
        stage('Build') {
            steps {
                dir('PunchMW') {
                    // Checkout PunchMW repo
                    checkoutSVN(
                        url: infra.getSVNRootURL() + '/PunchMW'
                    )
                    // Checkout environment config files
                    checkoutSVN(
                        url: infra.getSVNRootURL() + "/Documents/DevOps/Scripts/Env_Configs/$ENV_ID",
                        localDir: 'env'
                    )
                    // Compile PunchMW
                    buildMW()
                }
            }
        }

        stage('Start MW') {
            steps {
                startMW()
            }
        }

        stage('Publish') {
            // TODO: Temporary don't run this step twice
            when {
                expression { env.LABEL == "KAP-WFR-MW02" }
            }
            steps {
                // Zip MW and copy to shared folder
//                publishMW(
//                    repo: "$params.SHARED_FOLDER_URI"
//                )
                publishClientMW(
                    repo: "\\\\kap-wfr-fs01\\Build Server Data\\kap-wfr-ap01\\SharedFiles\\test"
                )
            }
        }
    }

    post {
        success {
            script {
                def isFixed = currentBuild.previousBuild.result in ['FAILURE', 'UNSTABLE']
                if (isFixed) {
                    sendMail(
                        body: '''<p><strong>${ENV,var="LABEL"} is up</strong></p>
                            <p>${JELLY_SCRIPT,template="html"}</p>'''
                    )
                } else {
                    // Successful build
                    sendMail(
                        body: '''<p><strong>${ENV,var="LABEL"} is up</strong></p>
                        <p>${JELLY_SCRIPT,template="html"}</p>'''
                    )
                }
            }
        }
        failure {
            sendMail(
                body: '''<p><strong>${ENV,var="LABEL"} is unavailable</strong></p>
                    <p>${JELLY_SCRIPT,template="html"}</p>'''
            )
        }
        unstable {
            sendMail(
                    body: '''<p><strong>${ENV,var="LABEL"} is up</strong></p>
                        <p>${JELLY_SCRIPT,template="html"}</p>
                        <p>${FAILED_TESTS}</p>''',
                    attachLog: false
            )
        }
    }

}
