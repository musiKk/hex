package com.github.musikk.hex;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

/**
 * A panel that displays binary data byte-wise in hexadecimal and character form
 * side by side. It allows selecting text and setting markers.
 *
 * @author Werner Hahn
 *
 */
public class HexPanel extends JPanel {
	private final Collection<MetricsUpdatedListener> metricsUpdatedListeners
			= new ArrayList<>();

	private final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 24);

	/**
	 * The {@link Marker} that shows the current selection. A selection is
	 * created by a standard dragging operation.
	 */
	private final RangeMarker SELECTION = new SimpleBorderMarker(Color.BLUE);
	/**
	 * The {@link Marker} that highlights the currently hovered byte.
	 */
	private final RangeMarker HOVER = new SimpleBorderMarker(Color.BLACK);
	/**
	 * The {@link Metrics} object that is used to export layout data for this
	 * {@code HexPanel}.
	 */
	private final Metrics metrics;

	private final Collection<Marker> markers = new ArrayList<>();

	/**
	 * The data that can be shown in this {@code HexPanel}.
	 */
	private final DataProvider data;
	/**
	 * The slice of data from {@link #data} that is to be displayed. Its size
	 * depends on the {@link #offset}, the size of the panel and
	 * {@linkplain DataProvider#getLength() the length of the available data}.
	 */
	private byte[] bytes;
	/**
	 * The starting position of the data.
	 */
	private long offset;

	/**
	 * Line length in bytes. Multiply by two to get the total number of hex
	 * characters (nibbles).
	 */
	private int lineLength;
	/**
	 * The number of visible lines. This is a theoretical count; if the panel is
	 * larger than the available data, this count is greater than the actual
	 * number of lines.
	 */
	private int lines;

	/**
	 * The width of a hex character in pixels.
	 */
	private int charWidth;
	/**
	 * The height of a hex character in pixels and then some. Currently this
	 * includes enough space for the characters to appear visibly appealing with
	 * not too much {@link #lineGap line gap}.
	 */
	private int charHeight;
	/**
	 * The gap between individual lines in pixels.
	 */
	private int lineGap;

	/**
	 * The width of the address column in pixels.
	 */
	private int addressWidth;
	/**
	 * The gap between the address column and the hex column in pixels.
	 */
	private int addressHexGap;

	/**
	 * The gap that is shown between two bytes (four hex characters) in the hex
	 * column.
	 */
	private int twoByteGap;
	/**
	 * The x-coordinate that denotes the start of the hex column. This is
	 * {@link #addressWidth} + {@link #addressHexGap} and solely here for
	 * convenience.
	 */
	private int hexX;
	/**
	 * The width of the hex column in pixels.
	 */
	private int hexWidth/*, hexHeight*/;

	/**
	 * The gap between the hex column and the ASCII column in pixels.
	 */
	private int hexAsciiGap;
	/**
	 * The x-coordinate that denotes the start of the ASCII column. This is
	 * {@link #hexX} + {@link #hexWidth} and solely here for convenience.
	 */
	private int asciiX;

	/*
	 * Metrics are dependent on width and height. In order not to calculate them
	 * over and over again, they are put here for caching.
	 *
	 * Metrics also depend on the font but the font cannot be changed currently.
	 */
	private int lastWidth = -1;
	private int lastHeight = -1;

	/**
	 * Creates a new {@code HexPanel} that displays the specified {@code data}.
	 * The offset is initially zero.
	 *
	 * @param data
	 *            the data that is to be shown
	 */
	public HexPanel(DataProvider data) {

		HoverListener l = new HoverListener(HOVER, SELECTION);
		addMouseMotionListener(l);
		addMouseListener(l);
		this.markers.add(HOVER);
		this.markers.add(SELECTION);

		this.data = data;

		this.metrics = new Metrics();

		HOVER.setSingleByte(true);
		MarkerUpdatedListener mul = new MarkerUpdatedListener() {
			@Override
			public void markerUpdated(Marker marker) {
				HexPanel.this.repaint();
			}
		};
		HOVER.addListener(mul);
		SELECTION.addListener(mul);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(font);

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.BLACK);

		calculateMetrics(g2);

		drawMarkers(g2);
		drawHexLetters(g2);
	}

	private void drawMarkers(Graphics2D g2) {
		for (Marker marker : markers) {
			drawMarker(g2, marker);
		}
	}

	private void drawMarker(Graphics2D g2, Marker marker) {
		marker.paint(g2, metrics);
	}

	private void drawHexLetters(Graphics2D g2) {
		int xBase = hexX;
		int asciiXBase = this.asciiX;
		int x = xBase;
		int asciiX = asciiXBase;
		int y = charHeight;
		int nibble = 0;
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];

			int high = (b >> 4) & 0x0F;
			int low = b & 0x0F;

			char charHigh = "0123456789ABCDEF".charAt(high);
			char charLow = "0123456789ABCDEF".charAt(low);

			drawNibble(g2, x, y, nibble++, charHigh);
			x += charWidth;
			drawNibble(g2, x, y, nibble++, charLow);
			x += charWidth;

			g2.drawString(new String(new byte[] { b }, Charset.forName("ASCII")), asciiX, y);
			asciiX += charWidth;

			if ((i + 1) % lineLength == 0) {
				g2.drawString(StringUtils.leftPad(Integer.toHexString((i + 1) - lineLength), 4, '0'), 0, y);

				x = xBase;
				asciiX = asciiXBase;
				y += charHeight + lineGap;
				continue;
			}

			if ((i + 1) % 2 == 0) {
				x += twoByteGap;
			}
		}
	}

	private void drawNibble(Graphics2D g2, int x, int y, int nibble, char c) {
		g2.drawString(String.valueOf(c), x, y);
		g2.setColor(Color.BLACK);
	}

	private void calculateMetrics(Graphics2D g2) {
		if (getWidth() == lastWidth && getHeight() == lastHeight) {
			return;
		}
		charWidth = g2.getFontMetrics().stringWidth("A");
		charHeight = g2.getFontMetrics().getAscent();
//			charHeight = font.createGlyphVector(g2.getFontRenderContext(), "A").getPixelBounds(g2.getFontRenderContext(), 0, 0).height;
		lineGap = 3;

		twoByteGap = charWidth / 2;
		addressHexGap = 2 * charWidth;
		addressWidth = 4 * charWidth;

		hexAsciiGap = 2 * charWidth;

		hexX = addressWidth + addressHexGap;

		lineLength = ((getWidth() - (hexX + hexAsciiGap)) / ((6 * charWidth) + twoByteGap)) * 2;
		lines = getHeight() / (charHeight + lineGap);

		hexWidth = lineLength * 2 * charWidth + ((lineLength / 2 - 1) * twoByteGap);
		// TODO calculate hexHeight once we know how :)

		asciiX = hexX + hexWidth + hexAsciiGap;

		getData();

		lastWidth = getWidth();
		lastHeight = getHeight();

		fireMetricsUpdated();
	}

	private void getData() {
		bytes = new byte[(int) Math.min(Math.max(0, data.getLength() - offset), lineLength * lines)];
		data.get(bytes, offset);
	}

	/**
	 * Set the offset to determine the start of the data that is to be
	 * displayed. Note that the offset is corrected by rounding down to the
	 * start of a line. This depends on the current line length.
	 *
	 * @param offset
	 *            the raw offset
	 * @see #setLineOffset(long)
	 */
	public void setOffset(long offset) {
		long newOffset = lineLength * (offset / lineLength);
		System.err.println("new corrected offset: " + newOffset + " (raw: " + offset + ", line length: " + lineLength + ")");
		if (this.offset == newOffset) {
			return;
		}
		this.offset = newOffset;
		getData();
		repaint();
	}

	/**
	 * Convenience method that performs the same operation as
	 * {@link #setOffset(long)} but on a line basis.
	 *
	 * @param lineOffset
	 *            the line of data that should be the first
	 */
	public void setLineOffset(long lineOffset) {
		this.setOffset(lineOffset * lineLength);
	}

	private class HoverListener extends MouseAdapter {
		private boolean dragging;
		private final RangeMarker hoverMarker;
		private final RangeMarker selectionMarker;

		public HoverListener(RangeMarker hoverMarker, RangeMarker selectionMarker) {
			this.hoverMarker = hoverMarker;
			this.selectionMarker = selectionMarker;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!dragging) {
				dragging = true;
				long markStart = metrics.getByteAtPosition(e.getX(), e.getY());
				selectionMarker.setByteStart(markStart);
				selectionMarker.setByteEnd(markStart);

				hoverMarker.invalidate();
			} else {
				long newMarkEnd = metrics.getByteAtPosition(e.getX(), e.getY());
				if (newMarkEnd != -1) {
					selectionMarker.setByteEnd(newMarkEnd);
				}
			}
			HexPanel.this.repaint();
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if (dragging) {
				dragging = false;
			} else {
				selectionMarker.invalidate();
			}
			HexPanel.this.repaint();
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			long newByte = metrics.getByteAtPosition(e.getX(), e.getY());
			hoverMarker.setByteStart(newByte);
		}

	}

	/**
	 * Returns the {@link Metrics} for this {@code HexPanel}.
	 *
	 * @return
	 * @see #addMetricsUpdatedListener(MetricsUpdatedListener)
	 * @see #removeMetricsUpdatedListener(MetricsUpdatedListener)
	 */
	public Metrics getMetrics() {
		return metrics;
	}

	public synchronized void addMetricsUpdatedListener(MetricsUpdatedListener l) {
		metricsUpdatedListeners.add(l);
	}

	public synchronized void removeMetricsUpdatedListener(MetricsUpdatedListener l) {
		metricsUpdatedListeners.remove(l);
	}

	private void fireMetricsUpdated() {
		for (MetricsUpdatedListener l : metricsUpdatedListeners) {
			l.metricsUpdated(metrics);
		}
	}

	/**
	 * Encapsulates layout information about a {@link HexPanel}. The class
	 * contains a few convenience methods that perform calculation for various
	 * layout related tasks.
	 *
	 * @author Werner Hahn
	 *
	 */
	public class Metrics {
		public int getCharWidth() {
			return charWidth;
		}
		public int getCharHeight() {
			return charHeight;
		}
		public int getHexX() {
			return hexX;
		}
		public int getHexWidth() {
			return hexWidth;
		}
		public int getLineGap() {
			return lineGap;
		}
		public int getLineLength() {
			return lineLength;
		}
		public int getLines() {
			return lines;
		}
		public long getLinesTotal() {
			if (lineLength == 0) {
				return -1;
			}
			return data.getLength() / lineLength + 1;
		}
		public HexPosition coordsFromIndex(long index) {
			int row = (int) (index / lineLength);
			int column = (int) (index % lineLength);

			int x = column * 2 * charWidth + hexX + (column / 2) * twoByteGap;
			int y = (row + 1) * charHeight + row * lineGap;

			return new HexPosition(x, y, column, row);
		}

		/**
		 * Returns the index of the byte that is at coordinates {@code x}
		 * and {@code y}.
		 *
		 * @param x
		 * @param y
		 * @return the index or -1 if no byte is at this position
		 */
		public long getByteAtPosition(int x, int y) {
			if (!(x >= hexX && x <= hexX + hexWidth)) {
				return -1;
			}
			int xNormalized = x - hexX;
			int twoByteBlock = xNormalized / (4 * charWidth + twoByteGap);
			int xTwoByte = xNormalized - twoByteBlock * (4 * charWidth + twoByteGap);
			if (xTwoByte > 4 * charWidth) {
				return -1;
			}

			int twoByteBlockHoveredByte = xTwoByte / (2 * charWidth);

			int byteCol = twoByteBlockHoveredByte + 2 * twoByteBlock;
			int row = y / (charHeight + lineGap);

			return byteCol + row * lineLength;
		}
	}

}