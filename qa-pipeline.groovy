#!/usr/bin/env groovy


//def files = getAllFiles(createFilePath('D:\\Temp\\wfr-artifactory'))
def filePath = new File('D:\\Temp\\wfr-artifactory').eachFileMatch('*.zip') { f ->
    echo "Files: $f"
}

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
