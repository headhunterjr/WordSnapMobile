package com.example.wordsnap

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsnap.adapter.DatabaseAdapter
import com.example.wordsnap.database.DatabaseManager

class MainActivity : AppCompatActivity() {
    private lateinit var databaseManager: DatabaseManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var tableSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseManager = DatabaseManager(this)
        databaseManager.populateTablesWithRandomData()

        recyclerView = findViewById(R.id.dataRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tableSpinner = findViewById(R.id.tableSpinner)
        val tables = listOf("Users", "CardSets", "Cards", "Progress")

        val spinnerAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            tables)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tableSpinner.adapter = spinnerAdapter

        tableSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTable = tables[position]
                val tableData = databaseManager.getTableData(selectedTable)
                recyclerView.adapter = DatabaseAdapter(tableData)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}