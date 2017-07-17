#!/usr/bin/env groovy

// Prerequisites:
// 1. Tomcat 7.0.65 installed at D:\Tomcat
// 2. Eclipse Compliler jar (ecj-4.4) in ANT_HOME/lib

// XXX: Verify usage Java 1.8 by the build

node {
    env.PATH = "${tool 'Ant-1.9.6'}\\bin;${env.PATH}"

    stage('Build') {
        syncRepo()
/*        parallel (
            "build-java" : {
                compileApp()
            },
            "build-js" : {
                buildJS()
            }
*/
        )
    }

/*
    parallel buildJava: {
        stage('Build: Java') {
            //syncRepo()
            compileApp()
        }
    }, buildJS: {
        stage('Build: JS') {
            buildJS()
        }
    }
*/

    stage('Test') {
        //runJUnitTests()
        // TODO: Try use splitTest to automatically split your test suite into
        // equal running parts that it can run concurrently.
    }

    stage('Package') {
        packageZip()
    }
}

private void syncRepo() {
  //echo '0. Sync repo'
  checkout([
    $class: 'SubversionSCM',
    locations: [[
      credentialsId: 'vital.lobachevskij-wrf-svn',
      depthOption: 'infinity',
      ignoreExternalsOption: true,
      local: '.',
      remote: 'svn://kap-wfr-svn.int.kronos.com/zeyt'
    ]],
    workspaceUpdater: [$class: 'UpdateWithRevertUpdater']
  ])

}

private void compileApp() {
  //bat 'ant -Dpackage.destination=D:\\Temp\\wfr-artifactory BuildEclipseCompiler JUnit'
  bat 'ant BuildEclipseCompiler'
}

private void runJUnitTests() {
  bat 'ant JUnit'
}

private void buildJS() {
  bat 'ant BuildJS'
}

private void packageZip() {
  echo 'ant -Dpackage.destination=D:\\Temp\\wfr-artifactory PackageWeb'
}
