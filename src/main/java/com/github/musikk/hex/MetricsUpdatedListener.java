package com.github.musikk.hex;

/**
 * Listener that receives events whenever metrics related information changes.
 *
 * @author Werner Hahn
 *
 */
public interface MetricsUpdatedListener {

	/**
	 * Fired if the metrics of the watched object change. The metrics provided
	 * as an argument are from the watched object.
	 *
	 * @param metrics
	 *            the {@code Metrics} containing the updated information
	 */
	void metricsUpdated(HexPanel.Metrics metrics);

}
