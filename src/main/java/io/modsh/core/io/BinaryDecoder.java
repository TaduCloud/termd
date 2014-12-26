package io.modsh.core.io;

import io.modsh.core.Handler;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class BinaryDecoder {

  final CharsetDecoder decoder;
  final ByteBuffer bBuf;
  final CharBuffer cBuf;
  final Handler<Integer> onChar;

  public BinaryDecoder(Charset charset, Handler<Integer> onChar) {
    decoder = charset.newDecoder();
    bBuf = ByteBuffer.allocate(4);
    cBuf = CharBuffer.allocate(2);
    this.onChar = onChar;
  }

  public void onByte(byte b) {
    bBuf.put(b);
    bBuf.flip();
    decoder.decode(bBuf, cBuf, false);
    cBuf.flip();
    switch (cBuf.remaining()) {
      case 0:
        break;
      case 1:
        char c = cBuf.get();
        onChar.handle((int) c);
        break;
      case 2:
        char high = cBuf.get();
        char low = cBuf.get();
        int codepoint = Character.toCodePoint(high, low);
        onChar.handle(codepoint);
        break;
      default:
        throw new AssertionError();
    }
    bBuf.compact();
    cBuf.compact();
  }
}
