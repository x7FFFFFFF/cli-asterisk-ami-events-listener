# cli-asterisk-ami-events-listener
```
Version 1.0.3
Console utility for Asterisk AMI protocol interaction.
Allows to execute AMI commands and subscribe to certain AMI events.
usage: java -jar cliAmi.jar <options>
 -e,--execute <arg>   Execute commands. Example: DND for 9001 =>  -e
                      *78#9001
 -f,--filter <arg>    Javascript boolean expression to filter events.
                      Predefined variables:
                      '$msg' - event fields as map, '$str'- event fields
                      as string
                      Example:
                      Event: Newchannel
                      Privilege: call,all
                      Channel: SIP/4956616060-0001245a
                      ChannelState: 0'
                      For this event $msg = {Event:'Newchannel',
                      Privilege: 'call,all',
                      Channel:'SIP/4956616060-0001245a', ChannelState:'0'}
                      $msg also support uppercase and lowercase keys:
                      $msg.Event===$msg.event===$msg.EVENT
                      $msg has additional methods:
                      $msg.keysIncludes(<String|RegExp>)
                      $msg.valuesIncludes(<String|RegExp>)
                      Example:
                      show only events containing a number 88009009001:
                      java -jar cliAmi.jar -f
                      "$str.includes('88009009001')"
                      show only HangupRequest events containing a number
                      88009009001:
                      java -jar cliAmi.jar -f
                      "$str.includes('88009009001')&&$msg.Event==='HangupR
                      equest'"
                      show only events containing fields that has any
                      match the regular expression:
                      java -jar cliAmi.jar -f
                      "$msg.keysIncludes(/^.*IDNum.*$/)"
 -h,--host <arg>      Ami server ip/name. Default - localhost
 -l,--listen <arg>    Subscribe on events:
                      (system,call,log,verbose,command,agent,user,config,c
                      ommand,dtmf,reporting,cdr,dialplan,originate,message
                      )
                      Example: '-s call,cdr'. Default: all
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
