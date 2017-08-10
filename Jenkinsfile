#!/usr/bin/env groovy

import java.text.MessageFormat

// Prerequisites:
// 1. Tomcat 7.0.7 installed at C:\Tomcat\apache-tomcat
// 2. Eclipse Compliler jar (ecj-4.4) in ANT_HOME/lib

// TODO:
// * First pipeline has parameter to skip/ignore REST test
// * Modify build.xml to append BUILD_ID to Zeyt.zip on PackageWeb task
// * Fix "Could not make new DB connection" on vagrant-node-1
// * Use custom workspace
// * Resolve issue with coping properties files. Should be build parameters for this.
// * Send notification before the build
// * Parametrize build: Skip Db Update; Select servers for deploy
// * Find a way to skip Update DB if there are no updates in db scripts

// BENEFITS
// * Don't need to copy System.properties and Connection.properties on server

// REQUIREMENTS
// * Should be possible to refresh MW02 separatelly without restarting MainDev

dbServerName = env.DB_SERVER_NAME ?: 'localhost'
dbServerPort = env.DB_SERVER_PORT ?: '1433'
dbName = env.DB_NAME ?: 'zeyt'
dbUserName = env.DB_USER_NAME ?: 'sa'
dbUserPass = env.DB_USER_PASS ?: 'Admin1234'

svnCredentialsId = 'vital.lobachevskij-wrf-svn'
svnRootURL = 'svn://kap-wfr-svn.int.kronos.com'

node('master') {
    env.PATH = "${tool 'Ant-1.9.6'}\\bin;${tool 'NodeJS v6'};${env.PATH}"

    stage('Build') {
        echo 'Building...'
        checkoutSVN(svnCredentialsId, "$svnRootURL/zeyt")
/*        parallel (
            "build-java" : {
                compileApp()
            },
            "build-js" : {
                buildJS()
            }
        )
*/
    }

    stage('Unit Tests') {
        echo 'Testing...'
//        runJUnitTests()
        // TODO: Try use splitTest to automatically split your test suite into
        // equal running parts that it can run concurrently.
    }

    stage('Publish') {
        packageZip()
        //        stash name: "zeyt-web", includes: "/reports/**,/sql/**,/web/**,/config/**,/quizzes/**,/tutorials/**"
    }

    stage('Deploy DEV') {
        node('master') {
            deploy('localhost')
        }
    }

    stage('REST Automated Tests') {
        echo 'Running REST Tests...'
        ws('C:\\TA\\zeyt') {
            dir('test-api') {
                checkoutSVN(svnCredentialsId, "$svnRootURL/zeyt/test-api")
            }
            dir('../test-api') {
                checkoutSVN(svnCredentialsId, "$svnRootURL/test-api")
            }
        }
    }
}

def checkoutSVN(credentialsId, url, depthOption='infinity') {
  checkout([
    $class: 'SubversionSCM',
    locations: [[
      credentialsId: "$credentialsId",
      depthOption: "$depthOption",
      ignoreExternalsOption: true,
      local: '.',
      remote: "$url"
    ]],
    workspaceUpdater: [$class: 'UpdateWithRevertUpdater']
  ])
}

def syncPsScripts() {
    git url: 'https://github.com/vlobachevsky/wfr-devops-scripts.git', credentialsId: 'vlobachevsky-github'
}

def powerShell(psCmd) {
    bat "powershell.exe -NonInteractive -ExecutionPolicy Bypass -Command \"\$ErrorActionPreference='Stop';[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;$psCmd;EXIT \$global:LastExitCode\""
}

def copySystemFiles(dbHost) {
    writeFile file: 'System.properties', text: """
DBPool.ReadOnly.url=jdbc:sqlserver://${dbHost}:1433;DatabaseName=ZEYT;encrypt=false
DBPool.ReadOnly.username=sa
DBPool.System.url=jdbc:sqlserver://${dbHost}:1433;DatabaseName=ZEYT;encrypt=false
DBPool.System.username=sa
DBPool.Main.url=jdbc:sqlserver://${dbHost}:1433;DatabaseName=ZEYT;encrypt=false
DBPool.Main.username=sa
DBPool.Main.supportQueryTimeout=false
DBPool.Reports.url=jdbc:sqlserver://${dbHost}:1433;DatabaseName=ZEYT;encrypt=false
DBPool.Reports.username=sa
DBPool.ScheduledReports.url=jdbc:sqlserver://${dbHost}:1433;DatabaseName=ZEYT;encrypt=false
DBPool.ScheduledReports.username=sa
pswd.path=./config/Connections.properties
"""
    writeFile file: '.\\config\\Connections.properties', text: """
DBPool.ReadOnly.password=c61baf0b2828776509c9915b670a03b8
DBPool.System.password=c61baf0b2828776509c9915b670a03b8
DBPool.Main.password=c61baf0b2828776509c9915b670a03b8
DBPool.Reports.password=c61baf0b2828776509c9915b670a03b8
DBPool.ScheduledReports.password=c61baf0b2828776509c9915b670a03b8
"""
}

def compileApp() {
    //bat 'ant -Dpackage.destination=D:\\Temp\\wfr-artifactory BuildEclipseCompiler JUnit'
    bat 'ant BuildEclipseCompiler'
}

def runJUnitTests() {
    bat 'ant JUnit'
}

def buildJS() {
    bat 'ant BuildJS'
}

def updateDB() {
    //bat './updateDB_Sprint.bat localhost sa Admin1234' //TODO: move to global vars
    //bat './updateDB.bat zeyt sa silver1i'
    //bat './runZeytSQL.bat localhost sa Admin1234 sql\\DBUpdateCurrentSprint.txt'
    bat "java -showversion -Xms512m -Xmx1024m -Xss1m -classpath \".\\web\\WEB-INF\\classes;.\\web\\WEB-INF\\lib\\*; \" RunSQL delay=0 output.result=0 output.sql=0 error.handling=EXIT output.verbose=1 uri=jdbc:sqlserver://$dbServerName:$dbServerPort;DatabaseName=$dbName;encrypt=false user=$dbUserName password=$dbUserPass input.file=sql\\DBUpdateCurrentSprint.txt  jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver"
    bat "java -showversion -Xms512m -Xmx1024m -Xss1m -classpath \".\\web\\WEB-INF\\classes;.\\web\\WEB-INF\\lib\\*; \" RunSQL delay=0 output.result=0 output.sql=0 error.handling=EXIT output.verbose=1 uri=jdbc:sqlserver://$dbServerName:$dbServerPort;DatabaseName=$dbName;encrypt=false user=$dbUserName password=$dbUserPass input.file=sql\\DBUpdate.txt  jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver"
}

def packageZip() {
    bat 'ant -Dpackage.destination=D:\\Temp\\wfr-artifactory PackageWeb'
}

def deployPackage(fromPath) {
    //echo 'Deployed package on $nodeName'
    bat "ant -f build.xml -Dpackage.destination=$fromPath -Dpackage.deploy.path=. DeployWeb"
}

def deploy(dbHost) {
    ws('C:\\TA\\zeyt') {
        dir('scripts') {
            syncPsScripts()
        }
        // Stop Tomcat
        powerShell(". '.\\scripts\\stop-tomcat.ps1'")
        checkoutSVN(svnCredentialsId, "$svnRootURL/zeyt", 'files')
//        unstash "zeyt-web"
        deployPackage('\\\\localhost\\wfr-artifactory')
//        updateDB()
        copySystemFiles(dbHost);
        // Start Tomcat
        powerShell(". '.\\scripts\\start-tomcat.ps1'")
    }
}

/*
private void setProperty(propsFile, pattern, Object... args) {
    // Examples:
    // setProperty('System.properties', '*.username', 'sa')
    // setProperty('System.properties', '*.url', '10.0.2.2')
    // setProperty('.\\config\\Connections.properties', '*.password', 'c61baf0b2828776509c9915b670a03b8')

    def content = readFile file: propsFile
    def props = new Properties()
    props.load(new ByteArrayInputStream(content.getBytes()))
    // 1) Loop throught all props
    // 2) If it matches the patterns, replace placeholder using code like this
    //    def output = MessageFormat.format((String) props.get("DBPool.ReadOnly.url"), args)
    // 3) Append the output
    // 4) Write all in the file with the same name as $propsFile
}
*/


/*
if( $VALUE1 == $VALUE2 ) {
   currentBuild.result = 'SUCCESS'
   return
}
*/
