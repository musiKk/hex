package com.github.musikk.hex;


import java.awt.Color;
import java.awt.Graphics2D;

import com.github.musikk.hex.HexPanel.Metrics;

public class SimpleBorderMarker extends RangeMarker {

	public SimpleBorderMarker(Color color) {
		super(color);
	}

	@Override
	public void paint(Graphics2D g2, Metrics metrics) {
		if (isInvalid()) {
			return;
		}
//		g2.setColor(getColor());

		int charHeight = metrics.getCharHeight();
		int charWidth = metrics.getCharWidth();
		int hexX = metrics.getHexX();
		int hexWidth = metrics.getHexWidth();
		int pad = 0;

		HexPosition startPoint;
		HexPosition endPoint;
		if (getByteStart() <= getByteEnd()) {
			startPoint = metrics.coordsFromIndex(getByteStart());
			endPoint = metrics.coordsFromIndex(getByteEnd());
		} else {
			startPoint = metrics.coordsFromIndex(getByteEnd());
			endPoint = metrics.coordsFromIndex(getByteStart());
		}

		if (startPoint.row == endPoint.row) {
			g2.drawRect(startPoint.x, startPoint.y - charHeight - pad/2, endPoint.x - startPoint.x + 2 * charWidth, charHeight + pad);
		} else {
			int xLeft = startPoint.x;
			int xRight = hexX + hexWidth;
			int yLow = startPoint.y + pad;
			int yHigh = startPoint.y - charHeight - pad;

			// [
			g2.drawLine(xLeft, yLow, xRight, yLow); // low
			g2.drawLine(xLeft, yLow, xLeft, yHigh); // left
			g2.drawLine(xLeft, yHigh, xRight, yHigh); // high

			int row = startPoint.row + 2;
			int y = row * charHeight + ((row - 1) * metrics.getLineGap());
			for (; row <= endPoint.row; row++) {
				g2.drawLine(hexX, y, hexX + hexWidth, y); // low
				g2.drawLine(hexX, y - charHeight, hexX + hexWidth, y - charHeight); // high

				y += metrics.getLineGap() + charHeight;
			}

			xLeft = hexX;
			xRight = endPoint.x + 2 * charWidth;
			yLow = endPoint.y + pad;
			yHigh = endPoint.y - charHeight - pad;

			// ]
			g2.drawLine(xRight, yLow, xLeft, yLow); // low
			g2.drawLine(xRight, yLow, xRight, yHigh); // right
			g2.drawLine(xRight, yHigh, xLeft, yHigh); // high
		}

	}

}
