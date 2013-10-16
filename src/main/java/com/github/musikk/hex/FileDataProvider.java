package com.github.musikk.hex;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileDataProvider implements DataProvider {

	private final RandomAccessFile file;
	private final long fileLength;

	public FileDataProvider(File file) throws IOException {
		this.file = new RandomAccessFile(file, "r");
		this.fileLength = file.length();
	}

	@Override
	public long getLength() {
		return fileLength;
	}

	@Override
	public int get(byte[] data, long offset) {
		try {
			file.seek(offset);

			int read = 0;
			while (read < data.length) {
				byte[] buf = new byte[Math.min(2048, data.length - read)];
				int r = file.read(buf);
				if (r < 0) {
					return read;
				}
				System.arraycopy(buf, 0, data, read, r);
				read += r;
			}
			return read;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
