import crypto from 'node:crypto';

export function normalizeKey(keyInput: string): Buffer {
  try {
    const kb = Buffer.from(keyInput, 'base64');
    if ([16, 24, 32].includes(kb.length)) return kb;
  } catch (e) {
    console.warn('Key is not valid base64, falling back to utf-8');
  }
  // fallback to utf-8 bytes
  const kb = Buffer.from(keyInput, 'utf8');
  if (![16, 24, 32].includes(kb.length)) {
    throw new Error('Key must be 16/24/32 bytes (after base64-decoding if encoded).');
  }
  return kb;
}

export function pkcs7Unpad(buf: Buffer): Buffer {
  const pad = buf[buf.length - 1];
  if (pad < 1 || pad > 16) throw new Error('Invalid PKCS7 padding');
  for (let i = 0; i < pad; i++) {
    if (buf[buf.length - 1 - i] !== pad) throw new Error('Invalid PKCS7 padding bytes');
  }
  return buf.slice(0, buf.length - pad);
}

export function decryptCryptString(cipherB64: string, keyInput: any): string {
  const key = normalizeKey(keyInput);
  const ct = Buffer.from(cipherB64, 'base64');
  const iv = Buffer.alloc(16, 0);
  const decipher = crypto.createDecipheriv(`aes-${key.length * 8}-cbc`, key, iv);
  decipher.setAutoPadding(false);
  let pt = Buffer.concat([decipher.update(ct), decipher.final()]);
  pt = pkcs7Unpad(pt);
  // remove last check character byte
  if (pt.length === 0) return '';
  return pt.slice(0, -1).toString('utf8');
}
