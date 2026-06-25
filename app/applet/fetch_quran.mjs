import https from 'https';

https.get('https://cdn.jsdelivr.net/npm/quran-json@3.1.2/dist/chapters/1.json', (res) => {
  let data = '';
  res.on('data', (chunk) => { data += chunk; });
  res.on('end', () => {
     console.log(data.substring(0, 500));
  });
}).on('error', (err) => {
  console.log('Error: ' + err.message);
});
