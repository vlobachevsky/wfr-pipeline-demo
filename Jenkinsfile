#!/usr/bin/env groovy

// Prerequisites:
// 1. Tomcat 7.0.7 installed at C:\Tomcat\apache-tomcat
// 2. Eclipse Compliler jar (ecj-4.4) in ANT_HOME/lib

// TODO:
// * Use custom workspace
// * Resolve issue with coping properties files. Should be build parameters for this.
// * Send notification before the build
// * Parametrize build: Skip Db Update; Select servers for deploy
// * Find a way to skip Update DB if there are no updates in db scripts

dbServerName = env.DB_SERVER_NAME ?: 'localhost'
dbServerPort = env.DB_SERVER_PORT ?: '1433'
dbName = env.DB_NAME ?: 'zeyt'
dbUserName = env.DB_USER_NAME ?: 'sa'
dbUserPass = env.DB_USER_PASS ?: 'Admin1234'

node('master') {
    env.PATH = "${tool 'Ant-1.9.6'}\\bin;${tool 'NodeJS v6'};${env.PATH}"

    stage('Build') {
//        syncRepo()
/*
        parallel (
            "build-java" : {
                compileApp()
            },
            "build-js" : {
                buildJS()
            }
        )
*/
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
//        runJUnitTests()
        // TODO: Try use splitTest to automatically split your test suite into
        // equal running parts that it can run concurrently.
    }

    stage('Package') {
        //packageZip()
        stash name: "zeyt-web", includes: "/reports/*,/sql/*,/web/*,/config/*,/quizzes/*,/tutorials/*"
    }

    stage('Update DB') {
//        updateDB()
    }

    stage('Deploy') {
        //node(nodeName = 'win-node-1') {
        node('win-node-1') {
            ws('C:\\TA\\zeyt') {
                syncBuildScript()
                //deployPackage('win-node-1')
                unstash "zeyt-web"
            }
        }

/*
        node(nodeName = 'node2') {
            deployPackage(nodeName)
        }
*/
    }
}

private void syncRepo() {
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

private void syncBuildScript() {
  checkout([
    $class: 'SubversionSCM',
    locations: [[
      credentialsId: 'vital.lobachevskij-wrf-svn',
      depthOption: 'files',
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

private  void buildJS() {
    bat 'ant BuildJS'
}

private void updateDB() {
    //bat './updateDB_Sprint.bat localhost sa Admin1234' //TODO: move to global vars
    //bat './updateDB.bat zeyt sa silver1i'
    //bat './runZeytSQL.bat localhost sa Admin1234 sql\\DBUpdateCurrentSprint.txt'
    bat "java -showversion -Xms512m -Xmx1024m -Xss1m -classpath \".\\web\\WEB-INF\\classes;.\\web\\WEB-INF\\lib\\*; \" RunSQL delay=0 output.result=0 output.sql=0 error.handling=EXIT output.verbose=1 uri=jdbc:sqlserver://$dbServerName:$dbServerPort;DatabaseName=$dbName;encrypt=false user=$dbUserName password=$dbUserPass input.file=sql\\DBUpdateCurrentSprint.txt  jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver"
    bat "java -showversion -Xms512m -Xmx1024m -Xss1m -classpath \".\\web\\WEB-INF\\classes;.\\web\\WEB-INF\\lib\\*; \" RunSQL delay=0 output.result=0 output.sql=0 error.handling=EXIT output.verbose=1 uri=jdbc:sqlserver://$dbServerName:$dbServerPort;DatabaseName=$dbName;encrypt=false user=$dbUserName password=$dbUserPass input.file=sql\\DBUpdate.txt  jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver"
}

private void packageZip() {
    bat 'ant -Dpackage.destination=D:\\Temp\\wfr-artifactory PackageWeb'
}

private void deployPackage(nodeName) {
    //echo 'Deployed package on $nodeName'
    bat 'ant -f zeyt/build.xml -Dpackage.destination=\\\\10.0.2.2\\wfr-artifactory -Dpackage.deploy.path=. DeployWeb'
}


/*
if( $VALUE1 == $VALUE2 ) {
   currentBuild.result = 'SUCCESS'
   return
}
*/
