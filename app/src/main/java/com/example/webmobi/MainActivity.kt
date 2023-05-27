package com.example.webmobi

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var res:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        val city: EditText =findViewById(R.id.cityName)
        res=findViewById(R.id.resultData)

        val btn=findViewById<Button>(R.id.suBmit)
        btn.setOnClickListener {
            if(city.text.toString().isNullOrEmpty()){
                city.setError("field is required")
            }
            else{
                val sharedPreferences = this.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("city", city.text.toString().trim())
                editor.apply()
                res.setText("")
                getData(city.text.toString().trim())
            }
        }
    }
    fun getData(cityname:String){
        val pd=ProgressDialog(this)
        pd.setTitle("Please Wait")
        pd.setMessage("Loading...")
        pd.show()
        res.setText("")
        val url="http://api.weatherstack.com/current?access_key=1904fe6d3f5e3f39f336ac0d15cc3738&query="+cityname
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response ->
                if(response!=null){
                   try{
                       val current = response.getJSONObject("current")
                       val loc= response.getJSONObject("location").getString("name")
                       val humidity=current.getString("humidity")
                       val temp=current.getString("temperature")
                       val speed=current.getString("wind_speed")
                       val weatherDescriptions = current.getJSONArray("weather_descriptions")
                       val wd = weatherDescriptions.optString(0)
                       val result="Location="+loc+"\n"+"Temperature="+temp+"\n"+"Humidity="+humidity+"\n"+"Wind Speed="+speed+"\n"+"Weather="+wd
                       res.setText(result)
                       pd.dismiss()
                   }catch (ex:Exception){
                       val current = response.getJSONObject("error").getString("info")
                       val er=current.removePrefix("Your API request failed.")
                       Toast.makeText(this,er.toString(), Toast.LENGTH_SHORT).show()
                       res.setText("")
                       pd.dismiss()
                   }
                }else{
                    Toast.makeText(this,"Try again later", Toast.LENGTH_SHORT).show()
                    pd.dismiss()
                }
            },
            { error ->
                Log.e("VolleyRequest", "Error: ${error.message}")
                Toast.makeText(this,"error "+error.message.toString(), Toast.LENGTH_SHORT).show()
                res.setText(error.message.toString())
                pd.dismiss()
            }
        )
        val requestQueue = Volley.newRequestQueue(this@MainActivity)
        requestQueue.add(jsonObjectRequest)
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = this.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val c=sharedPreferences.getString("city",null)
        if(c!=null){
            getData(c.toString())
        }else{
            res.setText("No History Present")
        }
    }
}