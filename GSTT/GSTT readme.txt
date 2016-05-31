GSTT readme by MFXL
--------------

********HOW TO START******** 

bat. or start all script in NUC 2

********HOW TO USE******** 

#STT#1#     -> listen for anything, no filter
#STT#0#     -> stop recording
#STT#name#  -> filter for name (can expand recognized name array)
#STT#yesno# -> filter for yes or no

counterLog.log: log the number of successful requests (SPEECH API limit, 50 requests per day, sometimes limit can be exceeded up to 250)

********SPEECH APIKEY******** 

1. Robert Account APIKEY: AIzaSyB6yR8DR6onz9YEBKkHmrLAOQZth5Vv2gs
2. Michelle: AIzaSyAtphCcVON9OU-URwD6jjqStwYtBNxK4oY
3. Marc: AIzaSyDe2nR4mdQYL74iwkZx5pOBM_3MVHNZS8c
4. Vignesh: AIzaSyCFhY2ogNV4iFX3Hg3EgGU5y9wGodmfLR8

********HOW TO AQUIRE APIKEY******** 

http://stackoverflow.com/questions/26485531/google-speech-api-v2

You have to be a member of chromium-dev discussion list before you can enable the Speech API in your Google Developers Console.

1. Go to Chromium Dev group and click Join the list. Do not post to the group regarding the Google Speech API, as it is completely off topic.

2. Go back to Google Developers Console, select your project, enter APIs & Auth / APIs. You'll now see Speech API. Click to enable it.

3. Go to Credentials, Create new Key, Server Key. You may optionally specify a list of IPs, for security.

You now may make queries to Google Speech API v2. Keep in mind that this is an experimental API, and limited to 50 queries per day per project.

********JAR FILE (JARVIS)********

https://github.com/lkuza2/java-speech-api 
***use Duplex: https://github.com/lkuza2/java-speech-api/wiki/Duplex---Hello-World
https://github.com/lkuza2/java-speech-api/releases
https://github.com/lkuza2/java-speech-api/issues/74

! created new JARVIS JAR FILE named JARVIS V2.5.2 

********JAR FILE (javaFlacEncoder-0.3.1-all)********

import javaFlacEncoder.FLACFileWriter; 
