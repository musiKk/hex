package com.github.musikk.hex;

import java.awt.Color;
import java.awt.Graphics2D;

import com.github.musikk.hex.HexPanel.Metrics;

/**
 * Simple {@link RangeMarker} that paints a line based border around the marked
 * region. This marker is more a proof of concept and not suitable for markings
 * spanning multiple lines.
 *
 * @author Werner Hahn
 *
 */
public class SimpleBorderMarker extends RangeMarker {

	public SimpleBorderMarker(Color color) {
		super(color);
	}

	@Override
	public void paintRangeMarker(Graphics2D g2, Metrics metrics) {
		g2.setColor(getColor());

		int charHeight = metrics.getCharHeight();
		int charWidth = metrics.getCharWidth();
		int hexX = metrics.getHexX();
		int hexWidth = metrics.getHexWidth();
		int pad = metrics.getLineGap() / 2;

		HexPosition startPoint;
		HexPosition endPoint;
		if (getByteStart() <= getByteEnd()) {
			startPoint = metrics.coordsFromIndex(getByteStart());
			endPoint = metrics.coordsFromIndex(getByteEnd());
		} else {
			startPoint = metrics.coordsFromIndex(getByteEnd());
			endPoint = metrics.coordsFromIndex(getByteStart());
		}

		boolean openStart = false;
		boolean openEnd = false;
		if (startPoint == null) {
			startPoint = new HexPosition(hexX, metrics.getHexY(), 0, 0);
			openStart = true;
		}
		if (endPoint == null) {
			endPoint = new HexPosition(hexX + hexWidth - 2 * charWidth, (charHeight + metrics.getLineGap()) * metrics.getLines(), metrics.getLineLength(), metrics.getLines());
			openEnd = true;
		}

		if (startPoint.row == endPoint.row) {
			g2.drawRect(startPoint.x, startPoint.y - charHeight - pad, endPoint.x - startPoint.x + 2 * charWidth, charHeight + 2 * pad);
		} else {
			int xLeft = startPoint.x;
			int xRight = hexX + hexWidth;
			int yLow = startPoint.y + pad;
			int yHigh = startPoint.y - charHeight - pad;

			// [
			g2.drawLine(xLeft, yLow, xRight, yLow); // low
			if (!openStart) {
				g2.drawLine(xLeft, yLow, xLeft, yHigh); // left
			}
			g2.drawLine(xLeft, yHigh, xRight, yHigh); // high

			int row = startPoint.row + 2;
			int y = row * (charHeight + metrics.getLineGap());
			for (; row <= endPoint.row; row++) {
				g2.drawLine(hexX, y + pad, hexX + hexWidth, y + pad); // low
				g2.drawLine(hexX, y - charHeight - pad, hexX + hexWidth, y - charHeight - pad); // high

				y += metrics.getLineGap() + charHeight;
			}

			xLeft = hexX;
			xRight = endPoint.x + 2 * charWidth;
			yLow = endPoint.y + pad;
			yHigh = endPoint.y - charHeight - pad;

			// ]
			g2.drawLine(xRight, yLow, xLeft, yLow); // low
			if (!openEnd) {
				g2.drawLine(xRight, yLow, xRight, yHigh); // right
			}
			g2.drawLine(xRight, yHigh, xLeft, yHigh); // high
		}

	}

}
