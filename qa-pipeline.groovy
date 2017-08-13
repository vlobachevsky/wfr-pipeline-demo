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

    stage('Select Package') {
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

    stage('Deploy AP01') {
        if (userInput.DEPLOY_ON_AP01) {
            echo ("Selected Package :: "+userInput.PACKAGE_TO_DEPLOY)
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
