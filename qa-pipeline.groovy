#!/usr/bin/env groovy

import static groovy.io.FileType.FILES
import com.cloudbees.groovy.cps.NonCPS

/*
properties([
  parameters([
    booleanParam(name: 'DEPLOY_ON_AP01', defaultValue: true,
        description: 'Deploys selected build on AP01 node.'),
    booleanParam(name: 'DEPLOY_ON_MW01', defaultValue: true,
        description: 'Deploys selected build on MW01 node.'),
    booleanParam(name: 'DEPLOY_ON_MW02', defaultValue: true,
        description: 'Deploys selected build on MW02 node.'),
    // choice(name: 'BUILD_TO_DEPLOY', defaultValue: '', choices: "$fileList",
    //     description: 'Logical group of agent to run the job on. ')
   ])
])
*/

def userInput

node('master') {

    stage('Approve') {
        userInput = input(
            id: 'userInput', message: '', parameters: [
                [
                    $class: 'BooleanParameterDefinition',
                    name: 'DEPLOY_ON_AP01',
                    defaultValue: true,
                    description: '',
                ],
                [
                    $class: 'BooleanParameterDefinition',
                    name: 'DEPLOY_ON_MW01',
                    defaultValue: true,
                    description: '',
                ],
                [
                    $class: 'BooleanParameterDefinition',
                    name: 'DEPLOY_ON_MW02',
                    defaultValue: true,
                    description: '',
                ],
                [
                    $class: 'ChoiceParameterDefinition',
                    name: 'PACKAGE_TO_DEPLOY',
                    choices: getAllFiles(),
                    description: '',
                ],
            ]
        )
    }

    stage('Update DB') {
        echo "Updating DB"
    }

    parallel firstBranch: {
        stage('Deploy AP01') {
            if (userInput.DEPLOY_ON_AP01) {
                sleep 90
                echo ("Deploying selected Package ${userInput.PACKAGE_TO_DEPLOY} on AP01")
            }
        }
    }, secondBranch: {
        stage('Deploy MW01') {
            if (userInput.DEPLOY_ON_MW01) {
                sleep 120
                echo ("Deploying selected Package ${userInput.PACKAGE_TO_DEPLOY} on MW01")
            }
        }
    }, thirdBranch: {
        stage('Deploy MW02') {
            if (userInput.DEPLOY_ON_MW02) {
                sleep 100
                echo ("Deploying selected Package ${userInput.PACKAGE_TO_DEPLOY} on MW02")
            }
        }
    }


}

@NonCPS
def getAllFiles() {
    def result = ''
    new File('D:\\Temp\\wfr-artifactory').traverse(type: FILES, nameFilter: ~/.*.zip/) { file ->
        result += "${file.name}\n"
    }
    return result
}

// def findAMIs() {
//     return UUID.randomUUID().toString().split('-').join('\n')
// }
