import https from 'https';

https.get('https://habous.gov.ma/prieres/prieres.php?ville=34', (res) => {
  let data = '';
  res.on('data', (chunk) => { data += chunk; });
  res.on('end', () => {
     console.log(data);
  });
}).on('error', (err) => {
  console.log('Error: ' + err.message);
});
