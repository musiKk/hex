package com.github.musikk.hex;

public interface HexSelectionListener {

	void onHover(HexSelectionEvent e);

	void onClick(HexSelectionEvent e);

	void onDrag(HexSelectionEvent e);

	public class HexSelectionEvent {
		public final boolean stillDragging;
		public final HexPosition position;

		public HexSelectionEvent(HexPosition position) {
			this(position, false);
		}

		public HexSelectionEvent(HexPosition position, boolean stillDragging) {
			this.position = position;
			this.stillDragging = stillDragging;
		}
	}

}
