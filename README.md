# cli-asterisk-ami-events-listener
```
Version 1.0.002
Console utility for Asterisk AMI protocol interaction.
Allows to execute AMI commands and subscribe to certain AMI events.
usage: java -jar cliAmi.jar <options>
 -e,--execute <arg>   Execute commands. Example: DND for 9001 =>  -e
                      *78#9001
 -f,--filter <arg>    Javascript boolean expression to filter events
                      Example:  -f '/*any*/$v==='9001' &&
                      $n.endsWith('Num')'
                      Where $n - field name (String), $v - field value
                      (String).
                      Expression must return boolean value. Expression may
                      start with '/*any*/', '/*none*/', '/*all*/'
                      any - any match (default), all - all match, none -
                      none match
 -h,--host <arg>      Ami server ip/name. Default - localhost
 -l,--listen <arg>    Subscribe on events:
                      (system,call,log,verbose,command,agent,user,config,c
                      ommand,dtmf,reporting,cdr,dialplan,originate,message
                      )
                      Example: '-s call,cdr'. Default: system
 -m,--macros <arg>    File with macroses for star commands like
                      '*79#<ext>'
 -p,--port <arg>      Ami server port. Default - 5038
 -s,--secret <arg>    Manager password from manager.conf.
 -u,--user <arg>      Manager username from manager.conf. Default - admin
----------------------
Default macroses:
*78 #Enables the do not disturb feature.
Action: DBPut
Family: DND
Key: ${0}
Val: YES

*79 #Disables the do not disturb feature.
Action: DBPut
Family: DND
Key: ${0}
Val: NO

```
