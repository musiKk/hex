package com.github.musikk.hex;

public class ByteArrayDataProvider implements DataProvider {

	private final byte[] data;

	public ByteArrayDataProvider(byte[] data) {
		this.data = data;
	}

	@Override
	public long getLength() {
		return data.length;
	}

	@Override
	public int get(byte[] dst, long offset) {
		if (offset > data.length) {
			return -1;
		}
		int bytesToCopy = (int) (dst.length > data.length - offset ? data.length - offset : dst.length);
		System.arraycopy(data, (int) offset, dst, 0, bytesToCopy);
		return bytesToCopy;
	}

}
