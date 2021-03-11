package com.example.dad

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.dad.bean.Movie
import com.example.dad.until.AudioTool
import com.example.dad.until.NetWork
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

var sList: ArrayList<Movie> = ArrayList()

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView;
    private val mHandler = MyHandler(this);
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById<ListView>(R.id.movie_list);

        //如果网络未开启，出现提示音
        if (!NetWork().isInternetAvailable(this)) {
            AudioTool().playerAudio(this, "wifi_tip.mp3");
            loopListener();
            return;
        } else {
            initView();
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this, MovieActivity::class.java);
            intent.putStringArrayListExtra("movieurl", sList[i].list);
            intent.putExtra("id", sList[i].id);
            startActivity(intent);
        }
    }

    //初始化视图
    private fun initView() {
        Thread(Runnable {
            sList = ArrayList();
            val request = Request.Builder()
                .url("https://demo.51jcjgzy.cn/movie/test.json")
                .build();
            val okHttpClient = OkHttpClient();
            val execute = okHttpClient.newCall(request);
            execute.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Toast.makeText(this@MainActivity, "数据获取失败", Toast.LENGTH_SHORT);
                }

                override fun onResponse(call: Call, response: Response) {
                    val dataMovice = response.body?.string();
                    val jsonArray = JSONArray(dataMovice);
                    for (i in 0..(jsonArray.length() - 1)) {
                        var tmp = jsonArray.get(i) as JSONObject;
                        var movice = Movie();
                        movice.img = tmp.getString("img");
                        movice.title = tmp.getString("title");
                        movice.id = tmp.getInt("id");
                        val list = tmp.getJSONArray("list");
                        var data_list = ArrayList<String>();
                        for (i in 0 until list.length()) {
                            data_list.add(list[i].toString());
                        }
                        movice.list = data_list;
                        sList.add(movice);
                    }
                    val msg = Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            });
        }).start();
    }

    override fun onDestroy() {
        super.onDestroy();
    }

    //如果检测到网络未开启，则10秒检测一次网络状态
    private fun loopListener() {
        var timer: Timer = Timer();
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (NetWork().isInternetAvailable(this@MainActivity)) {
                    initView();
                    timer.cancel();
                }
            }
        }, Date(), 1000 * 10);
    }

    class MyHandler(activity: MainActivity) : Handler() {
        private val mWeakReference = WeakReference<MainActivity>(activity)  // 弱引用 activity 避免内存泄漏

        override fun handleMessage(msg: Message) {
            val mainActivity = mWeakReference.get()
            when (msg.what) {
                1 -> {
                    mainActivity?.listView!!.adapter = MoviceListAdapter(mainActivity);
                }
            }
        }
    }

    private class MoviceListAdapter(context: Context) : BaseAdapter() {
        private val mInflator: LayoutInflater

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return sList.size
        }

        override fun getItem(position: Int): Any {
            return sList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.list_row, parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }
            Glide.with(vh.img).load(sList[position].img).into(vh.img);
            return view
        }
    }

    private class ListRowHolder(row: View?) {
        val img: ImageView

        init {
            this.img = row?.findViewById(R.id.img_item) as ImageView
        }
    }
}
