package com.github.musikk.hex;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import com.github.musikk.hex.HexPanel.Metrics;

/**
 * Provides a {@link HexPanel} with a scroll bar to the right that allows
 * scrolling through data that is to big to be shown on a single screen.
 *
 * @author Werner Hahn
 *
 */
public class ScrollableHexPanel extends JPanel {

	private static final int MAX_TICKS = 1000;

	public ScrollableHexPanel(final DataProvider data) {
		this.setLayout(new BorderLayout());

		final HexPanel hexPanel = new HexPanel(data);
		this.add(hexPanel, BorderLayout.CENTER);

		final JScrollBar scrollbar = new JScrollBar(JScrollBar.VERTICAL);
		scrollbar.setMinimum(0);
		scrollbar.setMaximum(0); // gets correct value in the listener
		scrollbar.setValue(0);

		hexPanel.addMetricsUpdatedListener(new MetricsUpdatedListener() {
			@Override
			public void metricsUpdated(HexPanel.Metrics metrics) {
				if (metrics.getLines() >= metrics.getLinesTotal()) {
					scrollbar.setMaximum(0);
					scrollbar.setValue(0);
					scrollbar.setEnabled(false);
					return;
				}
				scrollbar.setEnabled(true);
				int newMax = (int) Math.min(MAX_TICKS, metrics.getLinesTotal() - metrics.getLines());
				scrollbar.setMaximum(newMax);
				scrollbar.setVisibleAmount(newMax == MAX_TICKS ? 1 : (int) (newMax - (metrics.getLinesTotal() - metrics.getLines() - 1)));
			}
		});

		scrollbar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				Adjustable adj = e.getAdjustable();
				Metrics metrics = hexPanel.getMetrics();

				long newLine = (long) ((metrics.getLinesTotal() - metrics.getLines()) * (((float) adj.getValue()) / adj.getMaximum()));
				hexPanel.setLineOffset(newLine);
			}
		});

		this.add(scrollbar, BorderLayout.EAST);
	}

}
