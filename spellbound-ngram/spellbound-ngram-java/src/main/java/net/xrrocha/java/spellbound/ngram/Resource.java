package net.xrrocha.java.spellbound.ngram;

import java.io.InputStream;
import java.io.OutputStream;

public interface Resource {
  InputStream getInputStream();
  OutputStream getOutputStream();
  long getLastModified();
}
