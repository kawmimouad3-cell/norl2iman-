import fs from 'fs';
import https from 'https';

const fontDir = '/app/src/main/res/font';
if (!fs.existsSync(fontDir)) fs.mkdirSync(fontDir, { recursive: true });

const url = "https://raw.githubusercontent.com/google/fonts/main/ofl/amiriquan/AmiriQuran-Regular.ttf";
const file = fs.createWriteStream(fontDir + '/quran_font.ttf');

https.get(url, (response) => {
  response.pipe(file);
  file.on('finish', () => {
    file.close();
    console.log("Download complete");
  });
}).on('error', (err) => {
  console.error("Error: ", err.message);
});
