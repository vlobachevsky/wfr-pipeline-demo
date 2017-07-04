node {
  stage('Build') {
    syncRepo()
    compileApp()
    runJUnitTests()
    buildJS()
    packageZip()
  }
}

private void syncRepo() {
  echo '0. Sync repo'
  checkout([$class: 'SubversionSCM', remote: 'svn://kap-wfr-svn.int.kronos.com/zeyt', credentialsId: 'vlobachevsky-github'])
}

private void compileApp() {
  echo '1. Compile application'
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
