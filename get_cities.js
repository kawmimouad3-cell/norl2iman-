import https from 'https';

https.get('https://habous.gov.ma/prieres/prieres.php', (res) => {
  let data = '';
  res.on('data', (chunk) => { data += chunk; });
  res.on('end', () => {
    const regex = /<option\s+value="(\d+)">([^<]+)<\/option>/g;
    let match;
    while ((match = regex.exec(data)) !== null) {
      console.log(`"${match[2].trim()}": ${match[1]},`);
    }
  });
}).on('error', (err) => {
  console.log('Error: ' + err.message);
});
