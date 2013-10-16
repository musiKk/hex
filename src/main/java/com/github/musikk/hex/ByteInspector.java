package com.github.musikk.hex;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;

/**
 * Contains components that displays data from a {@link HexPanel} in various
 * formats.
 *
 * @author werner
 *
 */
class ByteInspector extends JPanel {

	/**
	 * The index in {@link bytes} where meaningful data begins. This is used to
	 * indicate a partially filled byte array which occurs when the index
	 * approaches the end of the available data.
	 */
	private int byteIndex = 0;
	/**
	 * The bytes to display.
	 */
	private final byte[] bytes = new byte[8];

	private final JTextField signedByteField = new JTextField();
	private final JTextField unsignedByteField = new JTextField();

	private final JTextField signedShortField = new JTextField();
	private final JTextField unsignedShortField = new JTextField();

	private final JTextField signedIntField = new JTextField();
	private final JTextField unsignedIntField = new JTextField();

	private final JTextField signedLongField = new JTextField();
	private final JTextField unsignedLongField = new JTextField();

	private final JToggleButton radixBinaryButton = new JToggleButton(new RadixAction(Radix.BINARY));
	private final JToggleButton radixOctalButton = new JToggleButton(new RadixAction(Radix.OCTAL));
	private final JToggleButton radixDecimalButton = new JToggleButton(new RadixAction(Radix.DECIMAL));
	private final JToggleButton radixHexButton = new JToggleButton(new RadixAction(Radix.HEX));

	private final JToggleButton[] radixButtons = {
			radixBinaryButton,
			radixOctalButton,
			radixDecimalButton,
			radixHexButton
	};

	private final JLabel[] labels = new JLabel[] {
			new JLabel("Signed Byte"), new JLabel("Unsigned Byte"),
			new JLabel("Signed Short"), new JLabel("Unsigned Short"),
			new JLabel("Signed Integer"), new JLabel("Unsigned Integer"),
			new JLabel("Signed Long"), new JLabel("Unsigned Long"),
	};

	private final JTextField[] textFields = new JTextField[] {
			signedByteField, unsignedByteField,
			signedShortField, unsignedShortField,
			signedIntField, unsignedIntField,
			signedLongField, unsignedLongField
	};

	public ByteInspector() {
		radixDecimalButton.setSelected(true);

		for (JTextField f : textFields) {
			f.setEditable(false);
		}

		setLayout(new BorderLayout());
		add(makeToolBar(), BorderLayout.NORTH);
		add(makeFieldPanel(), BorderLayout.CENTER);
	}

	private JToolBar makeToolBar() {
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setFloatable(false);

		for (final JToggleButton radixButton : radixButtons) {
			toolbar.add(radixButton);
		}

		return toolbar;
	}

	private JPanel makeFieldPanel() {
		Dimension textFieldDimension = new Dimension(
				new JTextField(Long.toString(Long.MAX_VALUE) + "1").getPreferredSize().width,
				signedByteField.getPreferredSize().height);
		// one is enough, the rest is done by the layout manager
		signedByteField.setPreferredSize(textFieldDimension);

		JPanel fieldGroup = new JPanel();
		TitledBorder titleBorder = BorderFactory.createTitledBorder("Hovered Position");
		fieldGroup.setBorder(titleBorder);


		fieldGroup.setLayout(new GridBagLayout());

		GridBagConstraints labelsConstraints = new GridBagConstraints();
		labelsConstraints.anchor = GridBagConstraints.WEST;
		labelsConstraints.weightx = 1.0;
		labelsConstraints.insets = new Insets(0, 5, 2, 5);
		labelsConstraints.gridx = 0;
		labelsConstraints.gridy = GridBagConstraints.RELATIVE;

		GridBagConstraints textFieldConstraints = (GridBagConstraints) labelsConstraints.clone();
		textFieldConstraints.anchor = GridBagConstraints.WEST;
		textFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
		textFieldConstraints.insets = new Insets(0, 5, 2, 5);
		textFieldConstraints.gridx = 1;

		for (int i = 0; i < labels.length; i++) {
			fieldGroup.add(labels[i], labelsConstraints);
			fieldGroup.add(textFields[i], textFieldConstraints);
		}
		return fieldGroup;
	}

	private void fillLabels(ByteInspector.Radix radix) {
		ByteBuffer b = ByteBuffer.wrap(bytes);

		signedByteField.setText(radix.getSignedByteString(b, byteIndex));
		unsignedByteField.setText(radix.getUnsignedByteString(b, byteIndex));

		signedShortField.setText(radix.getSignedShortString(b, byteIndex));
		unsignedShortField.setText(radix.getUnsignedShortString(b, byteIndex));

		signedIntField.setText(radix.getSignedIntString(b, byteIndex));
		unsignedIntField.setText(radix.getUnsignedIntString(b, byteIndex));

		signedLongField.setText(radix.getSignedLongString(b, byteIndex));
		unsignedLongField.setText(radix.getUnsignedLongString(b, byteIndex));
	}

	/**
	 * Updates the data that is displayed by this {@code ByteInspector}.
	 * @param data
	 * @param index
	 */
	void setData(DataProvider data, long index) {
		Arrays.fill(bytes, (byte) 0);
		int read = data.get(bytes, index);
		if (read < bytes.length) {
			byte[] temp = new byte[bytes.length];
			System.arraycopy(bytes, 0, temp, bytes.length - read, read);
			System.arraycopy(temp, 0, bytes, 0, bytes.length);
		}
		byteIndex = bytes.length - read;
		fillLabels(getSelectedRadix());
	}

	private ByteInspector.Radix getSelectedRadix() {
		for (JToggleButton b : radixButtons) {
			if (b.isSelected()) {
				return ((RadixAction) b.getAction()).getRadix();
			}
		}
		throw new IllegalStateException("Cannot determine selected radix.");
	}

	private static enum Radix {
		DECIMAL(10), BINARY(2), OCTAL(8), HEX(16);

		private final int radix;

		private Radix(int radix) {
			this.radix = radix;
		}

		public int getRadix() {
			return radix;
		}

		public String getSignedByteString(ByteBuffer b, int offset) {
			return Integer.toString(b.get(offset), radix);
		}
		public String getUnsignedByteString(ByteBuffer b, int offset) {
			return Integer.toString(b.get(offset) & 0xFF, radix);
		}
		public String getSignedShortString(ByteBuffer b, int offset) {
			return Integer.toString(b.getShort(Math.min(offset, b.capacity() - 2)), radix);
		}
		public String getUnsignedShortString(ByteBuffer b, int offset) {
			return Integer.toString(b.getShort(Math.min(offset, b.capacity() - 2)) & 0xFFFF, radix);
		}
		public String getSignedIntString(ByteBuffer b, int offset) {
			return Integer.toString(b.getInt(Math.min(offset, b.capacity() - 4)), radix);
		}
		public String getUnsignedIntString(ByteBuffer b, int offset) {
			return Long.toString(b.getInt(Math.min(offset, b.capacity() - 4)) & 0xFFFFFFFFL, radix);
		}
		public String getSignedLongString(ByteBuffer b, int offset) {
			return Long.toString(b.getLong(Math.min(offset, b.capacity() - 8)), radix);
		}
		public String getUnsignedLongString(ByteBuffer b, int offset) {
			byte[] longBytes = new byte[b.capacity() - Math.min(offset, b.capacity() - 8)];
			b.get(longBytes);

			BigInteger bigLong = new BigInteger(longBytes);
			if (bigLong.compareTo(BigInteger.ZERO) < 0) {
				bigLong = bigLong.add(BigInteger.ONE.shiftLeft(64));
			}
			return bigLong.toString(radix);
		}
	}

	class RadixAction extends AbstractAction {
		private final ByteInspector.Radix radix;
		RadixAction(ByteInspector.Radix radix) {
			this.radix = radix;
			this.putValue(NAME, Integer.toString(radix.getRadix()));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			for (JToggleButton radixButton : radixButtons) {
				radixButton.setSelected(e.getSource() == radixButton);
			}
			fillLabels(radix);
		}
		public ByteInspector.Radix getRadix() {
			return radix;
		}
	}

}