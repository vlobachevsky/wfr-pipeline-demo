if ($env:PROCESSOR_ARCHITEW6432 -eq "AMD64") {
 Write-Log -level warning "changing from 32bit to 64bit powershell"
 $powershell=join-path $PSHOME.tolower().replace("syswow64","sysnative").replace("system32","sysnative") powershell.exe
 if ($myInvocation.Line) {
 &"$powershell" -NonInteractive -NoProfile -ExecutionPolicy Bypass $myInvocation.Line
 } else {
 &"$powershell" -NonInteractive -NoProfile -ExecutionPolicy Bypass -file "$($myInvocation.InvocationName)" $args
 }
 exit $lastexitcode
}
Set-WebConfiguration system.webServer/httpRedirect "IIS:\sites\Default Web Site" -Value @{enabled="true";destination="/maintenance/index.html";exactDestination="true";httpResponseStatus="Temporary"}
iisreset

$tomcatService = Get-Service Tomcat7
if($tomcatService.Status -ne "Stopped") {
  if ((Get-Process tomcat7).Responding) {
     (Get-WmiObject -computerName localhost  Win32_Service -Filter "Name='Tomcat7'").StopService() | out-null
     Write-Host "Tomcat has been stopped."
   } else {
      kill -processname tomcat7
      Write-Host "Tomcat has been killed."
   }
} else {
Write-Host "Tomcat is not running. Nothing to stop."
}

$tc7pr =  (get-process Tomcat7).id
stop-process -id $tc7pr
wait-process -id $tc7pr
