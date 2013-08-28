package com.github.musikk.hex;

/**
 * Contains coordinates of a hex character both in absolute coordinates suitable
 * for painting and hex coordinates.
 *
 * @author Werner Hahn
 *
 */
class HexPosition {
	final int x;
	final int y;
	final int col;
	final int row;

	public HexPosition(int x, int y, int col, int row) {
		this.x = x;
		this.y = y;
		this.col = col;
		this.row = row;
	}
	@Override
	public String toString() {
		return String.format("HexPosition: %d:%d [col/row: %d/%d]", x, y, col, row);
	}
}