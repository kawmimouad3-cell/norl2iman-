import https from 'https';

https.get('https://api.aladhan.com/v1/timings?latitude=33.5731&longitude=-7.5898&method=21', (res) => {
  let data = '';
  res.on('data', (chunk) => { data += chunk; });
  res.on('end', () => {
     console.log(data);
  });
}).on('error', (err) => {
  console.log('Error: ' + err.message);
});
