node {
  stage('Build') {
    syncRepo()

    env.PATH = "${tool 'Ant-Default'}\\bin;${env.PATH}"
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
  //echo '1. Compile application'
  bat 'ant -version'
}

private void runJUnitTests() {
  echo '2. Run JUnit tests'
}

private void buildJS() {
  echo '3. Build JS'
}

private void packageZip() {
  echo '4. PackageZip'
}
