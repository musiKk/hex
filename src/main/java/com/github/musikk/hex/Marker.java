package com.github.musikk.hex;

import java.awt.Graphics2D;

/**
 * A marker for data in a {@link HexPanel}. A marker's purpose is to highlight a
 * portion of data in the panel. It is free to perform any kind of visualization
 * desired.
 *
 * @author Werner Hahn
 *
 */
public interface Marker {
	/**
	 * Paints the marker on the provided {@code Graphics2D} object. The marker
	 * is drawn before the hex characters are drawn, so if marker and character
	 * overlap, the latter may obscure the former.
	 *
	 * @param g2
	 *            the graphics object onto which the marker may draw itself
	 * @param metrics
	 *            {@code Metrics} that help determining where to draw
	 */
	void paint(Graphics2D g2, HexPanel.Metrics metrics);
	void addListener(MarkerUpdatedListener l);
	void removeListener(MarkerUpdatedListener l);
}