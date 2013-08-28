package com.github.musikk.hex;

/**
 * Listener that receives events whenever a {@link Marker} is updated.
 * @author Werner Hahn
 *
 */
public interface MarkerUpdatedListener {

	/**
	 * Fired if a {@link Marker} is updated.
	 *
	 * @param marker
	 *            the updated {@code Marker}
	 */
	void markerUpdated(Marker marker);

}
