package ru.jauseg.arduinolights;

public class BluetoothDevicePicker {
	// to show if bonding procedure needed.
	public static final String EXTRA_NEED_AUTH = "android.bluetooth.devicepicker.extra.NEED_AUTH";
	// the type of BT devices that want to show.
	public static final String EXTRA_FILTER_TYPE = "android.bluetooth.devicepicker.extra.FILTER_TYPE";
	// the package which the application belongs to.
	public static final String EXTRA_LAUNCH_PACKAGE = "android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE";
	// the class which will receive user's selected result from the BT list.
	public static final String EXTRA_LAUNCH_CLASS = "android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS";

	/**
	 * Broadcast when someone want to select one BT device from devices list.
	 * This intent contains below extra data: - {@link #EXTRA_NEED_AUTH}
	 * (boolean): if need authentication - {@link #EXTRA_FILTER_TYPE} (int):
	 * what kinds of device should be listed - {@link #EXTRA_LAUNCH_PACKAGE}
	 * (string): where(which package) this intent come from -
	 * {@link #EXTRA_LAUNCH_CLASS} (string): where(which class) this intent come
	 * from
	 */
	public static final String ACTION_LAUNCH = "android.bluetooth.devicepicker.action.LAUNCH";

	/**
	 * Broadcast when one BT device is selected from BT device picker screen.
	 * Selected BT device address is contained in extra string
	 * {@link BluetoothIntent}
	 */
	public static final String ACTION_DEVICE_SELECTED = "android.bluetooth.devicepicker.action.DEVICE_SELECTED";

	/** Ask device picker to show all kinds of BT devices */
	public static final int FILTER_TYPE_ALL = 0;
	/** Ask device picker to show BT devices that support AUDIO profiles */
	public static final int FILTER_TYPE_AUDIO = 1;
	/** Ask device picker to show BT devices that support Object Transfer */
	public static final int FILTER_TYPE_TRANSFER = 2;
}
