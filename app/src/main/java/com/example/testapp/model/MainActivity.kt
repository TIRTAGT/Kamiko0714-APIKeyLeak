package com.example.testapp.model

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.R
import com.example.testapp.storydata.StoryResponse
import com.example.testapp.utils.StoryApiClient
import com.example.testapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        storyAdapter = StoryAdapter()
        binding.recyclerViewStories.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewStories.adapter = storyAdapter

        getStories()
    }

    private fun getStories() {
        progressDialog.show()

        val call = StoryApiClient.storyApiService.getAllStories(
            page = 1,
            size = 10,
            location = 0,
            authorization = "Bearer " +  getString(R.string.story_api_key)
        )

        call.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                progressDialog.dismiss()

                if (response.isSuccessful) {
                    val storyResponse = response.body()

                    val isError = storyResponse?.error
                    val message = storyResponse?.message
                    val listStory = storyResponse?.listStory

                    if (listStory != null) {
                        storyAdapter.setData(listStory)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
