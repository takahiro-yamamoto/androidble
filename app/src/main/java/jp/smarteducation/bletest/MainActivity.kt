package jp.smarteducation.bletest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        peripheralBtn.setOnClickListener {
            val peripheral = Peripheral()
            peripheral.startAdvertise(this)
        }

        centralBtn.setOnClickListener {
            var central = Central(this)
            central.scan()
        }
    }
}
