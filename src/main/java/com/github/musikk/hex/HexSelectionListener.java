package com.github.musikk.hex;

public interface HexSelectionListener {

	void onHover(HexSelectionEvent e);

	void onClick(HexSelectionEvent e);

	void onDrag(HexSelectionEvent e);

	public class HexSelectionEvent {
		public final long index;
		public final boolean stillDragging;
		public final int x;
		public final int y;

		public HexSelectionEvent(long index, int x, int y) {
			this(index, false, x, y);
		}

		public HexSelectionEvent(long index, boolean stillDragging, int x, int y) {
			this.index = index;
			this.stillDragging = stillDragging;
			this.x = x;
			this.y = y;
		}
	}

}
