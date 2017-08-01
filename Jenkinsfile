#!/usr/bin/env groovy

import java.text.MessageFormat

// Prerequisites:
// 1. Tomcat 7.0.7 installed at C:\Tomcat\apache-tomcat
// 2. Eclipse Compliler jar (ecj-4.4) in ANT_HOME/lib

// TODO:
// * Fix "Could not make new DB connection" on vagrant-node-1
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
//        stash name: "zeyt-web", includes: "/reports/**,/sql/**,/web/**,/config/**,/quizzes/**,/tutorials/**"
    }

    stage('Update DB') {
//        updateDB()
    }

    stage('Deploy') {
        node('win-node-1') {
            ws('C:\\TA\\zeyt') {
                dir('scripts') {
//                    syncPsScripts()
                }
                // Stop Tomcat
//                powerShell(". '.\\scripts\\stop-tomcat.ps1'")
//                syncBuildScript()
 //               unstash "zeyt-web"

                // Override the property files
                //setProperty('System.properties', '*.username', 'sa')
                //setProperty('System.properties', '*.url', '10.0.2.2')
                //setProperty('.\\config\\Connections.properties', '*.password', 'c61baf0b2828776509c9915b670a03b8')

/*
                writeFile file: 'System.properties', text: '''
DBPool.ReadOnly.url=jdbc:sqlserver://{0}:1433;DatabaseName=ZEYT;encrypt=false
DBPool.ReadOnly.username=sa
DBPool.System.url=jdbc:sqlserver://10.0.2.2:1433;DatabaseName=ZEYT;encrypt=false
DBPool.System.username=sa
DBPool.Main.url=jdbc:sqlserver://10.0.2.2:1433;DatabaseName=ZEYT;encrypt=false
DBPool.Main.username=sa
DBPool.Main.supportQueryTimeout=false
DBPool.Reports.url=jdbc:sqlserver://10.0.2.2:1433;DatabaseName=ZEYT;encrypt=false
DBPool.Reports.username=sa
DBPool.ScheduledReports.url=jdbc:sqlserver://10.0.2.2:1433;DatabaseName=ZEYT;encrypt=false
DBPool.ScheduledReports.username=sa
pswd.path=./config/Connections.properties
'''
                writeFile file: '.\\config\\Connections.properties', text: '''
DBPool.ReadOnly.password=c61baf0b2828776509c9915b670a03b8
DBPool.System.password=c61baf0b2828776509c9915b670a03b8
DBPool.Main.password=c61baf0b2828776509c9915b670a03b8
DBPool.Reports.password=c61baf0b2828776509c9915b670a03b8
DBPool.ScheduledReports.password=c61baf0b2828776509c9915b670a03b8
'''
*/
                //File propsFile = new File('System.properties')
                def content = readFile 'System.properties'
                InputStream is = new ByteArrayInputStream(content.getBytes());
                def props = new Properties()
                props.load(is)
                // new File("${WORKSPACE}\\System.properties").withInputStream {
                //     props.load(it)
                // }
                def output = MessageFormat.format((String) props.get("DBPool.ReadOnly.url"), "XXX")
                echo "Output: $output"

                // Start Tomcat
//                powerShell(". '.\\scripts\\start-tomcat.ps1'")
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

private void syncPsScripts() {
    git url: 'https://github.com/vlobachevsky/wfr-devops-scripts.git', credentialsId: 'vlobachevsky-github'
}

private void stopTomcat() {
    powerShell(". '.\\scripts\\stop-tomcat.ps1'")
}

private def powerShell(psCmd) {
    bat "powershell.exe -NonInteractive -ExecutionPolicy Bypass -Command \"\$ErrorActionPreference='Stop';[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;$psCmd;EXIT \$global:LastExitCode\""
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
