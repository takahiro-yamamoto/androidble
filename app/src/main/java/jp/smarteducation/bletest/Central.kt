package jp.smarteducation.bletest

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import java.util.*
import java.util.logging.Handler


class Central {
    val SERVICE_UUID = "CF0DFF25-8BB9-46DA-8692-76B18C2B1282"
    val CHARA_UUID   = "D504DD54-AB0D-49F5-9868-3ACAC8FA4EA4"
    val SCAN_PERIOD = 10000
    var deviceList: List<BluetoothDevice> = mutableListOf()
    private lateinit var adapter: BluetoothAdapter
    private lateinit var scanner: BluetoothLeScanner
    private lateinit var scanCallback: ScanCallback
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var gattCallback: BluetoothGattCallback
    private lateinit var context: Context


    constructor(context: Context) {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        adapter = manager.adapter
        scanner = adapter.bluetoothLeScanner
        this.context = context
        gattCallback = object: BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)

                if(newState == BluetoothProfile.STATE_CONNECTED) {
                    bluetoothGatt = gatt
                    discoverService()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)

                val service = gatt?.getService(UUID.fromString(SERVICE_UUID))
                print(service)
            }
        }
    }

    fun initCallbacks(): ScanCallback {
        return object: ScanCallback() {
            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)

            }

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)

                val device = result?.device
                if (device != null) {
                    connect(context, device)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
            }
        }
    }

//    fun scan(filters: List<ScanFilter>, settings: ScanSettings, enable: Boolean) {
//        scanCallback = initCallbacks()
//        scanner.startScan(scanCallback)
//    }
    fun scan() {
        scanCallback = initCallbacks()
        scanner.startScan(scanCallback)
    }

    fun stopScan() {
        if(scanner != null) {
            scanner.stopScan(scanCallback)
        }
    }

    var connectFlg = false

    fun connect(context: Context, device: BluetoothDevice) {
        if(!connectFlg) {
            connectFlg = true
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            bluetoothGatt?.connect()
        }

    }

    fun discoverService() {
        bluetoothGatt?.discoverServices()
    }
}