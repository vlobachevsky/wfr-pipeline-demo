#!/usr/bin/env groovy

import java.text.MessageFormat

// Prerequisites:
// 1. Tomcat 7.0.7 installed at C:\Tomcat\apache-tomcat
// 2. Eclipse Compliler jar (ecj-4.4) in ANT_HOME/lib

// TODO:
// * Move SKIP_ACCEPTANCE_STAGE param to job params
// * Parametrize the build with Labes (set of Jenkins nodes)
// * Move Publish step to very end of pipeline
// * Modify build.xml to append BUILD_ID to Zeyt.zip on PackageWeb task
// * Resolve issue with coping properties files. Should be build parameters for this.
// * Send notification before the build
// * Find a way to skip Update DB if there are no updates in db scripts
// * This pipeline should be general for any env (MainDev, HF, ...)

// BENEFITS
// * Don't need to copy System.properties and Connection.properties on server
// * Run several pipelines in parallel
// * Rapid feedback
// * QA can deploy anytime anybuild
// * QA can deploy to particular server (AP01, MW01, MW02)
// * Deploy in DEV env and REST tests can be skipped

// REQUIREMENTS
// * Should be possible to refresh MW02 separatelly without restarting MainDev

//properties([[$class: 'BuildBlockerProperty', blockLevel: <object of type hudson.plugins.buildblocker.BuildBlockerProperty.BlockLevel>, blockingJobs: '', scanQueueFor: <object of type hudson.plugins.buildblocker.BuildBlockerProperty.QueueScanScope>, useBuildBlocker: false], parameters([booleanParam(defaultValue: false, description: 'Skips Deploy DEV and REST Automated Tests stages in the pipeline. Build marked as UNSTABLE in any case.', name: 'SKIP_ACCEPTANCE_STAGE')]), pipelineTriggers([])])


properties([
  parameters([
    booleanParam(name: 'SKIP_ACCEPTANCE_STAGE', defaultValue: false, description: 'Skips Deploy DEV and REST Automated Tests stages in the pipeline. Build marked as UNSTABLE in any case.', )
   ])
])


dbServerName = env.DB_SERVER_NAME ?: 'localhost'
dbServerPort = env.DB_SERVER_PORT ?: '1433'
dbName = env.DB_NAME ?: 'zeyt'
dbUserName = env.DB_USER_NAME ?: 'sa'
dbUserPass = env.DB_USER_PASS ?: 'Admin1234'
skipAcceptanceStage = params.SKIP_ACCEPTANCE_STAGE
svnCredentialsId = 'vital.lobachevskij-wrf-svn'
svnRootURL = 'svn://kap-wfr-svn.int.kronos.com'


node('master') {
    echo "DEPLOY_ENV: $params.DEPLOY_ENV"
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

    stage('Deploy DEV') {
        if (!skipAcceptanceStage) {
            //packageZip('D:\\Temp\\wfr-artifactory')
            stash name: "zeyt-web", includes: "/reports/**,/sql/**,/web/**,/config/**,/quizzes/**,/tutorials/**"
            node('master') {
                deploy('localhost')
            }
        }
    }

    stage('REST Automated Tests') {
        if(!skipAcceptanceStage) {
            ws('C:\\TA\\zeyt') {
                dir('test-api') {
                    checkoutSVN(svnCredentialsId, "$svnRootURL/zeyt/test-api")
                }
                dir('../test_api') {
                    checkoutSVN(svnCredentialsId, "$svnRootURL/test_api")
                    bat "ant -f build.xml -DBaseUrl=http://127.0.0.1:8080 -Dreport.dir=../report TestRestApi"
                }
            }
        }
    }


    stage('Publish') {
        packageZip('D:\\Temp\\wfr-artifactory')
    }

    if(skipAcceptanceStage) {
        currentBuild.result = 'UNSTABLE'
    }

}

def deploy(dbHost) {
    ws('C:\\TA\\zeyt') {
        dir('scripts') {
            syncPsScripts()
        }
        // Stop Tomcat
        powerShell(". '.\\scripts\\stop-tomcat.ps1'")
        checkoutSVN(svnCredentialsId, "$svnRootURL/zeyt", 'files')
        //deployPackage('\\\\localhost\\wfr-artifactory')
        unstash "zeyt-web"
//        updateDB()
        copySystemFiles(dbHost);
        // Start Tomcat
        powerShell(". '.\\scripts\\start-tomcat.ps1'")
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

def packageZip(toPath) {
    bat "ant -Dpackage.destination=$toPath PackageWeb"
}

def deployPackage(fromPath) {
    //echo 'Deployed package on $nodeName'
    bat "ant -f build.xml -Dpackage.destination=$fromPath -Dpackage.deploy.path=. DeployWeb"
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
