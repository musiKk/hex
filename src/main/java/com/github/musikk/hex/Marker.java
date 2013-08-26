package com.github.musikk.hex;


import java.awt.Graphics2D;

public interface Marker {
	void paint(Graphics2D g2, HexPanel.Metrics metrics);
	void addListener(MarkerUpdatedListener l);
	void removeListener(MarkerUpdatedListener l);
}