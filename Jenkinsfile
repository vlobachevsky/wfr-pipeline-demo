node {
  env.PATH = "${tool 'Ant-1.9.6'}\\bin;${env.PATH}"

  stage('Build') {
    //syncRepo()
    compileApp()
    runJUnitTests()
    buildJS()
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
  echo '3. Build JS'
}

private void packageZip() {
  echo '4. PackageZip'
}
