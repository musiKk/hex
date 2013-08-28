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
			return file.read(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
