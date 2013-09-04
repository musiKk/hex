package com.github.musikk.hex;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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

	private static final int MAX_TICKS = Integer.MAX_VALUE;

	private final HexPanel hexPanel;

	/**
	 * The {@link Marker} that highlights the currently hovered byte.
	 */
	private RangeMarker hoverMarker;

	/**
	 * The {@link Marker} that shows the current selection. A selection is
	 * created by a standard dragging operation.
	 */
	private RangeMarker selectionMarker;

	/**
	 * The scroll bar that is used to scroll through the data.
	 */
	private final JScrollBar scrollbar;

	/**
	 * The listener that gets notified when the scrollbar's value changes, i.e.,
	 * the user scrolls using the scrollbar.
	 */
	private AdjustmentListener scrollbarAdjustListener;

	public ScrollableHexPanel(final DataProvider data) {
		this.setLayout(new BorderLayout());

		hexPanel = new HexPanel(data);
		hexPanel.addHexSelectionListener(new HexHoverListener());
		hexPanel.addHexSelectionListener(new HexMarkingListener());
		this.add(hexPanel, BorderLayout.CENTER);

		scrollbar = new JScrollBar(JScrollBar.VERTICAL);
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

		scrollbarAdjustListener = new ScrollbarAdjustListener();
		scrollbar.addAdjustmentListener(scrollbarAdjustListener);

		hexPanel.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rotation = e.getWheelRotation();
				if (rotation == 0) {
					return;
				}
				scrollLines(rotation);
			}
		});

		this.add(scrollbar, BorderLayout.EAST);
	}

	private void scrollLines(int scrollLines) {
		HexPanel.Metrics metrics = hexPanel.getMetrics();

		long currentLine = metrics.getOffset() / metrics.getLineLength();
		long newLine = currentLine + scrollLines;
		if (scrollLines > 0) {
			newLine = Math.min(newLine, metrics.getLinesTotal() - metrics.getLines());
		} else {
			newLine = Math.max(newLine, 0);
		}
		hexPanel.setLineOffset(newLine);

		float fraction = ((float) newLine) / (metrics.getLinesTotal() - metrics.getLines());

		/*
		 * Remove the listener before setting the value. If the fractions of
		 * multiple consecutive values result in the same value value again, the
		 * user is unable to scroll further. Therefore the listener must not
		 * intervene.
		 *
		 * In any case for huge files where the total number of lines is greater
		 * than MAX_TICKS, the value here is only an approximation anyway.
		 */
		scrollbar.removeAdjustmentListener(scrollbarAdjustListener);
		scrollbar.setValue((int) (fraction * scrollbar.getMaximum()));
		scrollbar.addAdjustmentListener(scrollbarAdjustListener);
	}

	/**
	 * Removes the old hover marker (if any) and sets the new marker (if any).
	 * The new marker is set to {@linkplain RangeMarker#setSingleByte(boolean)
	 * single byte mode}.
	 *
	 * @param marker
	 */
	public void setHoverMarker(RangeMarker marker) {
		marker.setSingleByte(true);
		replaceMarker(hoverMarker, marker);
		hoverMarker = marker;
	}

	/**
	 * Removes the old selection marker (if any) and sets the new marker (if
	 * any).
	 *
	 * @param marker
	 */
	public void setSelectionMarker(RangeMarker marker) {
		replaceMarker(selectionMarker, marker);
		selectionMarker = marker;
	}

	private void replaceMarker(RangeMarker oldMarker, RangeMarker newMarker) {
		if (oldMarker != null) {
			oldMarker.invalidate();
			hexPanel.removeMarker(oldMarker);
		}
		if (newMarker != null) {
			newMarker.invalidate();
			hexPanel.addMarker(newMarker);
		}
	}

	private class ScrollbarAdjustListener implements AdjustmentListener {
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			Adjustable adj = e.getAdjustable();
			Metrics metrics = hexPanel.getMetrics();

			long newLine = (long) ((metrics.getLinesTotal() - metrics.getLines()) * (((float) adj.getValue()) / adj.getMaximum()));
			hexPanel.setLineOffset(newLine);
		}
	}

	private class HexHoverListener extends HexSelectionAdapter {
		@Override
		public void onHover(HexSelectionEvent e) {
			if (hoverMarker == null) {
				return;
			}
			hoverMarker.setByteStart(e.position.index);
			hexPanel.repaint();
		}
	}

	private class HexMarkingListener extends HexSelectionAdapter {
		private boolean dragging;
		@Override
		public void onDrag(HexSelectionEvent e) {
			if (!dragging) {
				// drag starts
				if (selectionMarker != null) {
					selectionMarker.setByteStartEnd(e.position.index, e.position.index);
				}
				if (hoverMarker != null) {
					hoverMarker.invalidate();
				}
			} else {
				if (selectionMarker != null) {
					selectionMarker.setByteEnd(e.position.index);
				}
			}
			dragging = e.stillDragging;
		}
		@Override
		public void onClick(HexSelectionEvent e) {
			if (selectionMarker == null) {
				return;
			}
			selectionMarker.invalidate();
		}
	}

}
