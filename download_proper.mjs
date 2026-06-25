import fs from 'fs';

async function download() {
  const fontDir = 'app/src/main/res/font';
  if (!fs.existsSync(fontDir)) fs.mkdirSync(fontDir, { recursive: true });

  const res = await fetch("https://unpkg.com/kfgqpc-uthmanic-script-hafs-regular@1.0.0/arabic.otf");
  if (!res.ok) throw new Error("bad " + res.status);
  const buff = await res.arrayBuffer();
  fs.writeFileSync(fontDir + '/quran_font.otf', Buffer.from(buff));
  console.log("Done", buff.byteLength);
}
download().catch(console.error);
