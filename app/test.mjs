import https from 'https';

https.get('https://cdn.jsdelivr.net/npm/quran-json@3.1.2/dist/chapters/1.json', (res) => {
  let data = '';
  res.on('data', (chunk) => { data += chunk; });
  res.on('end', () => {
     console.log("Length:", data.length);
     console.log(data);
  });
}).on('error', (err) => {
  console.log('Error: ' + err.message);
});
