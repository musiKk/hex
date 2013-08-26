package com.github.musikk.hex;

import java.awt.Color;

public abstract class RangeMarker extends AbstractMarker {

	private long byteStart = -1;
	private long byteEnd = -1;
	private int priority;
	private final Color color;
	private final Color backgroundColor;
	private boolean singleByte;

	public RangeMarker(Color color) {
		this(color, null);
	}
	public RangeMarker(Color color, Color backgroundColor) {
		this.color = color;
		this.backgroundColor = backgroundColor;
	}
	public void setByteStart(long byteStart) {
		if (byteStart == this.byteStart) {
			return;
		}
		if (singleByte) {
			this.byteEnd = byteStart;
		}
		this.byteStart = byteStart;
		fireEvent();
	}
	public long getByteStart() {
		return byteStart;
	}
	public void setByteEnd(long byteEnd) {
		if (byteEnd == this.byteEnd) {
			return;
		}
		if (singleByte) {
			this.byteStart = byteEnd;
		}
		this.byteEnd = byteEnd;
		fireEvent();
	}
	public long getByteEnd() {
		return byteEnd;
	}
	public void setByteStartEnd(long byteStart, long byteEnd) {
		if (singleByte && byteStart != byteEnd) {
			throw new IllegalArgumentException(String.format("Single byte mode is active " +
					"but start and end differ (start: %d, end: %d).",byteStart, byteEnd));
		}
		boolean fire = false;
		if (byteStart != this.byteStart) {
			this.byteStart = byteStart;
			fire = true;
		}
		if (byteEnd != this.byteEnd) {
			this.byteEnd = byteEnd;
			fire = true;
		}
		if (fire) {
			fireEvent();
		}
	}
	public int getPriority() {
		return priority;
	}
	public Color getColor() {
		return color;
	}
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	public void setSingleByte(boolean singleByte) {
		this.singleByte = singleByte;
	}
	public void invalidate() {
		byteStart = -1;
		byteEnd = -1;
		fireEvent();
	}
	public boolean isInvalid() {
		return byteStart == -1 && byteEnd == -1;
	}
}
