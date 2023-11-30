package com.example.oracleai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val etquestion = findViewById<EditText>(R.id.etQuestion)
        val btmSubmit = findViewById<Button>(R.id.Submit)
        val txtResponse = findViewById<TextView>(R.id.Response)


        btmSubmit.setOnClickListener {
            val question = etquestion.text.toString()
            Toast.makeText(this, question, Toast.LENGTH_SHORT).show()
            getResponse(question) { response ->
                runOnUiThread {
                    txtResponse.text = response
                }
            }
        }
    }
    fun getResponse(question: String, callback: (String) -> Unit) {
        val apiKey="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"
        val url="https://api.openai.com/v1/completions"

        val requestBody="""
            {"model": "gpt-3.5-turbo",
            "messages": "$question",
            "max_tokens":7,
            "temperature":0,
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body !=null){
                    Log.v("data",body)
                }
                else {
                    Log.v("data", "empty")
                }
                var jsonObject=JSONObject(body)
                val jsonArray:JSONArray=jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)

            }
            })}
}