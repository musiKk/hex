package com.github.musikk.hex;

import java.awt.Color;
import java.awt.Graphics2D;

import com.github.musikk.hex.HexPanel.Metrics;

/**
 * {@link Marker} implementation that provides support for a range of bytes in a
 * {@link com.github.musikk.hex.DataProvider DataProvider}. The range is
 * inclusive.
 *
 * @author Werner Hahn
 *
 */
public abstract class RangeMarker extends AbstractMarker {

	private long byteStart = -1;
	private long byteEnd = -1;
	private int priority;
	private final Color color;
	private final Color backgroundColor;
	private boolean singleByte;

	/**
	 * Creates a {@code RangeMarker} with the specified {@code color}. The
	 * background color is unspecified.
	 *
	 * @param color
	 */
	public RangeMarker(Color color) {
		this(color, null);
	}

	/**
	 * Creates a {@code RangeMarker} with the specified colors.
	 *
	 * @param color
	 * @param backgroundColor
	 */
	public RangeMarker(Color color, Color backgroundColor) {
		this.color = color;
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Sets the start of the range. If this marker is in single byte mode, the
	 * end is set to the same value. If the marker got updated, the listeners
	 * are notified of the change.
	 *
	 * @param byteStart
	 *            the new start of the range
	 */
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

	/**
	 * Sets the end of the range. If this marker is in single byte mode, the
	 * start is set to the same value. If the marker got updated, the listeners
	 * are notified of the change.
	 *
	 * @param byteEnd
	 *            the new end of the range
	 */
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

	/**
	 * Sets the start and the end of the range. IF the marker got updated, the
	 * listeners are notified of the change.
	 *
	 * @param byteStart
	 *            the new start of the range
	 * @param byteEnd
	 *            the new end of the range
	 * @throws IllegalArgumentException
	 *             if single byte mode is active and {@code byteStart} and
	 *             {@code byteEnd} differ
	 * @throws IllegalArgumentException
	 *             if one of the arguments is -1 but the other is not
	 */
	public void setByteStartEnd(long byteStart, long byteEnd) {
		if (singleByte && byteStart != byteEnd) {
			throw new IllegalArgumentException(String.format("Single byte mode is active " +
					"but start and end differ (start: %d, end: %d).",byteStart, byteEnd));
		}
		if (byteStart == -1 && byteEnd != -1 || byteStart != -1 && byteEnd == -1) {
			throw new IllegalArgumentException(String.format("Both or none of the arguments " +
					"must be -1 (start: %d, end: %d).", byteStart, byteEnd));
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

	/**
	 * Sets single byte mode. In single byte mode, start and end of this marker
	 * are synchronized to ensure that only one byte is ever selected. If single
	 * byte mode is enabled and start and end of this range differ, the end of
	 * the range is assigned the value of the start and listeners are notified
	 * accordingly.
	 *
	 * @param singleByte
	 *            whether single byte mode should be used
	 */
	public void setSingleByte(boolean singleByte) {
		if (singleByte != this.singleByte) {
			this.singleByte = singleByte;
			setByteEnd(byteStart);
		}
	}

	/**
	 * Sets the values of start and end to -1 to signal that this marker is not
	 * to be shown.
	 */
	public void invalidate() {
		setByteStartEnd(-1, -1);
	}

	/**
	 * Checks whether the marker is invalid. A marker is invalid iff both start
	 * and end are -1.
	 *
	 * @return whether the marker is invalid
	 */
	public boolean isInvalid() {
		return byteStart == -1 && byteEnd == -1;
	}

	@Override
	public final void paint(Graphics2D g2, Metrics metrics) {
		if (isInvalid()) {
			return;
		}
		long first = metrics.getOffset();
		long last = first + metrics.getLines() * metrics.getLineLength();

		long rangeStart = getByteStart();
		long rangeEnd = getByteEnd();
		if (rangeStart > rangeEnd) {
			long h = rangeStart;
			rangeStart = rangeEnd;
			rangeEnd = h;
		}
		if (rangeEnd < first || rangeStart > last) {
			return;
		}
		paintRangeMarker(g2, metrics);
	}

	protected abstract void paintRangeMarker(Graphics2D g2, Metrics metrics);

}
