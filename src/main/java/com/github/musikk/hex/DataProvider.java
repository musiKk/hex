package com.github.musikk.hex;

public interface DataProvider {

	long getLength();
	int get(byte[] data, long offset);

}
