import fs from 'fs';

async function download() {
  const res = await fetch("https://unpkg.com/kfgqpc-uthmanic-script-hafs-regular@1.0.0/");
  const text = await res.text();
  console.log(text);
}
download().catch(console.error);
