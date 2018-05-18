package jp.smarteducation.bletest

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.os.ParcelUuid
import java.util.*

// ref: https://qiita.com/anzai_k/items/131373caa0a6294efdd4
class Peripheral : AdvertiseCallback {

    val SERVICE_UUID = "CF0DFF25-8BB9-46DA-8692-76B18C2B1282"
    val CHARA_UUID   = "D504DD54-AB0D-49F5-9868-3ACAC8FA4EA4"

    private lateinit var advertiser: BluetoothLeAdvertiser
    private lateinit var gattServer: BluetoothGattServer

    constructor()


    fun startAdvertise(context: Context) {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = manager.adapter

        advertiser = getAdvertiser(adapter)
        gattServer = getGattServer(context, manager)

        setUuid()

        advertiser.startAdvertising(makeAdvertiseSetting(), makeAdvertiseData(), this)
    }

    fun stopAdvertise() {
        if(gattServer != null) {
            gattServer.clearServices()
            gattServer.close()
//            gattServer = null
        }

        if(advertiser != null) {
            advertiser.stopAdvertising(this)
        }
    }

    fun getAdvertiser(adapter: BluetoothAdapter): BluetoothLeAdvertiser {
        return adapter.bluetoothLeAdvertiser
    }

    fun getGattServer(context: Context, manager: BluetoothManager): BluetoothGattServer {
        val bleServer = BLEServer()
        manager.openGattServer(context, bleServer)
        val gattServer = manager.openGattServer(context, bleServer)
        bleServer.bluetoothGattServer = gattServer
        return gattServer
    }

    fun setUuid() {
        val service = BluetoothGattService(UUID.fromString(SERVICE_UUID), BluetoothGattService.SERVICE_TYPE_PRIMARY)

        val characteristic = BluetoothGattCharacteristic(
                UUID.fromString(CHARA_UUID),
                BluetoothGattCharacteristic.PROPERTY_READ or
                        BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ or
                        BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        service.addCharacteristic(characteristic)
        gattServer.addService(service)
    }

    private fun makeAdvertiseSetting(): AdvertiseSettings {
        val builder = AdvertiseSettings.Builder()

        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
        builder.setConnectable(true)
        builder.setTimeout(0)

        return builder.build()
    }

    private fun makeAdvertiseData(): AdvertiseData {
        val builder = AdvertiseData.Builder()
        builder.addServiceUuid(ParcelUuid(UUID.fromString(SERVICE_UUID)))

        return builder.build()
    }
}