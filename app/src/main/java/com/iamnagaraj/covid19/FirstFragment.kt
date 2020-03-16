package com.iamnagaraj.covid19

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.iamnagaraj.covid19.model.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileInputStream
import java.util.*

class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/
        GlobalScope.launch(Dispatchers.Main) {
            val result = get()
            val gson = Gson()
            val coronaData = gson.fromJson(result, DataModel::class.java)
        }
    }

    suspend fun get():String{
        var jsonContent=""
        withContext(IO) {
            jsonContent = fetchJSON()
            val path = context?.filesDir
            val dataDirectory = File(path, "data")
            if(!dataDirectory.exists())
                dataDirectory.mkdirs()
            val file = File(dataDirectory, "data.txt")
            file.appendText(jsonContent)
            val inputAsString = FileInputStream(file).bufferedReader().use { it.readText()
            }
        }
        return jsonContent
    }
    private suspend fun fetchJSON(): String {
        val doc: Document = Jsoup.connect("https://www.worldometers.info/coronavirus/").get()
        val table = doc.select("table").first()
        val tableChildren = doc.select("table").first().children().select("tr")
        val jsonKeys = mutableListOf<String>()
        val jsonObj = JSONObject()
        val jsonArr = JSONArray()
        tableChildren.forEachIndexed { index, value ->
            if (index == 0) {
                val th = value.select("th")
                th.forEach { element ->
                    val replace = element.text().replace(" ", "_").replace(",", "").replace("/", "")
                    //Log.d("INNER", replace)
                    jsonKeys.add(replace.toLowerCase(Locale.getDefault()))
                }
            }else{
                val th = value.select("td")
                val jo = JSONObject()
                th.forEachIndexed {i, elementTr ->
                    //(jsonKeys.indices).forEach {
                        jo.put(jsonKeys[i],elementTr.text().replace("+","").replace(",",""))
                    //}
                }
                jsonArr.put(jo)
            }
        }
        jsonObj.put("data", jsonArr)
        return jsonObj.toString()
    }

}
