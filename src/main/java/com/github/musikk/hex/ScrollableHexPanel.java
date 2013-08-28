package com.github.musikk.hex;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class ScrollableHexPanel extends JPanel {

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
				System.err.printf("metrics updated: lines: %d, lines total: %d%n",
						metrics.getLines(), metrics.getLinesTotal());
				if (metrics.getLines() >= metrics.getLinesTotal()) {
					scrollbar.setMaximum(0);
					scrollbar.setValue(0);
					scrollbar.setEnabled(false);
					return;
				}
				scrollbar.setEnabled(true);
				int newMax = (int) Math.min(1000, metrics.getLinesTotal() - metrics.getLines());
				System.err.println("new maximum: " + newMax);
				scrollbar.setMaximum(newMax);
				scrollbar.setVisibleAmount(newMax == 1000 ? 1 : (int) (newMax - (metrics.getLinesTotal() - metrics.getLines() - 1)));
				System.err.println("visible ammount: " + scrollbar.getVisibleAmount());
			}
		});

		scrollbar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				Adjustable adj = e.getAdjustable();
				long newLine = (long) ((hexPanel.getMetrics().getLinesTotal() - hexPanel.getMetrics().getLines()) * (((float) adj.getValue()) / adj.getMaximum()));
				System.err.printf("adjustment updated: %d/%d yields line %d%n",
						adj.getValue(), adj.getMaximum(), newLine);
				hexPanel.setLineOffset(newLine);
			}
		});

		this.add(scrollbar, BorderLayout.EAST);
	}

}
