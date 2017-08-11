#!/usr/bin/env groovy

properties([
  parameters([
    booleanParam(name: 'DEPLOY_ON_AP01', defaultValue: true,
        description: 'Deploys selected build on AP01 node.'),
    booleanParam(name: 'DEPLOY_ON_MW01', defaultValue: true,
        description: 'Deploys selected build on MW01 node.'),
    booleanParam(name: 'DEPLOY_ON_MW02', defaultValue: true,
        description: 'Deploys selected build on MW02 node.'),
    choice(name: 'BUILD_TO_DEPLOY', defaultValue: '', choices: 'Zeyt-XXX.zip\nZeyt-YYY.zip\nZeyt-ZZZ.zip',
        description: 'Logical group of agent to run the job on. ')
   ])
])


node() {

    stage('Deploy') {
        echo "BUILD_TO_DEPLOY: $params.BUILD_TO_DEPLOY"
    }

}
