#!/usr/bin/env groovy

import static groovy.io.FileType.FILES
import com.cloudbees.groovy.cps.NonCPS


@NonCPS
def getAllFiles() {
    def result = ''
    new File('D:\\Temp\\wfr-artifactory').traverse(type: FILES, nameFilter: ~/.*.zip/) { file ->
        result += "${file.name}\n"
    }
    return result
}

def fileList = getAllFiles()

properties([
  parameters([
    booleanParam(name: 'DEPLOY_ON_AP01', defaultValue: true,
        description: 'Deploys selected build on AP01 node.'),
    booleanParam(name: 'DEPLOY_ON_MW01', defaultValue: true,
        description: 'Deploys selected build on MW01 node.'),
    booleanParam(name: 'DEPLOY_ON_MW02', defaultValue: true,
        description: 'Deploys selected build on MW02 node.'),
    choice(name: 'BUILD_TO_DEPLOY', defaultValue: '', choices: "$fileList",
        description: 'Logical group of agent to run the job on. ')
   ])
])


node('master') {

    stage('Deploy') {
        echo "BUILD_TO_DEPLOY: $params.BUILD_TO_DEPLOY"
    }

}

/*
@NonCPS
def getAllFiles(rootPath) {
    def list = []
    for (subPath in rootPath.list()) {
        list << subPath.getName()
    }
    return list

}
*/

@NonCPS
def createFilePath(def path) {
    return new hudson.FilePath(path);
    // if (env['NODE_NAME'].equals("master")) {
    //     return new hudson.FilePath(path);
    // } else {
    //     return new hudson.FilePath(Jenkins.getInstance().getComputer(env['NODE_NAME']).getChannel(), path);
    // }
}
