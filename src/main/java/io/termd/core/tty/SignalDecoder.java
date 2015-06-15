package io.termd.core.tty;

import java.util.function.Consumer;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class SignalDecoder implements Consumer<int[]> {

  private Consumer<int[]> readHandler;
  private Consumer<Signal> signalHandler;
  private final int vintr;

  public SignalDecoder(int vintr) {
    this.vintr = vintr;
  }

  public Consumer<int[]> getReadHandler() {
    return readHandler;
  }

  public SignalDecoder setReadHandler(Consumer<int[]> readHandler) {
    this.readHandler = readHandler;
    return this;
  }

  public Consumer<Signal> getSignalHandler() {
    return signalHandler;
  }

  public SignalDecoder setSignalHandler(Consumer<Signal> signalHandler) {
    this.signalHandler = signalHandler;
    return this;
  }

  @Override
  public void accept(int[] data) {
    if (signalHandler != null) {
      for (int i = 0;i < data.length;i++) {
        if (data[i] == vintr) {
          if (signalHandler != null) {
            if (readHandler != null) {
              int[] a = new int[i];
              if (i > 0) {
                System.arraycopy(data, 0, a, 0, i);
                readHandler.accept(a);
              }
            }
            signalHandler.accept(Signal.INT);
            int[] a = new int[data.length - i - 1];
            System.arraycopy(data, i + 1, a, 0, a.length);
            data = a;
            i = 0;
          }
        }
      }
    }
    if (readHandler != null && data.length > 0) {
      readHandler.accept(data);
    }
  }
}
