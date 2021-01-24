package mx.tecnm.tepic.ladm_u4_tarea1

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val siPermiso = 98
    var hilito = Hilo(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALL_LOG,android.Manifest.permission.CALL_PHONE),siPermiso)
        } else {
            recuperar()
            hilito.start()
        }

        llamar.setOnClickListener {
            llamar()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == siPermiso){
            Toast.makeText(this,"PERMISOS DE LLAMAR Y LEER LLAMADAS OTORGADO", Toast.LENGTH_LONG).show()
            recuperar()
            hilito.start()
        }
    }

    //CODIGO PARA LEER LISTA DE LLAMADAS
    fun recuperar() {
        var cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,null,null,null,"date DESC")
        var registros = ArrayList<String>()

        if(cursor!!.moveToFirst()){
            var num = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            var tipo = cursor.getColumnIndex(CallLog.Calls.TYPE)

            do{
                var data = "LLAMADA DE:\nTELEFONO: ${cursor.getString(num)} \nTIPO: ${cursor.getString(tipo)}"
                registros.add(data)
            }while(cursor.moveToNext())

            llamadas.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, registros)
        }
    }

    //CODIGO PARA REALIZAR LLAMADAS
    private fun llamar(){
        val num = telefono.text.toString()
        val intent = Intent(Intent.ACTION_CALL)

        intent.data = Uri.parse("tel:$num")
        startActivity(intent)
    }

}

class Hilo (p: MainActivity) : Thread() {
    private var puntero = p

    override fun run() {
        super.run()

        while (true) {
            sleep(1000)
            puntero.runOnUiThread {
                puntero.recuperar()
            }
        }
    }
}