package com.github.musikk.hex;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMarker implements Marker {

	private final List<MarkerUpdatedListener> listeners = new ArrayList<>();

	@Override
	public synchronized void addListener(MarkerUpdatedListener l) {
		listeners.add(l);
	}
	@Override
	public synchronized void removeListener(MarkerUpdatedListener l) {
		listeners.remove(l);
	}
	protected synchronized void fireEvent() {
		for (MarkerUpdatedListener l : listeners) {
			l.markerUpdated(this);
		}
	}
}
