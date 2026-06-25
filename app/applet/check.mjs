import https from 'https';
https.request("https://github.com/aliftype/amiri/raw/main/fonts/AmiriQuran-Regular.ttf", {method: "HEAD"}, (res) => { console.log(res.statusCode); }).end();
https.request("https://github.com/hatemz/KFGQPC-Uthmanic-Script-HAFS/raw/master/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf", {method: "HEAD"}, (res) => { console.log(res.statusCode); }).end();
