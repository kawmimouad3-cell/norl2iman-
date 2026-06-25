import fs from 'fs';
import https from 'https';

const fontDir = 'app/src/main/res/font';
if (!fs.existsSync(fontDir)) fs.mkdirSync(fontDir, { recursive: true });

async function download() {
  const res = await fetch("https://raw.githubusercontent.com/google/fonts/main/ofl/amiriquan/AmiriQuran-Regular.ttf");
  if (!res.ok) throw new Error("bad " + res.status);
  const buff = await res.arrayBuffer();
  fs.writeFileSync(fontDir + '/quran_font.ttf', Buffer.from(buff));
  console.log("Done", buff.byteLength);
}
download().catch(console.error);
