package com.github.musikk.hex;

/**
 * Contains coordinates of a hex character both in absolute coordinates suitable
 * for painting and hex coordinates.
 *
 * @author Werner Hahn
 *
 */
class HexPosition {
	/**
	 * The index in the data from the associated {@link DataProvider}.
	 */
	final long index;
	/**
	 * The global x coordinate for the hex character at this position.
	 */
	final int x;
	/**
	 * The global y coordinate for the hex character at this position.
	 */
	final int y;
	/**
	 * The zero-based column for the hex character at this position.
	 */
	final int column;
	/**
	 * The zero-based row for the hex character at this position, relative to
	 * the top of the currently visible data.
	 */
	final int row;
	/**
	 * The zero-based row for the hex character at this position, relative to
	 * the start of the data contained in the associated {@link DataProvider}.
	 */
	final long totalRow;

	public HexPosition(long index, int x, int y, int column, int row, long totalRow) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.column = column;
		this.row = row;
		this.totalRow = totalRow;
	}

	@Override
	public String toString() {
		return String.format("HexPosition: %d:%d[%d] [column: %d, row: %d/%d]",
				x, y, index, column, row, totalRow);
	}
}
